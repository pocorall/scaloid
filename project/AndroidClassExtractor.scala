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
  private def isFinal(m: Member): Boolean = Modifier.isFinal(m.getModifiers)
  private def isFinal(c: Class[_]): Boolean = Modifier.isFinal(c.getModifiers)

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

    AndroidClass(name, pkg, tpe, parentType, props, listeners, isA, isAbstract(cls), isFinal(cls))
  }

  def extractTask = (streams) map { s =>

    val view: List[Class[_]] = {
      import android.view._
      List(
          classOf[View], classOf[ViewGroup], classOf[ContextMenu], classOf[SurfaceView], classOf[ViewStub]
      )
    }

    val widget: List[Class[_]] = {
      import android.widget._
      List(
          classOf[TextView], classOf[Button], classOf[AbsListView], classOf[ListView]
        , classOf[FrameLayout], classOf[LinearLayout], classOf[AdapterView[_]], classOf[ImageView]
        , classOf[ProgressBar], classOf[AnalogClock], classOf[GridView], classOf[ExpandableListView]
        , classOf[AbsSpinner], classOf[Spinner], classOf[Gallery], classOf[AbsSeekBar], classOf[SeekBar]
        , classOf[RatingBar], classOf[DatePicker], classOf[HorizontalScrollView], classOf[MediaController]
        , classOf[ScrollView], classOf[TabHost], classOf[TimePicker], classOf[ViewAnimator]
        , classOf[ViewFlipper], classOf[ViewSwitcher], classOf[ImageSwitcher], classOf[TextSwitcher]
        , classOf[EditText], classOf[TableRow], classOf[CompoundButton], classOf[CheckBox]
        , classOf[RadioButton], classOf[RadioGroup], classOf[TabWidget], classOf[TableLayout]
        , classOf[Chronometer], classOf[ToggleButton], classOf[CheckedTextView], classOf[DigitalClock]
        , classOf[QuickContactBadge], classOf[RatingBar], classOf[RelativeLayout], classOf[TwoLineListItem]
        , classOf[DialerFilter], classOf[VideoView], classOf[MultiAutoCompleteTextView]
        , classOf[AutoCompleteTextView], classOf[ZoomButton], classOf[AbsoluteLayout]
        , classOf[ZoomControls], classOf[ImageButton]

        // API Level 14 or above
        //, classOf[android.widget.Space]
      )
    }

    val systemService: List[Class[_]] = {
      import android.accounts._
      import android.app._
      import android.app.admin._
      import android.content._
      import android.hardware._
      import android.location._
      import android.media._
      import android.net._
      import android.net.wifi._
      import android.os._
      import android.telephony._
      import android.text._
      import android.view._
      import android.view.accessibility._
      import android.view.inputmethod._
      List(
          classOf[AccessibilityManager], classOf[AccountManager], classOf[ActivityManager]
        , classOf[AlarmManager], classOf[AudioManager], classOf[ClipboardManager]
        , classOf[ConnectivityManager], classOf[DevicePolicyManager], classOf[DropBoxManager]
        , classOf[InputMethodManager], classOf[KeyguardManager], classOf[LayoutInflater]
        , classOf[LocationManager], classOf[NotificationManager], classOf[PowerManager]
        , classOf[SearchManager], classOf[SensorManager], classOf[TelephonyManager]
        , classOf[UiModeManager], classOf[Vibrator], classOf[WallpaperManager]
        , classOf[WifiManager], classOf[WindowManager]

        // API Level 9 or Above
        // , classOf[DownloadManager]
        
        // API Level 10 or Above
        // , classOf[NfcManager], classOf[StorageManager]

        // API Level 12 or above
        // , classOf[usb.UsbManager]

        // API Level 14 or above
        // , classOf[textservice.TextServicesManager], classOf[pop.WifiP2pManager]

        // API Level 16 or above
        // , classOf[input.InputManager], classOf[MediaRouter], classOf[NsdManager]
      )
    }

    val preference: List[Class[_]] = {
      import android.preference._
      List(
        classOf[Preference], classOf[DialogPreference], classOf[EditTextPreference]
      )
    }

    val etc: List[Class[_]] = {
      import android._
      List(
          classOf[webkit.WebView], classOf[gesture.GestureOverlayView]
        , classOf[opengl.GLSurfaceView], classOf[opengl.GLSurfaceView]
        , classOf[inputmethodservice.ExtractEditText], classOf[inputmethodservice.KeyboardView]
        , classOf[appwidget.AppWidgetHostView]
      )
    }

    val clss = view ++ widget ++ systemService ++ preference ++ etc
    val res = clss.view
                .map(toAndroidClass)
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

