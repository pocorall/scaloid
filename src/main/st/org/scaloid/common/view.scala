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


$wholeClassDef(base=android.view.View, mixin="ConstantsSupport")$

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
