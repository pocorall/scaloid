import sbt._
import Keys._

import collection.immutable.HashSet
import java.beans.{PropertyDescriptor, IndexedPropertyDescriptor, Introspector}
import java.lang.reflect._


case class AndroidProperty(
  tpe: String,
  getter: Option[String],
  setter: Option[String],
  nameClashes: Boolean
)

case class AndroidClass(
  name: String,
  parent: Option[String],
  properties: Map[String, AndroidProperty]
)


object AndroidClassExtractor {

  val extractKey = TaskKey[List[AndroidClass]]("extract-android-classes")

  private def capitalize(s: String) = {
    s(0).toUpper + s.substring(1, s.length).toLowerCase
  }

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
        capitalize(t.getName)
      } else {
        t.getName.replace("$", ".")
      }
    }
    case _ => 
      throw new Error("Cannot find type of " + tpe.getClass + " ::" + tpe.toString)
  }

  private def toAndroidClass(cls: Class[_]) = {
    val superCls = cls.getSuperclass
    val superPropNames = 
      if (superCls == null)
        new HashSet[String]
      else
        Introspector.getBeanInfo(superCls).getPropertyDescriptors.toList.map(f => f.getName + f.getPropertyType).toSet

    def toAndroidProperty(pdesc: PropertyDescriptor): Option[(String, AndroidProperty)] = {
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

        Some((displayName, AndroidProperty(toScalaTypeName(tpe), getter, setter, nameClashes)))
      }
    }

    val props = Introspector.getBeanInfo(cls).getPropertyDescriptors.toList
                  .filter(!_.isInstanceOf[IndexedPropertyDescriptor])
                  .map(toAndroidProperty)

    AndroidClass(
      name = cls.getName,
      parent = Option(superCls) map (_.getName),
      properties = props.flatten.toMap[String, AndroidProperty]
    )

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

    clss map toAndroidClass
  }
}
