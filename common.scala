/*
 *
 *
 *
 * Less painful Android development with Scala
 *
 *
 * https://github.com/pocorall/android-scala-common
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

package net.pocorall.android

import android.app._
import admin.DevicePolicyManager
import android.view._
import android.net.{ConnectivityManager, Uri}
import android.os._
import android.media.{AudioManager, RingtoneManager}
import collection.mutable.ArrayBuffer
import android.util.Log

import android.text.{Editable, TextWatcher, ClipboardManager}
import android.view.accessibility.AccessibilityManager
import android.accounts.AccountManager
import android.view.inputmethod.InputMethodManager
import android.location.LocationManager
import android.nfc.NfcManager
import android.hardware.SensorManager
import storage.StorageManager
import android.telephony.TelephonyManager
import android.net.wifi.WifiManager
import android.content
import content._
import android.widget._
import android.preference.PreferenceManager
import android.view.WindowManager.LayoutParams._
import android.view.View._
import android.graphics.drawable.Drawable
import java.lang.CharSequence
import scala.Int
import android.view.ContextMenu.ContextMenuInfo
import android.text.method.MovementMethod
import annotation.target.{beanGetter, getter}
import android.view.ViewGroup.LayoutParams
import net.pocorall.android.LoggerTag


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

  trait TraitView[V <: View] {
    def view: V

    @inline def onClick(f: => Unit): V = {
      view.setOnClickListener(new OnClickListener {
        def onClick(view: View) {
          f
        }
      })
      view
    }

    @inline def onClick(f: View => Unit): V = {
      view.setOnClickListener(new OnClickListener {
        def onClick(view: View) {
          f(view)
        }
      })
      view
    }

    @inline def onLongClick(f: => Boolean): V = {
      view.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(view: View): Boolean = {
          f
        }
      })
      view
    }

    @inline def onLongClick(f: View => Boolean): V = {
      view.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(view: View): Boolean = {
          f(view)
        }
      })
      view
    }

    @inline def onCreateContextMenu(f: => Unit): V = {
      view.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
          f
        }
      })
      view
    }

    @inline def onCreateContextMenu(f: (ContextMenu, V, ContextMenuInfo) => Unit): V = {
      view.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
          f(menu, v.asInstanceOf[V], menuInfo)
        }
      })
      view
    }

    @inline def onFocusChanged(f: => Unit): V = {
      view.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(v: View, hasFocus: Boolean) {
          f
        }
      })
      view
    }

    @inline def onFocusChanged(f: (View, Boolean) => Unit): V = {
      view.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(v: View, hasFocus: Boolean) {
          f(v, hasFocus)
        }
      })
      view
    }

    @inline def onKey(f: => Boolean): V = {
      view.setOnKeyListener(new View.OnKeyListener {
        def onKey(v: View, keyCode: Int, event: KeyEvent) = f
      })
      view
    }

    @inline def onKey(f: (View, Int, KeyEvent) => Boolean): V = {
      view.setOnKeyListener(new View.OnKeyListener {
        def onKey(v: View, keyCode: Int, event: KeyEvent) = f(v, keyCode, event)
      })
      view
    }

    @inline def onTouch(f: => Boolean): V = {
      view.setOnTouchListener(new OnTouchListener {
        def onTouch(v: View, event: MotionEvent) = f
      })
      view
    }

    @inline def onTouch(f: (View, MotionEvent) => Boolean): V = {
      view.setOnTouchListener(new OnTouchListener {
        def onTouch(v: View, event: MotionEvent) = f(v, event)
      })
      view
    }

    @inline def backgroundResource_=(resId: Int): V = {
      view.setBackgroundResource(resId)
      view
    }

    @inline def backgroundResource(resId: Int) = backgroundResource_=(resId)

    @noEquivalentGetterExists
    @inline def backgroundResource: Int = 0

    @inline def clickable_=(click: Boolean): V = {
      view.setClickable(click)
      view
    }

    @inline def clickable(click: Boolean) = clickable_=(click)

    @noEquivalentGetterExists
    @inline def clickable: Boolean = true

    @inline def contentDescription_=(desc: CharSequence): V = {
      view.setContentDescription(desc)
      view
    }

    @inline def contentDescription(desc: CharSequence) = contentDescription_=(desc)

    @inline def contentDescription = view.getContentDescription

    @inline def drawingCacheQuality_=(quality: Int): V = {
      view.setDrawingCacheQuality(quality)
      view
    }

    @inline def drawingCacheQuality(quality: Int) = drawingCacheQuality_=(quality)

    @inline def drawingCacheQuality = view.getDrawingCacheQuality

    @inline def scrollbarFadingEnabled_=(fadeScrollbars: Boolean): V = {
      view.setScrollbarFadingEnabled(fadeScrollbars)
      view
    }

    @inline def scrollbarFadingEnabled(fadeScrollbars: Boolean) = scrollbarFadingEnabled_=(fadeScrollbars)

    @noEquivalentGetterExists
    @inline def scrollbarFadingEnabled = false


    @inline def layoutParams_=(lp: LayoutParams): V = {
      view.setLayoutParams(lp)
      view
    }

    @inline def layoutParams(lp: LayoutParams) = layoutParams_=(lp)

    @inline def layoutParams = view.getLayoutParams

    @inline def backgroundColor_=(color: Int): V = {
      view.setBackgroundColor(color)
      view
    }

    @inline def backgroundColor(color: Int) = backgroundColor_=(color)

    @noEquivalentGetterExists
    @inline def backgroundColor: Int = 0

    def layout[LP <: ViewGroupLayoutParams[_]](implicit defaultLayoutParam: (View) => LP): LP = {
      defaultLayoutParam(view)
    }
  }

  class RichView[V <: View](val view: V) extends TraitView[V]

  @inline implicit def view2RichView[V <: View](view: V) = new RichView[V](view)

  class $EditText(implicit context: Context) extends EditText(context) with TraitTextView[$EditText] {
    def view = this
  }

  trait TraitActivity {
    def activity: Activity

    @inline def contentView_=(view: View): Activity = {
      activity.setContentView(view)
      activity
    }

    @inline def contentView(view: View) = contentView_=(view)

    @noEquivalentGetterExists
    @inline def contentView: View = null
  }

  class RichActivity(val activity: Activity) extends TraitActivity

  @inline implicit def activity2RichActivity(activity: Activity) = new RichActivity(activity)

  trait TraitTextView[V <: TextView] extends TraitView[V] {
    def view: V

    @inline def beforeTextChanged(f: (CharSequence, Int, Int, Int) => Unit): V = {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          f(s, start, before, count)
        }

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {}
      })
      view
    }

    @inline def beforeTextChanged(f: => Unit): V = {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          f
        }

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {}
      })
      view
    }

    @inline def onTextChanged(f: => Unit): V = {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {
          f
        }

        def afterTextChanged(p1: Editable) {}
      })
      view
    }


    @inline def onTextChanged(f: (CharSequence, Int, Int, Int) => Unit): V = {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {
          f(p1, p2, p3, p4)
        }

        def afterTextChanged(p1: Editable) {}
      })
      view
    }

    @inline def afterTextChanged(f: Editable => Unit): V = {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {
          f(p1)
        }
      })
      view
    }

    @inline def afterTextChanged(f: => Unit): V = {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {
          f
        }
      })
      view
    }

    @inline def onEditorAction(f: => Boolean): V = {
      view.setOnEditorActionListener(new TextView.OnEditorActionListener {
        def onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean = {
          f
        }
      })
      view
    }

    @inline def onEditorAction(f: (TextView, Int, KeyEvent) => Boolean): V = {
      view.setOnEditorActionListener(new TextView.OnEditorActionListener {
        def onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean = {
          f(view, actionId, event)
        }
      })
      view
    }

    @inline def textSize_=(size: Float): V = {
      view.setTextSize(size)
      view
    }

    @inline def textSize(size: Float) = textSize_=(size)

    @inline def textSize: Float = view.getTextSize

    @inline def gravity_=(g: Int): V = {
      view.setGravity(g)
      view
    }

    @inline def gravity(g: Int) = gravity_=(g)

    @inline def gravity = view.getGravity

    @inline def movementMethod_=(movement: MovementMethod): V = {
      view.setMovementMethod(movement)
      view
    }

    @inline def movementMethod(movement: MovementMethod) = movementMethod_=(movement)

    @inline def movementMethod: MovementMethod = view.getMovementMethod

    @inline def text_=(txt: CharSequence): V = {
      view.setText(txt)
      view
    }

    @inline def text(txt: CharSequence) = text_=(txt)

    @inline def text: CharSequence = view.getText

    @inline def maxHeight_=(height: Int): V = {
      view.setMaxHeight(height)
      view
    }

    @inline def maxHeight(height: Int) = maxHeight_=(height)

    @noEquivalentGetterExists
    @inline def maxHeight: Int = 0 // view.getMaxHeight // higher than API Level 16

    @inline def maxLines_=(line: Int): V = {
      view.setMaxLines(line)
      view
    }

    @inline def maxLines(line: Int) = maxLines_=(line)

    @noEquivalentGetterExists
    @inline def maxLines: Int = 0 // view.getMaxLines  // available in API Level 16 or higher

    @inline def linkTextColor_=(color: Int): V = {
      view.setLinkTextColor(color)
      view
    }

    @inline def linkTextColor(color: Int) = linkTextColor_=(color)

    @noEquivalentGetterExists
    @inline def linkTextColor: Int = 0
  }

  class RichTextView[V <: TextView](val view: V) extends TraitTextView[V]

  @inline implicit def view2RichTextView[V <: TextView](view: V) = new RichTextView[V](view)


  class RichMenu(menu: Menu) {
    @inline def +=(txt: CharSequence) = menu.add(txt)
  }

  @inline implicit def menu2RichMenu(menu: Menu) = new RichMenu(menu)


  class RichContextMenu(menu: ContextMenu) {
    @inline def headerTitle_=(txt: CharSequence): ContextMenu = menu.setHeaderTitle(txt)

    @inline def headerTitle(txt: CharSequence) = headerTitle_=(txt)

    @noEquivalentGetterExists
    @inline def headerTitle: CharSequence = ""
  }

  @inline implicit def contextMenu2RichContextMenu(menu: ContextMenu) = new RichContextMenu(menu)

  class $ListView(implicit context: Context) extends ListView(context) with TraitListView[$ListView] {
    def view = this
  }

  trait TraitAbsListView[V <: AbsListView] extends TraitView[V] {
    @inline def cacheColorHint_=(color: Int): V = {
      view.setCacheColorHint(color)
      view
    }

    @inline def cacheColorHint(color: Int) = cacheColorHint_=(color)

    @inline def cacheColorHint = view.getCacheColorHint

    @inline def transcriptMode_=(mode: Int): V = {
      view.setTranscriptMode(mode)
      view
    }

    @inline def transcriptMode(mode: Int) = transcriptMode_=(mode)

    @inline def transcriptMode: Int = view.getTranscriptMode
  }

  trait TraitListView[V <: ListView] extends TraitAbsListView[V] {
    @inline def adapter_=(ad: ListAdapter): V = {
      view.setAdapter(ad)
      view
    }

    @inline def adapter = view.getAdapter

    @inline def selection_=(position: Int): V = {
      view.setSelection(position)
      view
    }

    @noEquivalentGetterExists
    @inline def selection: Int = 0

    @inline def dividerHeight_=(height: Int): V = {
      view.setDividerHeight(height)
      view
    }

    def dividerHeight(height: Int) = dividerHeight_=(height)

    @inline def dividerHeight: Int = view.getDividerHeight

    @inline def divider_=(divider: Drawable): V = {
      view.setDivider(divider)
      view
    }

    @inline def divider(divider: Drawable) = divider_=(divider)

    @inline def divider: Drawable = view.getDivider
  }

  class RichListView[V <: ListView](val view: V) extends TraitListView[V]

  @inline implicit def listView2RichListView[V <: ListView](view: V) = new RichListView[V](view)

  trait TraitViewGroup[V <: ViewGroup] extends TraitView[V] {
    @inline def +=(v: View): V = {
      view.addView(v)
      view
    }

    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
  }

  trait ViewGroupLayoutParams[LP <: ViewGroupLayoutParams[_]] extends ViewGroup.LayoutParams {
    def lp: LP

    def Width(w: Int): LP = {
      width = w
      lp
    }

    def Height(h: Int): LP = {
      height = h
      lp
    }

    def end: View
  }

  trait ViewGroupMarginLayoutParams[LP <: ViewGroupMarginLayoutParams[_]] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[LP] {
    // TODO: def
  }

  class RichViewGroup[V <: ViewGroup](val view: V) extends TraitViewGroup[V]

  @inline implicit def viewGroup2RichViewGroup[V <: ViewGroup](viewGroup: V) = new RichViewGroup[V](viewGroup)

  trait TraitFrameLayout[V <: FrameLayout] extends TraitViewGroup[V] {
    @inline def foreground_=(fg: Drawable): V = {
      view.setForeground(fg)
      view
    }

    @inline def foreground(fg: Drawable) = foreground_=(fg)

    @inline def foreground: Drawable = view.getForeground

    @inline def foregroundGravity_=(gravity: Int): V = {
      view.setForegroundGravity(gravity)
      view
    }

    @inline def foregroundGravity(gravity: Int) = foregroundGravity_=(gravity)

    @noEquivalentGetterExists
    @inline def foregroundGravity: Int = 0 // view.getForegroundGravity // available in API Level 16 or higher
  }

  class RichFrameLayout[V <: FrameLayout](val view: V) extends TraitFrameLayout[V]

  @inline implicit def frameLayout2RichFrameLayout[V <: FrameLayout](frameLayout: V) = new RichFrameLayout[V](frameLayout)


  trait TraitLinearLayout[V <: LinearLayout] extends TraitViewGroup[V] {
    @inline def orientation_=(orient: Int): LinearLayout = {
      view.setOrientation(orient)
      view
    }

    @inline def orientation(orient: Int) = orientation_=(orient)

    @inline def orientation = view.getOrientation
  }

  class RichLinearLayout[V <: LinearLayout](val view: V) extends TraitLinearLayout[V]

  @inline implicit def linearLaout2RichLinearLayout[V <: LinearLayout](linearLayout: V) = new RichLinearLayout[V](linearLayout)

  @inline class $LinearLayout(implicit context: Context) extends LinearLayout(context) with TraitLinearLayout[$LinearLayout] {
    def view = this

    val VERTICAL = LinearLayout.VERTICAL
    val HORIZONTAL = LinearLayout.HORIZONTAL

    implicit def defaultLayoutParams(v: View): LayoutParams = new LayoutParams(v)

    class LayoutParams(v: View) extends LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT) with ViewGroupMarginLayoutParams[LayoutParams] {
      def lp = this

      v.setLayoutParams(this)

      def Weight(w: Float) = {
        weight = w
        this
      }

      def end: View = v
    }

  }

  implicit def func2ViewOnLongClickListener(f: View => Boolean): View.OnLongClickListener =
    new View.OnLongClickListener() {
      def onLongClick(view: View): Boolean = {
        f(view)
      }
    }

  implicit def lazy2ViewOnLongClickListener(f: => Boolean): View.OnLongClickListener =
    new View.OnLongClickListener() {
      def onLongClick(view: View): Boolean = {
        f
      }
    }


  // requires API level 11 or higher
  //  implicit def func2ViewOnDragListener(f: View => Boolean): View.OnDragListener =
  //    new View.OnDragListener() {
  //      def onDrag(view: View, dragEvent: DragEvent) {
  //        f(view)
  //      }
  //    }
  //
  //  implicit def lazy2ViewOnDragListener(f: => Boolean): View.OnDragListener =
  //    new View.OnDragListener() {
  //      def onDrag(view: View, dragEvent: DragEvent) {
  //        f
  //      }
  //    }

  implicit def func2ViewOnFocusChangeListener[F](f: (View, Boolean) => F): OnFocusChangeListener =
    new OnFocusChangeListener {
      def onFocusChange(v: View, hasFocus: Boolean) {
        f(v, hasFocus)
      }
    }

  implicit def lazy2ViewOnFocusChangeListener[F](f: => F): OnFocusChangeListener =
    new OnFocusChangeListener {
      def onFocusChange(v: View, hasFocus: Boolean) {
        f
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


  implicit def lazy2OnEditorActionListener(f: => Boolean): TextView.OnEditorActionListener =
    new TextView.OnEditorActionListener {
      def onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean = {
        f
      }
    }

  implicit def func2OnKeyListener(f: (View, Int, KeyEvent) => Boolean): View.OnKeyListener =
    new View.OnKeyListener {
      def onKey(v: View, keyCode: Int, event: KeyEvent): Boolean = {
        f(v, keyCode, event)
      }
    }

  implicit def lazy2OnKeyListener(f: => Boolean): View.OnKeyListener =
    new View.OnKeyListener {
      def onKey(view: View, actionId: Int, event: KeyEvent): Boolean = {
        f
      }
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

  class $TextView(implicit context: Context) extends TextView(context) with TraitTextView[$TextView] {
    def view = this
  }

  object $Button {
    def apply(text: CharSequence, onClickListener: OnClickListener)(implicit context: Context): $Button = {
      val button = new $Button()(context)
      button.text = text
      button.setOnClickListener(onClickListener)
      button
    }
  }

  class $Button(implicit context: Context) extends Button(context) with TraitTextView[$Button] {
    def view = this
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

  @inline def downloadManager(implicit context: Context): DownloadManager =
    context.getSystemService(Context.DOWNLOAD_SERVICE).asInstanceOf[DownloadManager]

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

  @inline def nfcManager(implicit context: Context): NfcManager =
    context.getSystemService(Context.NFC_SERVICE).asInstanceOf[NfcManager]

  @inline def notificationManager(implicit context: Context): NotificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  @inline def powerManager(implicit context: Context): PowerManager =
    context.getSystemService(Context.POWER_SERVICE).asInstanceOf[PowerManager]

  @inline def searchManager(implicit context: Context): SearchManager =
    context.getSystemService(Context.SEARCH_SERVICE).asInstanceOf[SearchManager]

  @inline def sensorManager(implicit context: Context): SensorManager =
    context.getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]

  @inline def storageManager(implicit context: Context): StorageManager =
    context.getSystemService(Context.STORAGE_SERVICE).asInstanceOf[StorageManager]

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

  trait ContextUtil extends Context with TagUtil {
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
   * Provides utility methods for Activity
   */
  trait ActivityUtil extends Activity with TraitActivity {
    def activity = this

    def find[V <: View](id: Int): V = findViewById(id).asInstanceOf[V]
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

