import sbt._
import Keys._

import collection.immutable.HashSet
import collection.mutable.ArrayBuffer
import collection.mutable.StringBuilder
import java.beans.{PropertyDescriptor, IndexedPropertyDescriptor, Introspector}
import java.lang.reflect._
import scala.Array


case class AndroidProperty(
  tpe: String,
  getter: Option[String] = None,
  setter: Option[String] = None,
  nameClashes: Boolean = false
)

case class AndroidClass(
  name: String,
  parent: Option[String],
  properties: Map[String, AndroidProperty]
)



object AndroidClassExtractor {

  val extractKey = TaskKey[String]("extract-android-classes")

  def capitalize(s: String) = {
    s(0).toUpper + s.substring(1, s.length).toLowerCase
  }

  def toScalaTypeName(tpe: Type): String = tpe match {
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
  


  def toAndroidClass(cls: Class[_]) = {
    val superCls = cls.getSuperclass
    val superPropNames = 
      if (superCls == null)
        new HashSet[String]
      else
        Introspector.getBeanInfo(superCls).getPropertyDescriptors.toList.map(f => f.getName + f.getPropertyType).toSet

    def toAndroidProperty(pdesc: PropertyDescriptor): Option[(String, AndroidProperty)] = {
      var r = AndroidProperty(tpe = "ERROR")

      if (superPropNames(pdesc.getName + pdesc.getPropertyType))
        return None
      
      if ("adapter" equals pdesc.getName)
        return None
      
      val displayName = pdesc.getDisplayName
      try {
        cls.getMethod(displayName)
        r = r.copy(nameClashes = true)
      } catch {
        case e: NoSuchMethodException => // does nothing
      }

      val readMethod = pdesc.getReadMethod
      if (readMethod != null)
        if (!readMethod.getDeclaringClass.getName.equals(cls.getName))
          return None
        else 
          r = r.copy(getter = Some(readMethod.getName), tpe = toScalaTypeName(readMethod.getGenericReturnType))
      
      val writeMethod = pdesc.getWriteMethod
      if (writeMethod != null)
        if (!writeMethod.getDeclaringClass.getName.equals(cls.getName))
          return None
        else
          r = r.copy(setter = Some(writeMethod.getName), tpe = toScalaTypeName(writeMethod.getGenericParameterTypes()(0)))
      
      Some((displayName, r))
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

    val res = clss.map(toAndroidClass)
    res.toString
  }
}
