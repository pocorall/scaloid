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

trait WidgetFamily2 {

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

  $openClassDef("view", "ConstantsSupport")$

    def find[V <: View](id: Int): V = basis.findViewById(id).asInstanceOf[V]


    def basis: V

  $closeClassDef()$

}
