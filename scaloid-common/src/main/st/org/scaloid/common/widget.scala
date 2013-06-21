$license()$

package org.scaloid.common

import android.app._
import android.content._
import android.view._
import android.view.View._
import android.widget._
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions
import WidgetImplicits._


$wrapAllPackageMembers(android.widget)$

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

$wholeClassDef(android.inputmethodservice.ExtractEditText)$
$richClassDef(android.inputmethodservice.KeyboardView)$
$wholeClassDef(android.opengl.GLSurfaceView)$
$wholeClassDef(android.appwidget.AppWidgetHostView)$
$wholeClassDef(android.gesture.GestureOverlayView)$
$wholeClassDef(android.database.DataSetObserver)$
$wholeClassDef(android.webkit.WebView)$
