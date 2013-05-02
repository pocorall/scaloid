import sbt._
import Keys._

import collection.immutable.HashSet
import java.beans._
import java.lang.reflect._


case class AndroidProperty(
  name: String,
  tpe: String,
  getter: Option[String],
  setter: Option[String],
  nameClashes: Boolean
)

case class AndroidMethodParam(
  name: String,
  tpe: String
)

case class AndroidMethod(
  name: String,
  types: Seq[AndroidMethodParam],
  retType: String = "Unit"
)

case class AndroidListener(
  name: String,
  setter: String,
  methods: Seq[AndroidMethod]
)

case class AndroidClass(
  name: String,
  parent: Option[String],
  properties: Seq[AndroidProperty],
  listeners: Seq[AndroidListener]
)


object AndroidClassExtractor {

  val extractKey = TaskKey[Map[String, AndroidClass]]("extract-android-classes")

  private def toScalaTypeName(tpe: Type): String = tpe match {
    case null => throw new Error("Property cannot be null")
    case t: GenericArrayType => 
      "Array[" + toScalaTypeName(t.getGenericComponentType) + "]"
    case t: ParameterizedType => toScalaTypeName(t.getRawType) +
      "[" + t.getActualTypeArguments.map(toScalaTypeName(_)).mkString + "]"
    case t: TypeVariable[_] => t.getName
    case t: WildcardType => "_" //t.toString
    case t: Class[_] => {
      if (t.isArray) {
        "Array[" + toScalaTypeName(t.getComponentType) + "]"
      } else if (t.isPrimitive) {
        t.getName.capitalize
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
        Introspector.getBeanInfo(parent).getMethodDescriptors.toList.map(m => m.getName + m.getMethod.getName).toSet

    def toAndroidProperty(pdesc: PropertyDescriptor): Option[AndroidProperty] = {
      if (superPropNames(pdesc.getName + pdesc.getPropertyType) || "adapter".equals(pdesc.getName))
        return None
      
      val displayName = pdesc.getDisplayName
      var nameClashes = false

      try {
        cls.getMethod(displayName)
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

        Some(AndroidProperty(displayName, toScalaTypeName(tpe), getter, setter, nameClashes))
      }
    }

    def toAndroidListener(mdesc: MethodDescriptor): Option[AndroidListener] = {
      val method = mdesc.getMethod
      val name = mdesc.getName
      val paramsDescs: List[ParameterDescriptor] = Option(mdesc.getParameterDescriptors).toList.flatten

      if (superMethodNames(mdesc.getName + method.getName))
        return None

      val handler = method.getParameterTypes.toList.headOption
      val handlerName = handler.map(_.getName).getOrElse("None")

      println(cls.getName + "::"+ name +" -> "+ handlerName)

      Some(AndroidListener(name, "Type", Nil))
    }

    
    val props = Introspector.getBeanInfo(cls).getPropertyDescriptors.toSeq
                  .filter(!_.isInstanceOf[IndexedPropertyDescriptor])
                  .map(toAndroidProperty)
                  .flatten

    val listeners = Introspector.getBeanInfo(cls).getMethodDescriptors.toSeq
                  .filter(_.getName.endsWith("Listener"))
                  .map(toAndroidListener)
                  .flatten

    AndroidClass(cls.getName, Option(parent).map(_.getName), props, listeners)

  }

  def extract = {
    val clss = List(classOf[android.view.View], classOf[android.widget.TextView], classOf[android.widget.Button], classOf[android.widget.AbsListView]
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

    )

    clss.view.map(toAndroidClass).map(c => c.name -> c).toMap
  }
}
