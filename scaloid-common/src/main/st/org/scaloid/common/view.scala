$license()$

package org.scaloid.common

import android.app._
import android.content._
import android.view._
import android.view.View._
import android.widget._
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions
import ViewImplicits._


trait PressAndHoldable {
  def basis: android.view.View

  class PressAndHoldListener(interval: Int, onPressed: () => Unit) extends View.OnTouchListener with View.OnLongClickListener {
    var autoIncrementing: Boolean = false
    private val repeatUpdateHandler = new android.os.Handler()

    override def onTouch(v: View, event: MotionEvent): Boolean = {
      if (event.getAction == MotionEvent.ACTION_UP && autoIncrementing) {
        autoIncrementing = false
      }
      false
    }

    override def onLongClick(p1: View): Boolean = {
      autoIncrementing = true
      repeatUpdateHandler.post(new RptUpdater)
      false
    }

    class RptUpdater extends Runnable {
      override def run() {
        if (autoIncrementing) {
          onPressed()
          repeatUpdateHandler.postDelayed(this, interval)
        }
      }
    }
  }

  def onPressAndHold(interval: Int, onPressed: => Unit) {
    val listener = new PressAndHoldListener(interval, () => onPressed)
    basis.setOnTouchListener(listener)
    basis.setOnLongClickListener(listener)
  }
}


$wholeClassDef(android.view.View)$

$wholeClassDef(android.view.ViewGroup)$

trait ViewGroupLayoutParams[LP <: ViewGroupLayoutParams[_,_], V <: View] extends ViewGroup.LayoutParams {
  def basis: LP

  def fill = {
    width = ViewGroup.LayoutParams.MATCH_PARENT
    height = ViewGroup.LayoutParams.MATCH_PARENT
    basis
  }
  def wrap = {
    width = ViewGroup.LayoutParams.WRAP_CONTENT
    height = ViewGroup.LayoutParams.WRAP_CONTENT
    basis
  }

  def parent : TraitViewGroup[_]

  def >> : V
}

trait ViewGroupMarginLayoutParams[LP <: ViewGroupMarginLayoutParams[_,_], V <: View] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[LP, V] {

  def marginBottom(size: Int) = {
    bottomMargin = size
    basis
  }

  def marginTop(size: Int) = {
    topMargin = size
    basis
  }

  def marginLeft(size: Int) = {
    leftMargin = size
    basis
  }

  def marginRight(size: Int) = {
    rightMargin = size
    basis
  }

  def margin(size:Int) = {
    bottomMargin = size
    topMargin = size
    leftMargin = size
    rightMargin = size
    basis
  }

  def margin(top:Int, right:Int, bottom:Int, left:Int) = {
    bottomMargin = bottom
    topMargin = top
    leftMargin = left
    rightMargin = right
    basis
  }
}

$wholeClassDef(android.view.Menu)$
$wholeClassDef(android.view.ContextMenu)$
$wholeClassDef(android.view.SurfaceView)$
$wholeClassDef(android.view.ViewStub)$
$if(ver.gte_14)$
  $wholeClassDef(android.view.ActionProvider)$
$endif$


trait ViewImplicits {
  $implicitConversion(android.view.View)$
  $implicitConversion(android.view.ViewGroup)$
  $implicitConversion(android.view.Menu)$
  $implicitConversion(android.view.ContextMenu)$
  $implicitConversion(android.view.SurfaceView)$
  $implicitConversion(android.view.ViewStub)$
$if(ver.gte_14)$
  $implicitConversion(android.view.ActionProvider)$
$endif$
}
object ViewImplicits extends ViewImplicits