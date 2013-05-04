import sbt._
import Keys._

import collection.immutable.HashSet
import java.beans._
import java.lang.reflect._


object AndroidClassExtractor {

  def toScalaType(tpe: Type): String = tpe match {
    case null => throw new Error("Property cannot be null")
    case t: GenericArrayType => 
      "Array[" + toScalaType(t.getGenericComponentType) + "]"
    case t: ParameterizedType => toScalaType(t.getRawType) +
      "[" + t.getActualTypeArguments.map(toScalaType(_)).mkString + "]"
    case t: TypeVariable[_] => t.getName
    case t: WildcardType => "_" //t.toString
    case t: Class[_] => {
      if (t.isArray) {
        "Array[" + toScalaType(t.getComponentType) + "]"
      } else if (t.isPrimitive) {
        t.getName match {
          case "void" => "Unit"
          case n => n.capitalize
        }
      } else {
        t.getName.replace("$", ".")
      }
    }
    case _ => 
      throw new Error("Cannot find type of " + tpe.getClass + " ::" + tpe.toString)
  }

  private def toAndroidClass(cls: Class[_]) = {
    val parent = cls.getSuperclass
    val superPropNames = 
      if (parent == null)
        new HashSet[String]
      else
        Introspector.getBeanInfo(parent).getPropertyDescriptors.toList.map(p => p.getName + p.getPropertyType).toSet

    val superMethodNames = 
      if (parent == null)
        new HashSet[String]
      else
        Introspector.getBeanInfo(parent).getMethodDescriptors.toList.map(m => m.getName).toSet

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

      if (Seq(readMethod, writeMethod).flatten.exists( ! _.getDeclaringClass.getName.equals(cls.getName))) {
        None
      } else {
        val getter = readMethod map (_.getName)
        val setter = writeMethod map (_.getName)
        val tpe = readMethod.map(_.getGenericReturnType).getOrElse(writeMethod.get.getGenericParameterTypes()(0))
        val switch = if (name.endsWith("Enabled")) Some(name.replace("Enabled", "").capitalize) else None

        Some(AndroidProperty(name, toScalaType(tpe), getter, setter, switch, nameClashes))
      }
    }

    def isValidProperty(pdesc: PropertyDescriptor): Boolean =
      (! pdesc.isInstanceOf[IndexedPropertyDescriptor]) && pdesc.getDisplayName.matches("^[a-zA-z].*") &&
      (! superPropNames(pdesc.getName + pdesc.getPropertyType)) && ( ! "adapter".equals(pdesc.getName))

    def isListenerSetterOrAdder(mdesc: MethodDescriptor): Boolean = {
      val name = mdesc.getName
      name.matches("^(set|add).+Listener$") && (! superMethodNames(name))
    }

    def isCallbackMethod(mdesc: MethodDescriptor): Boolean =
      ! mdesc.getName.startsWith("get")

    def extractMethodsFromListener(callbackCls: Class[_]) =
      Introspector
        .getBeanInfo(callbackCls)
        .getMethodDescriptors
        .filter(isCallbackMethod)
        .map(toAndroidMethod)
        .toList

    def toAndroidMethod(mdesc: MethodDescriptor): AndroidMethod = {
      val m = mdesc.getMethod
      AndroidMethod(
        m.getName,
        Option(m.getGenericParameterTypes).flatten.toSeq.map(AndroidClassExtractor.toScalaType),
        AndroidClassExtractor.toScalaType(m.getReturnType)
      )
    }
    
    def toAndroidListeners(mdesc: MethodDescriptor): Seq[AndroidListener] = {
      val method = mdesc.getMethod
      val setter = mdesc.getName
      val paramsDescs: List[ParameterDescriptor] = Option(mdesc.getParameterDescriptors).toList.flatten
      val callbackClassName = toScalaType(method.getGenericParameterTypes()(0))
      val callbackMethods   = extractMethodsFromListener(method.getParameterTypes()(0))

      callbackMethods.map { cm =>
        AndroidListener(
          cm.name,
          callbackMethods.find(_.name == cm.name).get.retType,
          cm.paramTypes,
          cm.paramTypes.nonEmpty,
          setter,
          callbackClassName,
          callbackMethods.map { icm =>
            AndroidCallbackMethod(
              icm.name,
              icm.retType,
              icm.paramTypes,
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
    val simpleName = fullName.split('.').last
    val pkg = fullName.split('.').init.mkString

    val parentName = Option(parent) map (_.getName)

    val isA = getHierarchy(cls).toSet

    AndroidClass(fullName, simpleName, pkg, parentName, isA, props, listeners)
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
      , classOf[android.preference.EditTextPreference]
    )        

    val res = clss.view.map(toAndroidClass).map(c => c.fullName -> c).toMap

    val values = res.values.toList
    s.log.info("Extracted from Android")
    s.log.info("Classes: "+ values.length)
    s.log.info("Properties: "+ values.map(_.properties).flatten.length)
    s.log.info("Listeners: "+ values.map(_.listeners).flatten.length)
    res
  }
}

