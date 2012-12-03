
/*
 *
 *
 *
 * Less painful Android development with Scala
 *
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 *
 * Copyright 2012 Sung-Ho Lee
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
import android.net.{ConnectivityManager, Uri}
import android.os._
import android.media.{AudioManager, RingtoneManager}
import collection.mutable.ArrayBuffer
import android.util.Log

import android.text.{InputFilter, Editable, TextWatcher, ClipboardManager}
import android.view.accessibility.AccessibilityManager
import android.accounts.AccountManager
import android.view.inputmethod.InputMethodManager
import android.location.LocationManager
import android.hardware.SensorManager
import android.telephony.TelephonyManager
import android.net.wifi.WifiManager
import android.content._
import android.widget._
import android.preference._
import android.preference.Preference._
import android.view.WindowManager.LayoutParams._
import android.view.View._
import android.graphics.drawable.Drawable
import java.lang.CharSequence
import scala.Int
import android.view.ContextMenu.ContextMenuInfo
import android.text.method._
import annotation.target.{beanGetter, getter}
import android.view.ViewGroup.LayoutParams
import android.widget.TextView.OnEditorActionListener
import android.graphics.Typeface


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

implicit def func2ViewOnClickListener[F](f: View => F): View.OnClickListener =
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


    val FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT
    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

    def layout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (V) => LP): LP = {
      defaultLayoutParam(base)
    }

    def matchLayout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (V) => LP): LP = {
      val lp = defaultLayoutParam(base)
      lp.height = MATCH_PARENT
      lp.width = MATCH_PARENT
      lp
    }

    def wrapLayout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (V) => LP): LP = {
      val lp = defaultLayoutParam(base)
      lp.height = WRAP_CONTENT
      lp.width = WRAP_CONTENT
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
    def apply()(implicit context: Context): SEditText = new SEditText
    def apply(txt: CharSequence)(implicit context: Context): SEditText = new SEditText() text txt
  }

  class RichActivity[V <: Activity](val base: V) extends TraitActivity[V]

  @inline implicit def activity2RichActivity[V <: Activity](activity: V) = new RichActivity[V](activity)

  trait TraitActivity[V <: Activity] extends RunOnUiThread {

    @inline def contentView_=(p: View) = {
      base.setContentView(p)
      base
    }

    @inline def contentView(p: View) = contentView_=(p)

    @noEquivalentGetterExists
    @inline def contentView: View = null

    def base: Activity

    def find[V <: View](id: Int): V = base.findViewById(id).asInstanceOf[V]
  }

  trait SActivity extends Activity with SContext with TraitActivity[SActivity] {
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

    @inline def linkTextColor_=(p: android.content.res.ColorStateList) = {
      base.setLinkTextColor(p)
      base
    }

    @inline def linkTextColor(p: android.content.res.ColorStateList) = linkTextColor_=(p)

    @noEquivalentGetterExists
    @inline def linkTextColor: android.content.res.ColorStateList = defaultValue[android.content.res.ColorStateList]

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
    def apply()(implicit context: Context): STextView = new STextView
    def apply(txt: CharSequence)(implicit context: Context): STextView = new STextView text txt
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
    def apply()(implicit context: Context): SListView = new SListView
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

  trait ViewGroupLayoutParams[LP <: ViewGroupLayoutParams[_]] extends ViewGroup.LayoutParams {
    def base: LP

    def Width(w: Int) = {
      width = w
      base
    }

    def Height(h: Int) = {
      height = h
      base
    }

    def end: View
  }

  trait ViewGroupMarginLayoutParams[LP <: ViewGroupMarginLayoutParams[_]] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[LP] {
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

  class LayoutParams[V <: View](v: V) extends FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V]] {
    def base = this

    v.setLayoutParams(this)

    def Gravity(g: Int) = {
      gravity = g
      this
    }

    def end: V = v
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

    class LayoutParams[V <: View](v: V) extends LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V]] {
      def base = this

      v.setLayoutParams(this)

      def Weight(w: Float) = {
        weight = w
        this
      }

      def end: V = v
    }

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
    def apply()(implicit context: Context): SButton = new SButton
    def apply(text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context): SButton = {
      val button = new SButton()(context)
      button.text = text
      button.setOnClickListener(onClickListener)
      button
    }
  }

  @inline def toast(message: String)(implicit context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  @inline def longToast(message: String)(implicit context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
  }

  @inline def spinnerDialog(title: String, message: String)(implicit context: Context): ProgressDialog =
    ProgressDialog.show(context, title, message, true)

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

  @inline def clipboardManager(implicit context: Context): ClipboardManager =
    context.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]

  class RichClipboardManager(cm: ClipboardManager) {
    def text_=(txt: CharSequence) = cm.setText(txt)

    def text = cm.getText
  }

  @inline implicit def richClipboardManager(cm: ClipboardManager): RichClipboardManager = new RichClipboardManager(cm)

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

  trait SContext extends Context with TagUtil with RunOnUiThread {
    implicit val context = this

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
  trait RunOnUiThread {
    val handler = new Handler(Looper.getMainLooper)

    def runOnUiThread(f: => Unit) {
      handler.post(new Runnable() {
        def run() {
          f
        }
      })
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
  trait UnregisterReceiverActivity extends Activity with UnregisterReceiver {
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
  trait FollowParentBackButton extends Activity {
    override def onBackPressed() {
      val p = getParent
      if (p != null) p.onBackPressed()
    }
  }

  /**
   * Turn screen on and show the activity even if the screen is locked.
   * This is useful when notifying some important information.
   */
  trait ScreenOnActivity extends Activity {
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

