$license()$

package org.scaloid.common

import android.app._
import android.content._
import android.view._
import android.view.View._
import android.widget._
import scala.reflect._
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions
import WidgetImplicits._


$android.widget; format="wrap-all-classes"$

class SVerticalLayout(implicit context: Context, parentVGroup: TraitViewGroup[_] = null) extends SLinearLayout {
  orientation = VERTICAL
}

object SVerticalLayout {

  def apply[LP <: ViewGroupLayoutParams[_, SVerticalLayout]]()
        (implicit context: android.content.Context, defaultLayoutParam: SLinearLayout => LP): SVerticalLayout = {
    val v = new SVerticalLayout
    v.<<.parent.+=(v)
    v
  }
}

abstract class TextViewCompanion[T <: TextView : ClassTag] {
  def create[LP <: ViewGroupLayoutParams[_, T]]()(implicit context: Context, defaultLayoutParam: T => LP): T

  def apply[LP <: ViewGroupLayoutParams[_, T]](txt: CharSequence)
                                              (implicit context: Context, defaultLayoutParam: T => LP): T = {
    val v = create()
    v text txt
    v.<<.parent.+=(v)
    v
  }

  def apply(text: CharSequence, ignore: Nothing) = ??? // Just for implicit conversion of ViewOnClickListener
  /**
   * interval: If it is larger than 0, the button enables press-and-hold action with given interval in milliseconds.
   */
  def apply[LP <: ViewGroupLayoutParams[_, T]](text: CharSequence, onClickListener: ViewOnClickListener, interval: Int = -1)
                                                 (implicit context: Context, defaultLayoutParam: T => LP): T = {
    val v = apply(text, onClickListener.onClickListener)
    if (interval >= 0) v.onPressAndHold(interval, onClickListener.func(v)) else v
  }

  private def apply[LP <: ViewGroupLayoutParams[_, T]](text: CharSequence, onClickListener: View.OnClickListener)
                                                      (implicit context: Context, defaultLayoutParam: T => LP): T = {
    val v = create()
    v.text = text
    v.setOnClickListener(onClickListener)
    v.<<.parent.+=(v)
    v
  }
}

abstract class ImageViewCompanion[T <: ImageView : ClassTag] {
  def create[LP <: ViewGroupLayoutParams[_, T]]()(implicit context: Context, defaultLayoutParam: T => LP): T

  def apply[LP <: ViewGroupLayoutParams[_, T]](imageResource: android.graphics.drawable.Drawable)
      (implicit context: Context, defaultLayoutParam: T => LP): T = {
    val v = create()
    v.imageDrawable = imageResource
    v.<<.parent.+=(v)
    v
  }

  def apply(image: android.graphics.drawable.Drawable, ignore: Nothing) = ???  // Just for implicit conversion of ViewOnClickListener

  /**
    * interval: If it is larger than 0, the button enables press-and-hold action with given interval in milliseconds.
    */
  def apply[LP <: ViewGroupLayoutParams[_, T]](imageResource: android.graphics.drawable.Drawable, onClickListener: ViewOnClickListener, interval: Int = -1)
      (implicit context: Context, defaultLayoutParam: T => LP): T = {
    val v = apply(imageResource, onClickListener.onClickListener)
    if(interval >= 0) v.onPressAndHold(interval, onClickListener.func(v)) else v
  }

  private def apply[LP <: ViewGroupLayoutParams[_, T]](imageResource: android.graphics.drawable.Drawable, onClickListener: View.OnClickListener)
      (implicit context: Context, defaultLayoutParam: T => LP): T = {
    val v = create()
    v.imageDrawable = imageResource
    v.setOnClickListener(onClickListener)
    v.<<.parent.+=(v)
    v
  }
}

$android.inputmethodservice.ExtractEditText; format="whole"$
$android.inputmethodservice.KeyboardView; format="rich"$
$android.opengl.GLSurfaceView; format="whole"$
$android.appwidget.AppWidgetHostView; format="whole"$
$android.gesture.GestureOverlayView; format="whole"$
$android.database.DataSetObserver; format="whole"$
$android.webkit.WebView; format="whole"$
$android.graphics.Paint; format="whole"$


trait WidgetImplicits {
  $android.widget; format="package-implicit-conversions"$

  $android.inputmethodservice.ExtractEditText; format="implicit-conversion"$
  $android.inputmethodservice.KeyboardView; format="implicit-conversion"$
  $android.opengl.GLSurfaceView; format="implicit-conversion"$
  $android.appwidget.AppWidgetHostView; format="implicit-conversion"$
  $android.gesture.GestureOverlayView; format="implicit-conversion"$
  $android.database.DataSetObserver; format="implicit-conversion"$
  $android.webkit.WebView; format="implicit-conversion"$
}
object WidgetImplicits extends WidgetImplicits