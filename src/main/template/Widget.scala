package org.scaloid.common

import android.app._
import admin.DevicePolicyManager
import android.view._
import android.net._
import android.os._
import android.media._
import collection.mutable.ArrayBuffer
import android.util.Log

import android.text._
import android.view.accessibility._
import android.accounts._
import android.view.inputmethod._
import android.location._
import android.hardware._
import android.telephony._
import android.net.wifi._
import android.content._
import android.widget._
import android.inputmethodservice._
import android.preference._
import android.preference.Preference._
import android.view.WindowManager.LayoutParams._
import android.view.View._
import android.graphics.drawable.Drawable
import java.lang.CharSequence
import scala.Int
import android.view.ContextMenu.ContextMenuInfo
import android.text.method._
import android.gesture._
import android.appwidget._
import annotation.target.{beanGetter, getter}
import android.view.ViewGroup.LayoutParams
import android.widget.TextView.OnEditorActionListener
import android.graphics._
import android.opengl._

trait WidgetFamily {

  class ObjectSView[O <: View with TraitView[O] : Manifest] {
    def apply[LP <: ViewGroupLayoutParams[_, O]]()(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v.<<.parent.+=(v)
      v
    }
  }
  
  class ObjectSTextView[O <: TextView with TraitTextView[O] : Manifest] extends ObjectSView[O] {
    def apply[LP <: ViewGroupLayoutParams[_, O]](txt: CharSequence)(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v text txt
      v.<<.parent.+=(v)
      v
    }
  }
  
  class ObjectSButton[O <: Button with TraitButton[O] : Manifest] extends ObjectSTextView[O] {
    def apply[LP <: ViewGroupLayoutParams[_, O]](txt: CharSequence, onClickListener: (View) => Unit)(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v text txt
      v.setOnClickListener(func2ViewOnClickListener(onClickListener))
      v.<<.parent.+=(v)
      v
    }
	
	def apply[LP <: ViewGroupLayoutParams[_, O]](txt: CharSequence, onClickListener: OnClickListener = {})(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v text txt
      v.setOnClickListener(onClickListener)
      v.<<.parent.+=(v)
      v
    }
  }

  $openClassDef("View", "ConstantsSupport")$

    def find[V <: View](id: Int): V = basis.findViewById(id).asInstanceOf[V]

    $listeners(android.view.View)$

    $properties(android.view.View)$

    @inline def padding_=(p: Int) = {
      basis.setPadding(p, p, p, p)
      basis
    }

    @inline def padding(p: Int) = padding_=(p)

    @noEquivalentGetterExists
    @inline def padding: Int = 0

    def uniqueId(implicit activity: Activity): Int = {
      if(basis.getId < 0) {
        basis.setId(getUniqueId)
      }
      basis.getId
    }

    val FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT
$if(ver.gte_8)$
    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
$else$
    val MATCH_PARENT = ViewGroup.LayoutParams.FILL_PARENT
$endif$
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

    def <<[LP <: ViewGroupLayoutParams[_,_]](implicit defaultLayoutParam: (V) => LP): LP =
      defaultLayoutParam(basis)

    protected def parentViewGroupIfExists[LP <: ViewGroupLayoutParams[_,_]]
        (implicit defaultLayoutParam: (V) => LP = (v:V)=> null): TraitViewGroup[_] = {
      val lp = defaultLayoutParam(basis)
      if(lp==null) null else lp.parent
    }

    def <<[LP <: ViewGroupLayoutParams[_,_]](width:Int, height:Int)(implicit defaultLayoutParam: (V) => LP): LP = {
      val lp = defaultLayoutParam(basis)
      lp.height = height
      lp.width = width
      lp
    }

    def basis: V

    val parentViewGroup: TraitViewGroup[_] = null

  $closeClassDef()$

}
