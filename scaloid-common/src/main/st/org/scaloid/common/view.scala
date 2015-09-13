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


trait PressAndHoldable[+This <: View] {
  def basis: This

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
      if(interval == 0)
        onPressed()
      else
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

  def onPressAndHold(interval: Int, onPressed: => Unit): This = {
    val listener = new PressAndHoldListener(interval, () => onPressed)
    basis.onTouchListener(listener)
    basis.onLongClickListener(listener)
  }
}

trait ViewOnClickListener {
  def func: View => Unit

  def onClickListener: View.OnClickListener
}

$android.view.View; format="whole"$

$android.view.ViewGroup; format="whole"$

trait ViewGroupLayoutParams[+This <: ViewGroupLayoutParams[_,_], V <: View] extends ViewGroup.LayoutParams {
  def basis: This

  /**
   * A shorthand for <<(MATCH_PARENT, MATCH_PARENT)
   */
  def fill = {
    width = ViewGroup.LayoutParams.MATCH_PARENT
    height = ViewGroup.LayoutParams.MATCH_PARENT
    basis
  }
  /**
   * A shorthand for <<(WRAP_CONTENT, WRAP_CONTENT)
   */
  def wrap = {
    width = ViewGroup.LayoutParams.WRAP_CONTENT
    height = ViewGroup.LayoutParams.WRAP_CONTENT
    basis
  }
  /**
   * A shorthand for <<(MATCH_PARENT, WRAP_CONTENT)
   */
  def fw = {
    width = ViewGroup.LayoutParams.MATCH_PARENT
    height = ViewGroup.LayoutParams.WRAP_CONTENT
    basis
  }
  /**
   * A shorthand for <<(WRAP_CONTENT, MATCH_PARENT)
   */
  def wf = {
    width = ViewGroup.LayoutParams.WRAP_CONTENT
    height = ViewGroup.LayoutParams.MATCH_PARENT
    basis
  }

  def parent : TraitViewGroup[_]

  def >> : V
}

trait ViewGroupMarginLayoutParams[+This <: ViewGroupMarginLayoutParams[_,_], V <: View] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[This, V] {

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

$android.view.Menu; format="whole"$
$android.view.ContextMenu; format="whole"$
$android.view.SurfaceView; format="whole"$
$android.view.ViewStub; format="whole"$
$if(ver.gte_14)$
  $android.view.ActionProvider; format="whole"$
  $android.view.TextureView; format="whole"$
$endif$


trait ViewImplicits {
  $android.view.View; format="implicit-conversion"$
  $android.view.ViewGroup; format="implicit-conversion"$
  $android.view.Menu; format="implicit-conversion"$
  $android.view.ContextMenu; format="implicit-conversion"$
  $android.view.SurfaceView; format="implicit-conversion"$
  $android.view.ViewStub; format="implicit-conversion"$
$if(ver.gte_14)$
  $android.view.ActionProvider; format="implicit-conversion"$
  $android.view.TextureView; format="implicit-conversion"$
$endif$
}
object ViewImplicits extends ViewImplicits