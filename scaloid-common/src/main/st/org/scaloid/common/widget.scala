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