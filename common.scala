
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

  trait TraitView[V <: View] extends ConstantsSupport {
    def base: V

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

    @inline def backgroundResource_=(p: Int) = {
      base.setBackgroundResource(p)
      base
    }

    @inline def backgroundResource(p: Int) = backgroundResource_=(p)

    @noEquivalentGetterExists
    @inline def backgroundResource: Int = 0

    @inline def clickable_=(p: Boolean) = {
      base.setClickable(p)
      base
    }

    @inline def clickable(p: Boolean) = clickable_=(p)

    @noEquivalentGetterExists
    @inline def clickable: Boolean = true

    @inline def contentDescription_=(p: CharSequence) = {
      base.setContentDescription(p)
      base
    }

    @inline def contentDescription(p: CharSequence) = contentDescription_=(p)

    @inline def contentDescription: CharSequence = base.getContentDescription

    @inline def drawingCacheQuality_=(p: Int) = {
      base.setDrawingCacheQuality(p)
      base
    }

    @inline def drawingCacheQuality(p: Int) = drawingCacheQuality_=(p)

    @inline def drawingCacheQuality: Int = base.getDrawingCacheQuality

    @inline def enableScrollbarFading = {base.setScrollbarFadingEnabled(true); base}
    @inline def disableScrollbarFading = {base.setScrollbarFadingEnabled(false); base}
    @inline def scrollbarFadingEnabled_=(p: Boolean) = {
      base.setScrollbarFadingEnabled(p)
      base
    }

    @inline def scrollbarFadingEnabled(p: Boolean) = scrollbarFadingEnabled_=(p)

    @inline def scrollbarFadingEnabled: Boolean = base.isScrollbarFadingEnabled

    @inline def focusable_=(p: Boolean) = {
      base.setFocusable(p)
      base
    }

    @inline def focusable(p: Boolean) = focusable_=(p)

    @noEquivalentGetterExists
    @inline def focusable: Boolean = false

    @inline def focusableInTouchMode_=(p: Boolean) = {
      base.setFocusableInTouchMode(p)
      base
    }

    @inline def focusableInTouchMode(p: Boolean) = focusableInTouchMode_=(p)

    @inline def focusableInTouchMode: Boolean = base.isFocusableInTouchMode

    @inline def enableHapticFeedback = {base.setHapticFeedbackEnabled(true); base}
    @inline def disableHapticFeedback = {base.setHapticFeedbackEnabled(false); base}
    @inline def hapticFeedbackEnabled_=(p: Boolean) = {
      base.setHapticFeedbackEnabled(p)
      base
    }

    @inline def hapticFeedbackEnabled(p: Boolean) = hapticFeedbackEnabled_=(p)

    @inline def hapticFeedbackEnabled: Boolean = base.isHapticFeedbackEnabled

    @inline def id_=(p: Int) = {
      base.setId(p)
      base
    }

    @inline def id(p: Int) = id_=(p)

    @inline def id: Int = base.getId

    @inline def scrollContainer_=(p: Boolean) = {
      base.setScrollContainer(p)
      base
    }

    @inline def scrollContainer(p: Boolean) = scrollContainer_=(p)

    @noEquivalentGetterExists
    @inline def scrollContainer: Boolean = false

    @inline def keepScreenOn_=(p: Boolean) = {
      base.setKeepScreenOn(p)
      base
    }

    @inline def keepScreenOn(p: Boolean) = keepScreenOn_=(p)

    @inline def keepScreenOn: Boolean = base.getKeepScreenOn

    @inline def longClickable_=(p: Boolean) = {
      base.setLongClickable(p)
      base
    }

    @inline def longClickable(p: Boolean) = longClickable_=(p)

    @inline def longClickable: Boolean = base.isLongClickable

    @inline def minimumHeight_=(p: Int) = {
      base.setMinimumHeight(p)
      base
    }

    @inline def minimumHeight(p: Int) = minimumHeight_=(p)

    @noEquivalentGetterExists
    @inline def minimumHeight: Int = 0

    @inline def minimumWidth_=(p: Int) = {
      base.setMinimumWidth(p)
      base
    }

    @inline def minimumWidth(p: Int) = minimumWidth_=(p)

    @noEquivalentGetterExists
    @inline def minimumWidth: Int = 0

    @inline def nextFocusDownId_=(p: Int) = {
      base.setNextFocusDownId(p)
      base
    }

    @inline def nextFocusDownId(p: Int) = nextFocusDownId_=(p)

    @inline def nextFocusDownId: Int = base.getNextFocusDownId

    @inline def nextFocusLeftId_=(p: Int) = {
      base.setNextFocusLeftId(p)
      base
    }

    @inline def nextFocusLeftId(p: Int) = nextFocusLeftId_=(p)

    @inline def nextFocusLeftId: Int = base.getNextFocusLeftId

    @inline def nextFocusRightId_=(p: Int) = {
      base.setNextFocusRightId(p)
      base
    }

    @inline def nextFocusRightId(p: Int) = nextFocusRightId_=(p)

    @inline def nextFocusRightId: Int = base.getNextFocusRightId

    @inline def nextFocusUpId_=(p: Int) = {
      base.setNextFocusUpId(p)
      base
    }

    @inline def nextFocusUpId(p: Int) = nextFocusUpId_=(p)

    @inline def nextFocusUpId: Int = base.getNextFocusUpId

    @inline def enableVerticalFadingEdge = {base.setVerticalFadingEdgeEnabled(true); base}
    @inline def disableVerticalFadingEdge = {base.setVerticalFadingEdgeEnabled(false); base}
    @inline def verticalFadingEdgeEnabled_=(p: Boolean) = {
      base.setVerticalFadingEdgeEnabled(p)
      base
    }

    @inline def verticalFadingEdgeEnabled(p: Boolean) = verticalFadingEdgeEnabled_=(p)

    @inline def verticalFadingEdgeEnabled: Boolean = base.isVerticalFadingEdgeEnabled

    @inline def enableVerticalScrollBar = {base.setVerticalScrollBarEnabled(true); base}
    @inline def disableVerticalScrollBar = {base.setVerticalScrollBarEnabled(false); base}
    @inline def verticalScrollBarEnabled_=(p: Boolean) = {
      base.setVerticalScrollBarEnabled(p)
      base
    }

    @inline def verticalScrollBarEnabled(p: Boolean) = verticalScrollBarEnabled_=(p)

    @inline def verticalScrollBarEnabled: Boolean = base.isVerticalScrollBarEnabled

    @inline def enableSave = {base.setSaveEnabled(true); base}
    @inline def disableSave = {base.setSaveEnabled(false); base}
    @inline def saveEnabled_=(p: Boolean) = {
      base.setSaveEnabled(p)
      base
    }

    @inline def saveEnabled(p: Boolean) = saveEnabled_=(p)

    @inline def saveEnabled: Boolean = base.isSaveEnabled

    @inline def scrollBarStyle_=(p: Int) = {
      base.setScrollBarStyle(p)
      base
    }

    @inline def scrollBarStyle(p: Int) = scrollBarStyle_=(p)

    @inline def scrollBarStyle: Int = base.getScrollBarStyle

    @inline def enableSoundEffects = {base.setSoundEffectsEnabled(true); base}
    @inline def disableSoundEffects = {base.setSoundEffectsEnabled(false); base}
    @inline def soundEffectsEnabled_=(p: Boolean) = {
      base.setSoundEffectsEnabled(p)
      base
    }

    @inline def soundEffectsEnabled(p: Boolean) = soundEffectsEnabled_=(p)

    @inline def soundEffectsEnabled: Boolean = base.isSoundEffectsEnabled

    @inline def visibility_=(p: Int) = {
      base.setVisibility(p)
      base
    }

    @inline def visibility(p: Int) = visibility_=(p)

    @inline def visibility: Int = base.getVisibility

    @inline def layoutParams_=(p: LayoutParams) = {
      base.setLayoutParams(p)
      base
    }

    @inline def layoutParams(p: LayoutParams) = layoutParams_=(p)

    @inline def layoutParams: LayoutParams = base.getLayoutParams

    @inline def backgroundColor_=(p: Int) = {
      base.setBackgroundColor(p)
      base
    }

    @inline def backgroundColor(p: Int) = backgroundColor_=(p)

    @noEquivalentGetterExists
    @inline def backgroundColor: Int = 0

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
  }

  class RichView[V <: View](val base: V) extends TraitView[V]

  @inline implicit def view2RichView[V <: View](base: V) = new RichView[V](base)

  object $EditText {
    def apply(txt: CharSequence)(implicit context: Context): $EditText = new $EditText() text txt

    def apply()(implicit context: Context): $EditText = new $EditText()
  }

  class $EditText(implicit context: Context) extends EditText(context) with TraitTextView[$EditText] {
    def base = this
  }

class RichActivity[V <: Activity](val base: V) extends TraitActivity[V]

@inline implicit def activity2RichActivity[V <: Activity](activity: V) = new RichActivity[V](activity)

  trait TraitActivity[V <: Activity]  extends RunOnUiThread {
    def base: Activity
    @inline def contentView_=(p: View) = {
      base.setContentView(p)
      base
    }

    @inline def contentView(p: View) = contentView_=(p)

    @noEquivalentGetterExists
    @inline def contentView: View = null


    def find[V <: View](id: Int): V = base.findViewById(id).asInstanceOf[V]
  }

  trait $Activity extends Activity with $Context with TraitActivity[$Activity] {
    def base = this
  }

class RichTextView[V <: TextView](val base: V) extends TraitTextView[V]

@inline implicit def textView2RichTextView[V <: TextView](textView: V) = new RichTextView[V](textView)

  trait TraitTextView[V <: TextView]  extends TraitView[V] {
    def base: V
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

    @inline def autoLinkMask: Int = base.getAutoLinkMask

    @inline def cursorVisible_=(p: Boolean) = {
      base.setCursorVisible(p)
      base
    }

    @inline def cursorVisible(p: Boolean) = cursorVisible_=(p)

    @noEquivalentGetterExists
    @inline def cursorVisible: Boolean = false

    @inline def compoundDrawablePadding_=(p: Int) = {
      base.setCompoundDrawablePadding(p)
      base
    }

    @inline def compoundDrawablePadding(p: Int) = compoundDrawablePadding_=(p)

    @inline def compoundDrawablePadding: Int = base.getCompoundDrawablePadding

    @inline def inputExtras_=(p: Int) = {
      base.setInputExtras(p)
      base
    }

    @inline def inputExtras(p: Int) = inputExtras_=(p)

    @noEquivalentGetterExists
    @inline def inputExtras: Int = 0

    @inline def ems_=(p: Int) = {
      base.setEms(p)
      base
    }

    @inline def ems(p: Int) = ems_=(p)

    @noEquivalentGetterExists
    @inline def ems: Int = 0

    @inline def typeface_=(p: Typeface) = {
      base.setTypeface(p)
      base
    }

    @inline def typeface(p: Typeface) = typeface_=(p)

    @inline def typeface: Typeface = base.getTypeface

    @inline def freezesText_=(p: Boolean) = {
      base.setFreezesText(p)
      base
    }

    @inline def freezesText(p: Boolean) = freezesText_=(p)

    @inline def freezesText: Boolean = base.getFreezesText

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @inline def gravity: Int = base.getGravity

    @inline def height_=(p: Int) = {
      base.setHeight(p)
      base
    }

    @inline def height(p: Int) = height_=(p)

    @inline def height: Int = base.getHeight

    @inline def hint_=(p: CharSequence) = {
      base.setHint(p)
      base
    }

    @inline def hint(p: CharSequence) = hint_=(p)

    @noEquivalentGetterExists
    @inline def hint: CharSequence = ""

    @inline def imeOptions_=(p: Int) = {
      base.setImeOptions(p)
      base
    }

    @inline def imeOptions(p: Int) = imeOptions_=(p)

    @inline def imeOptions: Int = base.getImeOptions

    @inline def includeFontPadding_=(p: Boolean) = {
      base.setIncludeFontPadding(p)
      base
    }

    @inline def includeFontPadding(p: Boolean) = includeFontPadding_=(p)

    @noEquivalentGetterExists
    @inline def includeFontPadding: Boolean = false

    @inline def rawInputType_=(p: Int) = {
      base.setRawInputType(p)
      base
    }

    @inline def rawInputType(p: Int) = rawInputType_=(p)

    @noEquivalentGetterExists
    @inline def rawInputType: Int = 0

    @inline def lines_=(p: Int) = {
      base.setLines(p)
      base
    }

    @inline def lines(p: Int) = lines_=(p)

    @noEquivalentGetterExists
    @inline def lines: Int = 0

    @inline def linksClickable_=(p: Boolean) = {
      base.setLinksClickable(p)
      base
    }

    @inline def linksClickable(p: Boolean) = linksClickable_=(p)

    @inline def linksClickable: Boolean = base.getLinksClickable

    @inline def marqueeRepeatLimit_=(p: Int) = {
      base.setMarqueeRepeatLimit(p)
      base
    }

    @inline def marqueeRepeatLimit(p: Int) = marqueeRepeatLimit_=(p)

    @noEquivalentGetterExists
    @inline def marqueeRepeatLimit: Int = 0

    @inline def maxEms_=(p: Int) = {
      base.setMaxEms(p)
      base
    }

    @inline def maxEms(p: Int) = maxEms_=(p)

    @noEquivalentGetterExists
    @inline def maxEms: Int = 0

    @inline def maxHeight_=(p: Int) = {
      base.setMaxHeight(p)
      base
    }

    @inline def maxHeight(p: Int) = maxHeight_=(p)

    @noEquivalentGetterExists
    @inline def maxHeight: Int = 0

    @inline def filters_=(p: Array[InputFilter]) = {
      base.setFilters(p)
      base
    }

    @inline def filters(p: Array[InputFilter]) = filters_=(p)

    @noEquivalentGetterExists
    @inline def filters: Array[InputFilter] = null

    @inline def maxLines_=(p: Int) = {
      base.setMaxLines(p)
      base
    }

    @inline def maxLines(p: Int) = maxLines_=(p)

    @noEquivalentGetterExists
    @inline def maxLines: Int = 0

    @inline def maxWidth_=(p: Int) = {
      base.setMaxWidth(p)
      base
    }

    @inline def maxWidth(p: Int) = maxWidth_=(p)

    @noEquivalentGetterExists
    @inline def maxWidth: Int = 0

    @inline def minEms_=(p: Int) = {
      base.setMinEms(p)
      base
    }

    @inline def minEms(p: Int) = minEms_=(p)

    @noEquivalentGetterExists
    @inline def minEms: Int = 0

    @inline def minHeight_=(p: Int) = {
      base.setMinHeight(p)
      base
    }

    @inline def minHeight(p: Int) = minHeight_=(p)

    @noEquivalentGetterExists
    @inline def minHeight: Int = 0

    @inline def minLines_=(p: Int) = {
      base.setMinLines(p)
      base
    }

    @inline def minLines(p: Int) = minLines_=(p)

    @noEquivalentGetterExists
    @inline def minLines: Int = 0

    @inline def minWidth_=(p: Int) = {
      base.setMinWidth(p)
      base
    }

    @inline def minWidth(p: Int) = minWidth_=(p)

    @noEquivalentGetterExists
    @inline def minWidth: Int = 0

    @inline def transformationMethod_=(p: TransformationMethod) = {
      base.setTransformationMethod(p)
      base
    }

    @inline def transformationMethod(p: TransformationMethod) = transformationMethod_=(p)

    @inline def transformationMethod: TransformationMethod = base.getTransformationMethod

    @inline def privateImeOptions_=(p: String) = {
      base.setPrivateImeOptions(p)
      base
    }

    @inline def privateImeOptions(p: String) = privateImeOptions_=(p)

    @inline def privateImeOptions: String = base.getPrivateImeOptions

    @inline def horizontallyScrolling_=(p: Boolean) = {
      base.setHorizontallyScrolling(p)
      base
    }

    @inline def horizontallyScrolling(p: Boolean) = horizontallyScrolling_=(p)

    @noEquivalentGetterExists
    @inline def horizontallyScrolling: Boolean = false

    @inline def textSize_=(p: Float) = {
      base.setTextSize(p)
      base
    }

    @inline def textSize(p: Float) = textSize_=(p)

    @inline def textSize: Float = base.getTextSize

    @inline def movementMethod_=(p: MovementMethod) = {
      base.setMovementMethod(p)
      base
    }

    @inline def movementMethod(p: MovementMethod) = movementMethod_=(p)

    @inline def movementMethod: MovementMethod = base.getMovementMethod

    @inline def text_=(p: CharSequence) = {
      base.setText(p)
      base
    }

    @inline def text(p: CharSequence) = text_=(p)

    @inline def text: CharSequence = base.getText

    @inline def linkTextColor_=(p: Int) = {
      base.setLinkTextColor(p)
      base
    }

    @inline def linkTextColor(p: Int) = linkTextColor_=(p)

    @noEquivalentGetterExists
    @inline def linkTextColor: Int = 0

    @inline def inputType_=(p: Int) = {
      base.setInputType(p)
      base
    }

    @inline def inputType(p: Int) = inputType_=(p)

    @inline def inputType: Int = base.getInputType


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

  class $ListView(implicit context: Context) extends ListView(context) with TraitListView[$ListView] {
    def base = this
  }

  trait TraitAbsListView[V <: AbsListView] extends TraitView[V] {
    @inline def cacheColorHint_=(p: Int) = {
      base.setCacheColorHint(p)
      base
    }

    @inline def cacheColorHint(p: Int) = cacheColorHint_=(p)

    @inline def cacheColorHint: Int = base.getCacheColorHint

    @inline def transcriptMode_=(p: Int) = {
      base.setTranscriptMode(p)
      base
    }

    @inline def transcriptMode(p: Int) = transcriptMode_=(p)

    @inline def transcriptMode: Int = base.getTranscriptMode


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

  trait TraitListView[V <: ListView]  extends TraitAbsListView[V] {
    @inline def adapter_=(p: ListAdapter) = {
      base.setAdapter(p)
      base
    }

    @inline def adapter(p: ListAdapter) = adapter_=(p)

    @inline def adapter: ListAdapter = base.getAdapter

    @inline def selection_=(p: Int) = {
      base.setSelection(p)
      base
    }

    @inline def selection(p: Int) = selection_=(p)

    @noEquivalentGetterExists
    @inline def selection: Int = 0

    @inline def dividerHeight_=(p: Int) = {
      base.setDividerHeight(p)
      base
    }

    @inline def dividerHeight(p: Int) = dividerHeight_=(p)

    @inline def dividerHeight: Int = base.getDividerHeight

    @inline def divider_=(p: Drawable) = {
      base.setDivider(p)
      base
    }

    @inline def divider(p: Drawable) = divider_=(p)

    @inline def divider: Drawable = base.getDivider

  }

class RichViewGroup[V <: ViewGroup](val base: V) extends TraitViewGroup[V]

@inline implicit def viewGroup2RichViewGroup[V <: ViewGroup](viewGroup: V) = new RichViewGroup[V](viewGroup)

  trait TraitViewGroup[V <: ViewGroup]  extends TraitView[V] {
    @inline def +=(v: View): V = {
      base.addView(v)
      base
    }
  }

  trait ViewGroupLayoutParams[LP <: ViewGroupLayoutParams[_]] extends ViewGroup.LayoutParams {
    def base: LP

    def Width(w: Int): LP = {
      width = w
      base
    }

    def Height(h: Int): LP = {
      height = h
      base
    }

    def end: View
  }

  trait ViewGroupMarginLayoutParams[LP <: ViewGroupMarginLayoutParams[_]] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[LP] {
    def marginBottom(size: Int): LP = {
      bottomMargin = size
      base
    }

    def marginTop(size: Int): LP = {
      topMargin = size
      base
    }

    def marginLeft(size: Int): LP = {
      leftMargin = size
      base
    }

    def marginRight(size: Int): LP = {
      rightMargin = size
      base
    }
  }

class RichFrameLayout[V <: FrameLayout](val base: V) extends TraitFrameLayout[V]

@inline implicit def frameLayout2RichFrameLayout[V <: FrameLayout](frameLayout: V) = new RichFrameLayout[V](frameLayout)

  trait TraitFrameLayout[V <: FrameLayout]  extends TraitViewGroup[V] {
    @inline def foreground_=(p: Drawable) = {
      base.setForeground(p)
      base
    }

    @inline def foreground(p: Drawable) = foreground_=(p)

    @inline def foreground: Drawable = base.getForeground

    @inline def foregroundGravity_=(p: Int) = {
      base.setForegroundGravity(p)
      base
    }

    @inline def foregroundGravity(p: Int) = foregroundGravity_=(p)

    @noEquivalentGetterExists
    @inline def foregroundGravity: Int = 0

    @inline def measureAllChildren_=(p: Boolean) = {
      base.setMeasureAllChildren(p)
      base
    }

    @inline def measureAllChildren(p: Boolean) = measureAllChildren_=(p)

    @noEquivalentGetterExists
    @inline def measureAllChildren: Boolean = false

  }

@inline class $FrameLayout(implicit context: Context) extends FrameLayout(context) with TraitFrameLayout[$FrameLayout] {
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

  trait TraitLinearLayout[V <: LinearLayout]  extends TraitViewGroup[V] {
    @inline def baselineAligned_=(p: Boolean) = {
      base.setBaselineAligned(p)
      base
    }

    @inline def baselineAligned(p: Boolean) = baselineAligned_=(p)

    @inline def baselineAligned: Boolean = base.isBaselineAligned

    @inline def baselineAlignedChildIndex_=(p: Int) = {
      base.setBaselineAlignedChildIndex(p)
      base
    }

    @inline def baselineAlignedChildIndex(p: Int) = baselineAlignedChildIndex_=(p)

    @inline def baselineAlignedChildIndex: Int = base.getBaselineAlignedChildIndex

    @inline def gravity_=(p: Int) = {
      base.setGravity(p)
      base
    }

    @inline def gravity(p: Int) = gravity_=(p)

    @noEquivalentGetterExists
    @inline def gravity: Int = 0

    @inline def orientation_=(p: Int) = {
      base.setOrientation(p)
      base
    }

    @inline def orientation(p: Int) = orientation_=(p)

    @inline def orientation: Int = base.getOrientation

  }

  @inline class $LinearLayout(implicit context: Context) extends LinearLayout(context) with TraitLinearLayout[$LinearLayout] {
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

@inline implicit def EditTextPreference2RichEditTextPreference[V <: EditTextPreference](EditTextPreference: V) = new RichEditTextPreference[V](EditTextPreference)

  trait TraitEditTextPreference[V <: EditTextPreference]  {
  def base: V
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

  }

  @inline class $EditTextPreference(implicit context: Context) extends EditTextPreference(context) with TraitEditTextPreference[$EditTextPreference] {
    def base = this

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

  @inline def $Intent[T](implicit context: Context, mt: ClassManifest[T]) = new Intent(context, mt.erasure)

  @inline def $Intent[T](action: String)(implicit context: Context, mt: ClassManifest[T]): Intent = $Intent[T].setAction(action)

  object $TextView {
    def apply(txt: CharSequence)(implicit context: Context): $TextView = new $TextView text txt
  }

  class $TextView(implicit context: Context) extends TextView(context) with TraitTextView[$TextView] {
    def base = this
  }

  object $Button {
    def apply(text: CharSequence, onClickListener: OnClickListener = {})(implicit context: Context): $Button = {
      val button = new $Button()(context)
      button.text = text
      button.setOnClickListener(onClickListener)
      button
    }
  }

  class $Button(implicit context: Context) extends Button(context) with TraitTextView[$Button] {
    def base = this
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
    PendingIntent.getActivity(context, 0, $Intent[T], 0)

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

  trait $Context extends Context with TagUtil with RunOnUiThread {
    implicit val context = this

    def startActivity[T: ClassManifest] {
      startActivity($Intent[T])
    }

    def startService[T: ClassManifest] {
      startService($Intent[T])
    }

    def stopService[T: ClassManifest] {
      stopService($Intent[T])
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

