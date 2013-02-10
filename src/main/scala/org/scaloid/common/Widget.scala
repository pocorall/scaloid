
/*
 *
 *
 *
 *
 * Less painful Android development with Scala
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 *
 * Copyright 2013 Sung-Ho Lee
 *
 * Sung-Ho Lee licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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

  class RichView[V <: View](val basis: V) extends TraitView[V]
  @inline implicit def view2RichView[V <: View](view: V) = new RichView[V](view)

  trait TraitView[V <: View] extends ConstantsSupport {

    @inline def onClick(f:  => Unit): V = {
      basis.setOnClickListener(new OnClickListener {
        def onClick(p1: View): Unit = { f }
      })
      basis
    }

    @inline def onClick(f: (View) => Unit): V = {
      basis.setOnClickListener(new OnClickListener {
        def onClick(p1: View): Unit = { f(p1) }
      })
      basis
    }

    @inline def onCreateContextMenu(f:  => Unit): V = {
      basis.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(p1: ContextMenu, p2: View, p3: ContextMenuInfo): Unit = { f }
      })
      basis
    }

    @inline def onCreateContextMenu(f: (ContextMenu, View, ContextMenuInfo) => Unit): V = {
      basis.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(p1: ContextMenu, p2: View, p3: ContextMenuInfo): Unit = { f(p1, p2, p3) }
      })
      basis
    }

    @inline def onFocusChange(f:  => Unit): V = {
      basis.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(p1: View, p2: Boolean): Unit = { f }
      })
      basis
    }

    @inline def onFocusChange(f: (View, Boolean) => Unit): V = {
      basis.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(p1: View, p2: Boolean): Unit = { f(p1, p2) }
      })
      basis
    }

    @inline def onKey(f:  => Boolean): V = {
      basis.setOnKeyListener(new OnKeyListener {
        def onKey(p1: View, p2: Int, p3: KeyEvent): Boolean = { f }
      })
      basis
    }

    @inline def onKey(f: (View, Int, KeyEvent) => Boolean): V = {
      basis.setOnKeyListener(new OnKeyListener {
        def onKey(p1: View, p2: Int, p3: KeyEvent): Boolean = { f(p1, p2, p3) }
      })
      basis
    }

    @inline def onLongClick(f:  => Boolean): V = {
      basis.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(p1: View): Boolean = { f }
      })
      basis
    }

    @inline def onLongClick(f: (View) => Boolean): V = {
      basis.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(p1: View): Boolean = { f(p1) }
      })
      basis
    }

    @inline def onTouch(f:  => Boolean): V = {
      basis.setOnTouchListener(new OnTouchListener {
        def onTouch(p1: View, p2: MotionEvent): Boolean = { f }
      })
      basis
    }

    @inline def onTouch(f: (View, MotionEvent) => Boolean): V = {
      basis.setOnTouchListener(new OnTouchListener {
        def onTouch(p1: View, p2: MotionEvent): Boolean = { f(p1, p2) }
      })
      basis
    }

    @inline def animation = basis.getAnimation
    @inline def animation  (p: android.view.animation.Animation) =            animation_=  (p)
    @inline def animation_=(p: android.view.animation.Animation) = { basis.setAnimation    (p); basis }

    @inline def applicationWindowToken = basis.getApplicationWindowToken

    @inline def background = basis.getBackground

    @noEquivalentGetterExists
    @inline def backgroundColor    : Int  = defaultValue[Int]
    @inline def backgroundColor  (p: Int) =            backgroundColor_=  (p)
    @inline def backgroundColor_=(p: Int) = { basis.setBackgroundColor    (p); basis }

    @noEquivalentGetterExists
    @inline def backgroundDrawable    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def backgroundDrawable  (p: android.graphics.drawable.Drawable) =            backgroundDrawable_=  (p)
    @inline def backgroundDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setBackgroundDrawable    (p); basis }

    @noEquivalentGetterExists
    @inline def backgroundResource    : Int  = defaultValue[Int]
    @inline def backgroundResource  (p: Int) =            backgroundResource_=  (p)
    @inline def backgroundResource_=(p: Int) = { basis.setBackgroundResource    (p); basis }

    @inline def baseline = basis.getBaseline

    @inline def bottom = basis.getBottom

    @inline def clickable = basis.isClickable
    @inline def clickable  (p: Boolean) =            clickable_=  (p)
    @inline def clickable_=(p: Boolean) = { basis.setClickable    (p); basis }

    @inline def contentDescription = basis.getContentDescription
    @inline def contentDescription  (p: java.lang.CharSequence) =            contentDescription_=  (p)
    @inline def contentDescription_=(p: java.lang.CharSequence) = { basis.setContentDescription    (p); basis }

    @inline def context = basis.getContext

    @inline def drawableState = basis.getDrawableState

    @inline def drawingCache = basis.getDrawingCache

    @inline def drawingCacheBackgroundColor = basis.getDrawingCacheBackgroundColor
    @inline def drawingCacheBackgroundColor  (p: Int) =            drawingCacheBackgroundColor_=  (p)
    @inline def drawingCacheBackgroundColor_=(p: Int) = { basis.setDrawingCacheBackgroundColor    (p); basis }

    @inline def drawingCacheEnabled = basis.isDrawingCacheEnabled
    @inline def drawingCacheEnabled  (p: Boolean) =            drawingCacheEnabled_=  (p)
    @inline def drawingCacheEnabled_=(p: Boolean) = { basis.setDrawingCacheEnabled    (p); basis }
    @inline def  enableDrawingCache               = { basis.setDrawingCacheEnabled(true ); basis }
    @inline def disableDrawingCache               = { basis.setDrawingCacheEnabled(false); basis }

    @inline def drawingCacheQuality = basis.getDrawingCacheQuality
    @inline def drawingCacheQuality  (p: Int) =            drawingCacheQuality_=  (p)
    @inline def drawingCacheQuality_=(p: Int) = { basis.setDrawingCacheQuality    (p); basis }

    @inline def drawingTime = basis.getDrawingTime

    @inline def duplicateParentStateEnabled = basis.isDuplicateParentStateEnabled
    @inline def duplicateParentStateEnabled  (p: Boolean) =            duplicateParentStateEnabled_=  (p)
    @inline def duplicateParentStateEnabled_=(p: Boolean) = { basis.setDuplicateParentStateEnabled    (p); basis }
    @inline def  enableDuplicateParentState               = { basis.setDuplicateParentStateEnabled(true ); basis }
    @inline def disableDuplicateParentState               = { basis.setDuplicateParentStateEnabled(false); basis }

    @inline def enabled = basis.isEnabled
    @inline def enabled  (p: Boolean) =            enabled_=  (p)
    @inline def enabled_=(p: Boolean) = { basis.setEnabled    (p); basis }

    @noEquivalentGetterExists
    @inline def fadingEdgeLength    : Int  = defaultValue[Int]
    @inline def fadingEdgeLength  (p: Int) =            fadingEdgeLength_=  (p)
    @inline def fadingEdgeLength_=(p: Int) = { basis.setFadingEdgeLength    (p); basis }

    @inline def focusable = basis.isFocusable
    @inline def focusable  (p: Boolean) =            focusable_=  (p)
    @inline def focusable_=(p: Boolean) = { basis.setFocusable    (p); basis }

    @inline def focusableInTouchMode = basis.isFocusableInTouchMode
    @inline def focusableInTouchMode  (p: Boolean) =            focusableInTouchMode_=  (p)
    @inline def focusableInTouchMode_=(p: Boolean) = { basis.setFocusableInTouchMode    (p); basis }

    @inline def focused = basis.isFocused

    @inline def handler = basis.getHandler

    @inline def hapticFeedbackEnabled = basis.isHapticFeedbackEnabled
    @inline def hapticFeedbackEnabled  (p: Boolean) =            hapticFeedbackEnabled_=  (p)
    @inline def hapticFeedbackEnabled_=(p: Boolean) = { basis.setHapticFeedbackEnabled    (p); basis }
    @inline def  enableHapticFeedback               = { basis.setHapticFeedbackEnabled(true ); basis }
    @inline def disableHapticFeedback               = { basis.setHapticFeedbackEnabled(false); basis }

    @inline def height = basis.getHeight

    @inline def horizontalFadingEdgeEnabled = basis.isHorizontalFadingEdgeEnabled
    @inline def horizontalFadingEdgeEnabled  (p: Boolean) =            horizontalFadingEdgeEnabled_=  (p)
    @inline def horizontalFadingEdgeEnabled_=(p: Boolean) = { basis.setHorizontalFadingEdgeEnabled    (p); basis }
    @inline def  enableHorizontalFadingEdge               = { basis.setHorizontalFadingEdgeEnabled(true ); basis }
    @inline def disableHorizontalFadingEdge               = { basis.setHorizontalFadingEdgeEnabled(false); basis }

    @inline def horizontalFadingEdgeLength = basis.getHorizontalFadingEdgeLength

    @inline def horizontalScrollBarEnabled = basis.isHorizontalScrollBarEnabled
    @inline def horizontalScrollBarEnabled  (p: Boolean) =            horizontalScrollBarEnabled_=  (p)
    @inline def horizontalScrollBarEnabled_=(p: Boolean) = { basis.setHorizontalScrollBarEnabled    (p); basis }
    @inline def  enableHorizontalScrollBar               = { basis.setHorizontalScrollBarEnabled(true ); basis }
    @inline def disableHorizontalScrollBar               = { basis.setHorizontalScrollBarEnabled(false); basis }

    @inline def id = basis.getId
    @inline def id  (p: Int) =            id_=  (p)
    @inline def id_=(p: Int) = { basis.setId    (p); basis }

    @inline def inEditMode = basis.isInEditMode

    @inline def inTouchMode = basis.isInTouchMode

    @inline def keepScreenOn = basis.getKeepScreenOn
    @inline def keepScreenOn  (p: Boolean) =            keepScreenOn_=  (p)
    @inline def keepScreenOn_=(p: Boolean) = { basis.setKeepScreenOn    (p); basis }

    @inline def keyDispatcherState = basis.getKeyDispatcherState

    @inline def layoutParams = basis.getLayoutParams
    @inline def layoutParams  (p: android.view.ViewGroup.LayoutParams) =            layoutParams_=  (p)
    @inline def layoutParams_=(p: android.view.ViewGroup.LayoutParams) = { basis.setLayoutParams    (p); basis }

    @inline def layoutRequested = basis.isLayoutRequested

    @inline def left = basis.getLeft

    @inline def longClickable = basis.isLongClickable
    @inline def longClickable  (p: Boolean) =            longClickable_=  (p)
    @inline def longClickable_=(p: Boolean) = { basis.setLongClickable    (p); basis }

    @inline def measuredHeight = basis.getMeasuredHeight

    @inline def measuredWidth = basis.getMeasuredWidth

    @noEquivalentGetterExists
    @inline def minimumHeight    : Int  = defaultValue[Int]
    @inline def minimumHeight  (p: Int) =            minimumHeight_=  (p)
    @inline def minimumHeight_=(p: Int) = { basis.setMinimumHeight    (p); basis }

    @noEquivalentGetterExists
    @inline def minimumWidth    : Int  = defaultValue[Int]
    @inline def minimumWidth  (p: Int) =            minimumWidth_=  (p)
    @inline def minimumWidth_=(p: Int) = { basis.setMinimumWidth    (p); basis }

    @inline def nextFocusDownId = basis.getNextFocusDownId
    @inline def nextFocusDownId  (p: Int) =            nextFocusDownId_=  (p)
    @inline def nextFocusDownId_=(p: Int) = { basis.setNextFocusDownId    (p); basis }

    @inline def nextFocusLeftId = basis.getNextFocusLeftId
    @inline def nextFocusLeftId  (p: Int) =            nextFocusLeftId_=  (p)
    @inline def nextFocusLeftId_=(p: Int) = { basis.setNextFocusLeftId    (p); basis }

    @inline def nextFocusRightId = basis.getNextFocusRightId
    @inline def nextFocusRightId  (p: Int) =            nextFocusRightId_=  (p)
    @inline def nextFocusRightId_=(p: Int) = { basis.setNextFocusRightId    (p); basis }

    @inline def nextFocusUpId = basis.getNextFocusUpId
    @inline def nextFocusUpId  (p: Int) =            nextFocusUpId_=  (p)
    @inline def nextFocusUpId_=(p: Int) = { basis.setNextFocusUpId    (p); basis }

    @noEquivalentGetterExists
    @inline def onClickListener    : android.view.View.OnClickListener  = defaultValue[android.view.View.OnClickListener]
    @inline def onClickListener  (p: android.view.View.OnClickListener) =            onClickListener_=  (p)
    @inline def onClickListener_=(p: android.view.View.OnClickListener) = { basis.setOnClickListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onCreateContextMenuListener    : android.view.View.OnCreateContextMenuListener  = defaultValue[android.view.View.OnCreateContextMenuListener]
    @inline def onCreateContextMenuListener  (p: android.view.View.OnCreateContextMenuListener) =            onCreateContextMenuListener_=  (p)
    @inline def onCreateContextMenuListener_=(p: android.view.View.OnCreateContextMenuListener) = { basis.setOnCreateContextMenuListener    (p); basis }

    @inline def onFocusChangeListener = basis.getOnFocusChangeListener
    @inline def onFocusChangeListener  (p: android.view.View.OnFocusChangeListener) =            onFocusChangeListener_=  (p)
    @inline def onFocusChangeListener_=(p: android.view.View.OnFocusChangeListener) = { basis.setOnFocusChangeListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onKeyListener    : android.view.View.OnKeyListener  = defaultValue[android.view.View.OnKeyListener]
    @inline def onKeyListener  (p: android.view.View.OnKeyListener) =            onKeyListener_=  (p)
    @inline def onKeyListener_=(p: android.view.View.OnKeyListener) = { basis.setOnKeyListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onLongClickListener    : android.view.View.OnLongClickListener  = defaultValue[android.view.View.OnLongClickListener]
    @inline def onLongClickListener  (p: android.view.View.OnLongClickListener) =            onLongClickListener_=  (p)
    @inline def onLongClickListener_=(p: android.view.View.OnLongClickListener) = { basis.setOnLongClickListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onTouchListener    : android.view.View.OnTouchListener  = defaultValue[android.view.View.OnTouchListener]
    @inline def onTouchListener  (p: android.view.View.OnTouchListener) =            onTouchListener_=  (p)
    @inline def onTouchListener_=(p: android.view.View.OnTouchListener) = { basis.setOnTouchListener    (p); basis }

    @inline def opaque = basis.isOpaque

    @inline def paddingBottom = basis.getPaddingBottom

    @inline def paddingLeft = basis.getPaddingLeft

    @inline def paddingRight = basis.getPaddingRight

    @inline def paddingTop = basis.getPaddingTop

    @inline def parent = basis.getParent

    @inline def pressed = basis.isPressed
    @inline def pressed  (p: Boolean) =            pressed_=  (p)
    @inline def pressed_=(p: Boolean) = { basis.setPressed    (p); basis }

    @inline def resources = basis.getResources

    @inline def right = basis.getRight

    @inline def rootView = basis.getRootView

    @inline def saveEnabled = basis.isSaveEnabled
    @inline def saveEnabled  (p: Boolean) =            saveEnabled_=  (p)
    @inline def saveEnabled_=(p: Boolean) = { basis.setSaveEnabled    (p); basis }
    @inline def  enableSave               = { basis.setSaveEnabled(true ); basis }
    @inline def disableSave               = { basis.setSaveEnabled(false); basis }

    @inline def scrollBarStyle = basis.getScrollBarStyle
    @inline def scrollBarStyle  (p: Int) =            scrollBarStyle_=  (p)
    @inline def scrollBarStyle_=(p: Int) = { basis.setScrollBarStyle    (p); basis }

    @noEquivalentGetterExists
    @inline def scrollContainer    : Boolean  = defaultValue[Boolean]
    @inline def scrollContainer  (p: Boolean) =            scrollContainer_=  (p)
    @inline def scrollContainer_=(p: Boolean) = { basis.setScrollContainer    (p); basis }

    @inline def scrollX = basis.getScrollX

    @inline def scrollY = basis.getScrollY

    @inline def scrollbarFadingEnabled = basis.isScrollbarFadingEnabled
    @inline def scrollbarFadingEnabled  (p: Boolean) =            scrollbarFadingEnabled_=  (p)
    @inline def scrollbarFadingEnabled_=(p: Boolean) = { basis.setScrollbarFadingEnabled    (p); basis }
    @inline def  enableScrollbarFading               = { basis.setScrollbarFadingEnabled(true ); basis }
    @inline def disableScrollbarFading               = { basis.setScrollbarFadingEnabled(false); basis }

    @inline def selected = basis.isSelected
    @inline def selected  (p: Boolean) =            selected_=  (p)
    @inline def selected_=(p: Boolean) = { basis.setSelected    (p); basis }

    @inline def shown = basis.isShown

    @inline def solidColor = basis.getSolidColor

    @inline def soundEffectsEnabled = basis.isSoundEffectsEnabled
    @inline def soundEffectsEnabled  (p: Boolean) =            soundEffectsEnabled_=  (p)
    @inline def soundEffectsEnabled_=(p: Boolean) = { basis.setSoundEffectsEnabled    (p); basis }
    @inline def  enableSoundEffects               = { basis.setSoundEffectsEnabled(true ); basis }
    @inline def disableSoundEffects               = { basis.setSoundEffectsEnabled(false); basis }

    @inline def top = basis.getTop

    @inline def touchDelegate = basis.getTouchDelegate
    @inline def touchDelegate  (p: android.view.TouchDelegate) =            touchDelegate_=  (p)
    @inline def touchDelegate_=(p: android.view.TouchDelegate) = { basis.setTouchDelegate    (p); basis }

    @inline def touchables = basis.getTouchables

    @inline def verticalFadingEdgeEnabled = basis.isVerticalFadingEdgeEnabled
    @inline def verticalFadingEdgeEnabled  (p: Boolean) =            verticalFadingEdgeEnabled_=  (p)
    @inline def verticalFadingEdgeEnabled_=(p: Boolean) = { basis.setVerticalFadingEdgeEnabled    (p); basis }
    @inline def  enableVerticalFadingEdge               = { basis.setVerticalFadingEdgeEnabled(true ); basis }
    @inline def disableVerticalFadingEdge               = { basis.setVerticalFadingEdgeEnabled(false); basis }

    @inline def verticalFadingEdgeLength = basis.getVerticalFadingEdgeLength

    @inline def verticalScrollBarEnabled = basis.isVerticalScrollBarEnabled
    @inline def verticalScrollBarEnabled  (p: Boolean) =            verticalScrollBarEnabled_=  (p)
    @inline def verticalScrollBarEnabled_=(p: Boolean) = { basis.setVerticalScrollBarEnabled    (p); basis }
    @inline def  enableVerticalScrollBar               = { basis.setVerticalScrollBarEnabled(true ); basis }
    @inline def disableVerticalScrollBar               = { basis.setVerticalScrollBarEnabled(false); basis }

    @inline def verticalScrollbarWidth = basis.getVerticalScrollbarWidth

    @inline def viewTreeObserver = basis.getViewTreeObserver

    @inline def visibility = basis.getVisibility
    @inline def visibility  (p: Int) =            visibility_=  (p)
    @inline def visibility_=(p: Int) = { basis.setVisibility    (p); basis }

    @inline def width = basis.getWidth

    @inline def windowToken = basis.getWindowToken

    @inline def windowVisibility = basis.getWindowVisibility

    @inline def padding_=(p: Int) = {
      basis.setPadding(p, p, p, p)
      basis
    }

    @inline def padding(p: Int) = padding_=(p)

    @noEquivalentGetterExists
    @inline def padding: Int = 0

    def uniqueId(implicit activity:Activity):Int = {
      if(basis.getId < 0) {
        basis.setId(getUniqueId)
      }
      return basis.getId
    }

    val FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT
    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

    def <<[LP <: ViewGroupLayoutParams[_,_]](implicit defaultLayoutParam: (V) => LP): LP = {
      defaultLayoutParam(basis)
    }


    def <<[LP <: ViewGroupLayoutParams[_,_]](width:Int, height:Int)(implicit defaultLayoutParam: (V) => LP): LP = {
      val lp = defaultLayoutParam(basis)
      lp.height = height
      lp.width = width
      lp
    }

    def basis: V
  }

  class RichTextView[V <: TextView](val basis: V) extends TraitTextView[V]
  @inline implicit def textView2RichTextView[V <: TextView](textView: V) = new RichTextView[V](textView)

  trait TraitTextView[V <: TextView] extends TraitView[V] {

    @inline def autoLinkMask = basis.getAutoLinkMask
    @inline def autoLinkMask  (p: Int) =            autoLinkMask_=  (p)
    @inline def autoLinkMask_=(p: Int) = { basis.setAutoLinkMask    (p); basis }

    @inline def compoundDrawablePadding = basis.getCompoundDrawablePadding
    @inline def compoundDrawablePadding  (p: Int) =            compoundDrawablePadding_=  (p)
    @inline def compoundDrawablePadding_=(p: Int) = { basis.setCompoundDrawablePadding    (p); basis }

    @inline def compoundDrawables = basis.getCompoundDrawables

    @inline def compoundPaddingBottom = basis.getCompoundPaddingBottom

    @inline def compoundPaddingLeft = basis.getCompoundPaddingLeft

    @inline def compoundPaddingRight = basis.getCompoundPaddingRight

    @inline def compoundPaddingTop = basis.getCompoundPaddingTop

    @inline def currentHintTextColor = basis.getCurrentHintTextColor

    @inline def currentTextColor = basis.getCurrentTextColor

    @noEquivalentGetterExists
    @inline def cursorVisible    : Boolean  = defaultValue[Boolean]
    @inline def cursorVisible  (p: Boolean) =            cursorVisible_=  (p)
    @inline def cursorVisible_=(p: Boolean) = { basis.setCursorVisible    (p); basis }

    @noEquivalentGetterExists
    @inline def editableFactory    : android.text.Editable.Factory  = defaultValue[android.text.Editable.Factory]
    @inline def editableFactory  (p: android.text.Editable.Factory) =            editableFactory_=  (p)
    @inline def editableFactory_=(p: android.text.Editable.Factory) = { basis.setEditableFactory    (p); basis }

    @inline def editableText = basis.getEditableText

    @inline def ellipsize = basis.getEllipsize
    @inline def ellipsize  (p: android.text.TextUtils.TruncateAt) =            ellipsize_=  (p)
    @inline def ellipsize_=(p: android.text.TextUtils.TruncateAt) = { basis.setEllipsize    (p); basis }

    @noEquivalentGetterExists
    @inline def ems    : Int  = defaultValue[Int]
    @inline def ems  (p: Int) =            ems_=  (p)
    @inline def ems_=(p: Int) = { basis.setEms    (p); basis }

    @inline def error = basis.getError
    @inline def error  (p: java.lang.CharSequence) =            error_=  (p)
    @inline def error_=(p: java.lang.CharSequence) = { basis.setError    (p); basis }

    @inline def extendedPaddingBottom = basis.getExtendedPaddingBottom

    @inline def extendedPaddingTop = basis.getExtendedPaddingTop

    @noEquivalentGetterExists
    @inline def extractedText    : android.view.inputmethod.ExtractedText  = defaultValue[android.view.inputmethod.ExtractedText]
    @inline def extractedText  (p: android.view.inputmethod.ExtractedText) =            extractedText_=  (p)
    @inline def extractedText_=(p: android.view.inputmethod.ExtractedText) = { basis.setExtractedText    (p); basis }

    @inline def filters = basis.getFilters
    @inline def filters  (p: Array[android.text.InputFilter]) =            filters_=  (p)
    @inline def filters_=(p: Array[android.text.InputFilter]) = { basis.setFilters    (p); basis }

    @inline def freezesText = basis.getFreezesText
    @inline def freezesText  (p: Boolean) =            freezesText_=  (p)
    @inline def freezesText_=(p: Boolean) = { basis.setFreezesText    (p); basis }

    @inline def gravity = basis.getGravity
    @inline def gravity  (p: Int) =            gravity_=  (p)
    @inline def gravity_=(p: Int) = { basis.setGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def highlightColor    : Int  = defaultValue[Int]
    @inline def highlightColor  (p: Int) =            highlightColor_=  (p)
    @inline def highlightColor_=(p: Int) = { basis.setHighlightColor    (p); basis }

    @inline def hint = basis.getHint
    @inline def hint  (p: java.lang.CharSequence) =            hint_=  (p)
    @inline def hint_=(p: java.lang.CharSequence) = { basis.setHint    (p); basis }

    @noEquivalentGetterExists
    @inline def hintTextColor    : android.content.res.ColorStateList  = defaultValue[android.content.res.ColorStateList]
    @inline def hintTextColor  (p: android.content.res.ColorStateList) =            hintTextColor_=  (p)
    @inline def hintTextColor_=(p: android.content.res.ColorStateList) = { basis.setHintTextColor    (p); basis }

    @inline def hintTextColors = basis.getHintTextColors

    @noEquivalentGetterExists
    @inline def horizontallyScrolling    : Boolean  = defaultValue[Boolean]
    @inline def horizontallyScrolling  (p: Boolean) =            horizontallyScrolling_=  (p)
    @inline def horizontallyScrolling_=(p: Boolean) = { basis.setHorizontallyScrolling    (p); basis }

    @inline def imeActionId = basis.getImeActionId

    @inline def imeActionLabel = basis.getImeActionLabel

    @inline def imeOptions = basis.getImeOptions
    @inline def imeOptions  (p: Int) =            imeOptions_=  (p)
    @inline def imeOptions_=(p: Int) = { basis.setImeOptions    (p); basis }

    @noEquivalentGetterExists
    @inline def includeFontPadding    : Boolean  = defaultValue[Boolean]
    @inline def includeFontPadding  (p: Boolean) =            includeFontPadding_=  (p)
    @inline def includeFontPadding_=(p: Boolean) = { basis.setIncludeFontPadding    (p); basis }

    @noEquivalentGetterExists
    @inline def inputExtras    : Int  = defaultValue[Int]
    @inline def inputExtras  (p: Int) =            inputExtras_=  (p)
    @inline def inputExtras_=(p: Int) = { basis.setInputExtras    (p); basis }

    @inline def inputMethodTarget = basis.isInputMethodTarget

    @inline def inputType = basis.getInputType
    @inline def inputType  (p: Int) =            inputType_=  (p)
    @inline def inputType_=(p: Int) = { basis.setInputType    (p); basis }

    @inline def keyListener = basis.getKeyListener
    @inline def keyListener  (p: android.text.method.KeyListener) =            keyListener_=  (p)
    @inline def keyListener_=(p: android.text.method.KeyListener) = { basis.setKeyListener    (p); basis }

    @inline def lineCount = basis.getLineCount

    @inline def lineHeight = basis.getLineHeight

    @noEquivalentGetterExists
    @inline def lines    : Int  = defaultValue[Int]
    @inline def lines  (p: Int) =            lines_=  (p)
    @inline def lines_=(p: Int) = { basis.setLines    (p); basis }

    @noEquivalentGetterExists
    @inline def linkTextColor    : Int  = defaultValue[Int]
    @inline def linkTextColor  (p: Int) =            linkTextColor_=  (p)
    @inline def linkTextColor_=(p: Int) = { basis.setLinkTextColor    (p); basis }

    @inline def linkTextColors = basis.getLinkTextColors

    @inline def linksClickable = basis.getLinksClickable
    @inline def linksClickable  (p: Boolean) =            linksClickable_=  (p)
    @inline def linksClickable_=(p: Boolean) = { basis.setLinksClickable    (p); basis }

    @noEquivalentGetterExists
    @inline def marqueeRepeatLimit    : Int  = defaultValue[Int]
    @inline def marqueeRepeatLimit  (p: Int) =            marqueeRepeatLimit_=  (p)
    @inline def marqueeRepeatLimit_=(p: Int) = { basis.setMarqueeRepeatLimit    (p); basis }

    @noEquivalentGetterExists
    @inline def maxEms    : Int  = defaultValue[Int]
    @inline def maxEms  (p: Int) =            maxEms_=  (p)
    @inline def maxEms_=(p: Int) = { basis.setMaxEms    (p); basis }

    @noEquivalentGetterExists
    @inline def maxHeight    : Int  = defaultValue[Int]
    @inline def maxHeight  (p: Int) =            maxHeight_=  (p)
    @inline def maxHeight_=(p: Int) = { basis.setMaxHeight    (p); basis }

    @noEquivalentGetterExists
    @inline def maxLines    : Int  = defaultValue[Int]
    @inline def maxLines  (p: Int) =            maxLines_=  (p)
    @inline def maxLines_=(p: Int) = { basis.setMaxLines    (p); basis }

    @noEquivalentGetterExists
    @inline def maxWidth    : Int  = defaultValue[Int]
    @inline def maxWidth  (p: Int) =            maxWidth_=  (p)
    @inline def maxWidth_=(p: Int) = { basis.setMaxWidth    (p); basis }

    @noEquivalentGetterExists
    @inline def minEms    : Int  = defaultValue[Int]
    @inline def minEms  (p: Int) =            minEms_=  (p)
    @inline def minEms_=(p: Int) = { basis.setMinEms    (p); basis }

    @noEquivalentGetterExists
    @inline def minHeight    : Int  = defaultValue[Int]
    @inline def minHeight  (p: Int) =            minHeight_=  (p)
    @inline def minHeight_=(p: Int) = { basis.setMinHeight    (p); basis }

    @noEquivalentGetterExists
    @inline def minLines    : Int  = defaultValue[Int]
    @inline def minLines  (p: Int) =            minLines_=  (p)
    @inline def minLines_=(p: Int) = { basis.setMinLines    (p); basis }

    @noEquivalentGetterExists
    @inline def minWidth    : Int  = defaultValue[Int]
    @inline def minWidth  (p: Int) =            minWidth_=  (p)
    @inline def minWidth_=(p: Int) = { basis.setMinWidth    (p); basis }

    @inline def movementMethod = basis.getMovementMethod
    @inline def movementMethod  (p: android.text.method.MovementMethod) =            movementMethod_=  (p)
    @inline def movementMethod_=(p: android.text.method.MovementMethod) = { basis.setMovementMethod    (p); basis }

    @noEquivalentGetterExists
    @inline def onEditorActionListener    : android.widget.TextView.OnEditorActionListener  = defaultValue[android.widget.TextView.OnEditorActionListener]
    @inline def onEditorActionListener  (p: android.widget.TextView.OnEditorActionListener) =            onEditorActionListener_=  (p)
    @inline def onEditorActionListener_=(p: android.widget.TextView.OnEditorActionListener) = { basis.setOnEditorActionListener    (p); basis }

    @inline def paint = basis.getPaint

    @inline def paintFlags = basis.getPaintFlags
    @inline def paintFlags  (p: Int) =            paintFlags_=  (p)
    @inline def paintFlags_=(p: Int) = { basis.setPaintFlags    (p); basis }

    @inline def privateImeOptions = basis.getPrivateImeOptions
    @inline def privateImeOptions  (p: java.lang.String) =            privateImeOptions_=  (p)
    @inline def privateImeOptions_=(p: java.lang.String) = { basis.setPrivateImeOptions    (p); basis }

    @noEquivalentGetterExists
    @inline def rawInputType    : Int  = defaultValue[Int]
    @inline def rawInputType  (p: Int) =            rawInputType_=  (p)
    @inline def rawInputType_=(p: Int) = { basis.setRawInputType    (p); basis }

    @noEquivalentGetterExists
    @inline def scroller    : android.widget.Scroller  = defaultValue[android.widget.Scroller]
    @inline def scroller  (p: android.widget.Scroller) =            scroller_=  (p)
    @inline def scroller_=(p: android.widget.Scroller) = { basis.setScroller    (p); basis }

    @noEquivalentGetterExists
    @inline def selectAllOnFocus    : Boolean  = defaultValue[Boolean]
    @inline def selectAllOnFocus  (p: Boolean) =            selectAllOnFocus_=  (p)
    @inline def selectAllOnFocus_=(p: Boolean) = { basis.setSelectAllOnFocus    (p); basis }

    @inline def selectionEnd = basis.getSelectionEnd

    @inline def selectionStart = basis.getSelectionStart

    @noEquivalentGetterExists
    @inline def singleLine    : Boolean  = defaultValue[Boolean]
    @inline def singleLine  (p: Boolean) =            singleLine_=  (p)
    @inline def singleLine_=(p: Boolean) = { basis.setSingleLine    (p); basis }

    @noEquivalentGetterExists
    @inline def spannableFactory    : android.text.Spannable.Factory  = defaultValue[android.text.Spannable.Factory]
    @inline def spannableFactory  (p: android.text.Spannable.Factory) =            spannableFactory_=  (p)
    @inline def spannableFactory_=(p: android.text.Spannable.Factory) = { basis.setSpannableFactory    (p); basis }

    @inline def text = basis.getText
    @inline def text  (p: java.lang.CharSequence) =            text_=  (p)
    @inline def text_=(p: java.lang.CharSequence) = { basis.setText    (p); basis }

    @noEquivalentGetterExists
    @inline def textColor    : Int  = defaultValue[Int]
    @inline def textColor  (p: Int) =            textColor_=  (p)
    @inline def textColor_=(p: Int) = { basis.setTextColor    (p); basis }

    @inline def textColors = basis.getTextColors

    @noEquivalentGetterExists
    @inline def textKeepState    : java.lang.CharSequence  = defaultValue[java.lang.CharSequence]
    @inline def textKeepState  (p: java.lang.CharSequence) =            textKeepState_=  (p)
    @inline def textKeepState_=(p: java.lang.CharSequence) = { basis.setTextKeepState    (p); basis }

    @inline def textScaleX = basis.getTextScaleX
    @inline def textScaleX  (p: Float) =            textScaleX_=  (p)
    @inline def textScaleX_=(p: Float) = { basis.setTextScaleX    (p); basis }

    @inline def textSize = basis.getTextSize
    @inline def textSize  (p: Float) =            textSize_=  (p)
    @inline def textSize_=(p: Float) = { basis.setTextSize    (p); basis }

    @inline def totalPaddingBottom = basis.getTotalPaddingBottom

    @inline def totalPaddingLeft = basis.getTotalPaddingLeft

    @inline def totalPaddingRight = basis.getTotalPaddingRight

    @inline def totalPaddingTop = basis.getTotalPaddingTop

    @inline def transformationMethod = basis.getTransformationMethod
    @inline def transformationMethod  (p: android.text.method.TransformationMethod) =            transformationMethod_=  (p)
    @inline def transformationMethod_=(p: android.text.method.TransformationMethod) = { basis.setTransformationMethod    (p); basis }

    @inline def typeface = basis.getTypeface
    @inline def typeface  (p: android.graphics.Typeface) =            typeface_=  (p)
    @inline def typeface_=(p: android.graphics.Typeface) = { basis.setTypeface    (p); basis }

    @inline def urls = basis.getUrls

    @inline def beforeTextChanged(f:  => Unit): V = {
      basis.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = { f }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def afterTextChanged(p1: Editable): Unit = {  }
      })
      basis
    }

    @inline def onTextChanged(f:  => Unit): V = {
      basis.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = { f }
        def afterTextChanged(p1: Editable): Unit = {  }
      })
      basis
    }

    @inline def afterTextChanged(f:  => Unit): V = {
      basis.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def afterTextChanged(p1: Editable): Unit = { f }
      })
      basis
    }

    @inline def beforeTextChanged(f: (CharSequence, Int, Int, Int) => Unit): V = {
      basis.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = { f(p1, p2, p3, p4) }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def afterTextChanged(p1: Editable): Unit = {  }
      })
      basis
    }

    @inline def onTextChanged(f: (CharSequence, Int, Int, Int) => Unit): V = {
      basis.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = { f(p1, p2, p3, p4) }
        def afterTextChanged(p1: Editable): Unit = {  }
      })
      basis
    }

    @inline def afterTextChanged(f: (Editable) => Unit): V = {
      basis.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {  }
        def afterTextChanged(p1: Editable): Unit = { f(p1) }
      })
      basis
    }

    @inline def onEditorAction(f:  => Boolean): V = {
      basis.setOnEditorActionListener(new OnEditorActionListener {
        def onEditorAction(p1: TextView, p2: Int, p3: KeyEvent): Boolean = { f }
      })
      basis
    }

    @inline def onEditorAction(f: (TextView, Int, KeyEvent) => Boolean): V = {
      basis.setOnEditorActionListener(new OnEditorActionListener {
        def onEditorAction(p1: TextView, p2: Int, p3: KeyEvent): Boolean = { f(p1, p2, p3) }
      })
      basis
    }

  }

  class STextView(implicit context: Context) extends TextView(context) with TraitTextView[STextView] {
    def basis = this
  }

  object STextView {
    def apply[LP <: ViewGroupLayoutParams[_, STextView]]()(implicit context: Context, defaultLayoutParam: (STextView) => LP): STextView =  {
      val v = (new STextView)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, STextView]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (STextView) => LP): STextView =  {
      val v = (new STextView)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }


  trait TraitAbsListView[V <: AbsListView] extends TraitView[V] {
    @inline def cacheColorHint = basis.getCacheColorHint
    @inline def cacheColorHint  (p: Int) =            cacheColorHint_=  (p)
    @inline def cacheColorHint_=(p: Int) = { basis.setCacheColorHint    (p); basis }

    @noEquivalentGetterExists
    @inline def drawSelectorOnTop    : Boolean  = defaultValue[Boolean]
    @inline def drawSelectorOnTop  (p: Boolean) =            drawSelectorOnTop_=  (p)
    @inline def drawSelectorOnTop_=(p: Boolean) = { basis.setDrawSelectorOnTop    (p); basis }

    @inline def fastScrollEnabled = basis.isFastScrollEnabled
    @inline def fastScrollEnabled  (p: Boolean) =            fastScrollEnabled_=  (p)
    @inline def fastScrollEnabled_=(p: Boolean) = { basis.setFastScrollEnabled    (p); basis }
    @inline def  enableFastScroll               = { basis.setFastScrollEnabled(true ); basis }
    @inline def disableFastScroll               = { basis.setFastScrollEnabled(false); basis }

    @noEquivalentGetterExists
    @inline def filterText    : java.lang.String  = defaultValue[java.lang.String]
    @inline def filterText  (p: java.lang.String) =            filterText_=  (p)
    @inline def filterText_=(p: java.lang.String) = { basis.setFilterText    (p); basis }

    @inline def listPaddingBottom = basis.getListPaddingBottom

    @inline def listPaddingLeft = basis.getListPaddingLeft

    @inline def listPaddingRight = basis.getListPaddingRight

    @inline def listPaddingTop = basis.getListPaddingTop

    @noEquivalentGetterExists
    @inline def onScrollListener    : android.widget.AbsListView.OnScrollListener  = defaultValue[android.widget.AbsListView.OnScrollListener]
    @inline def onScrollListener  (p: android.widget.AbsListView.OnScrollListener) =            onScrollListener_=  (p)
    @inline def onScrollListener_=(p: android.widget.AbsListView.OnScrollListener) = { basis.setOnScrollListener    (p); basis }

    @noEquivalentGetterExists
    @inline def recyclerListener    : android.widget.AbsListView.RecyclerListener  = defaultValue[android.widget.AbsListView.RecyclerListener]
    @inline def recyclerListener  (p: android.widget.AbsListView.RecyclerListener) =            recyclerListener_=  (p)
    @inline def recyclerListener_=(p: android.widget.AbsListView.RecyclerListener) = { basis.setRecyclerListener    (p); basis }

    @inline def scrollingCacheEnabled = basis.isScrollingCacheEnabled
    @inline def scrollingCacheEnabled  (p: Boolean) =            scrollingCacheEnabled_=  (p)
    @inline def scrollingCacheEnabled_=(p: Boolean) = { basis.setScrollingCacheEnabled    (p); basis }
    @inline def  enableScrollingCache               = { basis.setScrollingCacheEnabled(true ); basis }
    @inline def disableScrollingCache               = { basis.setScrollingCacheEnabled(false); basis }

    @inline def selector = basis.getSelector
    @inline def selector  (p: android.graphics.drawable.Drawable) =            selector_=  (p)
    @inline def selector_=(p: android.graphics.drawable.Drawable) = { basis.setSelector    (p); basis }

    @inline def smoothScrollbarEnabled = basis.isSmoothScrollbarEnabled
    @inline def smoothScrollbarEnabled  (p: Boolean) =            smoothScrollbarEnabled_=  (p)
    @inline def smoothScrollbarEnabled_=(p: Boolean) = { basis.setSmoothScrollbarEnabled    (p); basis }
    @inline def  enableSmoothScrollbar               = { basis.setSmoothScrollbarEnabled(true ); basis }
    @inline def disableSmoothScrollbar               = { basis.setSmoothScrollbarEnabled(false); basis }

    @inline def stackFromBottom = basis.isStackFromBottom
    @inline def stackFromBottom  (p: Boolean) =            stackFromBottom_=  (p)
    @inline def stackFromBottom_=(p: Boolean) = { basis.setStackFromBottom    (p); basis }

    @inline def textFilter = basis.getTextFilter

    @inline def textFilterEnabled = basis.isTextFilterEnabled
    @inline def textFilterEnabled  (p: Boolean) =            textFilterEnabled_=  (p)
    @inline def textFilterEnabled_=(p: Boolean) = { basis.setTextFilterEnabled    (p); basis }
    @inline def  enableTextFilter               = { basis.setTextFilterEnabled(true ); basis }
    @inline def disableTextFilter               = { basis.setTextFilterEnabled(false); basis }

    @inline def transcriptMode = basis.getTranscriptMode
    @inline def transcriptMode  (p: Int) =            transcriptMode_=  (p)
    @inline def transcriptMode_=(p: Int) = { basis.setTranscriptMode    (p); basis }

  }

  class RichViewGroup[V <: ViewGroup](val basis: V) extends TraitViewGroup[V]
  @inline implicit def viewGroup2RichViewGroup[V <: ViewGroup](viewGroup: V) = new RichViewGroup[V](viewGroup)

  trait TraitViewGroup[V <: ViewGroup] extends TraitView[V] {

    @inline def alwaysDrawnWithCacheEnabled = basis.isAlwaysDrawnWithCacheEnabled
    @inline def alwaysDrawnWithCacheEnabled  (p: Boolean) =            alwaysDrawnWithCacheEnabled_=  (p)
    @inline def alwaysDrawnWithCacheEnabled_=(p: Boolean) = { basis.setAlwaysDrawnWithCacheEnabled    (p); basis }
    @inline def  enableAlwaysDrawnWithCache               = { basis.setAlwaysDrawnWithCacheEnabled(true ); basis }
    @inline def disableAlwaysDrawnWithCache               = { basis.setAlwaysDrawnWithCacheEnabled(false); basis }

    @inline def animationCacheEnabled = basis.isAnimationCacheEnabled
    @inline def animationCacheEnabled  (p: Boolean) =            animationCacheEnabled_=  (p)
    @inline def animationCacheEnabled_=(p: Boolean) = { basis.setAnimationCacheEnabled    (p); basis }
    @inline def  enableAnimationCache               = { basis.setAnimationCacheEnabled(true ); basis }
    @inline def disableAnimationCache               = { basis.setAnimationCacheEnabled(false); basis }

    @inline def childCount = basis.getChildCount

    @noEquivalentGetterExists
    @inline def clipChildren    : Boolean  = defaultValue[Boolean]
    @inline def clipChildren  (p: Boolean) =            clipChildren_=  (p)
    @inline def clipChildren_=(p: Boolean) = { basis.setClipChildren    (p); basis }

    @noEquivalentGetterExists
    @inline def clipToPadding    : Boolean  = defaultValue[Boolean]
    @inline def clipToPadding  (p: Boolean) =            clipToPadding_=  (p)
    @inline def clipToPadding_=(p: Boolean) = { basis.setClipToPadding    (p); basis }

    @inline def descendantFocusability = basis.getDescendantFocusability
    @inline def descendantFocusability  (p: Int) =            descendantFocusability_=  (p)
    @inline def descendantFocusability_=(p: Int) = { basis.setDescendantFocusability    (p); basis }

    @inline def focusedChild = basis.getFocusedChild

    @inline def layoutAnimation = basis.getLayoutAnimation
    @inline def layoutAnimation  (p: android.view.animation.LayoutAnimationController) =            layoutAnimation_=  (p)
    @inline def layoutAnimation_=(p: android.view.animation.LayoutAnimationController) = { basis.setLayoutAnimation    (p); basis }

    @inline def layoutAnimationListener = basis.getLayoutAnimationListener
    @inline def layoutAnimationListener  (p: android.view.animation.Animation.AnimationListener) =            layoutAnimationListener_=  (p)
    @inline def layoutAnimationListener_=(p: android.view.animation.Animation.AnimationListener) = { basis.setLayoutAnimationListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onHierarchyChangeListener    : android.view.ViewGroup.OnHierarchyChangeListener  = defaultValue[android.view.ViewGroup.OnHierarchyChangeListener]
    @inline def onHierarchyChangeListener  (p: android.view.ViewGroup.OnHierarchyChangeListener) =            onHierarchyChangeListener_=  (p)
    @inline def onHierarchyChangeListener_=(p: android.view.ViewGroup.OnHierarchyChangeListener) = { basis.setOnHierarchyChangeListener    (p); basis }

    @inline def persistentDrawingCache = basis.getPersistentDrawingCache
    @inline def persistentDrawingCache  (p: Int) =            persistentDrawingCache_=  (p)
    @inline def persistentDrawingCache_=(p: Int) = { basis.setPersistentDrawingCache    (p); basis }

    def +=(v: View) = {
      var viw = v
      styles.foreach {
        case st:PartialFunction[View, View] => if (st.isDefinedAt(viw)) viw = st(viw)
        case st => viw = st(viw)
      }
      basis.addView(viw)
      basis
    }

    val styles = new ArrayBuffer[View => View]

    def style(stl: View => View) = {
      styles += stl
      basis
    }
  }

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

  class RichFrameLayout[V <: FrameLayout](val basis: V) extends TraitFrameLayout[V]
  @inline implicit def frameLayout2RichFrameLayout[V <: FrameLayout](frameLayout: V) = new RichFrameLayout[V](frameLayout)

  trait TraitFrameLayout[V <: FrameLayout] extends TraitViewGroup[V] {

    @inline def considerGoneChildrenWhenMeasuring = basis.getConsiderGoneChildrenWhenMeasuring

    @inline def foreground = basis.getForeground
    @inline def foreground  (p: android.graphics.drawable.Drawable) =            foreground_=  (p)
    @inline def foreground_=(p: android.graphics.drawable.Drawable) = { basis.setForeground    (p); basis }

    @noEquivalentGetterExists
    @inline def foregroundGravity    : Int  = defaultValue[Int]
    @inline def foregroundGravity  (p: Int) =            foregroundGravity_=  (p)
    @inline def foregroundGravity_=(p: Int) = { basis.setForegroundGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def measureAllChildren    : Boolean  = defaultValue[Boolean]
    @inline def measureAllChildren  (p: Boolean) =            measureAllChildren_=  (p)
    @inline def measureAllChildren_=(p: Boolean) = { basis.setMeasureAllChildren    (p); basis }

  }

  class SFrameLayout(implicit context: Context) extends FrameLayout(context) with TraitFrameLayout[SFrameLayout] {
    def basis = this

  implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)
  <<

  class LayoutParams[V <: View](v: V) extends FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
    def basis = this

    v.setLayoutParams(this)

    def Gravity(g: Int) = {
      gravity = g
      this
    }

    def parent = SFrameLayout.this

    def >> : V = v
  }
}
  class RichRelativeLayout[V <: RelativeLayout](val basis: V) extends TraitRelativeLayout[V]
  @inline implicit def relativeLayout2RichRelativeLayout[V <: RelativeLayout](relativeLayout: V) = new RichRelativeLayout[V](relativeLayout)

  trait TraitRelativeLayout[V <: RelativeLayout] extends TraitViewGroup[V] {

    @noEquivalentGetterExists
    @inline def gravity    : Int  = defaultValue[Int]
    @inline def gravity  (p: Int) =            gravity_=  (p)
    @inline def gravity_=(p: Int) = { basis.setGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def horizontalGravity    : Int  = defaultValue[Int]
    @inline def horizontalGravity  (p: Int) =            horizontalGravity_=  (p)
    @inline def horizontalGravity_=(p: Int) = { basis.setHorizontalGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def ignoreGravity    : Int  = defaultValue[Int]
    @inline def ignoreGravity  (p: Int) =            ignoreGravity_=  (p)
    @inline def ignoreGravity_=(p: Int) = { basis.setIgnoreGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def verticalGravity    : Int  = defaultValue[Int]
    @inline def verticalGravity  (p: Int) =            verticalGravity_=  (p)
    @inline def verticalGravity_=(p: Int) = { basis.setVerticalGravity    (p); basis }

  }
  class SRelativeLayout(implicit context: Context) extends RelativeLayout(context) with TraitRelativeLayout[SRelativeLayout] {
    def basis = this

  implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)
  <<

  class LayoutParams[V <: View](v: V) extends RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
    def basis = this

    v.setLayoutParams(this)

    def Gravity(g: Int) = {
      gravity = g
      this
    }

    def above(otherView: View)(implicit activity: Activity) = {
      addRule(RelativeLayout.ABOVE, otherView.uniqueId)
      this
    }

    def alignBaseline = {
      addRule(RelativeLayout.ALIGN_BASELINE)
      this
    }

    def alignBottom = {
      addRule(RelativeLayout.ALIGN_BOTTOM)
      this
    }

//def alignEnd = {
//  addRule(RelativeLayout.ALIGN_END)
//  this
//}

    def alignLeft = {
      addRule(RelativeLayout.ALIGN_LEFT)
      this
    }

    def alignParentBottom = {
      addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
      this
    }

    //def alignParentEnd = {
    //  addRule(RelativeLayout.ALIGN_PARENT_END)
    //  this
    //}

    def alignParentLeft = {
      addRule(RelativeLayout.ALIGN_PARENT_LEFT)
      this
    }

    def alignParentRight = {
      addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
      this
    }

//def alignParentStart = {
//  addRule(RelativeLayout.ALIGN_PARENT_START)
//  this
//}

    def alignParentTop = {
      addRule(RelativeLayout.ALIGN_PARENT_TOP)
      this
    }

    def alignRight = {
      addRule(RelativeLayout.ALIGN_RIGHT)
      this
    }

//def alignStart = {
//  addRule(RelativeLayout.ALIGN_START)
//  this
//}

    def alignTop = {
      addRule(RelativeLayout.ALIGN_TOP)
      this
    }

    def below(otherView: View)(implicit activity: Activity) = {
      addRule(RelativeLayout.BELOW, otherView.uniqueId)
      this
    }

    def leftOf(otherView: View)(implicit activity: Activity) = {
      addRule(RelativeLayout.LEFT_OF, otherView.uniqueId)
      this
    }

    def rightOf(otherView: View)(implicit activity: Activity) = {
      addRule(RelativeLayout.RIGHT_OF, otherView.uniqueId)
      this
    }

    def centerHorizontal = {
      addRule(RelativeLayout.CENTER_HORIZONTAL)
      this
    }

    def centerInParent = {
      addRule(RelativeLayout.CENTER_IN_PARENT)
      this
    }

    def centerVertical = {
      addRule(RelativeLayout.CENTER_VERTICAL)
      this
    }

    def parent = SRelativeLayout.this

    def >> : V = v
  }
}
  class RichLinearLayout[V <: LinearLayout](val basis: V) extends TraitLinearLayout[V]
  @inline implicit def linearLayout2RichLinearLayout[V <: LinearLayout](linearLayout: V) = new RichLinearLayout[V](linearLayout)

  trait TraitLinearLayout[V <: LinearLayout] extends TraitViewGroup[V] {

    @inline def baselineAligned = basis.isBaselineAligned
    @inline def baselineAligned  (p: Boolean) =            baselineAligned_=  (p)
    @inline def baselineAligned_=(p: Boolean) = { basis.setBaselineAligned    (p); basis }

    @inline def baselineAlignedChildIndex = basis.getBaselineAlignedChildIndex
    @inline def baselineAlignedChildIndex  (p: Int) =            baselineAlignedChildIndex_=  (p)
    @inline def baselineAlignedChildIndex_=(p: Int) = { basis.setBaselineAlignedChildIndex    (p); basis }

    @noEquivalentGetterExists
    @inline def gravity    : Int  = defaultValue[Int]
    @inline def gravity  (p: Int) =            gravity_=  (p)
    @inline def gravity_=(p: Int) = { basis.setGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def horizontalGravity    : Int  = defaultValue[Int]
    @inline def horizontalGravity  (p: Int) =            horizontalGravity_=  (p)
    @inline def horizontalGravity_=(p: Int) = { basis.setHorizontalGravity    (p); basis }

    @inline def orientation = basis.getOrientation
    @inline def orientation  (p: Int) =            orientation_=  (p)
    @inline def orientation_=(p: Int) = { basis.setOrientation    (p); basis }

    @noEquivalentGetterExists
    @inline def verticalGravity    : Int  = defaultValue[Int]
    @inline def verticalGravity  (p: Int) =            verticalGravity_=  (p)
    @inline def verticalGravity_=(p: Int) = { basis.setVerticalGravity    (p); basis }

    @inline def weightSum = basis.getWeightSum
    @inline def weightSum  (p: Float) =            weightSum_=  (p)
    @inline def weightSum_=(p: Float) = { basis.setWeightSum    (p); basis }

  }

  class SLinearLayout(implicit context: Context) extends LinearLayout(context) with TraitLinearLayout[SLinearLayout] {
    def basis = this

    val VERTICAL = LinearLayout.VERTICAL
    val HORIZONTAL = LinearLayout.HORIZONTAL

    implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)
    <<

    class LayoutParams[V <: View](v: V) extends LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
      def basis = this

      v.setLayoutParams(this)

      def Weight(w: Float) = {
        weight = w
        this
      }
      def parent = SLinearLayout.this

      def >> : V = v
    }

  }

  class SVerticalLayout(implicit context: Context) extends SLinearLayout {
    orientation = VERTICAL
  }

  class RichEditText[V <: EditText](val basis: V) extends TraitEditText[V]
  @inline implicit def editText2RichEditText[V <: EditText](editText: V) = new RichEditText[V](editText)

  trait TraitEditText[V <: EditText] extends TraitTextView[V] {

  }

  class SEditText(implicit context: Context) extends EditText(context) with TraitEditText[SEditText] {
    def basis = this
  }

  object SEditText {
    def apply[LP <: ViewGroupLayoutParams[_, SEditText]]()(implicit context: Context, defaultLayoutParam: (SEditText) => LP): SEditText =  {
      val v = (new SEditText)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SEditText]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SEditText) => LP): SEditText =  {
      val v = (new SEditText)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  class RichExtractEditText[V <: ExtractEditText](val basis: V) extends TraitExtractEditText[V]
  @inline implicit def extractEditText2RichExtractEditText[V <: ExtractEditText](extractEditText: V) = new RichExtractEditText[V](extractEditText)

  trait TraitExtractEditText[V <: ExtractEditText] extends TraitEditText[V] {

  }

  class SExtractEditText(implicit context: Context) extends ExtractEditText(context) with TraitExtractEditText[SExtractEditText] {
    def basis = this
  }

  object SExtractEditText {
    def apply[LP <: ViewGroupLayoutParams[_, SExtractEditText]]()(implicit context: Context, defaultLayoutParam: (SExtractEditText) => LP): SExtractEditText =  {
      val v = (new SExtractEditText)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SExtractEditText]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SExtractEditText) => LP): SExtractEditText =  {
      val v = (new SExtractEditText)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichAutoCompleteTextView[V <: AutoCompleteTextView](val basis: V) extends TraitAutoCompleteTextView[V]
  @inline implicit def autoCompleteTextView2RichAutoCompleteTextView[V <: AutoCompleteTextView](autoCompleteTextView: V) = new RichAutoCompleteTextView[V](autoCompleteTextView)

  trait TraitAutoCompleteTextView[V <: AutoCompleteTextView] extends TraitEditText[V] {

    @noEquivalentGetterExists
    @inline def completionHint    : java.lang.CharSequence  = defaultValue[java.lang.CharSequence]
    @inline def completionHint  (p: java.lang.CharSequence) =            completionHint_=  (p)
    @inline def completionHint_=(p: java.lang.CharSequence) = { basis.setCompletionHint    (p); basis }

    @inline def dropDownAnchor = basis.getDropDownAnchor
    @inline def dropDownAnchor  (p: Int) =            dropDownAnchor_=  (p)
    @inline def dropDownAnchor_=(p: Int) = { basis.setDropDownAnchor    (p); basis }

    @inline def dropDownBackground = basis.getDropDownBackground

    @noEquivalentGetterExists
    @inline def dropDownBackgroundDrawable    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def dropDownBackgroundDrawable  (p: android.graphics.drawable.Drawable) =            dropDownBackgroundDrawable_=  (p)
    @inline def dropDownBackgroundDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setDropDownBackgroundDrawable    (p); basis }

    @noEquivalentGetterExists
    @inline def dropDownBackgroundResource    : Int  = defaultValue[Int]
    @inline def dropDownBackgroundResource  (p: Int) =            dropDownBackgroundResource_=  (p)
    @inline def dropDownBackgroundResource_=(p: Int) = { basis.setDropDownBackgroundResource    (p); basis }

    @inline def dropDownHeight = basis.getDropDownHeight
    @inline def dropDownHeight  (p: Int) =            dropDownHeight_=  (p)
    @inline def dropDownHeight_=(p: Int) = { basis.setDropDownHeight    (p); basis }

    @inline def dropDownHorizontalOffset = basis.getDropDownHorizontalOffset
    @inline def dropDownHorizontalOffset  (p: Int) =            dropDownHorizontalOffset_=  (p)
    @inline def dropDownHorizontalOffset_=(p: Int) = { basis.setDropDownHorizontalOffset    (p); basis }

    @inline def dropDownVerticalOffset = basis.getDropDownVerticalOffset
    @inline def dropDownVerticalOffset  (p: Int) =            dropDownVerticalOffset_=  (p)
    @inline def dropDownVerticalOffset_=(p: Int) = { basis.setDropDownVerticalOffset    (p); basis }

    @inline def dropDownWidth = basis.getDropDownWidth
    @inline def dropDownWidth  (p: Int) =            dropDownWidth_=  (p)
    @inline def dropDownWidth_=(p: Int) = { basis.setDropDownWidth    (p); basis }

    @inline def itemClickListener = basis.getItemClickListener

    @inline def itemSelectedListener = basis.getItemSelectedListener

    @inline def listSelection = basis.getListSelection
    @inline def listSelection  (p: Int) =            listSelection_=  (p)
    @inline def listSelection_=(p: Int) = { basis.setListSelection    (p); basis }

    @inline def onItemClickListener = basis.getOnItemClickListener
    @inline def onItemClickListener  (p: android.widget.AdapterView.OnItemClickListener) =            onItemClickListener_=  (p)
    @inline def onItemClickListener_=(p: android.widget.AdapterView.OnItemClickListener) = { basis.setOnItemClickListener    (p); basis }

    @inline def onItemSelectedListener = basis.getOnItemSelectedListener
    @inline def onItemSelectedListener  (p: android.widget.AdapterView.OnItemSelectedListener) =            onItemSelectedListener_=  (p)
    @inline def onItemSelectedListener_=(p: android.widget.AdapterView.OnItemSelectedListener) = { basis.setOnItemSelectedListener    (p); basis }

    @inline def performingCompletion = basis.isPerformingCompletion

    @inline def popupShowing = basis.isPopupShowing

    @inline def threshold = basis.getThreshold
    @inline def threshold  (p: Int) =            threshold_=  (p)
    @inline def threshold_=(p: Int) = { basis.setThreshold    (p); basis }

    @inline def validator = basis.getValidator
    @inline def validator  (p: android.widget.AutoCompleteTextView.Validator) =            validator_=  (p)
    @inline def validator_=(p: android.widget.AutoCompleteTextView.Validator) = { basis.setValidator    (p); basis }

  }

  class SAutoCompleteTextView(implicit context: Context) extends AutoCompleteTextView(context) with TraitAutoCompleteTextView[SAutoCompleteTextView] {
    def basis = this
  }

  object SAutoCompleteTextView {
    def apply[LP <: ViewGroupLayoutParams[_, SAutoCompleteTextView]]()(implicit context: Context, defaultLayoutParam: (SAutoCompleteTextView) => LP): SAutoCompleteTextView =  {
      val v = (new SAutoCompleteTextView)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SAutoCompleteTextView]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SAutoCompleteTextView) => LP): SAutoCompleteTextView =  {
      val v = (new SAutoCompleteTextView)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichMultiAutoCompleteTextView[V <: MultiAutoCompleteTextView](val basis: V) extends TraitMultiAutoCompleteTextView[V]
  @inline implicit def multiAutoCompleteTextView2RichMultiAutoCompleteTextView[V <: MultiAutoCompleteTextView](multiAutoCompleteTextView: V) = new RichMultiAutoCompleteTextView[V](multiAutoCompleteTextView)

  trait TraitMultiAutoCompleteTextView[V <: MultiAutoCompleteTextView] extends TraitAutoCompleteTextView[V] {

    @noEquivalentGetterExists
    @inline def tokenizer    : android.widget.MultiAutoCompleteTextView.Tokenizer  = defaultValue[android.widget.MultiAutoCompleteTextView.Tokenizer]
    @inline def tokenizer  (p: android.widget.MultiAutoCompleteTextView.Tokenizer) =            tokenizer_=  (p)
    @inline def tokenizer_=(p: android.widget.MultiAutoCompleteTextView.Tokenizer) = { basis.setTokenizer    (p); basis }

  }

  class SMultiAutoCompleteTextView(implicit context: Context) extends MultiAutoCompleteTextView(context) with TraitMultiAutoCompleteTextView[SMultiAutoCompleteTextView] {
    def basis = this
  }

  object SMultiAutoCompleteTextView {
    def apply[LP <: ViewGroupLayoutParams[_, SMultiAutoCompleteTextView]]()(implicit context: Context, defaultLayoutParam: (SMultiAutoCompleteTextView) => LP): SMultiAutoCompleteTextView =  {
      val v = (new SMultiAutoCompleteTextView)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SMultiAutoCompleteTextView]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SMultiAutoCompleteTextView) => LP): SMultiAutoCompleteTextView =  {
      val v = (new SMultiAutoCompleteTextView)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichListView[V <: ListView](val basis: V) extends TraitListView[V]
  @inline implicit def listView2RichListView[V <: ListView](listView: V) = new RichListView[V](listView)

  trait TraitListView[V <: ListView] extends TraitAbsListView[V] {

    @inline def checkItemIds = basis.getCheckItemIds

    @inline def checkedItemIds = basis.getCheckedItemIds

    @inline def checkedItemPosition = basis.getCheckedItemPosition

    @inline def checkedItemPositions = basis.getCheckedItemPositions

    @inline def choiceMode = basis.getChoiceMode
    @inline def choiceMode  (p: Int) =            choiceMode_=  (p)
    @inline def choiceMode_=(p: Int) = { basis.setChoiceMode    (p); basis }

    @inline def divider = basis.getDivider
    @inline def divider  (p: android.graphics.drawable.Drawable) =            divider_=  (p)
    @inline def divider_=(p: android.graphics.drawable.Drawable) = { basis.setDivider    (p); basis }

    @inline def dividerHeight = basis.getDividerHeight
    @inline def dividerHeight  (p: Int) =            dividerHeight_=  (p)
    @inline def dividerHeight_=(p: Int) = { basis.setDividerHeight    (p); basis }

    @noEquivalentGetterExists
    @inline def footerDividersEnabled    : Boolean  = defaultValue[Boolean]
    @inline def footerDividersEnabled  (p: Boolean) =            footerDividersEnabled_=  (p)
    @inline def footerDividersEnabled_=(p: Boolean) = { basis.setFooterDividersEnabled    (p); basis }
    @inline def  enableFooterDividers               = { basis.setFooterDividersEnabled(true ); basis }
    @inline def disableFooterDividers               = { basis.setFooterDividersEnabled(false); basis }

    @inline def footerViewsCount = basis.getFooterViewsCount

    @noEquivalentGetterExists
    @inline def headerDividersEnabled    : Boolean  = defaultValue[Boolean]
    @inline def headerDividersEnabled  (p: Boolean) =            headerDividersEnabled_=  (p)
    @inline def headerDividersEnabled_=(p: Boolean) = { basis.setHeaderDividersEnabled    (p); basis }
    @inline def  enableHeaderDividers               = { basis.setHeaderDividersEnabled(true ); basis }
    @inline def disableHeaderDividers               = { basis.setHeaderDividersEnabled(false); basis }

    @inline def headerViewsCount = basis.getHeaderViewsCount

    @inline def itemsCanFocus = basis.getItemsCanFocus
    @inline def itemsCanFocus  (p: Boolean) =            itemsCanFocus_=  (p)
    @inline def itemsCanFocus_=(p: Boolean) = { basis.setItemsCanFocus    (p); basis }

    @inline def maxScrollAmount = basis.getMaxScrollAmount

  }

  class SListView(implicit context: Context) extends ListView(context) with TraitListView[SListView] {
    def basis = this
  }

  object SListView {
    def apply[LP <: ViewGroupLayoutParams[_, SListView]]()(implicit context: Context, defaultLayoutParam: (SListView) => LP): SListView =  {
      val v = (new SListView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichButton[V <: Button](val basis: V) extends TraitButton[V]
  @inline implicit def button2RichButton[V <: Button](button: V) = new RichButton[V](button)

  trait TraitButton[V <: Button] extends TraitTextView[V] {

  }

  class SButton(implicit context: Context) extends Button(context) with TraitButton[SButton] {
    def basis = this
  }

  object SButton {
    def apply[LP <: ViewGroupLayoutParams[_, SButton]]()(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
      val v = (new SButton)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SButton]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
      val v = (new SButton)
      v text txt
      v.<<.parent.+=(v)
      v
    }


    def apply[LP <: ViewGroupLayoutParams[_, SButton]](text: CharSequence, onClickListener: (View) => Unit)(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
      apply(text, func2ViewOnClickListener(onClickListener))
    }
    def apply[LP <: ViewGroupLayoutParams[_, SButton]](text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
      val v = (new SButton)
      v.text = text;      v.setOnClickListener(onClickListener)
      v.<<.parent.+=(v)
      v
    }


  }
  

  class RichCompoundButton[V <: CompoundButton](val basis: V) extends TraitCompoundButton[V]
  @inline implicit def compoundButton2RichCompoundButton[V <: CompoundButton](compoundButton: V) = new RichCompoundButton[V](compoundButton)

  trait TraitCompoundButton[V <: CompoundButton] extends TraitButton[V] {

    @noEquivalentGetterExists
    @inline def buttonDrawable    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def buttonDrawable  (p: android.graphics.drawable.Drawable) =            buttonDrawable_=  (p)
    @inline def buttonDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setButtonDrawable    (p); basis }

    @inline def checked = basis.isChecked
    @inline def checked  (p: Boolean) =            checked_=  (p)
    @inline def checked_=(p: Boolean) = { basis.setChecked    (p); basis }

    @noEquivalentGetterExists
    @inline def onCheckedChangeListener    : android.widget.CompoundButton.OnCheckedChangeListener  = defaultValue[android.widget.CompoundButton.OnCheckedChangeListener]
    @inline def onCheckedChangeListener  (p: android.widget.CompoundButton.OnCheckedChangeListener) =            onCheckedChangeListener_=  (p)
    @inline def onCheckedChangeListener_=(p: android.widget.CompoundButton.OnCheckedChangeListener) = { basis.setOnCheckedChangeListener    (p); basis }

    }
  class RichCheckBox[V <: CheckBox](val basis: V) extends TraitCheckBox[V]
  @inline implicit def checkBox2RichCheckBox[V <: CheckBox](checkBox: V) = new RichCheckBox[V](checkBox)

  trait TraitCheckBox[V <: CheckBox] extends TraitCompoundButton[V] {

  }

  class SCheckBox(implicit context: Context) extends CheckBox(context) with TraitCheckBox[SCheckBox] {
    def basis = this
  }

  object SCheckBox {
    def apply[LP <: ViewGroupLayoutParams[_, SCheckBox]]()(implicit context: Context, defaultLayoutParam: (SCheckBox) => LP): SCheckBox =  {
      val v = (new SCheckBox)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SCheckBox]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SCheckBox) => LP): SCheckBox =  {
      val v = (new SCheckBox)
      v text txt
      v.<<.parent.+=(v)
      v
    }


    def apply[LP <: ViewGroupLayoutParams[_, SCheckBox]](text: CharSequence, onClickListener: (View) => Unit)(implicit context: Context, defaultLayoutParam: (SCheckBox) => LP): SCheckBox =  {
      apply(text, func2ViewOnClickListener(onClickListener))
    }
    def apply[LP <: ViewGroupLayoutParams[_, SCheckBox]](text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context, defaultLayoutParam: (SCheckBox) => LP): SCheckBox =  {
      val v = (new SCheckBox)
      v.text = text;      v.setOnClickListener(onClickListener)
      v.<<.parent.+=(v)
      v
    }


  }
  

  class RichRadioButton[V <: RadioButton](val basis: V) extends TraitRadioButton[V]
  @inline implicit def radioButton2RichRadioButton[V <: RadioButton](radioButton: V) = new RichRadioButton[V](radioButton)

  trait TraitRadioButton[V <: RadioButton] extends TraitCompoundButton[V] {

  }

  class SRadioButton(implicit context: Context) extends RadioButton(context) with TraitRadioButton[SRadioButton] {
    def basis = this
  }

  object SRadioButton {
    def apply[LP <: ViewGroupLayoutParams[_, SRadioButton]]()(implicit context: Context, defaultLayoutParam: (SRadioButton) => LP): SRadioButton =  {
      val v = (new SRadioButton)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SRadioButton]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SRadioButton) => LP): SRadioButton =  {
      val v = (new SRadioButton)
      v text txt
      v.<<.parent.+=(v)
      v
    }


    def apply[LP <: ViewGroupLayoutParams[_, SRadioButton]](text: CharSequence, onClickListener: (View) => Unit)(implicit context: Context, defaultLayoutParam: (SRadioButton) => LP): SRadioButton =  {
      apply(text, func2ViewOnClickListener(onClickListener))
    }
    def apply[LP <: ViewGroupLayoutParams[_, SRadioButton]](text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context, defaultLayoutParam: (SRadioButton) => LP): SRadioButton =  {
      val v = (new SRadioButton)
      v.text = text;      v.setOnClickListener(onClickListener)
      v.<<.parent.+=(v)
      v
    }


  }
  

  class RichToggleButton[V <: ToggleButton](val basis: V) extends TraitToggleButton[V]
  @inline implicit def toggleButton2RichToggleButton[V <: ToggleButton](toggleButton: V) = new RichToggleButton[V](toggleButton)

  trait TraitToggleButton[V <: ToggleButton] extends TraitCompoundButton[V] {

    @inline def textOff = basis.getTextOff
    @inline def textOff  (p: java.lang.CharSequence) =            textOff_=  (p)
    @inline def textOff_=(p: java.lang.CharSequence) = { basis.setTextOff    (p); basis }

    @inline def textOn = basis.getTextOn
    @inline def textOn  (p: java.lang.CharSequence) =            textOn_=  (p)
    @inline def textOn_=(p: java.lang.CharSequence) = { basis.setTextOn    (p); basis }

  }

  class SToggleButton(implicit context: Context) extends ToggleButton(context) with TraitToggleButton[SToggleButton] {
    def basis = this
  }

  object SToggleButton {
    def apply[LP <: ViewGroupLayoutParams[_, SToggleButton]]()(implicit context: Context, defaultLayoutParam: (SToggleButton) => LP): SToggleButton =  {
      val v = (new SToggleButton)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SToggleButton]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SToggleButton) => LP): SToggleButton =  {
      val v = (new SToggleButton)
      v text txt
      v.<<.parent.+=(v)
      v
    }


    def apply[LP <: ViewGroupLayoutParams[_, SToggleButton]](text: CharSequence, onClickListener: (View) => Unit)(implicit context: Context, defaultLayoutParam: (SToggleButton) => LP): SToggleButton =  {
      apply(text, func2ViewOnClickListener(onClickListener))
    }
    def apply[LP <: ViewGroupLayoutParams[_, SToggleButton]](text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context, defaultLayoutParam: (SToggleButton) => LP): SToggleButton =  {
      val v = (new SToggleButton)
      v.text = text;      v.setOnClickListener(onClickListener)
      v.<<.parent.+=(v)
      v
    }


  }
  

  class RichCheckedTextView[V <: CheckedTextView](val basis: V) extends TraitCheckedTextView[V]
  @inline implicit def checkedTextView2RichCheckedTextView[V <: CheckedTextView](checkedTextView: V) = new RichCheckedTextView[V](checkedTextView)

  trait TraitCheckedTextView[V <: CheckedTextView] extends TraitTextView[V] {

    @noEquivalentGetterExists
    @inline def checkMarkDrawable    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def checkMarkDrawable  (p: android.graphics.drawable.Drawable) =            checkMarkDrawable_=  (p)
    @inline def checkMarkDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setCheckMarkDrawable    (p); basis }

    @inline def checked = basis.isChecked
    @inline def checked  (p: Boolean) =            checked_=  (p)
    @inline def checked_=(p: Boolean) = { basis.setChecked    (p); basis }

  }

  class SCheckedTextView(implicit context: Context) extends CheckedTextView(context) with TraitCheckedTextView[SCheckedTextView] {
    def basis = this
  }

  object SCheckedTextView {
    def apply[LP <: ViewGroupLayoutParams[_, SCheckedTextView]]()(implicit context: Context, defaultLayoutParam: (SCheckedTextView) => LP): SCheckedTextView =  {
      val v = (new SCheckedTextView)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SCheckedTextView]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SCheckedTextView) => LP): SCheckedTextView =  {
      val v = (new SCheckedTextView)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichChronometer[V <: Chronometer](val basis: V) extends TraitChronometer[V]
  @inline implicit def chronometer2RichChronometer[V <: Chronometer](chronometer: V) = new RichChronometer[V](chronometer)

  trait TraitChronometer[V <: Chronometer] extends TraitTextView[V] {

    @inline def base = basis.getBase
    @inline def base  (p: Long) =            base_=  (p)
    @inline def base_=(p: Long) = { basis.setBase    (p); basis }

    @inline def format = basis.getFormat
    @inline def format  (p: java.lang.String) =            format_=  (p)
    @inline def format_=(p: java.lang.String) = { basis.setFormat    (p); basis }

    @inline def onChronometerTickListener = basis.getOnChronometerTickListener
    @inline def onChronometerTickListener  (p: android.widget.Chronometer.OnChronometerTickListener) =            onChronometerTickListener_=  (p)
    @inline def onChronometerTickListener_=(p: android.widget.Chronometer.OnChronometerTickListener) = { basis.setOnChronometerTickListener    (p); basis }

  }

  class SChronometer(implicit context: Context) extends Chronometer(context) with TraitChronometer[SChronometer] {
    def basis = this
  }

  object SChronometer {
    def apply[LP <: ViewGroupLayoutParams[_, SChronometer]]()(implicit context: Context, defaultLayoutParam: (SChronometer) => LP): SChronometer =  {
      val v = (new SChronometer)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SChronometer]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SChronometer) => LP): SChronometer =  {
      val v = (new SChronometer)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichDigitalClock[V <: DigitalClock](val basis: V) extends TraitDigitalClock[V]
  @inline implicit def digitalClock2RichDigitalClock[V <: DigitalClock](digitalClock: V) = new RichDigitalClock[V](digitalClock)

  trait TraitDigitalClock[V <: DigitalClock] extends TraitTextView[V] {

  }

  class SDigitalClock(implicit context: Context) extends DigitalClock(context) with TraitDigitalClock[SDigitalClock] {
    def basis = this
  }

  object SDigitalClock {
    def apply[LP <: ViewGroupLayoutParams[_, SDigitalClock]]()(implicit context: Context, defaultLayoutParam: (SDigitalClock) => LP): SDigitalClock =  {
      val v = (new SDigitalClock)
      
      v.<<.parent.+=(v)
      v
    }

    def apply[LP <: ViewGroupLayoutParams[_, SDigitalClock]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SDigitalClock) => LP): SDigitalClock =  {
      val v = (new SDigitalClock)
      v text txt
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichKeyboardView[V <: KeyboardView](val basis: V) extends TraitKeyboardView[V]
  @inline implicit def keyboardView2RichKeyboardView[V <: KeyboardView](keyboardView: V) = new RichKeyboardView[V](keyboardView)

  trait TraitKeyboardView[V <: KeyboardView] extends TraitView[V] {

    @inline def keyboard = basis.getKeyboard
    @inline def keyboard  (p: android.inputmethodservice.Keyboard) =            keyboard_=  (p)
    @inline def keyboard_=(p: android.inputmethodservice.Keyboard) = { basis.setKeyboard    (p); basis }

    @noEquivalentGetterExists
    @inline def onKeyboardActionListener    : android.inputmethodservice.KeyboardView.OnKeyboardActionListener  = defaultValue[android.inputmethodservice.KeyboardView.OnKeyboardActionListener]
    @inline def onKeyboardActionListener  (p: android.inputmethodservice.KeyboardView.OnKeyboardActionListener) =            onKeyboardActionListener_=  (p)
    @inline def onKeyboardActionListener_=(p: android.inputmethodservice.KeyboardView.OnKeyboardActionListener) = { basis.setOnKeyboardActionListener    (p); basis }

    @noEquivalentGetterExists
    @inline def popupParent    : android.view.View  = defaultValue[android.view.View]
    @inline def popupParent  (p: android.view.View) =            popupParent_=  (p)
    @inline def popupParent_=(p: android.view.View) = { basis.setPopupParent    (p); basis }

    @inline def previewEnabled = basis.isPreviewEnabled
    @inline def previewEnabled  (p: Boolean) =            previewEnabled_=  (p)
    @inline def previewEnabled_=(p: Boolean) = { basis.setPreviewEnabled    (p); basis }
    @inline def  enablePreview               = { basis.setPreviewEnabled(true ); basis }
    @inline def disablePreview               = { basis.setPreviewEnabled(false); basis }

    @inline def proximityCorrectionEnabled = basis.isProximityCorrectionEnabled
    @inline def proximityCorrectionEnabled  (p: Boolean) =            proximityCorrectionEnabled_=  (p)
    @inline def proximityCorrectionEnabled_=(p: Boolean) = { basis.setProximityCorrectionEnabled    (p); basis }
    @inline def  enableProximityCorrection               = { basis.setProximityCorrectionEnabled(true ); basis }
    @inline def disableProximityCorrection               = { basis.setProximityCorrectionEnabled(false); basis }

    @inline def shifted = basis.isShifted

    @noEquivalentGetterExists
    @inline def verticalCorrection    : Int  = defaultValue[Int]
    @inline def verticalCorrection  (p: Int) =            verticalCorrection_=  (p)
    @inline def verticalCorrection_=(p: Int) = { basis.setVerticalCorrection    (p); basis }

    }
  class RichImageView[V <: ImageView](val basis: V) extends TraitImageView[V]
  @inline implicit def imageView2RichImageView[V <: ImageView](imageView: V) = new RichImageView[V](imageView)

  trait TraitImageView[V <: ImageView] extends TraitView[V] {

    @noEquivalentGetterExists
    @inline def adjustViewBounds    : Boolean  = defaultValue[Boolean]
    @inline def adjustViewBounds  (p: Boolean) =            adjustViewBounds_=  (p)
    @inline def adjustViewBounds_=(p: Boolean) = { basis.setAdjustViewBounds    (p); basis }

    @noEquivalentGetterExists
    @inline def alpha    : Int  = defaultValue[Int]
    @inline def alpha  (p: Int) =            alpha_=  (p)
    @inline def alpha_=(p: Int) = { basis.setAlpha    (p); basis }

    @inline def drawable = basis.getDrawable

    @noEquivalentGetterExists
    @inline def imageBitmap    : android.graphics.Bitmap  = defaultValue[android.graphics.Bitmap]
    @inline def imageBitmap  (p: android.graphics.Bitmap) =            imageBitmap_=  (p)
    @inline def imageBitmap_=(p: android.graphics.Bitmap) = { basis.setImageBitmap    (p); basis }

    @noEquivalentGetterExists
    @inline def imageDrawable    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def imageDrawable  (p: android.graphics.drawable.Drawable) =            imageDrawable_=  (p)
    @inline def imageDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setImageDrawable    (p); basis }

    @noEquivalentGetterExists
    @inline def imageLevel    : Int  = defaultValue[Int]
    @inline def imageLevel  (p: Int) =            imageLevel_=  (p)
    @inline def imageLevel_=(p: Int) = { basis.setImageLevel    (p); basis }

    @inline def imageMatrix = basis.getImageMatrix
    @inline def imageMatrix  (p: android.graphics.Matrix) =            imageMatrix_=  (p)
    @inline def imageMatrix_=(p: android.graphics.Matrix) = { basis.setImageMatrix    (p); basis }

    @noEquivalentGetterExists
    @inline def imageResource    : Int  = defaultValue[Int]
    @inline def imageResource  (p: Int) =            imageResource_=  (p)
    @inline def imageResource_=(p: Int) = { basis.setImageResource    (p); basis }

    @noEquivalentGetterExists
    @inline def imageURI    : android.net.Uri  = defaultValue[android.net.Uri]
    @inline def imageURI  (p: android.net.Uri) =            imageURI_=  (p)
    @inline def imageURI_=(p: android.net.Uri) = { basis.setImageURI    (p); basis }

    @noEquivalentGetterExists
    @inline def maxHeight    : Int  = defaultValue[Int]
    @inline def maxHeight  (p: Int) =            maxHeight_=  (p)
    @inline def maxHeight_=(p: Int) = { basis.setMaxHeight    (p); basis }

    @noEquivalentGetterExists
    @inline def maxWidth    : Int  = defaultValue[Int]
    @inline def maxWidth  (p: Int) =            maxWidth_=  (p)
    @inline def maxWidth_=(p: Int) = { basis.setMaxWidth    (p); basis }

    @inline def scaleType = basis.getScaleType
    @inline def scaleType  (p: android.widget.ImageView.ScaleType) =            scaleType_=  (p)
    @inline def scaleType_=(p: android.widget.ImageView.ScaleType) = { basis.setScaleType    (p); basis }

  }

  class SImageView(implicit context: Context) extends ImageView(context) with TraitImageView[SImageView] {
    def basis = this
  }

  object SImageView {
    def apply[LP <: ViewGroupLayoutParams[_, SImageView]]()(implicit context: Context, defaultLayoutParam: (SImageView) => LP): SImageView =  {
      val v = (new SImageView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichImageButton[V <: ImageButton](val basis: V) extends TraitImageButton[V]
  @inline implicit def imageButton2RichImageButton[V <: ImageButton](imageButton: V) = new RichImageButton[V](imageButton)

  trait TraitImageButton[V <: ImageButton] extends TraitImageView[V] {

  }

  class SImageButton(implicit context: Context) extends ImageButton(context) with TraitImageButton[SImageButton] {
    def basis = this
  }

  object SImageButton {
    def apply[LP <: ViewGroupLayoutParams[_, SImageButton]]()(implicit context: Context, defaultLayoutParam: (SImageButton) => LP): SImageButton =  {
      val v = (new SImageButton)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichQuickContactBadge[V <: QuickContactBadge](val basis: V) extends TraitQuickContactBadge[V]
  @inline implicit def quickContactBadge2RichQuickContactBadge[V <: QuickContactBadge](quickContactBadge: V) = new RichQuickContactBadge[V](quickContactBadge)

  trait TraitQuickContactBadge[V <: QuickContactBadge] extends TraitImageView[V] {

    @noEquivalentGetterExists
    @inline def excludeMimes    : Array[java.lang.String]  = defaultValue[Array[java.lang.String]]
    @inline def excludeMimes  (p: Array[java.lang.String]) =            excludeMimes_=  (p)
    @inline def excludeMimes_=(p: Array[java.lang.String]) = { basis.setExcludeMimes    (p); basis }

    @noEquivalentGetterExists
    @inline def mode    : Int  = defaultValue[Int]
    @inline def mode  (p: Int) =            mode_=  (p)
    @inline def mode_=(p: Int) = { basis.setMode    (p); basis }

  }

  class SQuickContactBadge(implicit context: Context) extends QuickContactBadge(context) with TraitQuickContactBadge[SQuickContactBadge] {
    def basis = this
  }

  object SQuickContactBadge {
    def apply[LP <: ViewGroupLayoutParams[_, SQuickContactBadge]]()(implicit context: Context, defaultLayoutParam: (SQuickContactBadge) => LP): SQuickContactBadge =  {
      val v = (new SQuickContactBadge)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichZoomButton[V <: ZoomButton](val basis: V) extends TraitZoomButton[V]
  @inline implicit def zoomButton2RichZoomButton[V <: ZoomButton](zoomButton: V) = new RichZoomButton[V](zoomButton)

  trait TraitZoomButton[V <: ZoomButton] extends TraitImageButton[V] {

    @noEquivalentGetterExists
    @inline def zoomSpeed    : Long  = defaultValue[Long]
    @inline def zoomSpeed  (p: Long) =            zoomSpeed_=  (p)
    @inline def zoomSpeed_=(p: Long) = { basis.setZoomSpeed    (p); basis }

  }

  class SZoomButton(implicit context: Context) extends ZoomButton(context) with TraitZoomButton[SZoomButton] {
    def basis = this
  }

  object SZoomButton {
    def apply[LP <: ViewGroupLayoutParams[_, SZoomButton]]()(implicit context: Context, defaultLayoutParam: (SZoomButton) => LP): SZoomButton =  {
      val v = (new SZoomButton)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichProgressBar[V <: ProgressBar](val basis: V) extends TraitProgressBar[V]
  @inline implicit def progressBar2RichProgressBar[V <: ProgressBar](progressBar: V) = new RichProgressBar[V](progressBar)

  trait TraitProgressBar[V <: ProgressBar] extends TraitView[V] {

    @inline def indeterminate = basis.isIndeterminate
    @inline def indeterminate  (p: Boolean) =            indeterminate_=  (p)
    @inline def indeterminate_=(p: Boolean) = { basis.setIndeterminate    (p); basis }

    @inline def indeterminateDrawable = basis.getIndeterminateDrawable
    @inline def indeterminateDrawable  (p: android.graphics.drawable.Drawable) =            indeterminateDrawable_=  (p)
    @inline def indeterminateDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setIndeterminateDrawable    (p); basis }

    @inline def interpolator = basis.getInterpolator
    @inline def interpolator  (p: android.view.animation.Interpolator) =            interpolator_=  (p)
    @inline def interpolator_=(p: android.view.animation.Interpolator) = { basis.setInterpolator    (p); basis }

    @inline def max = basis.getMax
    @inline def max  (p: Int) =            max_=  (p)
    @inline def max_=(p: Int) = { basis.setMax    (p); basis }

    @inline def progress = basis.getProgress
    @inline def progress  (p: Int) =            progress_=  (p)
    @inline def progress_=(p: Int) = { basis.setProgress    (p); basis }

    @inline def progressDrawable = basis.getProgressDrawable
    @inline def progressDrawable  (p: android.graphics.drawable.Drawable) =            progressDrawable_=  (p)
    @inline def progressDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setProgressDrawable    (p); basis }

    @inline def secondaryProgress = basis.getSecondaryProgress
    @inline def secondaryProgress  (p: Int) =            secondaryProgress_=  (p)
    @inline def secondaryProgress_=(p: Int) = { basis.setSecondaryProgress    (p); basis }

  }

  class SProgressBar(implicit context: Context) extends ProgressBar(context) with TraitProgressBar[SProgressBar] {
    def basis = this
  }

  object SProgressBar {
    def apply[LP <: ViewGroupLayoutParams[_, SProgressBar]]()(implicit context: Context, defaultLayoutParam: (SProgressBar) => LP): SProgressBar =  {
      val v = (new SProgressBar)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichAnalogClock[V <: AnalogClock](val basis: V) extends TraitAnalogClock[V]
  @inline implicit def analogClock2RichAnalogClock[V <: AnalogClock](analogClock: V) = new RichAnalogClock[V](analogClock)

  trait TraitAnalogClock[V <: AnalogClock] extends TraitView[V] {

  }

  class SAnalogClock(implicit context: Context) extends AnalogClock(context) with TraitAnalogClock[SAnalogClock] {
    def basis = this
  }

  object SAnalogClock {
    def apply[LP <: ViewGroupLayoutParams[_, SAnalogClock]]()(implicit context: Context, defaultLayoutParam: (SAnalogClock) => LP): SAnalogClock =  {
      val v = (new SAnalogClock)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichSurfaceView[V <: SurfaceView](val basis: V) extends TraitSurfaceView[V]
  @inline implicit def surfaceView2RichSurfaceView[V <: SurfaceView](surfaceView: V) = new RichSurfaceView[V](surfaceView)

  trait TraitSurfaceView[V <: SurfaceView] extends TraitView[V] {

    @noEquivalentGetterExists
    @inline def ZOrderMediaOverlay    : Boolean  = defaultValue[Boolean]
    @inline def ZOrderMediaOverlay  (p: Boolean) =            ZOrderMediaOverlay_=  (p)
    @inline def ZOrderMediaOverlay_=(p: Boolean) = { basis.setZOrderMediaOverlay    (p); basis }

    @noEquivalentGetterExists
    @inline def ZOrderOnTop    : Boolean  = defaultValue[Boolean]
    @inline def ZOrderOnTop  (p: Boolean) =            ZOrderOnTop_=  (p)
    @inline def ZOrderOnTop_=(p: Boolean) = { basis.setZOrderOnTop    (p); basis }

    @inline def holder = basis.getHolder

  }

  class SSurfaceView(implicit context: Context) extends SurfaceView(context) with TraitSurfaceView[SSurfaceView] {
    def basis = this
  }

  object SSurfaceView {
    def apply[LP <: ViewGroupLayoutParams[_, SSurfaceView]]()(implicit context: Context, defaultLayoutParam: (SSurfaceView) => LP): SSurfaceView =  {
      val v = (new SSurfaceView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichGLSurfaceView[V <: GLSurfaceView](val basis: V) extends TraitGLSurfaceView[V]
  @inline implicit def gLSurfaceView2RichGLSurfaceView[V <: GLSurfaceView](gLSurfaceView: V) = new RichGLSurfaceView[V](gLSurfaceView)

  trait TraitGLSurfaceView[V <: GLSurfaceView] extends TraitSurfaceView[V] {

    @noEquivalentGetterExists
    @inline def EGLConfigChooser    : android.opengl.GLSurfaceView.EGLConfigChooser  = defaultValue[android.opengl.GLSurfaceView.EGLConfigChooser]
    @inline def EGLConfigChooser  (p: android.opengl.GLSurfaceView.EGLConfigChooser) =            EGLConfigChooser_=  (p)
    @inline def EGLConfigChooser_=(p: android.opengl.GLSurfaceView.EGLConfigChooser) = { basis.setEGLConfigChooser    (p); basis }

    @noEquivalentGetterExists
    @inline def EGLContextClientVersion    : Int  = defaultValue[Int]
    @inline def EGLContextClientVersion  (p: Int) =            EGLContextClientVersion_=  (p)
    @inline def EGLContextClientVersion_=(p: Int) = { basis.setEGLContextClientVersion    (p); basis }

    @noEquivalentGetterExists
    @inline def EGLContextFactory    : android.opengl.GLSurfaceView.EGLContextFactory  = defaultValue[android.opengl.GLSurfaceView.EGLContextFactory]
    @inline def EGLContextFactory  (p: android.opengl.GLSurfaceView.EGLContextFactory) =            EGLContextFactory_=  (p)
    @inline def EGLContextFactory_=(p: android.opengl.GLSurfaceView.EGLContextFactory) = { basis.setEGLContextFactory    (p); basis }

    @noEquivalentGetterExists
    @inline def EGLWindowSurfaceFactory    : android.opengl.GLSurfaceView.EGLWindowSurfaceFactory  = defaultValue[android.opengl.GLSurfaceView.EGLWindowSurfaceFactory]
    @inline def EGLWindowSurfaceFactory  (p: android.opengl.GLSurfaceView.EGLWindowSurfaceFactory) =            EGLWindowSurfaceFactory_=  (p)
    @inline def EGLWindowSurfaceFactory_=(p: android.opengl.GLSurfaceView.EGLWindowSurfaceFactory) = { basis.setEGLWindowSurfaceFactory    (p); basis }

    @noEquivalentGetterExists
    @inline def GLWrapper    : android.opengl.GLSurfaceView.GLWrapper  = defaultValue[android.opengl.GLSurfaceView.GLWrapper]
    @inline def GLWrapper  (p: android.opengl.GLSurfaceView.GLWrapper) =            GLWrapper_=  (p)
    @inline def GLWrapper_=(p: android.opengl.GLSurfaceView.GLWrapper) = { basis.setGLWrapper    (p); basis }

    @inline def debugFlags = basis.getDebugFlags
    @inline def debugFlags  (p: Int) =            debugFlags_=  (p)
    @inline def debugFlags_=(p: Int) = { basis.setDebugFlags    (p); basis }

    @inline def renderMode = basis.getRenderMode
    @inline def renderMode  (p: Int) =            renderMode_=  (p)
    @inline def renderMode_=(p: Int) = { basis.setRenderMode    (p); basis }

    @noEquivalentGetterExists
    @inline def renderer    : android.opengl.GLSurfaceView.Renderer  = defaultValue[android.opengl.GLSurfaceView.Renderer]
    @inline def renderer  (p: android.opengl.GLSurfaceView.Renderer) =            renderer_=  (p)
    @inline def renderer_=(p: android.opengl.GLSurfaceView.Renderer) = { basis.setRenderer    (p); basis }

  }

  class SGLSurfaceView(implicit context: Context) extends GLSurfaceView(context) with TraitGLSurfaceView[SGLSurfaceView] {
    def basis = this
  }

  object SGLSurfaceView {
    def apply[LP <: ViewGroupLayoutParams[_, SGLSurfaceView]]()(implicit context: Context, defaultLayoutParam: (SGLSurfaceView) => LP): SGLSurfaceView =  {
      val v = (new SGLSurfaceView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichVideoView[V <: VideoView](val basis: V) extends TraitVideoView[V]
  @inline implicit def videoView2RichVideoView[V <: VideoView](videoView: V) = new RichVideoView[V](videoView)

  trait TraitVideoView[V <: VideoView] extends TraitSurfaceView[V] {

    @inline def bufferPercentage = basis.getBufferPercentage

    @inline def currentPosition = basis.getCurrentPosition

    @inline def duration = basis.getDuration

    @noEquivalentGetterExists
    @inline def mediaController    : android.widget.MediaController  = defaultValue[android.widget.MediaController]
    @inline def mediaController  (p: android.widget.MediaController) =            mediaController_=  (p)
    @inline def mediaController_=(p: android.widget.MediaController) = { basis.setMediaController    (p); basis }

    @noEquivalentGetterExists
    @inline def onCompletionListener    : android.media.MediaPlayer.OnCompletionListener  = defaultValue[android.media.MediaPlayer.OnCompletionListener]
    @inline def onCompletionListener  (p: android.media.MediaPlayer.OnCompletionListener) =            onCompletionListener_=  (p)
    @inline def onCompletionListener_=(p: android.media.MediaPlayer.OnCompletionListener) = { basis.setOnCompletionListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onErrorListener    : android.media.MediaPlayer.OnErrorListener  = defaultValue[android.media.MediaPlayer.OnErrorListener]
    @inline def onErrorListener  (p: android.media.MediaPlayer.OnErrorListener) =            onErrorListener_=  (p)
    @inline def onErrorListener_=(p: android.media.MediaPlayer.OnErrorListener) = { basis.setOnErrorListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onPreparedListener    : android.media.MediaPlayer.OnPreparedListener  = defaultValue[android.media.MediaPlayer.OnPreparedListener]
    @inline def onPreparedListener  (p: android.media.MediaPlayer.OnPreparedListener) =            onPreparedListener_=  (p)
    @inline def onPreparedListener_=(p: android.media.MediaPlayer.OnPreparedListener) = { basis.setOnPreparedListener    (p); basis }

    @inline def playing = basis.isPlaying

    @noEquivalentGetterExists
    @inline def videoPath    : java.lang.String  = defaultValue[java.lang.String]
    @inline def videoPath  (p: java.lang.String) =            videoPath_=  (p)
    @inline def videoPath_=(p: java.lang.String) = { basis.setVideoPath    (p); basis }

    @noEquivalentGetterExists
    @inline def videoURI    : android.net.Uri  = defaultValue[android.net.Uri]
    @inline def videoURI  (p: android.net.Uri) =            videoURI_=  (p)
    @inline def videoURI_=(p: android.net.Uri) = { basis.setVideoURI    (p); basis }

  }

  class SVideoView(implicit context: Context) extends VideoView(context) with TraitVideoView[SVideoView] {
    def basis = this
  }

  object SVideoView {
    def apply[LP <: ViewGroupLayoutParams[_, SVideoView]]()(implicit context: Context, defaultLayoutParam: (SVideoView) => LP): SVideoView =  {
      val v = (new SVideoView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichViewStub[V <: ViewStub](val basis: V) extends TraitViewStub[V]
  @inline implicit def viewStub2RichViewStub[V <: ViewStub](viewStub: V) = new RichViewStub[V](viewStub)

  trait TraitViewStub[V <: ViewStub] extends TraitView[V] {

    @inline def inflatedId = basis.getInflatedId
    @inline def inflatedId  (p: Int) =            inflatedId_=  (p)
    @inline def inflatedId_=(p: Int) = { basis.setInflatedId    (p); basis }

    @inline def layoutResource = basis.getLayoutResource
    @inline def layoutResource  (p: Int) =            layoutResource_=  (p)
    @inline def layoutResource_=(p: Int) = { basis.setLayoutResource    (p); basis }

    @noEquivalentGetterExists
    @inline def onInflateListener    : android.view.ViewStub.OnInflateListener  = defaultValue[android.view.ViewStub.OnInflateListener]
    @inline def onInflateListener  (p: android.view.ViewStub.OnInflateListener) =            onInflateListener_=  (p)
    @inline def onInflateListener_=(p: android.view.ViewStub.OnInflateListener) = { basis.setOnInflateListener    (p); basis }

    }
  class RichGridView[V <: GridView](val basis: V) extends TraitGridView[V]
  @inline implicit def gridView2RichGridView[V <: GridView](gridView: V) = new RichGridView[V](gridView)

  trait TraitGridView[V <: GridView] extends TraitAbsListView[V] {

    @noEquivalentGetterExists
    @inline def columnWidth    : Int  = defaultValue[Int]
    @inline def columnWidth  (p: Int) =            columnWidth_=  (p)
    @inline def columnWidth_=(p: Int) = { basis.setColumnWidth    (p); basis }

    @noEquivalentGetterExists
    @inline def gravity    : Int  = defaultValue[Int]
    @inline def gravity  (p: Int) =            gravity_=  (p)
    @inline def gravity_=(p: Int) = { basis.setGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def horizontalSpacing    : Int  = defaultValue[Int]
    @inline def horizontalSpacing  (p: Int) =            horizontalSpacing_=  (p)
    @inline def horizontalSpacing_=(p: Int) = { basis.setHorizontalSpacing    (p); basis }

    @noEquivalentGetterExists
    @inline def numColumns    : Int  = defaultValue[Int]
    @inline def numColumns  (p: Int) =            numColumns_=  (p)
    @inline def numColumns_=(p: Int) = { basis.setNumColumns    (p); basis }

    @inline def stretchMode = basis.getStretchMode
    @inline def stretchMode  (p: Int) =            stretchMode_=  (p)
    @inline def stretchMode_=(p: Int) = { basis.setStretchMode    (p); basis }

    @noEquivalentGetterExists
    @inline def verticalSpacing    : Int  = defaultValue[Int]
    @inline def verticalSpacing  (p: Int) =            verticalSpacing_=  (p)
    @inline def verticalSpacing_=(p: Int) = { basis.setVerticalSpacing    (p); basis }

  }

  class SGridView(implicit context: Context) extends GridView(context) with TraitGridView[SGridView] {
    def basis = this
  }

  object SGridView {
    def apply[LP <: ViewGroupLayoutParams[_, SGridView]]()(implicit context: Context, defaultLayoutParam: (SGridView) => LP): SGridView =  {
      val v = (new SGridView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichExpandableListView[V <: ExpandableListView](val basis: V) extends TraitExpandableListView[V]
  @inline implicit def expandableListView2RichExpandableListView[V <: ExpandableListView](expandableListView: V) = new RichExpandableListView[V](expandableListView)

  trait TraitExpandableListView[V <: ExpandableListView] extends TraitListView[V] {

    @noEquivalentGetterExists
    @inline def childDivider    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def childDivider  (p: android.graphics.drawable.Drawable) =            childDivider_=  (p)
    @inline def childDivider_=(p: android.graphics.drawable.Drawable) = { basis.setChildDivider    (p); basis }

    @noEquivalentGetterExists
    @inline def childIndicator    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def childIndicator  (p: android.graphics.drawable.Drawable) =            childIndicator_=  (p)
    @inline def childIndicator_=(p: android.graphics.drawable.Drawable) = { basis.setChildIndicator    (p); basis }

    @inline def expandableListAdapter = basis.getExpandableListAdapter

    @noEquivalentGetterExists
    @inline def groupIndicator    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def groupIndicator  (p: android.graphics.drawable.Drawable) =            groupIndicator_=  (p)
    @inline def groupIndicator_=(p: android.graphics.drawable.Drawable) = { basis.setGroupIndicator    (p); basis }

    @noEquivalentGetterExists
    @inline def onChildClickListener    : android.widget.ExpandableListView.OnChildClickListener  = defaultValue[android.widget.ExpandableListView.OnChildClickListener]
    @inline def onChildClickListener  (p: android.widget.ExpandableListView.OnChildClickListener) =            onChildClickListener_=  (p)
    @inline def onChildClickListener_=(p: android.widget.ExpandableListView.OnChildClickListener) = { basis.setOnChildClickListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onGroupClickListener    : android.widget.ExpandableListView.OnGroupClickListener  = defaultValue[android.widget.ExpandableListView.OnGroupClickListener]
    @inline def onGroupClickListener  (p: android.widget.ExpandableListView.OnGroupClickListener) =            onGroupClickListener_=  (p)
    @inline def onGroupClickListener_=(p: android.widget.ExpandableListView.OnGroupClickListener) = { basis.setOnGroupClickListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onGroupCollapseListener    : android.widget.ExpandableListView.OnGroupCollapseListener  = defaultValue[android.widget.ExpandableListView.OnGroupCollapseListener]
    @inline def onGroupCollapseListener  (p: android.widget.ExpandableListView.OnGroupCollapseListener) =            onGroupCollapseListener_=  (p)
    @inline def onGroupCollapseListener_=(p: android.widget.ExpandableListView.OnGroupCollapseListener) = { basis.setOnGroupCollapseListener    (p); basis }

    @noEquivalentGetterExists
    @inline def onGroupExpandListener    : android.widget.ExpandableListView.OnGroupExpandListener  = defaultValue[android.widget.ExpandableListView.OnGroupExpandListener]
    @inline def onGroupExpandListener  (p: android.widget.ExpandableListView.OnGroupExpandListener) =            onGroupExpandListener_=  (p)
    @inline def onGroupExpandListener_=(p: android.widget.ExpandableListView.OnGroupExpandListener) = { basis.setOnGroupExpandListener    (p); basis }

    @noEquivalentGetterExists
    @inline def selectedGroup    : Int  = defaultValue[Int]
    @inline def selectedGroup  (p: Int) =            selectedGroup_=  (p)
    @inline def selectedGroup_=(p: Int) = { basis.setSelectedGroup    (p); basis }

    @inline def selectedId = basis.getSelectedId

    @inline def selectedPosition = basis.getSelectedPosition

  }

  class SExpandableListView(implicit context: Context) extends ExpandableListView(context) with TraitExpandableListView[SExpandableListView] {
    def basis = this
  }

  object SExpandableListView {
    def apply[LP <: ViewGroupLayoutParams[_, SExpandableListView]]()(implicit context: Context, defaultLayoutParam: (SExpandableListView) => LP): SExpandableListView =  {
      val v = (new SExpandableListView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  


trait TraitAdapterView[V <: AdapterView[_]] extends TraitView[V] {
  import android.widget.AdapterView.OnItemClickListener
  import android.widget.AdapterView.OnItemLongClickListener
    @inline def onItemClick(f:  => Unit): V = {
      basis.setOnItemClickListener(new OnItemClickListener {
        def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = { f }
      })
      basis
    }

    @inline def onItemClick(f: (AdapterView[_], View, Int, Long) => Unit): V = {
      basis.setOnItemClickListener(new OnItemClickListener {
        def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = { f(p1, p2, p3, p4) }
      })
      basis
    }

    @inline def onItemLongClick(f:  => Boolean): V = {
      basis.setOnItemLongClickListener(new OnItemLongClickListener {
        def onItemLongClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Boolean = { f }
      })
      basis
    }

    @inline def onItemLongClick(f: (AdapterView[_], View, Int, Long) => Boolean): V = {
      basis.setOnItemLongClickListener(new OnItemLongClickListener {
        def onItemLongClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Boolean = { f(p1, p2, p3, p4) }
      })
      basis
    }

    @inline def onItemSelected(f:  => Unit): V = {
      basis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = { f }
        def onNothingSelected(p1: AdapterView[_]): Unit = {  }
      })
      basis
    }

    @inline def onNothingSelected(f:  => Unit): V = {
      basis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {  }
        def onNothingSelected(p1: AdapterView[_]): Unit = { f }
      })
      basis
    }

    @inline def onItemSelected(f: (AdapterView[_], View, Int, Long) => Unit): V = {
      basis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = { f(p1, p2, p3, p4) }
        def onNothingSelected(p1: AdapterView[_]): Unit = {  }
      })
      basis
    }

    @inline def onNothingSelected(f: (AdapterView[_]) => Unit): V = {
      basis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {  }
        def onNothingSelected(p1: AdapterView[_]): Unit = { f(p1) }
      })
      basis
    }


}

  trait TraitAbsSpinner[V <: AbsSpinner] extends TraitAdapterView[V] {
  }

  class RichSpinner[V <: Spinner](val basis: V) extends TraitSpinner[V]
  @inline implicit def spinner2RichSpinner[V <: Spinner](spinner: V) = new RichSpinner[V](spinner)

  trait TraitSpinner[V <: Spinner] extends TraitAbsSpinner[V] {

    @inline def prompt = basis.getPrompt
    @inline def prompt  (p: java.lang.CharSequence) =            prompt_=  (p)
    @inline def prompt_=(p: java.lang.CharSequence) = { basis.setPrompt    (p); basis }

    @noEquivalentGetterExists
    @inline def promptId    : Int  = defaultValue[Int]
    @inline def promptId  (p: Int) =            promptId_=  (p)
    @inline def promptId_=(p: Int) = { basis.setPromptId    (p); basis }

  }

  class SSpinner(implicit context: Context) extends Spinner(context) with TraitSpinner[SSpinner] {
    def basis = this
  }

  object SSpinner {
    def apply[LP <: ViewGroupLayoutParams[_, SSpinner]]()(implicit context: Context, defaultLayoutParam: (SSpinner) => LP): SSpinner =  {
      val v = (new SSpinner)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichGallery[V <: Gallery](val basis: V) extends TraitGallery[V]
  @inline implicit def gallery2RichGallery[V <: Gallery](gallery: V) = new RichGallery[V](gallery)

  trait TraitGallery[V <: Gallery] extends TraitAbsSpinner[V] {

    @noEquivalentGetterExists
    @inline def animationDuration    : Int  = defaultValue[Int]
    @inline def animationDuration  (p: Int) =            animationDuration_=  (p)
    @inline def animationDuration_=(p: Int) = { basis.setAnimationDuration    (p); basis }

    @noEquivalentGetterExists
    @inline def callbackDuringFling    : Boolean  = defaultValue[Boolean]
    @inline def callbackDuringFling  (p: Boolean) =            callbackDuringFling_=  (p)
    @inline def callbackDuringFling_=(p: Boolean) = { basis.setCallbackDuringFling    (p); basis }

    @noEquivalentGetterExists
    @inline def gravity    : Int  = defaultValue[Int]
    @inline def gravity  (p: Int) =            gravity_=  (p)
    @inline def gravity_=(p: Int) = { basis.setGravity    (p); basis }

    @noEquivalentGetterExists
    @inline def spacing    : Int  = defaultValue[Int]
    @inline def spacing  (p: Int) =            spacing_=  (p)
    @inline def spacing_=(p: Int) = { basis.setSpacing    (p); basis }

    @noEquivalentGetterExists
    @inline def unselectedAlpha    : Float  = defaultValue[Float]
    @inline def unselectedAlpha  (p: Float) =            unselectedAlpha_=  (p)
    @inline def unselectedAlpha_=(p: Float) = { basis.setUnselectedAlpha    (p); basis }

  }

  class SGallery(implicit context: Context) extends Gallery(context) with TraitGallery[SGallery] {
    def basis = this
  }

  object SGallery {
    def apply[LP <: ViewGroupLayoutParams[_, SGallery]]()(implicit context: Context, defaultLayoutParam: (SGallery) => LP): SGallery =  {
      val v = (new SGallery)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichAbsSeekBar[V <: AbsSeekBar](val basis: V) extends TraitAbsSeekBar[V]
  @inline implicit def absSeekBar2RichAbsSeekBar[V <: AbsSeekBar](absSeekBar: V) = new RichAbsSeekBar[V](absSeekBar)

  trait TraitAbsSeekBar[V <: AbsSeekBar] extends TraitProgressBar[V] {

    @inline def keyProgressIncrement = basis.getKeyProgressIncrement
    @inline def keyProgressIncrement  (p: Int) =            keyProgressIncrement_=  (p)
    @inline def keyProgressIncrement_=(p: Int) = { basis.setKeyProgressIncrement    (p); basis }

    @noEquivalentGetterExists
    @inline def thumb    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def thumb  (p: android.graphics.drawable.Drawable) =            thumb_=  (p)
    @inline def thumb_=(p: android.graphics.drawable.Drawable) = { basis.setThumb    (p); basis }

    @inline def thumbOffset = basis.getThumbOffset
    @inline def thumbOffset  (p: Int) =            thumbOffset_=  (p)
    @inline def thumbOffset_=(p: Int) = { basis.setThumbOffset    (p); basis }

   }

  class RichSeekBar[V <: SeekBar](val basis: V) extends TraitSeekBar[V]
  @inline implicit def seekBar2RichSeekBar[V <: SeekBar](seekBar: V) = new RichSeekBar[V](seekBar)

  trait TraitSeekBar[V <: SeekBar] extends TraitAbsSeekBar[V] {

    @noEquivalentGetterExists
    @inline def onSeekBarChangeListener    : android.widget.SeekBar.OnSeekBarChangeListener  = defaultValue[android.widget.SeekBar.OnSeekBarChangeListener]
    @inline def onSeekBarChangeListener  (p: android.widget.SeekBar.OnSeekBarChangeListener) =            onSeekBarChangeListener_=  (p)
    @inline def onSeekBarChangeListener_=(p: android.widget.SeekBar.OnSeekBarChangeListener) = { basis.setOnSeekBarChangeListener    (p); basis }

    @inline def onProgressChanged(f:  => Unit): V = {
      basis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = { f }
        def onStartTrackingTouch(p1: SeekBar): Unit = {  }
        def onStopTrackingTouch(p1: SeekBar): Unit = {  }
      })
      basis
    }

    @inline def onStartTrackingTouch(f:  => Unit): V = {
      basis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {  }
        def onStartTrackingTouch(p1: SeekBar): Unit = { f }
        def onStopTrackingTouch(p1: SeekBar): Unit = {  }
      })
      basis
    }

    @inline def onStopTrackingTouch(f:  => Unit): V = {
      basis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {  }
        def onStartTrackingTouch(p1: SeekBar): Unit = {  }
        def onStopTrackingTouch(p1: SeekBar): Unit = { f }
      })
      basis
    }

    @inline def onProgressChanged(f: (SeekBar, Int, Boolean) => Unit): V = {
      basis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = { f(p1, p2, p3) }
        def onStartTrackingTouch(p1: SeekBar): Unit = {  }
        def onStopTrackingTouch(p1: SeekBar): Unit = {  }
      })
      basis
    }

    @inline def onStartTrackingTouch(f: (SeekBar) => Unit): V = {
      basis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {  }
        def onStartTrackingTouch(p1: SeekBar): Unit = { f(p1) }
        def onStopTrackingTouch(p1: SeekBar): Unit = {  }
      })
      basis
    }

    @inline def onStopTrackingTouch(f: (SeekBar) => Unit): V = {
      basis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {  }
        def onStartTrackingTouch(p1: SeekBar): Unit = {  }
        def onStopTrackingTouch(p1: SeekBar): Unit = { f(p1) }
      })
      basis
    }

  }
  class SSeekBar(implicit context: Context) extends SeekBar(context) with TraitSeekBar[SSeekBar] {
    def basis = this

  }

  object SSeekBar {
    def apply[LP <: ViewGroupLayoutParams[_, SSeekBar]]()(implicit context: Context, defaultLayoutParam: (SSeekBar) => LP): SSeekBar =  {
      val v = (new SSeekBar)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichRatingBar[V <: RatingBar](val basis: V) extends TraitRatingBar[V]
  @inline implicit def ratingBar2RichRatingBar[V <: RatingBar](ratingBar: V) = new RichRatingBar[V](ratingBar)

  trait TraitRatingBar[V <: RatingBar] extends TraitAbsSeekBar[V] {

    @inline def indicator = basis.isIndicator

    @inline def numStars = basis.getNumStars
    @inline def numStars  (p: Int) =            numStars_=  (p)
    @inline def numStars_=(p: Int) = { basis.setNumStars    (p); basis }

    @inline def onRatingBarChangeListener = basis.getOnRatingBarChangeListener
    @inline def onRatingBarChangeListener  (p: android.widget.RatingBar.OnRatingBarChangeListener) =            onRatingBarChangeListener_=  (p)
    @inline def onRatingBarChangeListener_=(p: android.widget.RatingBar.OnRatingBarChangeListener) = { basis.setOnRatingBarChangeListener    (p); basis }

    @inline def rating = basis.getRating
    @inline def rating  (p: Float) =            rating_=  (p)
    @inline def rating_=(p: Float) = { basis.setRating    (p); basis }

    @inline def stepSize = basis.getStepSize
    @inline def stepSize  (p: Float) =            stepSize_=  (p)
    @inline def stepSize_=(p: Float) = { basis.setStepSize    (p); basis }

  }

  class SRatingBar(implicit context: Context) extends RatingBar(context) with TraitRatingBar[SRatingBar] {
    def basis = this
  }

  object SRatingBar {
    def apply[LP <: ViewGroupLayoutParams[_, SRatingBar]]()(implicit context: Context, defaultLayoutParam: (SRatingBar) => LP): SRatingBar =  {
      val v = (new SRatingBar)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichAppWidgetHostView[V <: AppWidgetHostView](val basis: V) extends TraitAppWidgetHostView[V]
  @inline implicit def appWidgetHostView2RichAppWidgetHostView[V <: AppWidgetHostView](appWidgetHostView: V) = new RichAppWidgetHostView[V](appWidgetHostView)

  trait TraitAppWidgetHostView[V <: AppWidgetHostView] extends TraitFrameLayout[V] {

    @inline def appWidgetId = basis.getAppWidgetId

    @inline def appWidgetInfo = basis.getAppWidgetInfo

   }
	
  class RichHorizontalScrollView[V <: HorizontalScrollView](val basis: V) extends TraitHorizontalScrollView[V]
  @inline implicit def horizontalScrollView2RichHorizontalScrollView[V <: HorizontalScrollView](horizontalScrollView: V) = new RichHorizontalScrollView[V](horizontalScrollView)

  trait TraitHorizontalScrollView[V <: HorizontalScrollView] extends TraitFrameLayout[V] {

    @inline def fillViewport = basis.isFillViewport
    @inline def fillViewport  (p: Boolean) =            fillViewport_=  (p)
    @inline def fillViewport_=(p: Boolean) = { basis.setFillViewport    (p); basis }

    @inline def maxScrollAmount = basis.getMaxScrollAmount

    @inline def smoothScrollingEnabled = basis.isSmoothScrollingEnabled
    @inline def smoothScrollingEnabled  (p: Boolean) =            smoothScrollingEnabled_=  (p)
    @inline def smoothScrollingEnabled_=(p: Boolean) = { basis.setSmoothScrollingEnabled    (p); basis }
    @inline def  enableSmoothScrolling               = { basis.setSmoothScrollingEnabled(true ); basis }
    @inline def disableSmoothScrolling               = { basis.setSmoothScrollingEnabled(false); basis }

  }

  class SHorizontalScrollView(implicit context: Context) extends HorizontalScrollView(context) with TraitHorizontalScrollView[SHorizontalScrollView] {
    def basis = this
  }

  object SHorizontalScrollView {
    def apply[LP <: ViewGroupLayoutParams[_, SHorizontalScrollView]]()(implicit context: Context, defaultLayoutParam: (SHorizontalScrollView) => LP): SHorizontalScrollView =  {
      val v = (new SHorizontalScrollView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichMediaController[V <: MediaController](val basis: V) extends TraitMediaController[V]
  @inline implicit def mediaController2RichMediaController[V <: MediaController](mediaController: V) = new RichMediaController[V](mediaController)

  trait TraitMediaController[V <: MediaController] extends TraitFrameLayout[V] {

    @noEquivalentGetterExists
    @inline def anchorView    : android.view.View  = defaultValue[android.view.View]
    @inline def anchorView  (p: android.view.View) =            anchorView_=  (p)
    @inline def anchorView_=(p: android.view.View) = { basis.setAnchorView    (p); basis }

    @noEquivalentGetterExists
    @inline def mediaPlayer    : android.widget.MediaController.MediaPlayerControl  = defaultValue[android.widget.MediaController.MediaPlayerControl]
    @inline def mediaPlayer  (p: android.widget.MediaController.MediaPlayerControl) =            mediaPlayer_=  (p)
    @inline def mediaPlayer_=(p: android.widget.MediaController.MediaPlayerControl) = { basis.setMediaPlayer    (p); basis }

    @inline def showing = basis.isShowing

  }

  class SMediaController(implicit context: Context) extends MediaController(context) with TraitMediaController[SMediaController] {
    def basis = this
  }

  object SMediaController {
    def apply[LP <: ViewGroupLayoutParams[_, SMediaController]]()(implicit context: Context, defaultLayoutParam: (SMediaController) => LP): SMediaController =  {
      val v = (new SMediaController)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichScrollView[V <: ScrollView](val basis: V) extends TraitScrollView[V]
  @inline implicit def scrollView2RichScrollView[V <: ScrollView](scrollView: V) = new RichScrollView[V](scrollView)

  trait TraitScrollView[V <: ScrollView] extends TraitFrameLayout[V] {

    @inline def fillViewport = basis.isFillViewport
    @inline def fillViewport  (p: Boolean) =            fillViewport_=  (p)
    @inline def fillViewport_=(p: Boolean) = { basis.setFillViewport    (p); basis }

    @inline def maxScrollAmount = basis.getMaxScrollAmount

    @inline def smoothScrollingEnabled = basis.isSmoothScrollingEnabled
    @inline def smoothScrollingEnabled  (p: Boolean) =            smoothScrollingEnabled_=  (p)
    @inline def smoothScrollingEnabled_=(p: Boolean) = { basis.setSmoothScrollingEnabled    (p); basis }
    @inline def  enableSmoothScrolling               = { basis.setSmoothScrollingEnabled(true ); basis }
    @inline def disableSmoothScrolling               = { basis.setSmoothScrollingEnabled(false); basis }

  }

  class SScrollView(implicit context: Context) extends ScrollView(context) with TraitScrollView[SScrollView] {
    def basis = this
  }

  object SScrollView {
    def apply[LP <: ViewGroupLayoutParams[_, SScrollView]]()(implicit context: Context, defaultLayoutParam: (SScrollView) => LP): SScrollView =  {
      val v = (new SScrollView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichTabHost[V <: TabHost](val basis: V) extends TraitTabHost[V]
  @inline implicit def tabHost2RichTabHost[V <: TabHost](tabHost: V) = new RichTabHost[V](tabHost)

  trait TraitTabHost[V <: TabHost] extends TraitFrameLayout[V] {

    @inline def currentTab = basis.getCurrentTab
    @inline def currentTab  (p: Int) =            currentTab_=  (p)
    @inline def currentTab_=(p: Int) = { basis.setCurrentTab    (p); basis }

    @noEquivalentGetterExists
    @inline def currentTabByTag    : java.lang.String  = defaultValue[java.lang.String]
    @inline def currentTabByTag  (p: java.lang.String) =            currentTabByTag_=  (p)
    @inline def currentTabByTag_=(p: java.lang.String) = { basis.setCurrentTabByTag    (p); basis }

    @inline def currentTabTag = basis.getCurrentTabTag

    @inline def currentTabView = basis.getCurrentTabView

    @inline def currentView = basis.getCurrentView

    @noEquivalentGetterExists
    @inline def onTabChangedListener    : android.widget.TabHost.OnTabChangeListener  = defaultValue[android.widget.TabHost.OnTabChangeListener]
    @inline def onTabChangedListener  (p: android.widget.TabHost.OnTabChangeListener) =            onTabChangedListener_=  (p)
    @inline def onTabChangedListener_=(p: android.widget.TabHost.OnTabChangeListener) = { basis.setOnTabChangedListener    (p); basis }

    @inline def tabContentView = basis.getTabContentView

    @inline def tabWidget = basis.getTabWidget

    @noEquivalentGetterExists
    @inline def up    : android.app.LocalActivityManager  = defaultValue[android.app.LocalActivityManager]
    @inline def up  (p: android.app.LocalActivityManager) =            up_=  (p)
    @inline def up_=(p: android.app.LocalActivityManager) = { basis.setup    (p); basis }

  }

  class STabHost(implicit context: Context) extends TabHost(context) with TraitTabHost[STabHost] {
    def basis = this
  }

  object STabHost {
    def apply[LP <: ViewGroupLayoutParams[_, STabHost]]()(implicit context: Context, defaultLayoutParam: (STabHost) => LP): STabHost =  {
      val v = (new STabHost)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichTimePicker[V <: TimePicker](val basis: V) extends TraitTimePicker[V]
  @inline implicit def timePicker2RichTimePicker[V <: TimePicker](timePicker: V) = new RichTimePicker[V](timePicker)

  trait TraitTimePicker[V <: TimePicker] extends TraitFrameLayout[V] {

    @inline def currentHour = basis.getCurrentHour
    @inline def currentHour  (p: java.lang.Integer) =            currentHour_=  (p)
    @inline def currentHour_=(p: java.lang.Integer) = { basis.setCurrentHour    (p); basis }

    @inline def currentMinute = basis.getCurrentMinute
    @inline def currentMinute  (p: java.lang.Integer) =            currentMinute_=  (p)
    @inline def currentMinute_=(p: java.lang.Integer) = { basis.setCurrentMinute    (p); basis }

    @noEquivalentGetterExists
    @inline def onTimeChangedListener    : android.widget.TimePicker.OnTimeChangedListener  = defaultValue[android.widget.TimePicker.OnTimeChangedListener]
    @inline def onTimeChangedListener  (p: android.widget.TimePicker.OnTimeChangedListener) =            onTimeChangedListener_=  (p)
    @inline def onTimeChangedListener_=(p: android.widget.TimePicker.OnTimeChangedListener) = { basis.setOnTimeChangedListener    (p); basis }

  }

  class STimePicker(implicit context: Context) extends TimePicker(context) with TraitTimePicker[STimePicker] {
    def basis = this
  }

  object STimePicker {
    def apply[LP <: ViewGroupLayoutParams[_, STimePicker]]()(implicit context: Context, defaultLayoutParam: (STimePicker) => LP): STimePicker =  {
      val v = (new STimePicker)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichViewAnimator[V <: ViewAnimator](val basis: V) extends TraitViewAnimator[V]
  @inline implicit def viewAnimator2RichViewAnimator[V <: ViewAnimator](viewAnimator: V) = new RichViewAnimator[V](viewAnimator)

  trait TraitViewAnimator[V <: ViewAnimator] extends TraitFrameLayout[V] {

    @noEquivalentGetterExists
    @inline def animateFirstView    : Boolean  = defaultValue[Boolean]
    @inline def animateFirstView  (p: Boolean) =            animateFirstView_=  (p)
    @inline def animateFirstView_=(p: Boolean) = { basis.setAnimateFirstView    (p); basis }

    @inline def currentView = basis.getCurrentView

    @inline def displayedChild = basis.getDisplayedChild
    @inline def displayedChild  (p: Int) =            displayedChild_=  (p)
    @inline def displayedChild_=(p: Int) = { basis.setDisplayedChild    (p); basis }

    @inline def inAnimation = basis.getInAnimation
    @inline def inAnimation  (p: android.view.animation.Animation) =            inAnimation_=  (p)
    @inline def inAnimation_=(p: android.view.animation.Animation) = { basis.setInAnimation    (p); basis }

    @inline def outAnimation = basis.getOutAnimation
    @inline def outAnimation  (p: android.view.animation.Animation) =            outAnimation_=  (p)
    @inline def outAnimation_=(p: android.view.animation.Animation) = { basis.setOutAnimation    (p); basis }

  }

  class SViewAnimator(implicit context: Context) extends ViewAnimator(context) with TraitViewAnimator[SViewAnimator] {
    def basis = this
  }

  object SViewAnimator {
    def apply[LP <: ViewGroupLayoutParams[_, SViewAnimator]]()(implicit context: Context, defaultLayoutParam: (SViewAnimator) => LP): SViewAnimator =  {
      val v = (new SViewAnimator)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichViewFlipper[V <: ViewFlipper](val basis: V) extends TraitViewFlipper[V]
  @inline implicit def viewFlipper2RichViewFlipper[V <: ViewFlipper](viewFlipper: V) = new RichViewFlipper[V](viewFlipper)

  trait TraitViewFlipper[V <: ViewFlipper] extends TraitViewAnimator[V] {

    @inline def autoStart = basis.isAutoStart
    @inline def autoStart  (p: Boolean) =            autoStart_=  (p)
    @inline def autoStart_=(p: Boolean) = { basis.setAutoStart    (p); basis }

    @noEquivalentGetterExists
    @inline def flipInterval    : Int  = defaultValue[Int]
    @inline def flipInterval  (p: Int) =            flipInterval_=  (p)
    @inline def flipInterval_=(p: Int) = { basis.setFlipInterval    (p); basis }

    @inline def flipping = basis.isFlipping

  }

  class SViewFlipper(implicit context: Context) extends ViewFlipper(context) with TraitViewFlipper[SViewFlipper] {
    def basis = this
  }

  object SViewFlipper {
    def apply[LP <: ViewGroupLayoutParams[_, SViewFlipper]]()(implicit context: Context, defaultLayoutParam: (SViewFlipper) => LP): SViewFlipper =  {
      val v = (new SViewFlipper)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichViewSwitcher[V <: ViewSwitcher](val basis: V) extends TraitViewSwitcher[V]
  @inline implicit def viewSwitcher2RichViewSwitcher[V <: ViewSwitcher](viewSwitcher: V) = new RichViewSwitcher[V](viewSwitcher)

  trait TraitViewSwitcher[V <: ViewSwitcher] extends TraitViewAnimator[V] {

    @noEquivalentGetterExists
    @inline def factory    : android.widget.ViewSwitcher.ViewFactory  = defaultValue[android.widget.ViewSwitcher.ViewFactory]
    @inline def factory  (p: android.widget.ViewSwitcher.ViewFactory) =            factory_=  (p)
    @inline def factory_=(p: android.widget.ViewSwitcher.ViewFactory) = { basis.setFactory    (p); basis }

    @inline def nextView = basis.getNextView

  }

  class SViewSwitcher(implicit context: Context) extends ViewSwitcher(context) with TraitViewSwitcher[SViewSwitcher] {
    def basis = this
  }

  object SViewSwitcher {
    def apply[LP <: ViewGroupLayoutParams[_, SViewSwitcher]]()(implicit context: Context, defaultLayoutParam: (SViewSwitcher) => LP): SViewSwitcher =  {
      val v = (new SViewSwitcher)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichImageSwitcher[V <: ImageSwitcher](val basis: V) extends TraitImageSwitcher[V]
  @inline implicit def imageSwitcher2RichImageSwitcher[V <: ImageSwitcher](imageSwitcher: V) = new RichImageSwitcher[V](imageSwitcher)

  trait TraitImageSwitcher[V <: ImageSwitcher] extends TraitViewSwitcher[V] {

    @noEquivalentGetterExists
    @inline def imageDrawable    : android.graphics.drawable.Drawable  = defaultValue[android.graphics.drawable.Drawable]
    @inline def imageDrawable  (p: android.graphics.drawable.Drawable) =            imageDrawable_=  (p)
    @inline def imageDrawable_=(p: android.graphics.drawable.Drawable) = { basis.setImageDrawable    (p); basis }

    @noEquivalentGetterExists
    @inline def imageResource    : Int  = defaultValue[Int]
    @inline def imageResource  (p: Int) =            imageResource_=  (p)
    @inline def imageResource_=(p: Int) = { basis.setImageResource    (p); basis }

    @noEquivalentGetterExists
    @inline def imageURI    : android.net.Uri  = defaultValue[android.net.Uri]
    @inline def imageURI  (p: android.net.Uri) =            imageURI_=  (p)
    @inline def imageURI_=(p: android.net.Uri) = { basis.setImageURI    (p); basis }

  }

  class SImageSwitcher(implicit context: Context) extends ImageSwitcher(context) with TraitImageSwitcher[SImageSwitcher] {
    def basis = this
  }

  object SImageSwitcher {
    def apply[LP <: ViewGroupLayoutParams[_, SImageSwitcher]]()(implicit context: Context, defaultLayoutParam: (SImageSwitcher) => LP): SImageSwitcher =  {
      val v = (new SImageSwitcher)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichTextSwitcher[V <: TextSwitcher](val basis: V) extends TraitTextSwitcher[V]
  @inline implicit def textSwitcher2RichTextSwitcher[V <: TextSwitcher](textSwitcher: V) = new RichTextSwitcher[V](textSwitcher)

  trait TraitTextSwitcher[V <: TextSwitcher] extends TraitViewSwitcher[V] {

    @noEquivalentGetterExists
    @inline def currentText    : java.lang.CharSequence  = defaultValue[java.lang.CharSequence]
    @inline def currentText  (p: java.lang.CharSequence) =            currentText_=  (p)
    @inline def currentText_=(p: java.lang.CharSequence) = { basis.setCurrentText    (p); basis }

    @noEquivalentGetterExists
    @inline def text    : java.lang.CharSequence  = defaultValue[java.lang.CharSequence]
    @inline def text  (p: java.lang.CharSequence) =            text_=  (p)
    @inline def text_=(p: java.lang.CharSequence) = { basis.setText    (p); basis }

  }

  class STextSwitcher(implicit context: Context) extends TextSwitcher(context) with TraitTextSwitcher[STextSwitcher] {
    def basis = this
  }

  object STextSwitcher {
    def apply[LP <: ViewGroupLayoutParams[_, STextSwitcher]]()(implicit context: Context, defaultLayoutParam: (STextSwitcher) => LP): STextSwitcher =  {
      val v = (new STextSwitcher)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichDatePicker[V <: DatePicker](val basis: V) extends TraitDatePicker[V]
  @inline implicit def datePicker2RichDatePicker[V <: DatePicker](datePicker: V) = new RichDatePicker[V](datePicker)

  trait TraitDatePicker[V <: DatePicker] extends TraitFrameLayout[V] {

    @inline def dayOfMonth = basis.getDayOfMonth

    @inline def month = basis.getMonth

    @inline def year = basis.getYear

  }

  class SDatePicker(implicit context: Context) extends DatePicker(context) with TraitDatePicker[SDatePicker] {
    def basis = this
  }

  object SDatePicker {
    def apply[LP <: ViewGroupLayoutParams[_, SDatePicker]]()(implicit context: Context, defaultLayoutParam: (SDatePicker) => LP): SDatePicker =  {
      val v = (new SDatePicker)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

  class RichGestureOverlayView[V <: GestureOverlayView](val basis: V) extends TraitGestureOverlayView[V]
  @inline implicit def gestureOverlayView2RichGestureOverlayView[V <: GestureOverlayView](gestureOverlayView: V) = new RichGestureOverlayView[V](gestureOverlayView)

  trait TraitGestureOverlayView[V <: GestureOverlayView] extends TraitFrameLayout[V] {

    @inline def currentStroke = basis.getCurrentStroke

    @inline def eventsInterceptionEnabled = basis.isEventsInterceptionEnabled
    @inline def eventsInterceptionEnabled  (p: Boolean) =            eventsInterceptionEnabled_=  (p)
    @inline def eventsInterceptionEnabled_=(p: Boolean) = { basis.setEventsInterceptionEnabled    (p); basis }
    @inline def  enableEventsInterception               = { basis.setEventsInterceptionEnabled(true ); basis }
    @inline def disableEventsInterception               = { basis.setEventsInterceptionEnabled(false); basis }

    @inline def fadeEnabled = basis.isFadeEnabled
    @inline def fadeEnabled  (p: Boolean) =            fadeEnabled_=  (p)
    @inline def fadeEnabled_=(p: Boolean) = { basis.setFadeEnabled    (p); basis }
    @inline def  enableFade               = { basis.setFadeEnabled(true ); basis }
    @inline def disableFade               = { basis.setFadeEnabled(false); basis }

    @inline def fadeOffset = basis.getFadeOffset
    @inline def fadeOffset  (p: Long) =            fadeOffset_=  (p)
    @inline def fadeOffset_=(p: Long) = { basis.setFadeOffset    (p); basis }

    @inline def gesture = basis.getGesture
    @inline def gesture  (p: android.gesture.Gesture) =            gesture_=  (p)
    @inline def gesture_=(p: android.gesture.Gesture) = { basis.setGesture    (p); basis }

    @inline def gestureColor = basis.getGestureColor
    @inline def gestureColor  (p: Int) =            gestureColor_=  (p)
    @inline def gestureColor_=(p: Int) = { basis.setGestureColor    (p); basis }

    @inline def gesturePath = basis.getGesturePath

    @inline def gestureStrokeAngleThreshold = basis.getGestureStrokeAngleThreshold
    @inline def gestureStrokeAngleThreshold  (p: Float) =            gestureStrokeAngleThreshold_=  (p)
    @inline def gestureStrokeAngleThreshold_=(p: Float) = { basis.setGestureStrokeAngleThreshold    (p); basis }

    @inline def gestureStrokeLengthThreshold = basis.getGestureStrokeLengthThreshold
    @inline def gestureStrokeLengthThreshold  (p: Float) =            gestureStrokeLengthThreshold_=  (p)
    @inline def gestureStrokeLengthThreshold_=(p: Float) = { basis.setGestureStrokeLengthThreshold    (p); basis }

    @inline def gestureStrokeSquarenessTreshold = basis.getGestureStrokeSquarenessTreshold
    @inline def gestureStrokeSquarenessTreshold  (p: Float) =            gestureStrokeSquarenessTreshold_=  (p)
    @inline def gestureStrokeSquarenessTreshold_=(p: Float) = { basis.setGestureStrokeSquarenessTreshold    (p); basis }

    @inline def gestureStrokeType = basis.getGestureStrokeType
    @inline def gestureStrokeType  (p: Int) =            gestureStrokeType_=  (p)
    @inline def gestureStrokeType_=(p: Int) = { basis.setGestureStrokeType    (p); basis }

    @inline def gestureStrokeWidth = basis.getGestureStrokeWidth
    @inline def gestureStrokeWidth  (p: Float) =            gestureStrokeWidth_=  (p)
    @inline def gestureStrokeWidth_=(p: Float) = { basis.setGestureStrokeWidth    (p); basis }

    @inline def gestureVisible = basis.isGestureVisible
    @inline def gestureVisible  (p: Boolean) =            gestureVisible_=  (p)
    @inline def gestureVisible_=(p: Boolean) = { basis.setGestureVisible    (p); basis }

    @inline def gesturing = basis.isGesturing

    @inline def orientation = basis.getOrientation
    @inline def orientation  (p: Int) =            orientation_=  (p)
    @inline def orientation_=(p: Int) = { basis.setOrientation    (p); basis }

    @inline def uncertainGestureColor = basis.getUncertainGestureColor
    @inline def uncertainGestureColor  (p: Int) =            uncertainGestureColor_=  (p)
    @inline def uncertainGestureColor_=(p: Int) = { basis.setUncertainGestureColor    (p); basis }

  }

  class SGestureOverlayView(implicit context: Context) extends GestureOverlayView(context) with TraitGestureOverlayView[SGestureOverlayView] {
    def basis = this
  }

  object SGestureOverlayView {
    def apply[LP <: ViewGroupLayoutParams[_, SGestureOverlayView]]()(implicit context: Context, defaultLayoutParam: (SGestureOverlayView) => LP): SGestureOverlayView =  {
      val v = (new SGestureOverlayView)
      
      v.<<.parent.+=(v)
      v
    }

  }
  

}