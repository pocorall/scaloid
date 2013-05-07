import sbt._
import Keys._

import collection.immutable.HashSet
import java.beans._
import java.lang.reflect._


object AndroidClassExtractor {

  def toScalaType(tpe: Type): ScalaType =
    tpe match {
      case null => throw new Error("Property cannot be null")
      case t: GenericArrayType =>
        ScalaType("Array", Seq(toScalaType(t.getGenericComponentType)))
      case t: ParameterizedType =>
        ScalaType(
          toScalaType(t.getRawType).name,
          t.getActualTypeArguments.map(toScalaType).toSeq
        )
      case t: TypeVariable[_] =>
        ScalaType(t.getName, t.getBounds.map(toScalaType).toSeq, true)
      case t: WildcardType => ScalaType("_")
      case t: Class[_] => {
        if (t.isArray) {
          ScalaType("Array", Seq(toScalaType(t.getComponentType)))
        } else if (t.isPrimitive) {
          ScalaType(t.getName match {
            case "void" => "Unit"
            case n => n.capitalize
          })
        } else {
          ScalaType(
            t.getName.replace("$", "."),
            t.getTypeParameters.take(1).map(_ => ScalaType("_")).toSeq
          )
        }
      }
      case _ =>
        throw new Error("Cannot find type of " + tpe.getClass + " ::" + tpe.toString)
    }

  private def isAbstract(m: Member): Boolean = Modifier.isAbstract(m.getModifiers)
  private def isAbstract(c: Class[_]): Boolean = Modifier.isAbstract(c.getModifiers)

  private def methodSignature(m: Method): String = List(
    m.getName,
    m.getReturnType.getName,
    "["+m.getParameterTypes.map(_.getName).toList.mkString(",")+"]"
  ).mkString(":")

  private def methodSignature(mdesc: MethodDescriptor): String =
    methodSignature(mdesc.getMethod)

  private def propSignature(pdesc: PropertyDescriptor): String = List(
    pdesc.getName,
    pdesc.getPropertyType
  ).mkString(":")

  private def toAndroidClass(cls: Class[_]) = {
    implicit val _cls = cls

    val parentBeanInfo = Option(cls.getSuperclass).map(Introspector.getBeanInfo)


    val superProps: Set[String] = 
      parentBeanInfo.map { 
        _.getPropertyDescriptors.toList.map(propSignature).toSet
      }.getOrElse(Set())

    val superMethods: Set[String] = 
      parentBeanInfo.map {
        _.getMethodDescriptors.toList.map(methodSignature).toSet
      }.getOrElse(Set())

    val superGetters: Set[String] = 
      parentBeanInfo.map {
        _.getPropertyDescriptors.toList
          .map(m => Option(m.getReadMethod))
          .filter(_.nonEmpty)
          .map(_.get.getName).toSet
      }.getOrElse(Set())


    def toAndroidMethod(m: Method): AndroidMethod = {
      val name = m.getName
      val retType = AndroidClassExtractor.toScalaType(m.getGenericReturnType)
      val argTypes = Option(m.getGenericParameterTypes)
                      .flatten
                      .toSeq
                      .map(AndroidClassExtractor.toScalaType(_))
      val paramedTypes = (retType +: argTypes).filter(_.isVar).distinct

      AndroidMethod(name, retType, argTypes, paramedTypes, isAbstract(m))
    }

    def isValidProperty(pdesc: PropertyDescriptor): Boolean =
      (! pdesc.isInstanceOf[IndexedPropertyDescriptor]) && pdesc.getDisplayName.matches("^[a-zA-z].*") &&
      (! superProps(propSignature(pdesc)))

    def isListenerSetterOrAdder(mdesc: MethodDescriptor): Boolean = {
      val name = mdesc.getName
      name.matches("^(set|add).+Listener$") && !superMethods(methodSignature(mdesc))
    }

    def isCallbackMethod(mdesc: MethodDescriptor): Boolean =
      ! mdesc.getName.startsWith("get")

    def extractMethodsFromListener(callbackCls: Class[_]): List[AndroidMethod] =
      Introspector.getBeanInfo(callbackCls).getMethodDescriptors
        .filter(isCallbackMethod)
        .map(_.getMethod)
        .map(toAndroidMethod)
        .toList

    def getPolymorphicSetters(method: Method): Seq[AndroidMethod] = {
      val name = method.getName
      Introspector.getBeanInfo(cls).getMethodDescriptors.view
        .map(_.getMethod)
        .filter { m => 
          !isAbstract(m) && m.getName == name && 
            m.getParameterTypes.length == 1 && 
            !superMethods(methodSignature(m)) 
        }
        .map(toAndroidMethod)
        .toSeq
    }

    def toAndroidProperty(pdesc: PropertyDescriptor): Option[AndroidProperty] = {
      val name = pdesc.getDisplayName
      var nameClashes = false

      try {
        cls.getMethod(name)
        nameClashes = true
      } catch {
        case e: NoSuchMethodException => // does nothing
      }

      val readMethod = Option(pdesc.getReadMethod) 
      val writeMethod = Option(pdesc.getWriteMethod)

      val getter = readMethod
                      .filter(m => ! superMethods(methodSignature(m)))
                      .map { m =>
                        val am = toAndroidMethod(m)
                        if (superGetters(am.name))
                          am.copy(isOverride = true)
                        else
                          am
                      }

      val setters = writeMethod
                      .map(getPolymorphicSetters)
                      .toSeq.flatten
                      .sortBy(_.argTypes(0).name)

      (getter, setters) match {
        case (None, Nil) => None
        case (g, ss) =>
          val tpe = getter.map(_.retType).getOrElse(setters.first.argTypes.first)
          val switch = if (name.endsWith("Enabled")) Some(name.replace("Enabled", "").capitalize) else None
          Some(AndroidProperty(name, tpe, getter, setters, switch, nameClashes))
      }
    }



    
    def toAndroidListeners(mdesc: MethodDescriptor): Seq[AndroidListener] = {
      val method = mdesc.getMethod
      val setter = mdesc.getName
      val paramsDescs: List[ParameterDescriptor] = Option(mdesc.getParameterDescriptors).toList.flatten
      val callbackClassName = toScalaType(method.getGenericParameterTypes()(0)).name
      val callbackMethods   = extractMethodsFromListener(method.getParameterTypes()(0))

      callbackMethods.map { cm =>
        AndroidListener(
          cm.name,
          callbackMethods.find(_.name == cm.name).get.retType,
          cm.argTypes,
          cm.argTypes.nonEmpty,
          setter,
          callbackClassName,
          callbackMethods.map { icm =>
            AndroidCallbackMethod(
              icm.name,
              icm.retType,
              icm.argTypes,
              icm.name == cm.name
            )
          }
        )
      }.filter(_.isSafe)
    }

    def getHierarchy(c: Class[_], accu: List[String] = Nil): List[String] =
      if (c == null) accu
      else getHierarchy(c.getSuperclass, c.getName.split('.').last :: accu)

    val props = Introspector.getBeanInfo(cls).getPropertyDescriptors.toSeq
                  .filter(isValidProperty)
                  .map(toAndroidProperty)
                  .flatten
                  .sortBy(_.name)

    val listeners = Introspector.getBeanInfo(cls).getMethodDescriptors.toSeq
                  .filter(isListenerSetterOrAdder)
                  .map(toAndroidListeners)
                  .flatten
                  .sortBy(_.name)

    val fullName = cls.getName

    val name = fullName.split('.').last
    val tpe = toScalaType(cls)
    val pkg = fullName.split('.').init.mkString
    val parentType = Option(cls.getGenericSuperclass)
                        .map(toScalaType)
                        .filter(_.name.startsWith("android"))

    val isA = getHierarchy(cls).toSet

    AndroidClass(name, pkg, tpe, parentType, props, listeners, isA, isAbstract(cls))
  }

  def extractTask = (streams) map { s =>
    val clss = List(
      // Widget
        classOf[android.view.View], classOf[android.widget.TextView], classOf[android.widget.Button], classOf[android.widget.AbsListView]
      , classOf[android.widget.ListView], classOf[android.view.ViewGroup], classOf[android.widget.FrameLayout]
      , classOf[android.widget.LinearLayout], classOf[android.view.ContextMenu], classOf[android.widget.AdapterView[_]]
      , classOf[android.inputmethodservice.KeyboardView], classOf[android.widget.ImageView], classOf[android.widget.ProgressBar]
      , classOf[android.widget.AnalogClock], classOf[android.view.SurfaceView], classOf[android.view.ViewStub]
      , classOf[android.widget.GridView], classOf[android.widget.ExpandableListView], classOf[android.widget.AbsSpinner]
      , classOf[android.widget.Spinner], classOf[android.widget.Gallery], classOf[android.widget.AbsSeekBar]
      , classOf[android.widget.SeekBar], classOf[android.widget.RatingBar], classOf[android.appwidget.AppWidgetHostView]
      , classOf[android.widget.DatePicker], classOf[android.gesture.GestureOverlayView], classOf[android.widget.HorizontalScrollView]
      , classOf[android.widget.MediaController], classOf[android.widget.ScrollView], classOf[android.widget.TabHost]
      , classOf[android.widget.TimePicker], classOf[android.widget.ViewAnimator], classOf[android.widget.ViewFlipper]
      , classOf[android.widget.ViewSwitcher], classOf[android.widget.ImageSwitcher], classOf[android.widget.TextSwitcher]
      , classOf[android.widget.EditText], classOf[android.widget.TableRow]
      , classOf[android.widget.CompoundButton], classOf[android.widget.CheckBox], classOf[android.widget.RadioButton]
      , classOf[android.widget.RadioGroup], classOf[android.widget.TabWidget], classOf[android.widget.TableLayout]
      , classOf[android.widget.Chronometer], classOf[android.widget.ToggleButton], classOf[android.widget.CheckedTextView]
      , classOf[android.widget.DigitalClock], classOf[android.widget.QuickContactBadge], classOf[android.widget.RatingBar]
      , classOf[android.widget.RelativeLayout], classOf[android.widget.TwoLineListItem], classOf[android.widget.DialerFilter]
      , classOf[android.widget.VideoView], classOf[android.widget.MultiAutoCompleteTextView]
      , classOf[android.widget.AutoCompleteTextView], classOf[android.widget.ZoomButton], classOf[android.webkit.WebView]
      , classOf[android.widget.AbsoluteLayout], classOf[android.widget.ZoomControls], classOf[android.widget.ImageButton]
      , classOf[android.opengl.GLSurfaceView], classOf[android.opengl.GLSurfaceView], classOf[android.inputmethodservice.ExtractEditText]

      // API Level 14 or above
      //, classOf[android.widget.Space]

      // Service
      , classOf[android.telephony.TelephonyManager]

      // Preference
      , classOf[android.preference.Preference]
      , classOf[android.preference.DialogPreference]
      , classOf[android.preference.EditTextPreference]
    )        

    val res = clss.view
                .map(toAndroidClass)
                .map{c =>
                  //println(c.name, c.tpe.params)
                  c
                }
                .map(c => c.tpe.name -> c)
                .toMap

    val values = res.values.toList
    s.log.info("Extracted from Android")
    s.log.info("Classes: "+ values.length)
    s.log.info("Properties: "+ values.map(_.properties).flatten.length)
    s.log.info("Listeners: "+ values.map(_.listeners).flatten.length)
    res
  }
}

