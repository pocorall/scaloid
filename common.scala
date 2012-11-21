
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

    @inline def backgroundResource_=(param: Int) = {
      base.setBackgroundResource(param)
      base
    }

    @inline def backgroundResource(param: Int) = backgroundResource_=(param)

    @noEquivalentGetterExists
    @inline def backgroundResource: Int = 0

    @inline def clickable_=(param: Boolean) = {
      base.setClickable(param)
      base
    }

    @inline def clickable(param: Boolean) = clickable_=(param)

    @noEquivalentGetterExists
    @inline def clickable: Boolean = true

    @inline def contentDescription_=(param: CharSequence) = {
      base.setContentDescription(param)
      base
    }

    @inline def contentDescription(param: CharSequence) = contentDescription_=(param)

    @inline def contentDescription: CharSequence = base.getContentDescription

    @inline def drawingCacheQuality_=(param: Int) = {
      base.setDrawingCacheQuality(param)
      base
    }

    @inline def drawingCacheQuality(param: Int) = drawingCacheQuality_=(param)

    @inline def drawingCacheQuality: Int = base.getDrawingCacheQuality

    @inline def scrollbarFadingEnabled_=(param: Boolean) = {
      base.setScrollbarFadingEnabled(param)
      base
    }

    @inline def scrollbarFadingEnabled(param: Boolean) = scrollbarFadingEnabled_=(param)

    @inline def scrollbarFadingEnabled: Boolean = base.isScrollbarFadingEnabled

    @inline def focusable_=(param: Boolean) = {
      base.setFocusable(param)
      base
    }

    @inline def focusable(param: Boolean) = focusable_=(param)

    @noEquivalentGetterExists
    @inline def focusable: Boolean = false

    @inline def focusableInTouchMode_=(param: Boolean) = {
      base.setFocusableInTouchMode(param)
      base
    }

    @inline def focusableInTouchMode(param: Boolean) = focusableInTouchMode_=(param)

    @inline def focusableInTouchMode: Boolean = base.isFocusableInTouchMode

    @inline def hapticFeedbackEnabled_=(param: Boolean) = {
      base.setHapticFeedbackEnabled(param)
      base
    }

    @inline def hapticFeedbackEnabled(param: Boolean) = hapticFeedbackEnabled_=(param)

    @inline def hapticFeedbackEnabled: Boolean = base.isHapticFeedbackEnabled

    @inline def id_=(param: Int) = {
      base.setId(param)
      base
    }

    @inline def id(param: Int) = id_=(param)

    @inline def id: Int = base.getId

    @inline def scrollContainer_=(param: Boolean) = {
      base.setScrollContainer(param)
      base
    }

    @inline def scrollContainer(param: Boolean) = scrollContainer_=(param)

    @noEquivalentGetterExists
    @inline def scrollContainer: Boolean = false

    @inline def keepScreenOn_=(param: Boolean) = {
      base.setKeepScreenOn(param)
      base
    }

    @inline def keepScreenOn(param: Boolean) = keepScreenOn_=(param)

    @inline def keepScreenOn: Boolean = base.getKeepScreenOn

    @inline def longClickable_=(param: Boolean) = {
      base.setLongClickable(param)
      base
    }

    @inline def longClickable(param: Boolean) = longClickable_=(param)

    @inline def longClickable: Boolean = base.isLongClickable

    @inline def minimumHeight_=(param: Int) = {
      base.setMinimumHeight(param)
      base
    }

    @inline def minimumHeight(param: Int) = minimumHeight_=(param)

    @noEquivalentGetterExists
    @inline def minimumHeight: Int = 0

    @inline def minimumWidth_=(param: Int) = {
      base.setMinimumWidth(param)
      base
    }

    @inline def minimumWidth(param: Int) = minimumWidth_=(param)

    @noEquivalentGetterExists
    @inline def minimumWidth: Int = 0

    @inline def nextFocusDownId_=(param: Int) = {
      base.setNextFocusDownId(param)
      base
    }

    @inline def nextFocusDownId(param: Int) = nextFocusDownId_=(param)

    @inline def nextFocusDownId: Int = base.getNextFocusDownId

    @inline def nextFocusLeftId_=(param: Int) = {
      base.setNextFocusLeftId(param)
      base
    }

    @inline def nextFocusLeftId(param: Int) = nextFocusLeftId_=(param)

    @inline def nextFocusLeftId: Int = base.getNextFocusLeftId

    @inline def nextFocusRightId_=(param: Int) = {
      base.setNextFocusRightId(param)
      base
    }

    @inline def nextFocusRightId(param: Int) = nextFocusRightId_=(param)

    @inline def nextFocusRightId: Int = base.getNextFocusRightId

    @inline def nextFocusUpId_=(param: Int) = {
      base.setNextFocusUpId(param)
      base
    }

    @inline def nextFocusUpId(param: Int) = nextFocusUpId_=(param)

    @inline def nextFocusUpId: Int = base.getNextFocusUpId

    @inline def verticalFadingEdgeEnabled_=(param: Boolean) = {
      base.setVerticalFadingEdgeEnabled(param)
      base
    }

    @inline def verticalFadingEdgeEnabled(param: Boolean) = verticalFadingEdgeEnabled_=(param)

    @inline def verticalFadingEdgeEnabled: Boolean = base.isVerticalFadingEdgeEnabled

    @inline def saveEnabled_=(param: Boolean) = {
      base.setSaveEnabled(param)
      base
    }

    @inline def saveEnabled(param: Boolean) = saveEnabled_=(param)

    @inline def saveEnabled: Boolean = base.isSaveEnabled

    @inline def scrollBarStyle_=(param: Int) = {
      base.setScrollBarStyle(param)
      base
    }

    @inline def scrollBarStyle(param: Int) = scrollBarStyle_=(param)

    @inline def scrollBarStyle: Int = base.getScrollBarStyle

    @inline def soundEffectsEnabled_=(param: Boolean) = {
      base.setSoundEffectsEnabled(param)
      base
    }

    @inline def soundEffectsEnabled(param: Boolean) = soundEffectsEnabled_=(param)

    @inline def soundEffectsEnabled: Boolean = base.isSoundEffectsEnabled

    @inline def visibility_=(param: Int) = {
      base.setVisibility(param)
      base
    }

    @inline def visibility(param: Int) = visibility_=(param)

    @inline def visibility: Int = base.getVisibility

    @inline def layoutParams_=(param: LayoutParams) = {
      base.setLayoutParams(param)
      base
    }

    @inline def layoutParams(param: LayoutParams) = layoutParams_=(param)

    @inline def layoutParams: LayoutParams = base.getLayoutParams

    @inline def backgroundColor_=(param: Int) = {
      base.setBackgroundColor(param)
      base
    }

    @inline def backgroundColor(param: Int) = backgroundColor_=(param)

    @noEquivalentGetterExists
    @inline def backgroundColor: Int = 0


    @inline def padding(pad: Int): V = {
      base.setPadding(pad, pad, pad, pad)
      base
    }

    val FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT
    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

    def layout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (View) => LP): LP = {
      defaultLayoutParam(base)
    }

    def matchLayout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (View) => LP): LP = {
      val lp = defaultLayoutParam(base)
      lp.height = MATCH_PARENT
      lp.width = MATCH_PARENT
      lp
    }

    def wrapLayout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (View) => LP): LP = {
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
    @inline def contentView_=(param: View) = {
      base.setContentView(param)
      base
    }

    @inline def contentView(param: View) = contentView_=(param)

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

    @inline def autoLinkMask_=(param: Int) = {
      base.setAutoLinkMask(param)
      base
    }

    @inline def autoLinkMask(param: Int) = autoLinkMask_=(param)

    @inline def autoLinkMask: Int = base.getAutoLinkMask

    @inline def cursorVisible_=(param: Boolean) = {
      base.setCursorVisible(param)
      base
    }

    @inline def cursorVisible(param: Boolean) = cursorVisible_=(param)

    @noEquivalentGetterExists
    @inline def cursorVisible: Boolean = false

    @inline def compoundDrawablePadding_=(param: Int) = {
      base.setCompoundDrawablePadding(param)
      base
    }

    @inline def compoundDrawablePadding(param: Int) = compoundDrawablePadding_=(param)

    @inline def compoundDrawablePadding: Int = base.getCompoundDrawablePadding

    @inline def inputExtras_=(param: Int) = {
      base.setInputExtras(param)
      base
    }

    @inline def inputExtras(param: Int) = inputExtras_=(param)

    @noEquivalentGetterExists
    @inline def inputExtras: Int = 0

    @inline def ems_=(param: Int) = {
      base.setEms(param)
      base
    }

    @inline def ems(param: Int) = ems_=(param)

    @noEquivalentGetterExists
    @inline def ems: Int = 0

    @inline def typeface_=(param: Typeface) = {
      base.setTypeface(param)
      base
    }

    @inline def typeface(param: Typeface) = typeface_=(param)

    @inline def typeface: Typeface = base.getTypeface

    @inline def freezesText_=(param: Boolean) = {
      base.setFreezesText(param)
      base
    }

    @inline def freezesText(param: Boolean) = freezesText_=(param)

    @inline def freezesText: Boolean = base.getFreezesText

    @inline def gravity_=(param: Int) = {
      base.setGravity(param)
      base
    }

    @inline def gravity(param: Int) = gravity_=(param)

    @inline def gravity: Int = base.getGravity

    @inline def height_=(param: Int) = {
      base.setHeight(param)
      base
    }

    @inline def height(param: Int) = height_=(param)

    @inline def height: Int = base.getHeight

    @inline def hint_=(param: CharSequence) = {
      base.setHint(param)
      base
    }

    @inline def hint(param: CharSequence) = hint_=(param)

    @noEquivalentGetterExists
    @inline def hint: CharSequence = ""

    @inline def imeOptions_=(param: Int) = {
      base.setImeOptions(param)
      base
    }

    @inline def imeOptions(param: Int) = imeOptions_=(param)

    @inline def imeOptions: Int = base.getImeOptions

    @inline def includeFontPadding_=(param: Boolean) = {
      base.setIncludeFontPadding(param)
      base
    }

    @inline def includeFontPadding(param: Boolean) = includeFontPadding_=(param)

    @noEquivalentGetterExists
    @inline def includeFontPadding: Boolean = false

    @inline def rawInputType_=(param: Int) = {
      base.setRawInputType(param)
      base
    }

    @inline def rawInputType(param: Int) = rawInputType_=(param)

    @noEquivalentGetterExists
    @inline def rawInputType: Int = 0

    @inline def lines_=(param: Int) = {
      base.setLines(param)
      base
    }

    @inline def lines(param: Int) = lines_=(param)

    @noEquivalentGetterExists
    @inline def lines: Int = 0

    @inline def linksClickable_=(param: Boolean) = {
      base.setLinksClickable(param)
      base
    }

    @inline def linksClickable(param: Boolean) = linksClickable_=(param)

    @inline def linksClickable: Boolean = base.getLinksClickable

    @inline def marqueeRepeatLimit_=(param: Int) = {
      base.setMarqueeRepeatLimit(param)
      base
    }

    @inline def marqueeRepeatLimit(param: Int) = marqueeRepeatLimit_=(param)

    @noEquivalentGetterExists
    @inline def marqueeRepeatLimit: Int = 0

    @inline def maxEms_=(param: Int) = {
      base.setMaxEms(param)
      base
    }

    @inline def maxEms(param: Int) = maxEms_=(param)

    @noEquivalentGetterExists
    @inline def maxEms: Int = 0

    @inline def maxHeight_=(param: Int) = {
      base.setMaxHeight(param)
      base
    }

    @inline def maxHeight(param: Int) = maxHeight_=(param)

    @noEquivalentGetterExists
    @inline def maxHeight: Int = 0

    @inline def filters_=(param: Array[InputFilter]) = {
      base.setFilters(param)
      base
    }

    @inline def filters(param: Array[InputFilter]) = filters_=(param)

    @noEquivalentGetterExists
    @inline def filters: Array[InputFilter] = null

    @inline def maxLines_=(param: Int) = {
      base.setMaxLines(param)
      base
    }

    @inline def maxLines(param: Int) = maxLines_=(param)

    @noEquivalentGetterExists
    @inline def maxLines: Int = 0

    @inline def maxWidth_=(param: Int) = {
      base.setMaxWidth(param)
      base
    }

    @inline def maxWidth(param: Int) = maxWidth_=(param)

    @noEquivalentGetterExists
    @inline def maxWidth: Int = 0

    @inline def minEms_=(param: Int) = {
      base.setMinEms(param)
      base
    }

    @inline def minEms(param: Int) = minEms_=(param)

    @noEquivalentGetterExists
    @inline def minEms: Int = 0

    @inline def minHeight_=(param: Int) = {
      base.setMinHeight(param)
      base
    }

    @inline def minHeight(param: Int) = minHeight_=(param)

    @noEquivalentGetterExists
    @inline def minHeight: Int = 0

    @inline def minLines_=(param: Int) = {
      base.setMinLines(param)
      base
    }

    @inline def minLines(param: Int) = minLines_=(param)

    @noEquivalentGetterExists
    @inline def minLines: Int = 0

    @inline def minWidth_=(param: Int) = {
      base.setMinWidth(param)
      base
    }

    @inline def minWidth(param: Int) = minWidth_=(param)

    @noEquivalentGetterExists
    @inline def minWidth: Int = 0

    @inline def transformationMethod_=(param: TransformationMethod) = {
      base.setTransformationMethod(param)
      base
    }

    @inline def transformationMethod(param: TransformationMethod) = transformationMethod_=(param)

    @inline def transformationMethod: TransformationMethod = base.getTransformationMethod

    @inline def privateImeOptions_=(param: String) = {
      base.setPrivateImeOptions(param)
      base
    }

    @inline def privateImeOptions(param: String) = privateImeOptions_=(param)

    @inline def privateImeOptions: String = base.getPrivateImeOptions

    @inline def horizontallyScrolling_=(param: Boolean) = {
      base.setHorizontallyScrolling(param)
      base
    }

    @inline def horizontallyScrolling(param: Boolean) = horizontallyScrolling_=(param)

    @noEquivalentGetterExists
    @inline def horizontallyScrolling: Boolean = false

    @inline def textSize_=(param: Float) = {
      base.setTextSize(param)
      base
    }

    @inline def textSize(param: Float) = textSize_=(param)

    @inline def textSize: Float = base.getTextSize

    @inline def movementMethod_=(param: MovementMethod) = {
      base.setMovementMethod(param)
      base
    }

    @inline def movementMethod(param: MovementMethod) = movementMethod_=(param)

    @inline def movementMethod: MovementMethod = base.getMovementMethod

    @inline def text_=(param: CharSequence) = {
      base.setText(param)
      base
    }

    @inline def text(param: CharSequence) = text_=(param)

    @inline def text: CharSequence = base.getText

    @inline def linkTextColor_=(param: Int) = {
      base.setLinkTextColor(param)
      base
    }

    @inline def linkTextColor(param: Int) = linkTextColor_=(param)

    @noEquivalentGetterExists
    @inline def linkTextColor: Int = 0

    @inline def inputType_=(param: Int) = {
      base.setInputType(param)
      base
    }

    @inline def inputType(param: Int) = inputType_=(param)

    @inline def inputType: Int = base.getInputType


  }

  class RichMenu(menu: Menu) {
    @inline def +=(txt: CharSequence) = menu.add(txt)
  }

  @inline implicit def menu2RichMenu(menu: Menu) = new RichMenu(menu)


  class RichContextMenu(base: ContextMenu) {
    @inline def headerTitle_=(param: CharSequence) = {
      base.setHeaderTitle(param)
      base
    }

    @inline def headerTitle(param: CharSequence) = headerTitle_=(param)

    @noEquivalentGetterExists
    @inline def headerTitle: CharSequence = ""

  }

  @inline implicit def contextMenu2RichContextMenu(menu: ContextMenu) = new RichContextMenu(menu)

  class $ListView(implicit context: Context) extends ListView(context) with TraitListView[$ListView] {
    def base = this
  }

  trait TraitAbsListView[V <: AbsListView] extends TraitView[V] {
    @inline def cacheColorHint_=(param: Int) = {
      base.setCacheColorHint(param)
      base
    }

    @inline def cacheColorHint(param: Int) = cacheColorHint_=(param)

    @inline def cacheColorHint: Int = base.getCacheColorHint

    @inline def transcriptMode_=(param: Int) = {
      base.setTranscriptMode(param)
      base
    }

    @inline def transcriptMode(param: Int) = transcriptMode_=(param)

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
    @inline def adapter_=(param: ListAdapter) = {
      base.setAdapter(param)
      base
    }

    @inline def adapter(param: ListAdapter) = adapter_=(param)

    @inline def adapter: ListAdapter = base.getAdapter

    @inline def selection_=(param: Int) = {
      base.setSelection(param)
      base
    }

    @inline def selection(param: Int) = selection_=(param)

    @noEquivalentGetterExists
    @inline def selection: Int = 0

    @inline def dividerHeight_=(param: Int) = {
      base.setDividerHeight(param)
      base
    }

    @inline def dividerHeight(param: Int) = dividerHeight_=(param)

    @inline def dividerHeight: Int = base.getDividerHeight

    @inline def divider_=(param: Drawable) = {
      base.setDivider(param)
      base
    }

    @inline def divider(param: Drawable) = divider_=(param)

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
    @inline def foreground_=(param: Drawable) = {
      base.setForeground(param)
      base
    }

    @inline def foreground(param: Drawable) = foreground_=(param)

    @inline def foreground: Drawable = base.getForeground

    @inline def foregroundGravity_=(param: Int) = {
      base.setForegroundGravity(param)
      base
    }

    @inline def foregroundGravity(param: Int) = foregroundGravity_=(param)

    @noEquivalentGetterExists
    @inline def foregroundGravity: Int = 0

    @inline def measureAllChildren_=(param: Boolean) = {
      base.setMeasureAllChildren(param)
      base
    }

    @inline def measureAllChildren(param: Boolean) = measureAllChildren_=(param)

    @noEquivalentGetterExists
    @inline def measureAllChildren: Boolean = false

  }

@inline class $FrameLayout(implicit context: Context) extends FrameLayout(context) with TraitFrameLayout[$FrameLayout] {
  def base = this

  implicit def defaultLayoutParams(v: View): LayoutParams = new LayoutParams(v)

  class LayoutParams(v: View) extends FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams] {
    def base = this

    v.setLayoutParams(this)

    def Gravity(g: Int): LayoutParams = {
      gravity = g
      this
    }

    def end: View = v
  }
}

class RichLinearLayout[V <: LinearLayout](val base: V) extends TraitLinearLayout[V]

@inline implicit def linearLayout2RichLinearLayout[V <: LinearLayout](linearLayout: V) = new RichLinearLayout[V](linearLayout)

  trait TraitLinearLayout[V <: LinearLayout]  extends TraitViewGroup[V] {
    @inline def baselineAligned_=(param: Boolean) = {
      base.setBaselineAligned(param)
      base
    }

    @inline def baselineAligned(param: Boolean) = baselineAligned_=(param)

    @inline def baselineAligned: Boolean = base.isBaselineAligned

    @inline def baselineAlignedChildIndex_=(param: Int) = {
      base.setBaselineAlignedChildIndex(param)
      base
    }

    @inline def baselineAlignedChildIndex(param: Int) = baselineAlignedChildIndex_=(param)

    @inline def baselineAlignedChildIndex: Int = base.getBaselineAlignedChildIndex

    @inline def gravity_=(param: Int) = {
      base.setGravity(param)
      base
    }

    @inline def gravity(param: Int) = gravity_=(param)

    @noEquivalentGetterExists
    @inline def gravity: Int = 0

    @inline def orientation_=(param: Int) = {
      base.setOrientation(param)
      base
    }

    @inline def orientation(param: Int) = orientation_=(param)

    @inline def orientation: Int = base.getOrientation

  }

  @inline class $LinearLayout(implicit context: Context) extends LinearLayout(context) with TraitLinearLayout[$LinearLayout] {
    def base = this

    val VERTICAL = LinearLayout.VERTICAL
    val HORIZONTAL = LinearLayout.HORIZONTAL

    implicit def defaultLayoutParams(v: View): LayoutParams = new LayoutParams(v)

    class LayoutParams(v: View) extends LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams] {
      def base = this

      v.setLayoutParams(this)

      def Weight(w: Float) = {
        weight = w
        this
      }

      def end: View = v
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

