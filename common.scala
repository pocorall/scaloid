
/*
 *
 *
 *
 *
 * Less painful Android development with Scala
 *
 *
 *
 *
 *
 * Scaloid version 0.8-SNAPSHOT
 * http://scaloid.org
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

package org.scaloid

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


case class LoggerTag(_tag: String) {
  private val MAX_TAG_LEN = 22
  val tag = if (_tag.length < MAX_TAG_LEN) _tag else ":" + _tag.substring(_tag.length - (MAX_TAG_LEN - 1), _tag.length)
}


package object common {

  @getter
  @beanGetter
  class noEquivalentGetterExists extends annotation.StaticAnnotation

  implicit def resourceIdToTextResource(id: Int)(implicit context: Context): CharSequence = context.getText(id)

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   */
  def openUri(uri: Uri)(implicit context: Context) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, uri))
  }

  def play(uri: Uri = notificationSound)(implicit context: Context) {
    val r = RingtoneManager.getRingtone(context, uri)
    if (r != null) {
      r.play()
    }
  }

implicit def func2ViewOnClickListener[F](f: (View) => F): View.OnClickListener =
  new View.OnClickListener() {
    def onClick(view: View) {
      f(view)
    }
  }

implicit def lazy2ViewOnClickListener[F](f: => F): View.OnClickListener =
  new View.OnClickListener() {
    def onClick(view: View) {
      f
    }
  }

def defaultValue[U]: U = {
  class Default[W] {
    var default: W = _
  }
  new Default[U].default
}

  trait ConstantsSupport {
    // android:inputType constants for TextView

    import android.text.InputType._

    val NONE = 0
    val TEXT = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL
    val TEXT_CAP_CHARACTERS = TYPE_TEXT_FLAG_CAP_CHARACTERS
    val TEXT_CAP_WORDS = TYPE_TEXT_FLAG_CAP_WORDS
    val TEXT_CAP_SENTENCES = TYPE_TEXT_FLAG_CAP_SENTENCES
    val TEXT_AUTO_CORRECT = TYPE_TEXT_FLAG_AUTO_CORRECT
    val TEXT_AUTO_COMPLETE = TYPE_TEXT_FLAG_AUTO_COMPLETE
    val TEXT_MULTI_LINE = TYPE_TEXT_FLAG_MULTI_LINE
    val TEXT_IME_MULTI_LINE = TYPE_TEXT_FLAG_IME_MULTI_LINE
    val TEXT_NO_SUGGESTIONS = TYPE_TEXT_FLAG_NO_SUGGESTIONS
    val TEXT_URI = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_URI
    val TEXT_EMAIL_ADDRESS = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    val TEXT_EMAIL_SUBJECT = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_SUBJECT
    val TEXT_SHORT_MESSAGE = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_SHORT_MESSAGE
    val TEXT_LONG_MESSAGE = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_LONG_MESSAGE
    val TEXT_PERSION_NAME = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME
    val TEXT_POSTAL_ADDRESS = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS
    val TEXT_PASSWORD = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
    // TODO: write more (http://developer.android.com/reference/android/widget/TextView.html#attr_android:inputType)
  }



  class RichView[V <: View](val base: V) extends TraitView[V]

  @inline implicit def view2RichView[V <: View](view: V) = new RichView[V](view)

  trait TraitView[V <: View] extends ConstantsSupport {

    @inline def onClick(f:  => Unit): V = {
      base.setOnClickListener(new OnClickListener {
        def onClick(p1: View): Unit = {
          f
        }
      })
      base
    }

    @inline def onClick(f: (View) => Unit): V = {
      base.setOnClickListener(new OnClickListener {
        def onClick(p1: View): Unit = {
          f(p1)
        }
      })
      base
    }

    @inline def onCreateContextMenu(f:  => Unit): V = {
      base.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(p1: ContextMenu, p2: View, p3: ContextMenuInfo): Unit = {
          f
        }
      })
      base
    }

    @inline def onCreateContextMenu(f: (ContextMenu, View, ContextMenuInfo) => Unit): V = {
      base.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(p1: ContextMenu, p2: View, p3: ContextMenuInfo): Unit = {
          f(p1, p2, p3)
        }
      })
      base
    }

    @inline def onFocusChange(f:  => Unit): V = {
      base.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(p1: View, p2: Boolean): Unit = {
          f
        }
      })
      base
    }

    @inline def onFocusChange(f: (View, Boolean) => Unit): V = {
      base.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(p1: View, p2: Boolean): Unit = {
          f(p1, p2)
        }
      })
      base
    }

    @inline def onKey(f:  => Boolean): V = {
      base.setOnKeyListener(new OnKeyListener {
        def onKey(p1: View, p2: Int, p3: KeyEvent): Boolean = {
          f
        }
      })
      base
    }

    @inline def onKey(f: (View, Int, KeyEvent) => Boolean): V = {
      base.setOnKeyListener(new OnKeyListener {
        def onKey(p1: View, p2: Int, p3: KeyEvent): Boolean = {
          f(p1, p2, p3)
        }
      })
      base
    }

    @inline def onLongClick(f:  => Boolean): V = {
      base.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(p1: View): Boolean = {
          f
        }
      })
      base
    }

    @inline def onLongClick(f: (View) => Boolean): V = {
      base.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(p1: View): Boolean = {
          f(p1)
        }
      })
      base
    }

    @inline def onTouch(f:  => Boolean): V = {
      base.setOnTouchListener(new OnTouchListener {
        def onTouch(p1: View, p2: MotionEvent): Boolean = {
          f
        }
      })
      base
    }

    @inline def onTouch(f: (View, MotionEvent) => Boolean): V = {
      base.setOnTouchListener(new OnTouchListener {
        def onTouch(p1: View, p2: MotionEvent): Boolean = {
          f(p1, p2)
        }
      })
      base
    }

    @inline def animation_=(p: android.view.animation.Animation) = {
      base.setAnimation(p)
      base
    }

    @inline def animation(p: android.view.animation.Animation) = animation_=(p)

    @inline def animation = base.getAnimation

    @inline def applicationWindowToken = base.getApplicationWindowToken

    @inline def background = base.getBackground

    @inline def backgroundColor_=(p: Int) = {
      base.setBackgroundColor(p)
      base
    }

    @inline def backgroundColor(p: Int) = backgroundColor_=(p)

    @noEquivalentGetterExists
    @inline def backgroundColor: Int = defaultValue[Int]

    @inline def backgroundDrawable_=(p: android.graphics.drawable.Drawable) = {
      base.setBackgroundDrawable(p)
      base
    }

    @inline def backgroundDrawable(p: android.graphics.drawable.Drawable) = backgroundDrawable_=(p)

    @noEquivalentGetterExists
    @inline def backgroundDrawable: android.graphics.drawable.Drawable = defaultValue[android.graphics.drawable.Drawable]

    @inline def backgroundResource_=(p: Int) = {
      base.setBackgroundResource(p)
      base
    }

    @inline def backgroundResource(p: Int) = backgroundResource_=(p)

    @noEquivalentGetterExists
    @inline def backgroundResource: Int = defaultValue[Int]

    @inline def baseline = base.getBaseline

    @inline def bottom = base.getBottom

    @inline def clickable_=(p: Boolean) = {
      base.setClickable(p)
      base
    }

    @inline def clickable(p: Boolean) = clickable_=(p)

    @inline def clickable = base.isClickable

    @inline def contentDescription_=(p: java.lang.CharSequence) = {
      base.setContentDescription(p)
      base
    }

    @inline def contentDescription(p: java.lang.CharSequence) = contentDescription_=(p)

    @inline def contentDescription = base.getContentDescription

    @inline def context = base.getContext

    @inline def drawableState = base.getDrawableState

    @inline def drawingCache = base.getDrawingCache

    @inline def drawingCacheBackgroundColor_=(p: Int) = {
      base.setDrawingCacheBackgroundColor(p)
      base
    }

    @inline def drawingCacheBackgroundColor(p: Int) = drawingCacheBackgroundColor_=(p)

    @inline def drawingCacheBackgroundColor = base.getDrawingCacheBackgroundColor

    @inline def enableDrawingCache = {base.setDrawingCacheEnabled(true); base}
    @inline def disableDrawingCache = {base.setDrawingCacheEnabled(false); base}
    @inline def drawingCacheEnabled_=(p: Boolean) = {
      base.setDrawingCacheEnabled(p)
      base
    }

    @inline def drawingCacheEnabled(p: Boolean) = drawingCacheEnabled_=(p)

    @inline def drawingCacheEnabled = base.isDrawingCacheEnabled

    @inline def drawingCacheQuality_=(p: Int) = {
      base.setDrawingCacheQuality(p)
      base
    }

    @inline def drawingCacheQuality(p: Int) = drawingCacheQuality_=(p)

    @inline def drawingCacheQuality = base.getDrawingCacheQuality

    @inline def drawingTime = base.getDrawingTime

    @inline def enableDuplicateParentState = {base.setDuplicateParentStateEnabled(true); base}
    @inline def disableDuplicateParentState = {base.setDuplicateParentStateEnabled(false); base}
    @inline def duplicateParentStateEnabled_=(p: Boolean) = {
      base.setDuplicateParentStateEnabled(p)
      base
    }

    @inline def duplicateParentStateEnabled(p: Boolean) = duplicateParentStateEnabled_=(p)

    @inline def duplicateParentStateEnabled = base.isDuplicateParentStateEnabled

    @inline def enabled_=(p: Boolean) = {
      base.setEnabled(p)
      base
    }

    @inline def enabled(p: Boolean) = enabled_=(p)

    @inline def enabled = base.isEnabled

    @inline def fadingEdgeLength_=(p: Int) = {
      base.setFadingEdgeLength(p)
      base
    }

    @inline def fadingEdgeLength(p: Int) = fadingEdgeLength_=(p)

    @noEquivalentGetterExists
    @inline def fadingEdgeLength: Int = defaultValue[Int]

    @inline def focusable_=(p: Boolean) = {
      base.setFocusable(p)
      base
    }

    @inline def focusable(p: Boolean) = focusable_=(p)

    @inline def focusable = base.isFocusable

    @inline def focusableInTouchMode_=(p: Boolean) = {
      base.setFocusableInTouchMode(p)
      base
    }

    @inline def focusableInTouchMode(p: Boolean) = focusableInTouchMode_=(p)

    @inline def focusableInTouchMode = base.isFocusableInTouchMode

    @inline def focused = base.isFocused

    @inline def handler = base.getHandler

    @inline def enableHapticFeedback = {base.setHapticFeedbackEnabled(true); base}
    @inline def disableHapticFeedback = {base.setHapticFeedbackEnabled(false); base}
    @inline def hapticFeedbackEnabled_=(p: Boolean) = {
      base.setHapticFeedbackEnabled(p)
      base
    }

    @inline def hapticFeedbackEnabled(p: Boolean) = hapticFeedbackEnabled_=(p)

    @inline def hapticFeedbackEnabled = base.isHapticFeedbackEnabled

    @inline def height = base.getHeight

    @inline def enableHorizontalFadingEdge = {base.setHorizontalFadingEdgeEnabled(true); base}
    @inline def disableHorizontalFadingEdge = {base.setHorizontalFadingEdgeEnabled(false); base}
    @inline def horizontalFadingEdgeEnabled_=(p: Boolean) = {
      base.setHorizontalFadingEdgeEnabled(p)
      base
    }

    @inline def horizontalFadingEdgeEnabled(p: Boolean) = horizontalFadingEdgeEnabled_=(p)

    @inline def horizontalFadingEdgeEnabled = base.isHorizontalFadingEdgeEnabled

    @inline def horizontalFadingEdgeLength = base.getHorizontalFadingEdgeLength

    @inline def enableHorizontalScrollBar = {base.setHorizontalScrollBarEnabled(true); base}
    @inline def disableHorizontalScrollBar = {base.setHorizontalScrollBarEnabled(false); base}
    @inline def horizontalScrollBarEnabled_=(p: Boolean) = {
      base.setHorizontalScrollBarEnabled(p)
      base
    }

    @inline def horizontalScrollBarEnabled(p: Boolean) = horizontalScrollBarEnabled_=(p)

    @inline def horizontalScrollBarEnabled = base.isHorizontalScrollBarEnabled

    @inline def id_=(p: Int) = {
      base.setId(p)
      base
    }

    @inline def id(p: Int) = id_=(p)

    @inline def id = base.getId

    @inline def inEditMode = base.isInEditMode

    @inline def inTouchMode = base.isInTouchMode

    @inline def keepScreenOn_=(p: Boolean) = {
      base.setKeepScreenOn(p)
      base
    }

    @inline def keepScreenOn(p: Boolean) = keepScreenOn_=(p)

    @inline def keepScreenOn = base.getKeepScreenOn

    @inline def keyDispatcherState = base.getKeyDispatcherState

    @inline def layoutParams_=(p: android.view.ViewGroup.LayoutParams) = {
      base.setLayoutParams(p)
      base
    }

    @inline def layoutParams(p: android.view.ViewGroup.LayoutParams) = layoutParams_=(p)

    @inline def layoutParams = base.getLayoutParams

    @inline def layoutRequested = base.isLayoutRequested

    @inline def left = base.getLeft

    @inline def longClickable_=(p: Boolean) = {
      base.setLongClickable(p)
      base
    }

    @inline def longClickable(p: Boolean) = longClickable_=(p)

    @inline def longClickable = base.isLongClickable

    @inline def measuredHeight = base.getMeasuredHeight

    @inline def measuredWidth = base.getMeasuredWidth

    @inline def minimumHeight_=(p: Int) = {
      base.setMinimumHeight(p)
      base
    }

    @inline def minimumHeight(p: Int) = minimumHeight_=(p)

    @noEquivalentGetterExists
    @inline def minimumHeight: Int = defaultValue[Int]

    @inline def minimumWidth_=(p: Int) = {
      base.setMinimumWidth(p)
      base
    }

    @inline def minimumWidth(p: Int) = minimumWidth_=(p)

    @noEquivalentGetterExists
    @inline def minimumWidth: Int = defaultValue[Int]

    @inline def nextFocusDownId_=(p: Int) = {
      base.setNextFocusDownId(p)
      base
    }

    @inline def nextFocusDownId(p: Int) = nextFocusDownId_=(p)

    @inline def nextFocusDownId = base.getNextFocusDownId

    @inline def nextFocusLeftId_=(p: Int) = {
      base.setNextFocusLeftId(p)
      base
    }

    @inline def nextFocusLeftId(p: Int) = nextFocusLeftId_=(p)

    @inline def nextFocusLeftId = base.getNextFocusLeftId

    @inline def nextFocusRightId_=(p: Int) = {
      base.setNextFocusRightId(p)
      base
    }

    @inline def nextFocusRightId(p: Int) = nextFocusRightId_=(p)

    @inline def nextFocusRightId = base.getNextFocusRightId

    @inline def nextFocusUpId_=(p: Int) = {
      base.setNextFocusUpId(p)
      base
    }

    @inline def nextFocusUpId(p: Int) = nextFocusUpId_=(p)

    @inline def nextFocusUpId = base.getNextFocusUpId

    @inline def onClickListener_=(p: android.view.View.OnClickListener) = {
      base.setOnClickListener(p)
      base
    }

    @inline def onClickListener(p: android.view.View.OnClickListener) = onClickListener_=(p)

    @noEquivalentGetterExists
    @inline def onClickListener: android.view.View.OnClickListener = defaultValue[android.view.View.OnClickListener]

    @inline def onCreateContextMenuListener_=(p: android.view.View.OnCreateContextMenuListener) = {
      base.setOnCreateContextMenuListener(p)
      base
    }

    @inline def onCreateContextMenuListener(p: android.view.View.OnCreateContextMenuListener) = onCreateContextMenuListener_=(p)

    @noEquivalentGetterExists
    @inline def onCreateContextMenuListener: android.view.View.OnCreateContextMenuListener = defaultValue[android.view.View.OnCreateContextMenuListener]

    @inline def onFocusChangeListener_=(p: android.view.View.OnFocusChangeListener) = {
      base.setOnFocusChangeListener(p)
      base
    }

    @inline def onFocusChangeListener(p: android.view.View.OnFocusChangeListener) = onFocusChangeListener_=(p)

    @inline def onFocusChangeListener = base.getOnFocusChangeListener

    @inline def onKeyListener_=(p: android.view.View.OnKeyListener) = {
      base.setOnKeyListener(p)
      base
    }

    @inline def onKeyListener(p: android.view.View.OnKeyListener) = onKeyListener_=(p)

    @noEquivalentGetterExists
    @inline def onKeyListener: android.view.View.OnKeyListener = defaultValue[android.view.View.OnKeyListener]

    @inline def onLongClickListener_=(p: android.view.View.OnLongClickListener) = {
      base.setOnLongClickListener(p)
      base
    }

    @inline def onLongClickListener(p: android.view.View.OnLongClickListener) = onLongClickListener_=(p)

    @noEquivalentGetterExists
    @inline def onLongClickListener: android.view.View.OnLongClickListener = defaultValue[android.view.View.OnLongClickListener]

    @inline def onTouchListener_=(p: android.view.View.OnTouchListener) = {
      base.setOnTouchListener(p)
      base
    }

    @inline def onTouchListener(p: android.view.View.OnTouchListener) = onTouchListener_=(p)

    @noEquivalentGetterExists
    @inline def onTouchListener: android.view.View.OnTouchListener = defaultValue[android.view.View.OnTouchListener]

    @inline def opaque = base.isOpaque

    @inline def paddingBottom = base.getPaddingBottom

    @inline def paddingLeft = base.getPaddingLeft

    @inline def paddingRight = base.getPaddingRight

    @inline def paddingTop = base.getPaddingTop

    @inline def parent = base.getParent

    @inline def pressed_=(p: Boolean) = {
      base.setPressed(p)
      base
    }

    @inline def pressed(p: Boolean) = pressed_=(p)

    @inline def pressed = base.isPressed

    @inline def resources = base.getResources

    @inline def right = base.getRight

    @inline def rootView = base.getRootView

    @inline def enableSave = {base.setSaveEnabled(true); base}
    @inline def disableSave = {base.setSaveEnabled(false); base}
    @inline def saveEnabled_=(p: Boolean) = {
      base.setSaveEnabled(p)
      base
    }

    @inline def saveEnabled(p: Boolean) = saveEnabled_=(p)

    @inline def saveEnabled = base.isSaveEnabled

    @inline def scrollBarStyle_=(p: Int) = {
      base.setScrollBarStyle(p)
      base
    }

    @inline def scrollBarStyle(p: Int) = scrollBarStyle_=(p)

    @inline def scrollBarStyle = base.getScrollBarStyle

    @inline def scrollContainer_=(p: Boolean) = {
      base.setScrollContainer(p)
      base
    }

    @inline def scrollContainer(p: Boolean) = scrollContainer_=(p)

    @noEquivalentGetterExists
    @inline def scrollContainer: Boolean = defaultValue[Boolean]

    @inline def scrollX = base.getScrollX

    @inline def scrollY = base.getScrollY

    @inline def enableScrollbarFading = {base.setScrollbarFadingEnabled(true); base}
    @inline def disableScrollbarFading = {base.setScrollbarFadingEnabled(false); base}
    @inline def scrollbarFadingEnabled_=(p: Boolean) = {
      base.setScrollbarFadingEnabled(p)
      base
    }

    @inline def scrollbarFadingEnabled(p: Boolean) = scrollbarFadingEnabled_=(p)

    @inline def scrollbarFadingEnabled = base.isScrollbarFadingEnabled

    @inline def selected_=(p: Boolean) = {
      base.setSelected(p)
      base
    }

    @inline def selected(p: Boolean) = selected_=(p)

    @inline def selected = base.isSelected

    @inline def shown = base.isShown

    @inline def solidColor = base.getSolidColor

    @inline def enableSoundEffects = {base.setSoundEffectsEnabled(true); base}
    @inline def disableSoundEffects = {base.setSoundEffectsEnabled(false); base}
    @inline def soundEffectsEnabled_=(p: Boolean) = {
      base.setSoundEffectsEnabled(p)
      base
    }

    @inline def soundEffectsEnabled(p: Boolean) = soundEffectsEnabled_=(p)

    @inline def soundEffectsEnabled = base.isSoundEffectsEnabled

    @inline def top = base.getTop

    @inline def touchDelegate_=(p: android.view.TouchDelegate) = {
      base.setTouchDelegate(p)
      base
    }

    @inline def touchDelegate(p: android.view.TouchDelegate) = touchDelegate_=(p)

    @inline def touchDelegate = base.getTouchDelegate

    @inline def touchables = base.getTouchables

    @inline def enableVerticalFadingEdge = {base.setVerticalFadingEdgeEnabled(true); base}
    @inline def disableVerticalFadingEdge = {base.setVerticalFadingEdgeEnabled(false); base}
    @inline def verticalFadingEdgeEnabled_=(p: Boolean) = {
      base.setVerticalFadingEdgeEnabled(p)
      base
    }

    @inline def verticalFadingEdgeEnabled(p: Boolean) = verticalFadingEdgeEnabled_=(p)

    @inline def verticalFadingEdgeEnabled = base.isVerticalFadingEdgeEnabled

    @inline def verticalFadingEdgeLength = base.getVerticalFadingEdgeLength

    @inline def enableVerticalScrollBar = {base.setVerticalScrollBarEnabled(true); base}
    @inline def disableVerticalScrollBar = {base.setVerticalScrollBarEnabled(false); base}
    @inline def verticalScrollBarEnabled_=(p: Boolean) = {
      base.setVerticalScrollBarEnabled(p)
      base
    }

    @inline def verticalScrollBarEnabled(p: Boolean) = verticalScrollBarEnabled_=(p)

    @inline def verticalScrollBarEnabled = base.isVerticalScrollBarEnabled

    @inline def verticalScrollbarWidth = base.getVerticalScrollbarWidth

    @inline def viewTreeObserver = base.getViewTreeObserver

    @inline def visibility_=(p: Int) = {
      base.setVisibility(p)
      base
    }

    @inline def visibility(p: Int) = visibility_=(p)

    @inline def visibility = base.getVisibility

    @inline def width = base.getWidth

    @inline def windowToken = base.getWindowToken

    @inline def windowVisibility = base.getWindowVisibility

    @inline def padding_=(p: Int) = {
      base.setPadding(p, p, p, p)
      base
    }

    @inline def padding(p: Int) = padding_=(p)

    @noEquivalentGetterExists
    @inline def padding: Int = 0

    def uniqueId(implicit activity:Activity):Int = {
      if(base.getId < 0) {
        base.setId(getUniqueId)
      }
      return base.getId
    }

    val FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT
    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

    def <<[LP <: ViewGroupLayoutParams[_,_]](implicit defaultLayoutParam: (V) => LP): LP = {
      defaultLayoutParam(base)
    }


    def <<[LP <: ViewGroupLayoutParams[_,_]](width:Int, height:Int)(implicit defaultLayoutParam: (V) => LP): LP = {
      val lp = defaultLayoutParam(base)
      lp.height = height
      lp.width = width
      lp
    }

    def base: V
  }

  class RichEditText[V <: EditText](val base: V) extends TraitEditText[V]

  @inline implicit def editText2RichEditText[V <: EditText](editText: V) = new RichEditText[V](editText)

  trait TraitEditText[V <: EditText] extends TraitTextView[V] {

  }

  class SEditText(implicit context: Context) extends EditText(context) with TraitEditText[SEditText] {
    def base = this
  }

  object SEditText {
    def apply[LP <: ViewGroupLayoutParams[_, SEditText]]()(implicit context: Context, defaultLayoutParam: (SEditText) => LP): SEditText =  {
    val view = (new SEditText)
    view.<<.parent.+=(view)
    view }
    def apply[LP <: ViewGroupLayoutParams[_, SEditText]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (SEditText) => LP): SEditText =  {
val view = new SEditText
view text txt
view.<<.parent.+=(view)
view
    }
  }

  val idSequence = new java.util.concurrent.atomic.AtomicInteger(0)

  def getUniqueId(implicit activity:Activity): Int = {
     var candidate:Int = 0
     do {
       candidate = idSequence.incrementAndGet
     } while(activity.findViewById(candidate) != null)
     candidate
  }

  class RichActivity[V <: Activity](val base: V) extends TraitActivity[V]

  @inline implicit def activity2RichActivity[V <: Activity](activity: V) = new RichActivity[V](activity)

  trait TraitActivity[V <: Activity] {

    @inline def contentView_=(p: View) = {
      base.setContentView(p)
      base
    }

    @inline def contentView(p: View) = contentView_=(p)

    @noEquivalentGetterExists
    @inline def contentView: View = null

    def base: Activity

    def find[V <: View](id: Int): V = base.findViewById(id).asInstanceOf[V]

    def runOnUiThread (f: => Unit)  {
      if(uiThread == Thread.currentThread) {
        f
      } else {
        handler.post(new Runnable() {
          def run() {
            f
          }
        })
      }
    }
  }

  trait SActivity extends Activity with SContext with TraitActivity[SActivity] {
    override implicit val ctx = this

    def base = this
  }

  class RichTextView[V <: TextView](val base: V) extends TraitTextView[V]

  @inline implicit def textView2RichTextView[V <: TextView](textView: V) = new RichTextView[V](textView)

  trait TraitTextView[V <: TextView] extends TraitView[V] {

    @inline def beforeTextChanged(f:  => Unit): V = {
      base.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
          f
        }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def afterTextChanged(p1: Editable): Unit = {
        }
      })
      base
    }

    @inline def onTextChanged(f:  => Unit): V = {
      base.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
          f
        }
        def afterTextChanged(p1: Editable): Unit = {
        }
      })
      base
    }

    @inline def afterTextChanged(f:  => Unit): V = {
      base.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def afterTextChanged(p1: Editable): Unit = {
          f
        }
      })
      base
    }

    @inline def beforeTextChanged(f: (CharSequence, Int, Int, Int) => Unit): V = {
      base.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
          f(p1, p2, p3, p4)
        }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def afterTextChanged(p1: Editable): Unit = {
        }
      })
      base
    }

    @inline def onTextChanged(f: (CharSequence, Int, Int, Int) => Unit): V = {
      base.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
          f(p1, p2, p3, p4)
        }
        def afterTextChanged(p1: Editable): Unit = {
        }
      })
      base
    }

    @inline def afterTextChanged(f: (Editable) => Unit): V = {
      base.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int): Unit = {
        }
        def afterTextChanged(p1: Editable): Unit = {
          f(p1)
        }
      })
      base
    }

    @inline def onEditorAction(f:  => Boolean): V = {
      base.setOnEditorActionListener(new OnEditorActionListener {
        def onEditorAction(p1: TextView, p2: Int, p3: KeyEvent): Boolean = {
          f
        }
      })
      base
    }

    @inline def onEditorAction(f: (TextView, Int, KeyEvent) => Boolean): V = {
      base.setOnEditorActionListener(new OnEditorActionListener {
        def onEditorAction(p1: TextView, p2: Int, p3: KeyEvent): Boolean = {
          f(p1, p2, p3)
        }
      })
      base
    }

    @inline def autoLinkMask_=(p: Int) = {
      base.setAutoLinkMask(p)
      base
    }

    @inline def autoLinkMask(p: Int) = autoLinkMask_=(p)

    @inline def autoLinkMask = base.getAutoLinkMask

    @inline def compoundDrawablePadding_=(p: Int) = {
      base.setCompoundDrawablePadding(p)
      base
    }

    @inline def compoundDrawablePadding(p: Int) = compoundDrawablePadding_=(p)

    @inline def compoundDrawablePadding = base.getCompoundDrawablePadding

    @inline def compoundDrawables = base.getCompoundDrawables

    @inline def compoundPaddingBottom = base.getCompoundPaddingBottom

    @inline def compoundPaddingLeft = base.getCompoundPaddingLeft

    @inline def compoundPaddingRight = base.getCompoundPaddingRight

    @inline def compoundPaddingTop = base.getCompoundPaddingTop

    @inline def currentHintTextColor = base.getCurrentHintTextColor

    @inline def currentTextColor = base.getCurrentTextColor

    @inline def cursorVisible_=(p: Boolean) = {
      base.setCursorVisible(p)
      base
    }

    @inline def cursorVisible(p: Boolean) = cursorVisible_=(p)

    @noEquivalentGetterExists
    @inline def cursorVisible: Boolean = defaultValue[Boolean]

    @inline def editableFactory_=(p: android.text.Editable.Factory) = {
      base.setEditableFactory(p)
      base
    }

    @inline def editableFactory(p: android.text.Editable.Factory) = editableFactory_=(p)

    @noEquivalentGetterExists
    @inline def editableFactory: android.text.Editable.Factory = defaultValue[android.text.Editable.Factory]

    @inline def editableText = base.getEditableText

    @inline def ellipsize_=(p: android.text.TextUtils.TruncateAt) = {
      base.setEllipsize(p)
      base
    }

    @inline def ellipsize(p: android.text.TextUtils.TruncateAt) = ellipsize_=(p)

    @inline def ellipsize = base.getEllipsize

    @inline def ems_=(p: Int) = {
      base.setEms(p)
      base
    }

    @inline def ems(p: Int) = ems_=(p)

    @noEquivalentGetterExists
    @inline def ems: Int = defaultValue[Int]

    @inline def error_=(p: java.lang.CharSequence) = {
      base.setError(p)
      base
    }

    @inline def error(p: java.lang.CharSequence) = error_=(p)

    @inline def error = base.getError

    @inline def extendedPaddingBottom = base.getExtendedPaddingBottom

    @inline def extendedPaddingTop = base.getExtendedPaddingTop

    @inline def extractedText_=(p: android.view.inputmethod.ExtractedText) = {
      base.setExtractedText(p)
      base
    }

    @inline def extractedText(p: android.view.inputmethod.ExtractedText) = extractedText_=(p)

    @noEquivalentGetterExists
    @inline def extractedText: android.view.inputmethod.ExtractedText = defaultValue[android.view.inputmethod.ExtractedText]

    @inline def filters_=(p: Array[android.text.InputFilter]) = {
      base.setFilters(p)
      base
    }

    @inline def filters(p: Array[android.text.InputFilter]) = filters_=(p)

    @inline def filters = base.getFilters

    @inline def freezesText_=(p: Boolean) = {
      base.setFreezesText(p)
      base
    }

    @inline def freezesText(p: Boolean) = freezesText_=(p)

    @inline def freezesText = base.getFreezesText

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @inline def gravity = base.getGravity

    @inline def highlightColor_=(p: Int) = {
      base.setHighlightColor(p)
      base
    }

    @inline def highlightColor(p: Int) = highlightColor_=(p)

    @noEquivalentGetterExists
    @inline def highlightColor: Int = defaultValue[Int]

    @inline def hint_=(p: java.lang.CharSequence) = {
      base.setHint(p)
      base
    }

    @inline def hint(p: java.lang.CharSequence) = hint_=(p)

    @inline def hint = base.getHint

    @inline def hintTextColor_=(p: android.content.res.ColorStateList) = {
      base.setHintTextColor(p)
      base
    }

    @inline def hintTextColor(p: android.content.res.ColorStateList) = hintTextColor_=(p)

    @noEquivalentGetterExists
    @inline def hintTextColor: android.content.res.ColorStateList = defaultValue[android.content.res.ColorStateList]

    @inline def hintTextColors = base.getHintTextColors

    @inline def horizontallyScrolling_=(p: Boolean) = {
      base.setHorizontallyScrolling(p)
      base
    }

    @inline def horizontallyScrolling(p: Boolean) = horizontallyScrolling_=(p)

    @noEquivalentGetterExists
    @inline def horizontallyScrolling: Boolean = defaultValue[Boolean]

    @inline def imeActionId = base.getImeActionId

    @inline def imeActionLabel = base.getImeActionLabel

    @inline def imeOptions_=(p: Int) = {
      base.setImeOptions(p)
      base
    }

    @inline def imeOptions(p: Int) = imeOptions_=(p)

    @inline def imeOptions = base.getImeOptions

    @inline def includeFontPadding_=(p: Boolean) = {
      base.setIncludeFontPadding(p)
      base
    }

    @inline def includeFontPadding(p: Boolean) = includeFontPadding_=(p)

    @noEquivalentGetterExists
    @inline def includeFontPadding: Boolean = defaultValue[Boolean]

    @inline def inputExtras_=(p: Int) = {
      base.setInputExtras(p)
      base
    }

    @inline def inputExtras(p: Int) = inputExtras_=(p)

    @noEquivalentGetterExists
    @inline def inputExtras: Int = defaultValue[Int]

    @inline def inputMethodTarget = base.isInputMethodTarget

    @inline def inputType_=(p: Int) = {
      base.setInputType(p)
      base
    }

    @inline def inputType(p: Int) = inputType_=(p)

    @inline def inputType = base.getInputType

    @inline def keyListener_=(p: android.text.method.KeyListener) = {
      base.setKeyListener(p)
      base
    }

    @inline def keyListener(p: android.text.method.KeyListener) = keyListener_=(p)

    @inline def keyListener = base.getKeyListener

    @inline def lineCount = base.getLineCount

    @inline def lineHeight = base.getLineHeight

    @inline def lines_=(p: Int) = {
      base.setLines(p)
      base
    }

    @inline def lines(p: Int) = lines_=(p)

    @noEquivalentGetterExists
    @inline def lines: Int = defaultValue[Int]

    @inline def linkTextColor_=(p: Int) = {
      base.setLinkTextColor(p)
      base
    }

    @inline def linkTextColor(p: Int) = linkTextColor_=(p)

    @noEquivalentGetterExists
    @inline def linkTextColor: Int = defaultValue[Int]

    @inline def linkTextColors = base.getLinkTextColors

    @inline def linksClickable_=(p: Boolean) = {
      base.setLinksClickable(p)
      base
    }

    @inline def linksClickable(p: Boolean) = linksClickable_=(p)

    @inline def linksClickable = base.getLinksClickable

    @inline def marqueeRepeatLimit_=(p: Int) = {
      base.setMarqueeRepeatLimit(p)
      base
    }

    @inline def marqueeRepeatLimit(p: Int) = marqueeRepeatLimit_=(p)

    @noEquivalentGetterExists
    @inline def marqueeRepeatLimit: Int = defaultValue[Int]

    @inline def maxEms_=(p: Int) = {
      base.setMaxEms(p)
      base
    }

    @inline def maxEms(p: Int) = maxEms_=(p)

    @noEquivalentGetterExists
    @inline def maxEms: Int = defaultValue[Int]

    @inline def maxHeight_=(p: Int) = {
      base.setMaxHeight(p)
      base
    }

    @inline def maxHeight(p: Int) = maxHeight_=(p)

    @noEquivalentGetterExists
    @inline def maxHeight: Int = defaultValue[Int]

    @inline def maxLines_=(p: Int) = {
      base.setMaxLines(p)
      base
    }

    @inline def maxLines(p: Int) = maxLines_=(p)

    @noEquivalentGetterExists
    @inline def maxLines: Int = defaultValue[Int]

    @inline def maxWidth_=(p: Int) = {
      base.setMaxWidth(p)
      base
    }

    @inline def maxWidth(p: Int) = maxWidth_=(p)

    @noEquivalentGetterExists
    @inline def maxWidth: Int = defaultValue[Int]

    @inline def minEms_=(p: Int) = {
      base.setMinEms(p)
      base
    }

    @inline def minEms(p: Int) = minEms_=(p)

    @noEquivalentGetterExists
    @inline def minEms: Int = defaultValue[Int]

    @inline def minHeight_=(p: Int) = {
      base.setMinHeight(p)
      base
    }

    @inline def minHeight(p: Int) = minHeight_=(p)

    @noEquivalentGetterExists
    @inline def minHeight: Int = defaultValue[Int]

    @inline def minLines_=(p: Int) = {
      base.setMinLines(p)
      base
    }

    @inline def minLines(p: Int) = minLines_=(p)

    @noEquivalentGetterExists
    @inline def minLines: Int = defaultValue[Int]

    @inline def minWidth_=(p: Int) = {
      base.setMinWidth(p)
      base
    }

    @inline def minWidth(p: Int) = minWidth_=(p)

    @noEquivalentGetterExists
    @inline def minWidth: Int = defaultValue[Int]

    @inline def movementMethod_=(p: android.text.method.MovementMethod) = {
      base.setMovementMethod(p)
      base
    }

    @inline def movementMethod(p: android.text.method.MovementMethod) = movementMethod_=(p)

    @inline def movementMethod = base.getMovementMethod

    @inline def onEditorActionListener_=(p: android.widget.TextView.OnEditorActionListener) = {
      base.setOnEditorActionListener(p)
      base
    }

    @inline def onEditorActionListener(p: android.widget.TextView.OnEditorActionListener) = onEditorActionListener_=(p)

    @noEquivalentGetterExists
    @inline def onEditorActionListener: android.widget.TextView.OnEditorActionListener = defaultValue[android.widget.TextView.OnEditorActionListener]

    @inline def paint = base.getPaint

    @inline def paintFlags_=(p: Int) = {
      base.setPaintFlags(p)
      base
    }

    @inline def paintFlags(p: Int) = paintFlags_=(p)

    @inline def paintFlags = base.getPaintFlags

    @inline def privateImeOptions_=(p: java.lang.String) = {
      base.setPrivateImeOptions(p)
      base
    }

    @inline def privateImeOptions(p: java.lang.String) = privateImeOptions_=(p)

    @inline def privateImeOptions = base.getPrivateImeOptions

    @inline def rawInputType_=(p: Int) = {
      base.setRawInputType(p)
      base
    }

    @inline def rawInputType(p: Int) = rawInputType_=(p)

    @noEquivalentGetterExists
    @inline def rawInputType: Int = defaultValue[Int]

    @inline def scroller_=(p: android.widget.Scroller) = {
      base.setScroller(p)
      base
    }

    @inline def scroller(p: android.widget.Scroller) = scroller_=(p)

    @noEquivalentGetterExists
    @inline def scroller: android.widget.Scroller = defaultValue[android.widget.Scroller]

    @inline def selectAllOnFocus_=(p: Boolean) = {
      base.setSelectAllOnFocus(p)
      base
    }

    @inline def selectAllOnFocus(p: Boolean) = selectAllOnFocus_=(p)

    @noEquivalentGetterExists
    @inline def selectAllOnFocus: Boolean = defaultValue[Boolean]

    @inline def selectionEnd = base.getSelectionEnd

    @inline def selectionStart = base.getSelectionStart

    @inline def singleLine_=(p: Boolean) = {
      base.setSingleLine(p)
      base
    }

    @inline def singleLine(p: Boolean) = singleLine_=(p)

    @noEquivalentGetterExists
    @inline def singleLine: Boolean = defaultValue[Boolean]

    @inline def spannableFactory_=(p: android.text.Spannable.Factory) = {
      base.setSpannableFactory(p)
      base
    }

    @inline def spannableFactory(p: android.text.Spannable.Factory) = spannableFactory_=(p)

    @noEquivalentGetterExists
    @inline def spannableFactory: android.text.Spannable.Factory = defaultValue[android.text.Spannable.Factory]

    @inline def text_=(p: java.lang.CharSequence) = {
      base.setText(p)
      base
    }

    @inline def text(p: java.lang.CharSequence) = text_=(p)

    @inline def text = base.getText

    @inline def textColor_=(p: Int) = {
      base.setTextColor(p)
      base
    }

    @inline def textColor(p: Int) = textColor_=(p)

    @noEquivalentGetterExists
    @inline def textColor: Int = defaultValue[Int]

    @inline def textColors = base.getTextColors

    @inline def textKeepState_=(p: java.lang.CharSequence) = {
      base.setTextKeepState(p)
      base
    }

    @inline def textKeepState(p: java.lang.CharSequence) = textKeepState_=(p)

    @noEquivalentGetterExists
    @inline def textKeepState: java.lang.CharSequence = defaultValue[java.lang.CharSequence]

    @inline def textScaleX_=(p: Float) = {
      base.setTextScaleX(p)
      base
    }

    @inline def textScaleX(p: Float) = textScaleX_=(p)

    @inline def textScaleX = base.getTextScaleX

    @inline def textSize_=(p: Float) = {
      base.setTextSize(p)
      base
    }

    @inline def textSize(p: Float) = textSize_=(p)

    @inline def textSize = base.getTextSize

    @inline def totalPaddingBottom = base.getTotalPaddingBottom

    @inline def totalPaddingLeft = base.getTotalPaddingLeft

    @inline def totalPaddingRight = base.getTotalPaddingRight

    @inline def totalPaddingTop = base.getTotalPaddingTop

    @inline def transformationMethod_=(p: android.text.method.TransformationMethod) = {
      base.setTransformationMethod(p)
      base
    }

    @inline def transformationMethod(p: android.text.method.TransformationMethod) = transformationMethod_=(p)

    @inline def transformationMethod = base.getTransformationMethod

    @inline def typeface_=(p: android.graphics.Typeface) = {
      base.setTypeface(p)
      base
    }

    @inline def typeface(p: android.graphics.Typeface) = typeface_=(p)

    @inline def typeface = base.getTypeface

    @inline def urls = base.getUrls


  }

  class STextView(implicit context: Context) extends TextView(context) with TraitTextView[STextView] {
    def base = this
  }

  object STextView {
    def apply[LP <: ViewGroupLayoutParams[_, STextView]]()(implicit context: Context, defaultLayoutParam: (STextView) => LP): STextView =  {
    val view = (new STextView)
    view.<<.parent.+=(view)
    view }
    def apply[LP <: ViewGroupLayoutParams[_, STextView]](txt: CharSequence)(implicit context: Context, defaultLayoutParam: (STextView) => LP): STextView =  {
val view = new STextView
view text txt
view.<<.parent.+=(view)
view
    }
  }


  class RichMenu(menu: Menu) {
    @inline def +=(txt: CharSequence) = menu.add(txt)
  }

  @inline implicit def menu2RichMenu(menu: Menu) = new RichMenu(menu)


  class RichContextMenu(base: ContextMenu) {
    @inline def headerTitle_=(p: CharSequence) = {
      base.setHeaderTitle(p)
      base
    }

    @inline def headerTitle(p: CharSequence) = headerTitle_=(p)

    @noEquivalentGetterExists
    @inline def headerTitle: CharSequence = ""

  }

  @inline implicit def contextMenu2RichContextMenu(menu: ContextMenu) = new RichContextMenu(menu)


  trait TraitAbsListView[V <: AbsListView] extends TraitView[V] {
    @inline def cacheColorHint_=(p: Int) = {
      base.setCacheColorHint(p)
      base
    }

    @inline def cacheColorHint(p: Int) = cacheColorHint_=(p)

    @inline def cacheColorHint = base.getCacheColorHint

    @inline def drawSelectorOnTop_=(p: Boolean) = {
      base.setDrawSelectorOnTop(p)
      base
    }

    @inline def drawSelectorOnTop(p: Boolean) = drawSelectorOnTop_=(p)

    @noEquivalentGetterExists
    @inline def drawSelectorOnTop: Boolean = defaultValue[Boolean]

    @inline def enableFastScroll = {base.setFastScrollEnabled(true); base}
    @inline def disableFastScroll = {base.setFastScrollEnabled(false); base}
    @inline def fastScrollEnabled_=(p: Boolean) = {
      base.setFastScrollEnabled(p)
      base
    }

    @inline def fastScrollEnabled(p: Boolean) = fastScrollEnabled_=(p)

    @inline def fastScrollEnabled = base.isFastScrollEnabled

    @inline def filterText_=(p: java.lang.String) = {
      base.setFilterText(p)
      base
    }

    @inline def filterText(p: java.lang.String) = filterText_=(p)

    @noEquivalentGetterExists
    @inline def filterText: java.lang.String = defaultValue[java.lang.String]

    @inline def listPaddingBottom = base.getListPaddingBottom

    @inline def listPaddingLeft = base.getListPaddingLeft

    @inline def listPaddingRight = base.getListPaddingRight

    @inline def listPaddingTop = base.getListPaddingTop

    @inline def onScrollListener_=(p: android.widget.AbsListView.OnScrollListener) = {
      base.setOnScrollListener(p)
      base
    }

    @inline def onScrollListener(p: android.widget.AbsListView.OnScrollListener) = onScrollListener_=(p)

    @noEquivalentGetterExists
    @inline def onScrollListener: android.widget.AbsListView.OnScrollListener = defaultValue[android.widget.AbsListView.OnScrollListener]

    @inline def recyclerListener_=(p: android.widget.AbsListView.RecyclerListener) = {
      base.setRecyclerListener(p)
      base
    }

    @inline def recyclerListener(p: android.widget.AbsListView.RecyclerListener) = recyclerListener_=(p)

    @noEquivalentGetterExists
    @inline def recyclerListener: android.widget.AbsListView.RecyclerListener = defaultValue[android.widget.AbsListView.RecyclerListener]

    @inline def enableScrollingCache = {base.setScrollingCacheEnabled(true); base}
    @inline def disableScrollingCache = {base.setScrollingCacheEnabled(false); base}
    @inline def scrollingCacheEnabled_=(p: Boolean) = {
      base.setScrollingCacheEnabled(p)
      base
    }

    @inline def scrollingCacheEnabled(p: Boolean) = scrollingCacheEnabled_=(p)

    @inline def scrollingCacheEnabled = base.isScrollingCacheEnabled

    @inline def selector_=(p: android.graphics.drawable.Drawable) = {
      base.setSelector(p)
      base
    }

    @inline def selector(p: android.graphics.drawable.Drawable) = selector_=(p)

    @inline def selector = base.getSelector

    @inline def enableSmoothScrollbar = {base.setSmoothScrollbarEnabled(true); base}
    @inline def disableSmoothScrollbar = {base.setSmoothScrollbarEnabled(false); base}
    @inline def smoothScrollbarEnabled_=(p: Boolean) = {
      base.setSmoothScrollbarEnabled(p)
      base
    }

    @inline def smoothScrollbarEnabled(p: Boolean) = smoothScrollbarEnabled_=(p)

    @inline def smoothScrollbarEnabled = base.isSmoothScrollbarEnabled

    @inline def stackFromBottom_=(p: Boolean) = {
      base.setStackFromBottom(p)
      base
    }

    @inline def stackFromBottom(p: Boolean) = stackFromBottom_=(p)

    @inline def stackFromBottom = base.isStackFromBottom

    @inline def textFilter = base.getTextFilter

    @inline def enableTextFilter = {base.setTextFilterEnabled(true); base}
    @inline def disableTextFilter = {base.setTextFilterEnabled(false); base}
    @inline def textFilterEnabled_=(p: Boolean) = {
      base.setTextFilterEnabled(p)
      base
    }

    @inline def textFilterEnabled(p: Boolean) = textFilterEnabled_=(p)

    @inline def textFilterEnabled = base.isTextFilterEnabled

    @inline def transcriptMode_=(p: Int) = {
      base.setTranscriptMode(p)
      base
    }

    @inline def transcriptMode(p: Int) = transcriptMode_=(p)

    @inline def transcriptMode = base.getTranscriptMode


  }

  class UnitConversion(val ext: Double)(implicit context: Context) {
    def dip: Int = (ext * context.getResources().getDisplayMetrics().density).toInt

    def sp: Int = (ext * context.getResources().getDisplayMetrics().scaledDensity).toInt
  }

  @inline implicit def Double2unitConversion(ext: Double)(implicit context: Context): UnitConversion = new UnitConversion(ext)(context)

  @inline implicit def Long2unitConversion(ext: Long)(implicit context: Context): UnitConversion = new UnitConversion(ext)(context)

  @inline implicit def Int2unitConversion(ext: Int)(implicit context: Context): UnitConversion = new UnitConversion(ext)(context)

  class RichListView[V <: ListView](val base: V) extends TraitListView[V]

  @inline implicit def listView2RichListView[V <: ListView](listView: V) = new RichListView[V](listView)

  trait TraitListView[V <: ListView] extends TraitAbsListView[V] {

    @inline def checkItemIds = base.getCheckItemIds

    @inline def checkedItemIds = base.getCheckedItemIds

    @inline def checkedItemPosition = base.getCheckedItemPosition

    @inline def checkedItemPositions = base.getCheckedItemPositions

    @inline def choiceMode_=(p: Int) = {
      base.setChoiceMode(p)
      base
    }

    @inline def choiceMode(p: Int) = choiceMode_=(p)

    @inline def choiceMode = base.getChoiceMode

    @inline def divider_=(p: android.graphics.drawable.Drawable) = {
      base.setDivider(p)
      base
    }

    @inline def divider(p: android.graphics.drawable.Drawable) = divider_=(p)

    @inline def divider = base.getDivider

    @inline def dividerHeight_=(p: Int) = {
      base.setDividerHeight(p)
      base
    }

    @inline def dividerHeight(p: Int) = dividerHeight_=(p)

    @inline def dividerHeight = base.getDividerHeight

    @inline def enableFooterDividers = {base.setFooterDividersEnabled(true); base}
    @inline def disableFooterDividers = {base.setFooterDividersEnabled(false); base}
    @inline def footerDividersEnabled_=(p: Boolean) = {
      base.setFooterDividersEnabled(p)
      base
    }

    @inline def footerDividersEnabled(p: Boolean) = footerDividersEnabled_=(p)

    @noEquivalentGetterExists
    @inline def footerDividersEnabled: Boolean = defaultValue[Boolean]

    @inline def footerViewsCount = base.getFooterViewsCount

    @inline def enableHeaderDividers = {base.setHeaderDividersEnabled(true); base}
    @inline def disableHeaderDividers = {base.setHeaderDividersEnabled(false); base}
    @inline def headerDividersEnabled_=(p: Boolean) = {
      base.setHeaderDividersEnabled(p)
      base
    }

    @inline def headerDividersEnabled(p: Boolean) = headerDividersEnabled_=(p)

    @noEquivalentGetterExists
    @inline def headerDividersEnabled: Boolean = defaultValue[Boolean]

    @inline def headerViewsCount = base.getHeaderViewsCount

    @inline def itemsCanFocus_=(p: Boolean) = {
      base.setItemsCanFocus(p)
      base
    }

    @inline def itemsCanFocus(p: Boolean) = itemsCanFocus_=(p)

    @inline def itemsCanFocus = base.getItemsCanFocus

    @inline def maxScrollAmount = base.getMaxScrollAmount

  }

  class SListView(implicit context: Context) extends ListView(context) with TraitListView[SListView] {
    def base = this
  }

  object SListView {
    def apply[LP <: ViewGroupLayoutParams[_, SListView]]()(implicit context: Context, defaultLayoutParam: (SListView) => LP): SListView =  {
    val view = (new SListView)
    view.<<.parent.+=(view)
    view }
  }

  class RichViewGroup[V <: ViewGroup](val base: V) extends TraitViewGroup[V]

  @inline implicit def viewGroup2RichViewGroup[V <: ViewGroup](viewGroup: V) = new RichViewGroup[V](viewGroup)

  trait TraitViewGroup[V <: ViewGroup] extends TraitView[V] {

    @inline def enableAlwaysDrawnWithCache = {base.setAlwaysDrawnWithCacheEnabled(true); base}
    @inline def disableAlwaysDrawnWithCache = {base.setAlwaysDrawnWithCacheEnabled(false); base}
    @inline def alwaysDrawnWithCacheEnabled_=(p: Boolean) = {
      base.setAlwaysDrawnWithCacheEnabled(p)
      base
    }

    @inline def alwaysDrawnWithCacheEnabled(p: Boolean) = alwaysDrawnWithCacheEnabled_=(p)

    @inline def alwaysDrawnWithCacheEnabled = base.isAlwaysDrawnWithCacheEnabled

    @inline def enableAnimationCache = {base.setAnimationCacheEnabled(true); base}
    @inline def disableAnimationCache = {base.setAnimationCacheEnabled(false); base}
    @inline def animationCacheEnabled_=(p: Boolean) = {
      base.setAnimationCacheEnabled(p)
      base
    }

    @inline def animationCacheEnabled(p: Boolean) = animationCacheEnabled_=(p)

    @inline def animationCacheEnabled = base.isAnimationCacheEnabled

    @inline def childCount = base.getChildCount

    @inline def clipChildren_=(p: Boolean) = {
      base.setClipChildren(p)
      base
    }

    @inline def clipChildren(p: Boolean) = clipChildren_=(p)

    @noEquivalentGetterExists
    @inline def clipChildren: Boolean = defaultValue[Boolean]

    @inline def clipToPadding_=(p: Boolean) = {
      base.setClipToPadding(p)
      base
    }

    @inline def clipToPadding(p: Boolean) = clipToPadding_=(p)

    @noEquivalentGetterExists
    @inline def clipToPadding: Boolean = defaultValue[Boolean]

    @inline def descendantFocusability_=(p: Int) = {
      base.setDescendantFocusability(p)
      base
    }

    @inline def descendantFocusability(p: Int) = descendantFocusability_=(p)

    @inline def descendantFocusability = base.getDescendantFocusability

    @inline def focusedChild = base.getFocusedChild

    @inline def layoutAnimation_=(p: android.view.animation.LayoutAnimationController) = {
      base.setLayoutAnimation(p)
      base
    }

    @inline def layoutAnimation(p: android.view.animation.LayoutAnimationController) = layoutAnimation_=(p)

    @inline def layoutAnimation = base.getLayoutAnimation

    @inline def layoutAnimationListener_=(p: android.view.animation.Animation.AnimationListener) = {
      base.setLayoutAnimationListener(p)
      base
    }

    @inline def layoutAnimationListener(p: android.view.animation.Animation.AnimationListener) = layoutAnimationListener_=(p)

    @inline def layoutAnimationListener = base.getLayoutAnimationListener

    @inline def onHierarchyChangeListener_=(p: android.view.ViewGroup.OnHierarchyChangeListener) = {
      base.setOnHierarchyChangeListener(p)
      base
    }

    @inline def onHierarchyChangeListener(p: android.view.ViewGroup.OnHierarchyChangeListener) = onHierarchyChangeListener_=(p)

    @noEquivalentGetterExists
    @inline def onHierarchyChangeListener: android.view.ViewGroup.OnHierarchyChangeListener = defaultValue[android.view.ViewGroup.OnHierarchyChangeListener]

    @inline def persistentDrawingCache_=(p: Int) = {
      base.setPersistentDrawingCache(p)
      base
    }

    @inline def persistentDrawingCache(p: Int) = persistentDrawingCache_=(p)

    @inline def persistentDrawingCache = base.getPersistentDrawingCache

    @inline def +=(v: View) = {
      base.addView(v)
      base
    }
  }

  trait ViewGroupLayoutParams[LP <: ViewGroupLayoutParams[_,_], V <: View] extends ViewGroup.LayoutParams {
    def base: LP

    def fill = {
      width = ViewGroup.LayoutParams.MATCH_PARENT
      height = ViewGroup.LayoutParams.MATCH_PARENT
      base
    }
    def wrap = {
      width = ViewGroup.LayoutParams.WRAP_CONTENT
      height = ViewGroup.LayoutParams.WRAP_CONTENT
      base
    }

    def parent : TraitViewGroup[_]

    def >> : V
  }

  trait ViewGroupMarginLayoutParams[LP <: ViewGroupMarginLayoutParams[_,_], V <: View] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[LP, V] {
    def marginBottom(size: Int) = {
      bottomMargin = size
      base
    }

    def marginTop(size: Int) = {
      topMargin = size
      base
    }

    def marginLeft(size: Int) = {
      leftMargin = size
      base
    }

    def marginRight(size: Int) = {
      rightMargin = size
      base
    }

def margin(size:Int) = {
bottomMargin = size
topMargin = size
leftMargin = size
rightMargin = size
base
}

def margin(top:Int, right:Int, bottom:Int, left:Int) = {
bottomMargin = bottom
topMargin = top
leftMargin = left
rightMargin = right
base
}
  }

  class RichFrameLayout[V <: FrameLayout](val base: V) extends TraitFrameLayout[V]

  @inline implicit def frameLayout2RichFrameLayout[V <: FrameLayout](frameLayout: V) = new RichFrameLayout[V](frameLayout)

  trait TraitFrameLayout[V <: FrameLayout] extends TraitViewGroup[V] {

    @inline def considerGoneChildrenWhenMeasuring = base.getConsiderGoneChildrenWhenMeasuring

    @inline def foreground_=(p: android.graphics.drawable.Drawable) = {
      base.setForeground(p)
      base
    }

    @inline def foreground(p: android.graphics.drawable.Drawable) = foreground_=(p)

    @inline def foreground = base.getForeground

    @inline def foregroundGravity_=(p: Int) = {
      base.setForegroundGravity(p)
      base
    }

    @inline def foregroundGravity(p: Int) = foregroundGravity_=(p)

    @noEquivalentGetterExists
    @inline def foregroundGravity: Int = defaultValue[Int]

    @inline def measureAllChildren_=(p: Boolean) = {
      base.setMeasureAllChildren(p)
      base
    }

    @inline def measureAllChildren(p: Boolean) = measureAllChildren_=(p)

    @noEquivalentGetterExists
    @inline def measureAllChildren: Boolean = defaultValue[Boolean]

  }

  class SFrameLayout(implicit context: Context) extends FrameLayout(context) with TraitFrameLayout[SFrameLayout] {
    def base = this

  implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)

  class LayoutParams[V <: View](v: V) extends FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
    def base = this

    v.setLayoutParams(this)

    def Gravity(g: Int) = {
      gravity = g
      this
    }

    def parent = SFrameLayout.this

    def >> : V = v
  }
}
  class RichRelativeLayout[V <: RelativeLayout](val base: V) extends TraitRelativeLayout[V]

  @inline implicit def relativeLayout2RichRelativeLayout[V <: RelativeLayout](relativeLayout: V) = new RichRelativeLayout[V](relativeLayout)

  trait TraitRelativeLayout[V <: RelativeLayout] extends TraitViewGroup[V] {

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @noEquivalentGetterExists
    @inline def gravity: Int = defaultValue[Int]

    @inline def horizontalGravity_=(p: Int) = {
      base.setHorizontalGravity(p)
      base
    }

    @inline def horizontalGravity(p: Int) = horizontalGravity_=(p)

    @noEquivalentGetterExists
    @inline def horizontalGravity: Int = defaultValue[Int]

    @inline def ignoreGravity_=(p: Int) = {
      base.setIgnoreGravity(p)
      base
    }

    @inline def ignoreGravity(p: Int) = ignoreGravity_=(p)

    @noEquivalentGetterExists
    @inline def ignoreGravity: Int = defaultValue[Int]

    @inline def verticalGravity_=(p: Int) = {
      base.setVerticalGravity(p)
      base
    }

    @inline def verticalGravity(p: Int) = verticalGravity_=(p)

    @noEquivalentGetterExists
    @inline def verticalGravity: Int = defaultValue[Int]

  }
  class SRelativeLayout(implicit context: Context) extends RelativeLayout(context) with TraitRelativeLayout[SRelativeLayout] {
    def base = this

  implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)

  class LayoutParams[V <: View](v: V) extends RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
    def base = this

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
  class RichLinearLayout[V <: LinearLayout](val base: V) extends TraitLinearLayout[V]

  @inline implicit def linearLayout2RichLinearLayout[V <: LinearLayout](linearLayout: V) = new RichLinearLayout[V](linearLayout)

  trait TraitLinearLayout[V <: LinearLayout] extends TraitViewGroup[V] {

    @inline def baselineAligned_=(p: Boolean) = {
      base.setBaselineAligned(p)
      base
    }

    @inline def baselineAligned(p: Boolean) = baselineAligned_=(p)

    @inline def baselineAligned = base.isBaselineAligned

    @inline def baselineAlignedChildIndex_=(p: Int) = {
      base.setBaselineAlignedChildIndex(p)
      base
    }

    @inline def baselineAlignedChildIndex(p: Int) = baselineAlignedChildIndex_=(p)

    @inline def baselineAlignedChildIndex = base.getBaselineAlignedChildIndex

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @noEquivalentGetterExists
    @inline def gravity: Int = defaultValue[Int]

    @inline def horizontalGravity_=(p: Int) = {
      base.setHorizontalGravity(p)
      base
    }

    @inline def horizontalGravity(p: Int) = horizontalGravity_=(p)

    @noEquivalentGetterExists
    @inline def horizontalGravity: Int = defaultValue[Int]

    @inline def orientation_=(p: Int) = {
      base.setOrientation(p)
      base
    }

    @inline def orientation(p: Int) = orientation_=(p)

    @inline def orientation = base.getOrientation

    @inline def verticalGravity_=(p: Int) = {
      base.setVerticalGravity(p)
      base
    }

    @inline def verticalGravity(p: Int) = verticalGravity_=(p)

    @noEquivalentGetterExists
    @inline def verticalGravity: Int = defaultValue[Int]

    @inline def weightSum_=(p: Float) = {
      base.setWeightSum(p)
      base
    }

    @inline def weightSum(p: Float) = weightSum_=(p)

    @inline def weightSum = base.getWeightSum

  }

  class SLinearLayout(implicit context: Context) extends LinearLayout(context) with TraitLinearLayout[SLinearLayout] {
    def base = this

    val VERTICAL = LinearLayout.VERTICAL
    val HORIZONTAL = LinearLayout.HORIZONTAL

    implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)

    class LayoutParams[V <: View](v: V) extends LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
      def base = this

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

  class RichEditTextPreference[V <: EditTextPreference](val base: V) extends TraitEditTextPreference[V]

  @inline implicit def editTextPreference2RichEditTextPreference[V <: EditTextPreference](editTextPreference: V) = new RichEditTextPreference[V](editTextPreference)

  trait TraitEditTextPreference[V <: EditTextPreference] {

    @inline def onPreferenceChange(f:  => Boolean): V = {
      base.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(p1: Preference, p2: Object): Boolean = {
          f
        }
      })
      base
    }

    @inline def onPreferenceChange(f: (Preference, Object) => Boolean): V = {
      base.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(p1: Preference, p2: Object): Boolean = {
          f(p1, p2)
        }
      })
      base
    }

    @inline def onPreferenceClick(f:  => Boolean): V = {
      base.setOnPreferenceClickListener(new OnPreferenceClickListener {
        def onPreferenceClick(p1: Preference): Boolean = {
          f
        }
      })
      base
    }

    @inline def onPreferenceClick(f: (Preference) => Boolean): V = {
      base.setOnPreferenceClickListener(new OnPreferenceClickListener {
        def onPreferenceClick(p1: Preference): Boolean = {
          f(p1)
        }
      })
      base
    }

    def base: V
  }

  class SEditTextPreference(implicit context: Context) extends EditTextPreference(context) with TraitEditTextPreference[SEditTextPreference] {
    def base = this

  }


  object SEditTextPreference {
    def apply()(implicit context: Context): SEditTextPreference = new SEditTextPreference

  }

  implicit def func2runnable[F](f: () => F): Runnable =
    new Runnable() {
      def run() {
        f()
      }
    }

  implicit def lazy2runnable[F](f: => F): Runnable =
    new Runnable() {
      def run() {
        f
      }
    }

  implicit def int2Drawable(resourceId: Int)(implicit context: Context): Drawable = {
    context.getResources.getDrawable(resourceId)
  }

  class AlertDialogBuilder(_title: CharSequence = null, _message: CharSequence = null)(implicit context: Context) extends AlertDialog.Builder(context) {
    if (_title != null) setTitle(_title)
    if (_message != null) setMessage(_message)


    @inline def positiveButton(name: CharSequence = android.R.string.yes, onClick: => Unit = {}): AlertDialogBuilder =
      positiveButton(name, (_, _) => {
        onClick
      })

    @inline def positiveButton(name: CharSequence, onClick: (DialogInterface, Int) => Unit): AlertDialogBuilder = {
      setPositiveButton(name, func2DialogOnClickListener(onClick))
      this
    }

    @inline def neutralButton(name: CharSequence = android.R.string.ok, onClick: => Unit = {}): AlertDialogBuilder =
      neutralButton(name, (_, _) => {
        onClick
      })

    @inline def neutralButton(name: CharSequence, onClick: (DialogInterface, Int) => Unit): AlertDialogBuilder = {
      setNeutralButton(name, func2DialogOnClickListener(onClick))
      this
    }

    @inline def negativeButton(name: CharSequence, onClick: => Unit): AlertDialogBuilder =
      negativeButton(name, (_, _) => {
        onClick
      })

    @inline def negativeButton(name: CharSequence = android.R.string.no, onClick: (DialogInterface, Int) => Unit = (d, _) => {
      d.cancel()
    }): AlertDialogBuilder = {
      setNegativeButton(name, func2DialogOnClickListener(onClick))
      this
    }

    var tit: CharSequence = null

    @inline def title_=(str: CharSequence) = {
      tit = str
      setTitle(str)
    }

    @inline def title = tit

    var msg: CharSequence = null

    @inline def message_=(str: CharSequence) = {
      tit = str
      setMessage(str)
    }

    @inline def message = tit

    override def show():AlertDialog = runOnUiThread(super.show())
  }

  @inline def alert(title: CharSequence, text: CharSequence, clickCallback: => Unit = {})(implicit context: Context) {
    new AlertDialogBuilder(title, text) {
      neutralButton(android.R.string.ok, clickCallback)
    }.show()
  }

  @inline implicit def stringToUri(str: String): Uri = Uri.parse(str)

  object SIntent {
    @inline def apply[T]()(implicit context: Context, mt: ClassManifest[T]) = new Intent(context, mt.erasure)

    @inline def apply[T](action: String)(implicit context: Context, mt: ClassManifest[T]): Intent = SIntent[T].setAction(action)
  }

  class RichButton[V <: Button](val base: V) extends TraitButton[V]

  @inline implicit def button2RichButton[V <: Button](button: V) = new RichButton[V](button)

  trait TraitButton[V <: Button] extends TraitTextView[V] {

  }

  class SButton(implicit context: Context) extends Button(context) with TraitButton[SButton] {
    def base = this

  }

  object SButton {
    def apply[LP <: ViewGroupLayoutParams[_, SButton]]()(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
    val view = (new SButton)
    view.<<.parent.+=(view)
    view }
    def apply[LP <: ViewGroupLayoutParams[_, SButton]](text: CharSequence, onClickListener: (View) => Unit)(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
      apply(text, func2ViewOnClickListener(onClickListener))
    }
    def apply[LP <: ViewGroupLayoutParams[_, SButton]](text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context, defaultLayoutParam: (SButton) => LP): SButton =  {
      val button = new SButton()(context)
      button.text = text
      button.setOnClickListener(onClickListener)
      button.<<.parent.+=(button)
      button
    }
  }

  class RichKeyboardView[V <: KeyboardView](val base: V) extends TraitKeyboardView[V]

  @inline implicit def keyboardView2RichKeyboardView[V <: KeyboardView](keyboardView: V) = new RichKeyboardView[V](keyboardView)

  trait TraitKeyboardView[V <: KeyboardView] extends TraitView[V] {

    @inline def keyboard_=(p: android.inputmethodservice.Keyboard) = {
      base.setKeyboard(p)
      base
    }

    @inline def keyboard(p: android.inputmethodservice.Keyboard) = keyboard_=(p)

    @inline def keyboard = base.getKeyboard

    @inline def onKeyboardActionListener_=(p: android.inputmethodservice.KeyboardView.OnKeyboardActionListener) = {
      base.setOnKeyboardActionListener(p)
      base
    }

    @inline def onKeyboardActionListener(p: android.inputmethodservice.KeyboardView.OnKeyboardActionListener) = onKeyboardActionListener_=(p)

    @noEquivalentGetterExists
    @inline def onKeyboardActionListener: android.inputmethodservice.KeyboardView.OnKeyboardActionListener = defaultValue[android.inputmethodservice.KeyboardView.OnKeyboardActionListener]

    @inline def popupParent_=(p: android.view.View) = {
      base.setPopupParent(p)
      base
    }

    @inline def popupParent(p: android.view.View) = popupParent_=(p)

    @noEquivalentGetterExists
    @inline def popupParent: android.view.View = defaultValue[android.view.View]

    @inline def enablePreview = {base.setPreviewEnabled(true); base}
    @inline def disablePreview = {base.setPreviewEnabled(false); base}
    @inline def previewEnabled_=(p: Boolean) = {
      base.setPreviewEnabled(p)
      base
    }

    @inline def previewEnabled(p: Boolean) = previewEnabled_=(p)

    @inline def previewEnabled = base.isPreviewEnabled

    @inline def enableProximityCorrection = {base.setProximityCorrectionEnabled(true); base}
    @inline def disableProximityCorrection = {base.setProximityCorrectionEnabled(false); base}
    @inline def proximityCorrectionEnabled_=(p: Boolean) = {
      base.setProximityCorrectionEnabled(p)
      base
    }

    @inline def proximityCorrectionEnabled(p: Boolean) = proximityCorrectionEnabled_=(p)

    @inline def proximityCorrectionEnabled = base.isProximityCorrectionEnabled

    @inline def shifted = base.isShifted

    @inline def verticalCorrection_=(p: Int) = {
      base.setVerticalCorrection(p)
      base
    }

    @inline def verticalCorrection(p: Int) = verticalCorrection_=(p)

    @noEquivalentGetterExists
    @inline def verticalCorrection: Int = defaultValue[Int]

   }

  class RichImageView[V <: ImageView](val base: V) extends TraitImageView[V]

  @inline implicit def ImageView2RichImageView[V <: ImageView](ImageView: V) = new RichImageView[V](ImageView)

  trait TraitImageView[V <: ImageView] extends TraitView[V] {

    @inline def adjustViewBounds_=(p: Boolean) = {
      base.setAdjustViewBounds(p)
      base
    }

    @inline def adjustViewBounds(p: Boolean) = adjustViewBounds_=(p)

    @noEquivalentGetterExists
    @inline def adjustViewBounds: Boolean = defaultValue[Boolean]

    @inline def alpha_=(p: Int) = {
      base.setAlpha(p)
      base
    }

    @inline def alpha(p: Int) = alpha_=(p)

    @noEquivalentGetterExists
    @inline def alpha: Int = defaultValue[Int]

    @inline def drawable = base.getDrawable

    @inline def imageBitmap_=(p: android.graphics.Bitmap) = {
      base.setImageBitmap(p)
      base
    }

    @inline def imageBitmap(p: android.graphics.Bitmap) = imageBitmap_=(p)

    @noEquivalentGetterExists
    @inline def imageBitmap: android.graphics.Bitmap = defaultValue[android.graphics.Bitmap]

    @inline def imageDrawable_=(p: android.graphics.drawable.Drawable) = {
      base.setImageDrawable(p)
      base
    }

    @inline def imageDrawable(p: android.graphics.drawable.Drawable) = imageDrawable_=(p)

    @noEquivalentGetterExists
    @inline def imageDrawable: android.graphics.drawable.Drawable = defaultValue[android.graphics.drawable.Drawable]

    @inline def imageLevel_=(p: Int) = {
      base.setImageLevel(p)
      base
    }

    @inline def imageLevel(p: Int) = imageLevel_=(p)

    @noEquivalentGetterExists
    @inline def imageLevel: Int = defaultValue[Int]

    @inline def imageMatrix_=(p: android.graphics.Matrix) = {
      base.setImageMatrix(p)
      base
    }

    @inline def imageMatrix(p: android.graphics.Matrix) = imageMatrix_=(p)

    @inline def imageMatrix = base.getImageMatrix

    @inline def imageResource_=(p: Int) = {
      base.setImageResource(p)
      base
    }

    @inline def imageResource(p: Int) = imageResource_=(p)

    @noEquivalentGetterExists
    @inline def imageResource: Int = defaultValue[Int]

    @inline def imageURI_=(p: android.net.Uri) = {
      base.setImageURI(p)
      base
    }

    @inline def imageURI(p: android.net.Uri) = imageURI_=(p)

    @noEquivalentGetterExists
    @inline def imageURI: android.net.Uri = defaultValue[android.net.Uri]

    @inline def maxHeight_=(p: Int) = {
      base.setMaxHeight(p)
      base
    }

    @inline def maxHeight(p: Int) = maxHeight_=(p)

    @noEquivalentGetterExists
    @inline def maxHeight: Int = defaultValue[Int]

    @inline def maxWidth_=(p: Int) = {
      base.setMaxWidth(p)
      base
    }

    @inline def maxWidth(p: Int) = maxWidth_=(p)

    @noEquivalentGetterExists
    @inline def maxWidth: Int = defaultValue[Int]

    @inline def scaleType_=(p: android.widget.ImageView.ScaleType) = {
      base.setScaleType(p)
      base
    }

    @inline def scaleType(p: android.widget.ImageView.ScaleType) = scaleType_=(p)

    @inline def scaleType = base.getScaleType

   }

  class RichProgressBar[V <: ProgressBar](val base: V) extends TraitProgressBar[V]

  @inline implicit def progressBar2RichProgressBar[V <: ProgressBar](progressBar: V) = new RichProgressBar[V](progressBar)

  trait TraitProgressBar[V <: ProgressBar] extends TraitView[V] {

    @inline def indeterminate_=(p: Boolean) = {
      base.setIndeterminate(p)
      base
    }

    @inline def indeterminate(p: Boolean) = indeterminate_=(p)

    @inline def indeterminate = base.isIndeterminate

    @inline def indeterminateDrawable_=(p: android.graphics.drawable.Drawable) = {
      base.setIndeterminateDrawable(p)
      base
    }

    @inline def indeterminateDrawable(p: android.graphics.drawable.Drawable) = indeterminateDrawable_=(p)

    @inline def indeterminateDrawable = base.getIndeterminateDrawable

    @inline def interpolator_=(p: android.view.animation.Interpolator) = {
      base.setInterpolator(p)
      base
    }

    @inline def interpolator(p: android.view.animation.Interpolator) = interpolator_=(p)

    @inline def interpolator = base.getInterpolator

    @inline def max_=(p: Int) = {
      base.setMax(p)
      base
    }

    @inline def max(p: Int) = max_=(p)

    @inline def max = base.getMax

    @inline def progress_=(p: Int) = {
      base.setProgress(p)
      base
    }

    @inline def progress(p: Int) = progress_=(p)

    @inline def progress = base.getProgress

    @inline def progressDrawable_=(p: android.graphics.drawable.Drawable) = {
      base.setProgressDrawable(p)
      base
    }

    @inline def progressDrawable(p: android.graphics.drawable.Drawable) = progressDrawable_=(p)

    @inline def progressDrawable = base.getProgressDrawable

    @inline def secondaryProgress_=(p: Int) = {
      base.setSecondaryProgress(p)
      base
    }

    @inline def secondaryProgress(p: Int) = secondaryProgress_=(p)

    @inline def secondaryProgress = base.getSecondaryProgress

   }

  class SProgressBar(implicit context: Context) extends ProgressBar(context) with TraitProgressBar[SProgressBar] {
    def base = this

  }

  object SProgressBar {
    def apply[LP <: ViewGroupLayoutParams[_, SProgressBar]]()(implicit context: Context, defaultLayoutParam: (SProgressBar) => LP): SProgressBar =  {
    val view = (new SProgressBar)
    view.<<.parent.+=(view)
    view }
  }

  class RichAnalogClock[V <: AnalogClock](val base: V) extends TraitAnalogClock[V]

  @inline implicit def analogClock2RichAnalogClock[V <: AnalogClock](analogClock: V) = new RichAnalogClock[V](analogClock)

  trait TraitAnalogClock[V <: AnalogClock] extends TraitView[V] {

   }

  class SAnalogClock(implicit context: Context) extends AnalogClock(context) with TraitAnalogClock[SAnalogClock] {
    def base = this

  }

  object SAnalogClock {
    def apply[LP <: ViewGroupLayoutParams[_, SAnalogClock]]()(implicit context: Context, defaultLayoutParam: (SAnalogClock) => LP): SAnalogClock =  {
    val view = (new SAnalogClock)
    view.<<.parent.+=(view)
    view }
  }

  class RichSurfaceView[V <: SurfaceView](val base: V) extends TraitSurfaceView[V]

  @inline implicit def surfaceView2RichSurfaceView[V <: SurfaceView](surfaceView: V) = new RichSurfaceView[V](surfaceView)

  trait TraitSurfaceView[V <: SurfaceView] extends TraitView[V] {

    @inline def ZOrderMediaOverlay_=(p: Boolean) = {
      base.setZOrderMediaOverlay(p)
      base
    }

    @inline def ZOrderMediaOverlay(p: Boolean) = ZOrderMediaOverlay_=(p)

    @noEquivalentGetterExists
    @inline def ZOrderMediaOverlay: Boolean = defaultValue[Boolean]

    @inline def ZOrderOnTop_=(p: Boolean) = {
      base.setZOrderOnTop(p)
      base
    }

    @inline def ZOrderOnTop(p: Boolean) = ZOrderOnTop_=(p)

    @noEquivalentGetterExists
    @inline def ZOrderOnTop: Boolean = defaultValue[Boolean]

    @inline def holder = base.getHolder

   }


  class RichViewStub[V <: ViewStub](val base: V) extends TraitViewStub[V]

  @inline implicit def viewStub2RichViewStub[V <: ViewStub](viewStub: V) = new RichViewStub[V](viewStub)

  trait TraitViewStub[V <: ViewStub] extends TraitView[V] {

    @inline def inflatedId_=(p: Int) = {
      base.setInflatedId(p)
      base
    }

    @inline def inflatedId(p: Int) = inflatedId_=(p)

    @inline def inflatedId = base.getInflatedId

    @inline def layoutResource_=(p: Int) = {
      base.setLayoutResource(p)
      base
    }

    @inline def layoutResource(p: Int) = layoutResource_=(p)

    @inline def layoutResource = base.getLayoutResource

    @inline def onInflateListener_=(p: android.view.ViewStub.OnInflateListener) = {
      base.setOnInflateListener(p)
      base
    }

    @inline def onInflateListener(p: android.view.ViewStub.OnInflateListener) = onInflateListener_=(p)

    @noEquivalentGetterExists
    @inline def onInflateListener: android.view.ViewStub.OnInflateListener = defaultValue[android.view.ViewStub.OnInflateListener]

   }

  class RichGridView[V <: GridView](val base: V) extends TraitGridView[V]

  @inline implicit def gridView2RichGridView[V <: GridView](gridView: V) = new RichGridView[V](gridView)

  trait TraitGridView[V <: GridView] extends TraitAbsListView[V] {

    @inline def columnWidth_=(p: Int) = {
      base.setColumnWidth(p)
      base
    }

    @inline def columnWidth(p: Int) = columnWidth_=(p)

    @noEquivalentGetterExists
    @inline def columnWidth: Int = defaultValue[Int]

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @noEquivalentGetterExists
    @inline def gravity: Int = defaultValue[Int]

    @inline def horizontalSpacing_=(p: Int) = {
      base.setHorizontalSpacing(p)
      base
    }

    @inline def horizontalSpacing(p: Int) = horizontalSpacing_=(p)

    @noEquivalentGetterExists
    @inline def horizontalSpacing: Int = defaultValue[Int]

    @inline def numColumns_=(p: Int) = {
      base.setNumColumns(p)
      base
    }

    @inline def numColumns(p: Int) = numColumns_=(p)

    @noEquivalentGetterExists
    @inline def numColumns: Int = defaultValue[Int]

    @inline def stretchMode_=(p: Int) = {
      base.setStretchMode(p)
      base
    }

    @inline def stretchMode(p: Int) = stretchMode_=(p)

    @inline def stretchMode = base.getStretchMode

    @inline def verticalSpacing_=(p: Int) = {
      base.setVerticalSpacing(p)
      base
    }

    @inline def verticalSpacing(p: Int) = verticalSpacing_=(p)

    @noEquivalentGetterExists
    @inline def verticalSpacing: Int = defaultValue[Int]

   }

  class RichExpandableListView[V <: ExpandableListView](val base: V) extends TraitExpandableListView[V]

  @inline implicit def expandableListView2RichExpandableListView[V <: ExpandableListView](expandableListView: V) = new RichExpandableListView[V](expandableListView)

  trait TraitExpandableListView[V <: ExpandableListView] extends TraitListView[V] {

    @inline def childDivider_=(p: android.graphics.drawable.Drawable) = {
      base.setChildDivider(p)
      base
    }

    @inline def childDivider(p: android.graphics.drawable.Drawable) = childDivider_=(p)

    @noEquivalentGetterExists
    @inline def childDivider: android.graphics.drawable.Drawable = defaultValue[android.graphics.drawable.Drawable]

    @inline def childIndicator_=(p: android.graphics.drawable.Drawable) = {
      base.setChildIndicator(p)
      base
    }

    @inline def childIndicator(p: android.graphics.drawable.Drawable) = childIndicator_=(p)

    @noEquivalentGetterExists
    @inline def childIndicator: android.graphics.drawable.Drawable = defaultValue[android.graphics.drawable.Drawable]

    @inline def expandableListAdapter = base.getExpandableListAdapter

    @inline def groupIndicator_=(p: android.graphics.drawable.Drawable) = {
      base.setGroupIndicator(p)
      base
    }

    @inline def groupIndicator(p: android.graphics.drawable.Drawable) = groupIndicator_=(p)

    @noEquivalentGetterExists
    @inline def groupIndicator: android.graphics.drawable.Drawable = defaultValue[android.graphics.drawable.Drawable]

    @inline def onChildClickListener_=(p: android.widget.ExpandableListView.OnChildClickListener) = {
      base.setOnChildClickListener(p)
      base
    }

    @inline def onChildClickListener(p: android.widget.ExpandableListView.OnChildClickListener) = onChildClickListener_=(p)

    @noEquivalentGetterExists
    @inline def onChildClickListener: android.widget.ExpandableListView.OnChildClickListener = defaultValue[android.widget.ExpandableListView.OnChildClickListener]

    @inline def onGroupClickListener_=(p: android.widget.ExpandableListView.OnGroupClickListener) = {
      base.setOnGroupClickListener(p)
      base
    }

    @inline def onGroupClickListener(p: android.widget.ExpandableListView.OnGroupClickListener) = onGroupClickListener_=(p)

    @noEquivalentGetterExists
    @inline def onGroupClickListener: android.widget.ExpandableListView.OnGroupClickListener = defaultValue[android.widget.ExpandableListView.OnGroupClickListener]

    @inline def onGroupCollapseListener_=(p: android.widget.ExpandableListView.OnGroupCollapseListener) = {
      base.setOnGroupCollapseListener(p)
      base
    }

    @inline def onGroupCollapseListener(p: android.widget.ExpandableListView.OnGroupCollapseListener) = onGroupCollapseListener_=(p)

    @noEquivalentGetterExists
    @inline def onGroupCollapseListener: android.widget.ExpandableListView.OnGroupCollapseListener = defaultValue[android.widget.ExpandableListView.OnGroupCollapseListener]

    @inline def onGroupExpandListener_=(p: android.widget.ExpandableListView.OnGroupExpandListener) = {
      base.setOnGroupExpandListener(p)
      base
    }

    @inline def onGroupExpandListener(p: android.widget.ExpandableListView.OnGroupExpandListener) = onGroupExpandListener_=(p)

    @noEquivalentGetterExists
    @inline def onGroupExpandListener: android.widget.ExpandableListView.OnGroupExpandListener = defaultValue[android.widget.ExpandableListView.OnGroupExpandListener]

    @inline def selectedGroup_=(p: Int) = {
      base.setSelectedGroup(p)
      base
    }

    @inline def selectedGroup(p: Int) = selectedGroup_=(p)

    @noEquivalentGetterExists
    @inline def selectedGroup: Int = defaultValue[Int]

    @inline def selectedId = base.getSelectedId

    @inline def selectedPosition = base.getSelectedPosition

   }





trait TraitAdapterView[V <: AdapterView[_]] extends TraitView[V] {
  import android.widget.AdapterView.OnItemClickListener
  import android.widget.AdapterView.OnItemLongClickListener
    @inline def onItemClick(f:  => Unit): V = {
      base.setOnItemClickListener(new OnItemClickListener {
        def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {
          f
        }
      })
      base
    }

    @inline def onItemClick(f: (AdapterView[_], View, Int, Long) => Unit): V = {
      base.setOnItemClickListener(new OnItemClickListener {
        def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {
          f(p1, p2, p3, p4)
        }
      })
      base
    }

    @inline def onItemLongClick(f:  => Boolean): V = {
      base.setOnItemLongClickListener(new OnItemLongClickListener {
        def onItemLongClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Boolean = {
          f
        }
      })
      base
    }

    @inline def onItemLongClick(f: (AdapterView[_], View, Int, Long) => Boolean): V = {
      base.setOnItemLongClickListener(new OnItemLongClickListener {
        def onItemLongClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Boolean = {
          f(p1, p2, p3, p4)
        }
      })
      base
    }

    @inline def onItemSelected(f:  => Unit): V = {
      base.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {
          f
        }
        def onNothingSelected(p1: AdapterView[_]): Unit = {
        }
      })
      base
    }

    @inline def onNothingSelected(f:  => Unit): V = {
      base.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {
        }
        def onNothingSelected(p1: AdapterView[_]): Unit = {
          f
        }
      })
      base
    }

    @inline def onItemSelected(f: (AdapterView[_], View, Int, Long) => Unit): V = {
      base.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {
          f(p1, p2, p3, p4)
        }
        def onNothingSelected(p1: AdapterView[_]): Unit = {
        }
      })
      base
    }

    @inline def onNothingSelected(f: (AdapterView[_]) => Unit): V = {
      base.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener {
        def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long): Unit = {
        }
        def onNothingSelected(p1: AdapterView[_]): Unit = {
          f(p1)
        }
      })
      base
    }


}

trait TraitAbsSpinner[V <: AbsSpinner] extends TraitAdapterView[V] {
   }

  class RichSpinner[V <: Spinner](val base: V) extends TraitSpinner[V]

  @inline implicit def spinner2RichSpinner[V <: Spinner](spinner: V) = new RichSpinner[V](spinner)

  trait TraitSpinner[V <: Spinner] extends TraitAbsSpinner[V] {

    @inline def prompt_=(p: java.lang.CharSequence) = {
      base.setPrompt(p)
      base
    }

    @inline def prompt(p: java.lang.CharSequence) = prompt_=(p)

    @inline def prompt = base.getPrompt

    @inline def promptId_=(p: Int) = {
      base.setPromptId(p)
      base
    }

    @inline def promptId(p: Int) = promptId_=(p)

    @noEquivalentGetterExists
    @inline def promptId: Int = defaultValue[Int]

   }

  class SSpinner(implicit context: Context) extends Spinner(context) with TraitSpinner[SSpinner] {
    def base = this

  }

  object SSpinner {
    def apply[LP <: ViewGroupLayoutParams[_, SSpinner]]()(implicit context: Context, defaultLayoutParam: (SSpinner) => LP): SSpinner =  {
    val view = (new SSpinner)
    view.<<.parent.+=(view)
    view }
  }

  class RichGallery[V <: Gallery](val base: V) extends TraitGallery[V]

  @inline implicit def gallery2RichGallery[V <: Gallery](gallery: V) = new RichGallery[V](gallery)

  trait TraitGallery[V <: Gallery] extends TraitAbsSpinner[V] {

    @inline def animationDuration_=(p: Int) = {
      base.setAnimationDuration(p)
      base
    }

    @inline def animationDuration(p: Int) = animationDuration_=(p)

    @noEquivalentGetterExists
    @inline def animationDuration: Int = defaultValue[Int]

    @inline def callbackDuringFling_=(p: Boolean) = {
      base.setCallbackDuringFling(p)
      base
    }

    @inline def callbackDuringFling(p: Boolean) = callbackDuringFling_=(p)

    @noEquivalentGetterExists
    @inline def callbackDuringFling: Boolean = defaultValue[Boolean]

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @noEquivalentGetterExists
    @inline def gravity: Int = defaultValue[Int]

    @inline def spacing_=(p: Int) = {
      base.setSpacing(p)
      base
    }

    @inline def spacing(p: Int) = spacing_=(p)

    @noEquivalentGetterExists
    @inline def spacing: Int = defaultValue[Int]

    @inline def unselectedAlpha_=(p: Float) = {
      base.setUnselectedAlpha(p)
      base
    }

    @inline def unselectedAlpha(p: Float) = unselectedAlpha_=(p)

    @noEquivalentGetterExists
    @inline def unselectedAlpha: Float = defaultValue[Float]

   }

  class SGallery(implicit context: Context) extends Gallery(context) with TraitGallery[SGallery] {
    def base = this

  }

  object SGallery {
    def apply[LP <: ViewGroupLayoutParams[_, SGallery]]()(implicit context: Context, defaultLayoutParam: (SGallery) => LP): SGallery =  {
    val view = (new SGallery)
    view.<<.parent.+=(view)
    view }
  }

  class RichAbsSeekBar[V <: AbsSeekBar](val base: V) extends TraitAbsSeekBar[V]

  @inline implicit def absSeekBar2RichAbsSeekBar[V <: AbsSeekBar](absSeekBar: V) = new RichAbsSeekBar[V](absSeekBar)

  trait TraitAbsSeekBar[V <: AbsSeekBar] extends TraitProgressBar[V] {

    @inline def keyProgressIncrement_=(p: Int) = {
      base.setKeyProgressIncrement(p)
      base
    }

    @inline def keyProgressIncrement(p: Int) = keyProgressIncrement_=(p)

    @inline def keyProgressIncrement = base.getKeyProgressIncrement

    @inline def thumb_=(p: android.graphics.drawable.Drawable) = {
      base.setThumb(p)
      base
    }

    @inline def thumb(p: android.graphics.drawable.Drawable) = thumb_=(p)

    @noEquivalentGetterExists
    @inline def thumb: android.graphics.drawable.Drawable = defaultValue[android.graphics.drawable.Drawable]

    @inline def thumbOffset_=(p: Int) = {
      base.setThumbOffset(p)
      base
    }

    @inline def thumbOffset(p: Int) = thumbOffset_=(p)

    @inline def thumbOffset = base.getThumbOffset

   }

  class RichSeekBar[V <: SeekBar](val base: V) extends TraitSeekBar[V]

  @inline implicit def seekBar2RichSeekBar[V <: SeekBar](seekBar: V) = new RichSeekBar[V](seekBar)

  trait TraitSeekBar[V <: SeekBar] extends TraitAbsSeekBar[V] {

    @inline def onSeekBarChangeListener_=(p: android.widget.SeekBar.OnSeekBarChangeListener) = {
      base.setOnSeekBarChangeListener(p)
      base
    }

    @inline def onSeekBarChangeListener(p: android.widget.SeekBar.OnSeekBarChangeListener) = onSeekBarChangeListener_=(p)

    @noEquivalentGetterExists
    @inline def onSeekBarChangeListener: android.widget.SeekBar.OnSeekBarChangeListener = defaultValue[android.widget.SeekBar.OnSeekBarChangeListener]

    @inline def onProgressChanged(f:  => Unit): V = {
      base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {
          f
        }
        def onStartTrackingTouch(p1: SeekBar): Unit = {
        }
        def onStopTrackingTouch(p1: SeekBar): Unit = {
        }
      })
      base
    }

    @inline def onStartTrackingTouch(f:  => Unit): V = {
      base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {
        }
        def onStartTrackingTouch(p1: SeekBar): Unit = {
          f
        }
        def onStopTrackingTouch(p1: SeekBar): Unit = {
        }
      })
      base
    }

    @inline def onStopTrackingTouch(f:  => Unit): V = {
      base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {
        }
        def onStartTrackingTouch(p1: SeekBar): Unit = {
        }
        def onStopTrackingTouch(p1: SeekBar): Unit = {
          f
        }
      })
      base
    }

    @inline def onProgressChanged(f: (SeekBar, Int, Boolean) => Unit): V = {
      base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {
          f(p1, p2, p3)
        }
        def onStartTrackingTouch(p1: SeekBar): Unit = {
        }
        def onStopTrackingTouch(p1: SeekBar): Unit = {
        }
      })
      base
    }

    @inline def onStartTrackingTouch(f: (SeekBar) => Unit): V = {
      base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {
        }
        def onStartTrackingTouch(p1: SeekBar): Unit = {
          f(p1)
        }
        def onStopTrackingTouch(p1: SeekBar): Unit = {
        }
      })
      base
    }

    @inline def onStopTrackingTouch(f: (SeekBar) => Unit): V = {
      base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(p1: SeekBar, p2: Int, p3: Boolean): Unit = {
        }
        def onStartTrackingTouch(p1: SeekBar): Unit = {
        }
        def onStopTrackingTouch(p1: SeekBar): Unit = {
          f(p1)
        }
      })
      base
    }

  }
  class SSeekBar(implicit context: Context) extends SeekBar(context) with TraitSeekBar[SSeekBar] {
    def base = this

  }

  object SSeekBar {
    def apply[LP <: ViewGroupLayoutParams[_, SSeekBar]]()(implicit context: Context, defaultLayoutParam: (SSeekBar) => LP): SSeekBar =  {
    val view = (new SSeekBar)
    view.<<.parent.+=(view)
    view }
   }

  class RichRatingBar[V <: RatingBar](val base: V) extends TraitRatingBar[V]

  @inline implicit def ratingBar2RichRatingBar[V <: RatingBar](ratingBar: V) = new RichRatingBar[V](ratingBar)

  trait TraitRatingBar[V <: RatingBar] extends TraitAbsSeekBar[V] {

    @inline def indicator = base.isIndicator

    @inline def numStars_=(p: Int) = {
      base.setNumStars(p)
      base
    }

    @inline def numStars(p: Int) = numStars_=(p)

    @inline def numStars = base.getNumStars

    @inline def onRatingBarChangeListener_=(p: android.widget.RatingBar.OnRatingBarChangeListener) = {
      base.setOnRatingBarChangeListener(p)
      base
    }

    @inline def onRatingBarChangeListener(p: android.widget.RatingBar.OnRatingBarChangeListener) = onRatingBarChangeListener_=(p)

    @inline def onRatingBarChangeListener = base.getOnRatingBarChangeListener

    @inline def rating_=(p: Float) = {
      base.setRating(p)
      base
    }

    @inline def rating(p: Float) = rating_=(p)

    @inline def rating = base.getRating

    @inline def stepSize_=(p: Float) = {
      base.setStepSize(p)
      base
    }

    @inline def stepSize(p: Float) = stepSize_=(p)

    @inline def stepSize = base.getStepSize

   }

  class SRatingBar(implicit context: Context) extends RatingBar(context) with TraitRatingBar[SRatingBar] {
    def base = this

  }

  object SRatingBar {
    def apply[LP <: ViewGroupLayoutParams[_, SRatingBar]]()(implicit context: Context, defaultLayoutParam: (SRatingBar) => LP): SRatingBar =  {
    val view = (new SRatingBar)
    view.<<.parent.+=(view)
    view }
  }

  class RichAppWidgetHostView[V <: AppWidgetHostView](val base: V) extends TraitAppWidgetHostView[V]

  @inline implicit def appWidgetHostView2RichAppWidgetHostView[V <: AppWidgetHostView](appWidgetHostView: V) = new RichAppWidgetHostView[V](appWidgetHostView)

  trait TraitAppWidgetHostView[V <: AppWidgetHostView] extends TraitFrameLayout[V] {

    @inline def appWidgetId = base.getAppWidgetId

    @inline def appWidgetInfo = base.getAppWidgetInfo

   }

  class RichDatePicker[V <: DatePicker](val base: V) extends TraitDatePicker[V]

  @inline implicit def datePicker2RichDatePicker[V <: DatePicker](datePicker: V) = new RichDatePicker[V](datePicker)

  trait TraitDatePicker[V <: DatePicker] extends TraitFrameLayout[V] {

    @inline def dayOfMonth = base.getDayOfMonth

    @inline def month = base.getMonth

    @inline def year = base.getYear

   }

  class SDatePicker(implicit context: Context) extends DatePicker(context) with TraitDatePicker[SDatePicker] {
    def base = this

  }

  object SDatePicker {
    def apply[LP <: ViewGroupLayoutParams[_, SDatePicker]]()(implicit context: Context, defaultLayoutParam: (SDatePicker) => LP): SDatePicker =  {
    val view = (new SDatePicker)
    view.<<.parent.+=(view)
    view }
  }

  class RichGestureOverlayView[V <: GestureOverlayView](val base: V) extends TraitGestureOverlayView[V]

  @inline implicit def gestureOverlayView2RichGestureOverlayView[V <: GestureOverlayView](gestureOverlayView: V) = new RichGestureOverlayView[V](gestureOverlayView)

  trait TraitGestureOverlayView[V <: GestureOverlayView] extends TraitFrameLayout[V] {

    @inline def currentStroke = base.getCurrentStroke

    @inline def enableEventsInterception = {base.setEventsInterceptionEnabled(true); base}
    @inline def disableEventsInterception = {base.setEventsInterceptionEnabled(false); base}
    @inline def eventsInterceptionEnabled_=(p: Boolean) = {
      base.setEventsInterceptionEnabled(p)
      base
    }

    @inline def eventsInterceptionEnabled(p: Boolean) = eventsInterceptionEnabled_=(p)

    @inline def eventsInterceptionEnabled = base.isEventsInterceptionEnabled

    @inline def enableFade = {base.setFadeEnabled(true); base}
    @inline def disableFade = {base.setFadeEnabled(false); base}
    @inline def fadeEnabled_=(p: Boolean) = {
      base.setFadeEnabled(p)
      base
    }

    @inline def fadeEnabled(p: Boolean) = fadeEnabled_=(p)

    @inline def fadeEnabled = base.isFadeEnabled

    @inline def fadeOffset_=(p: Long) = {
      base.setFadeOffset(p)
      base
    }

    @inline def fadeOffset(p: Long) = fadeOffset_=(p)

    @inline def fadeOffset = base.getFadeOffset

    @inline def gesture_=(p: android.gesture.Gesture) = {
      base.setGesture(p)
      base
    }

    @inline def gesture(p: android.gesture.Gesture) = gesture_=(p)

    @inline def gesture = base.getGesture

    @inline def gestureColor_=(p: Int) = {
      base.setGestureColor(p)
      base
    }

    @inline def gestureColor(p: Int) = gestureColor_=(p)

    @inline def gestureColor = base.getGestureColor

    @inline def gesturePath = base.getGesturePath

    @inline def gestureStrokeAngleThreshold_=(p: Float) = {
      base.setGestureStrokeAngleThreshold(p)
      base
    }

    @inline def gestureStrokeAngleThreshold(p: Float) = gestureStrokeAngleThreshold_=(p)

    @inline def gestureStrokeAngleThreshold = base.getGestureStrokeAngleThreshold

    @inline def gestureStrokeLengthThreshold_=(p: Float) = {
      base.setGestureStrokeLengthThreshold(p)
      base
    }

    @inline def gestureStrokeLengthThreshold(p: Float) = gestureStrokeLengthThreshold_=(p)

    @inline def gestureStrokeLengthThreshold = base.getGestureStrokeLengthThreshold

    @inline def gestureStrokeSquarenessTreshold_=(p: Float) = {
      base.setGestureStrokeSquarenessTreshold(p)
      base
    }

    @inline def gestureStrokeSquarenessTreshold(p: Float) = gestureStrokeSquarenessTreshold_=(p)

    @inline def gestureStrokeSquarenessTreshold = base.getGestureStrokeSquarenessTreshold

    @inline def gestureStrokeType_=(p: Int) = {
      base.setGestureStrokeType(p)
      base
    }

    @inline def gestureStrokeType(p: Int) = gestureStrokeType_=(p)

    @inline def gestureStrokeType = base.getGestureStrokeType

    @inline def gestureStrokeWidth_=(p: Float) = {
      base.setGestureStrokeWidth(p)
      base
    }

    @inline def gestureStrokeWidth(p: Float) = gestureStrokeWidth_=(p)

    @inline def gestureStrokeWidth = base.getGestureStrokeWidth

    @inline def gestureVisible_=(p: Boolean) = {
      base.setGestureVisible(p)
      base
    }

    @inline def gestureVisible(p: Boolean) = gestureVisible_=(p)

    @inline def gestureVisible = base.isGestureVisible

    @inline def gesturing = base.isGesturing

    @inline def orientation_=(p: Int) = {
      base.setOrientation(p)
      base
    }

    @inline def orientation(p: Int) = orientation_=(p)

    @inline def orientation = base.getOrientation

    @inline def uncertainGestureColor_=(p: Int) = {
      base.setUncertainGestureColor(p)
      base
    }

    @inline def uncertainGestureColor(p: Int) = uncertainGestureColor_=(p)

    @inline def uncertainGestureColor = base.getUncertainGestureColor

   }

  @inline def toast(message: CharSequence)(implicit context: Context) {
    runOnUiThread(Toast.makeText(context, message, Toast.LENGTH_SHORT).show())
  }

  @inline def longToast(message: CharSequence)(implicit context: Context) {
    runOnUiThread(Toast.makeText(context, message, Toast.LENGTH_LONG).show())
  }

  @inline def spinnerDialog(title: String, message: String)(implicit context: Context): ProgressDialog =
    runOnUiThread(ProgressDialog.show(context, title, message, true))

  @inline def pendingService(intent: Intent)(implicit context: Context) =
    PendingIntent.getService(context, 0, intent, 0)

  @inline def pendingActivity(intent: Intent)(implicit context: Context) =
    PendingIntent.getActivity(context, 0, intent, 0)

  @inline def pendingActivity[T](implicit context: Context, mt: ClassManifest[T]) =
    PendingIntent.getActivity(context, 0, SIntent[T], 0)

  @inline def notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

  @inline def ringtoneSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

  @inline def alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

  @inline def defaultSharedPreferences(implicit context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

  trait TagUtil {
    implicit val tag = LoggerTag(this.getClass.getName)
  }

  @inline def accessibilityManager(implicit context: Context): AccessibilityManager =
    context.getSystemService(Context.ACCESSIBILITY_SERVICE).asInstanceOf[AccessibilityManager]

  @inline def accountManager(implicit context: Context): AccountManager =
    context.getSystemService(Context.ACCOUNT_SERVICE).asInstanceOf[AccountManager]

  @inline def activityManager(implicit context: Context): ActivityManager =
    context.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]

  @inline def alarmManager(implicit context: Context): AlarmManager =
    context.getSystemService(Context.ALARM_SERVICE).asInstanceOf[AlarmManager]

  @inline def audioManager(implicit context: Context): AudioManager =
    context.getSystemService(Context.AUDIO_SERVICE).asInstanceOf[AudioManager]

  @inline def clipboardManager(implicit context: Context): android.text.ClipboardManager =
    context.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[android.text.ClipboardManager]

  class RichClipboardManager(cm: android.text.ClipboardManager) {
    def text_=(txt: CharSequence) = cm.setText(txt)

    def text = cm.getText
  }

  @inline implicit def richClipboardManager(cm: android.text.ClipboardManager): RichClipboardManager = new RichClipboardManager(cm)

  @inline def connectivityManager(implicit context: Context): ConnectivityManager =
    context.getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]

  @inline def devicePolicyManager(implicit context: Context): DevicePolicyManager =
    context.getSystemService(Context.DEVICE_POLICY_SERVICE).asInstanceOf[DevicePolicyManager]


  @inline def dropBoxManager(implicit context: Context): DropBoxManager =
    context.getSystemService(Context.DROPBOX_SERVICE).asInstanceOf[DropBoxManager]


  @inline def inputMethodManager(implicit context: Context): InputMethodManager =
    context.getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]

  @inline def keyguardManager(implicit context: Context): KeyguardManager =
    context.getSystemService(Context.KEYGUARD_SERVICE).asInstanceOf[KeyguardManager]

  @inline def layoutInflater(implicit context: Context): LayoutInflater =
    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  @inline def locationManager(implicit context: Context): LocationManager =
    context.getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]



  @inline def notificationManager(implicit context: Context): NotificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]


  @inline def powerManager(implicit context: Context): PowerManager =
    context.getSystemService(Context.POWER_SERVICE).asInstanceOf[PowerManager]

  @inline def searchManager(implicit context: Context): SearchManager =
    context.getSystemService(Context.SEARCH_SERVICE).asInstanceOf[SearchManager]

  @inline def sensorManager(implicit context: Context): SensorManager =
    context.getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]


  @inline def telephonyManager(implicit context: Context): TelephonyManager =
    context.getSystemService(Context.TELEPHONY_SERVICE).asInstanceOf[TelephonyManager]


  @inline def uiModeManager(implicit context: Context): UiModeManager =
    context.getSystemService(Context.UI_MODE_SERVICE).asInstanceOf[UiModeManager]


  @inline def vibrator(implicit context: Context): Vibrator =
    context.getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]

  @inline def wallpaperManager(implicit context: Context): WallpaperManager =
    context.getSystemService(Context.WALLPAPER_SERVICE).asInstanceOf[WallpaperManager]


  @inline def wifiManager(implicit context: Context): WifiManager =
    context.getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]

  @inline def windowManager(implicit context: Context): WindowManager =
    context.getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]

  trait SContext extends Context with TagUtil {
    implicit val ctx = this

    def startActivity[T: ClassManifest] {
      startActivity(SIntent[T])
    }

    def startService[T: ClassManifest] {
      startService(SIntent[T])
    }

    def stopService[T: ClassManifest] {
      stopService(SIntent[T])
    }
  }

  /**
   * Provides handler instance and runOnUiThread() utility method.
   */
    lazy val handler = new Handler(Looper.getMainLooper)

    lazy val uiThread = Looper.getMainLooper.getThread

    def runOnUiThread[T >: Null](f: => T):T = {
      if(uiThread == Thread.currentThread) {
        return f
      } else {
        handler.post(new Runnable() {
          def run() {
            f
          }
        })
        return null
      }
    }

  trait UnregisterReceiver extends Context {
    val receiverList = new ArrayBuffer[BroadcastReceiver]()

    protected def unregister() {
      Log.i("ScalaUtils", "Unregister " + receiverList.size + " BroadcastReceivers.")
      for (receiver <- receiverList) try {
        unregisterReceiver(receiver)
      } catch {
        // Suppress "Receiver not registered" exception
        // Refer to http://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android
        case e: IllegalArgumentException => e.printStackTrace()
      }
    }
  }

  /**
   * Automatically unregisters BroadcastReceiver when onDestroy() called
   */
  trait UnregisterReceiverService extends Service with UnregisterReceiver {
    override def registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter): android.content.Intent = {
      receiverList += receiver
      super.registerReceiver(receiver, filter)
    }

    override def onDestroy() {
      unregister()
      super.onDestroy()
    }
  }

  /**
   * Automatically unregisters BroadcastReceiver when onDestroy() called
   */
  trait UnregisterReceiverActivity extends SActivity with UnregisterReceiver {
    // TODO: can we merge UnregisterReceiverActivity and UnregisterReceiverService?
    // Please submit a patch if you know a better solution.
    override def registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter): android.content.Intent = {
      receiverList += receiver
      super.registerReceiver(receiver, filter)
    }

    override def onDestroy() {
      unregister()
      super.onDestroy()
    }
  }

  /**
   * Follows a parent's action of onBackPressed().
   * When an activity is a tab that hosted by TabActivity, you may want a common back-button action for each tab.
   *
   * Please refer http://stackoverflow.com/questions/2796050/key-events-in-tabactivities
   */
  trait FollowParentBackButton extends SActivity {
    override def onBackPressed() {
      val p = getParent
      if (p != null) p.onBackPressed()
    }
  }

  /**
   * Turn screen on and show the activity even if the screen is locked.
   * This is useful when notifying some important information.
   */
  trait ScreenOnActivity extends SActivity {
    override def onCreate(savedInstanceState: Bundle) {
      super.onCreate(savedInstanceState)
      getWindow.addFlags(FLAG_DISMISS_KEYGUARD | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON)
    }
  }

implicit def func2DialogOnClickListener[F](f: (DialogInterface, Int) => F): DialogInterface.OnClickListener =
  new DialogInterface.OnClickListener {
    def onClick(dialog: DialogInterface, which: Int) {
      f(dialog, which)
    }
  }

implicit def lazy2DialogOnClickListener[F](f: => F): DialogInterface.OnClickListener =
  new DialogInterface.OnClickListener {
    def onClick(dialog: DialogInterface, which: Int) {
      f
    }
  }

  @inline private def loggingText(str: String, t: Throwable) = str + (if (t == null) "" else "\n" + Log.getStackTraceString(t))

  @inline def verbose(str: => String, t: Throwable = null)(implicit tag: LoggerTag) {
    if (Log.isLoggable(tag.tag, Log.VERBOSE)) Log.v(tag.tag, loggingText(str, t))
  }

  @inline def debug(str: => String, t: Throwable = null)(implicit tag: LoggerTag) {
    if (Log.isLoggable(tag.tag, Log.DEBUG)) Log.d(tag.tag, loggingText(str, t))
  }

  @inline def info(str: => String, t: Throwable = null)(implicit tag: LoggerTag) {
    if (Log.isLoggable(tag.tag, Log.INFO)) Log.i(tag.tag, loggingText(str, t))
  }

  @inline def warn(str: => String, t: Throwable = null)(implicit tag: LoggerTag) {
    if (Log.isLoggable(tag.tag, Log.WARN)) Log.w(tag.tag, loggingText(str, t))
  }

  @inline def error(str: => String, t: Throwable = null)(implicit tag: LoggerTag) {
    if (Log.isLoggable(tag.tag, Log.ERROR)) Log.e(tag.tag, loggingText(str, t))
  }

  @inline def wtf(str: => String, t: Throwable = null)(implicit tag: LoggerTag) {
    if (Log.isLoggable(tag.tag, Log.ASSERT)) Log.wtf(tag.tag, loggingText(str, t))
  }

}


