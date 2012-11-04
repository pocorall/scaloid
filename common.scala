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


case class LoggerTag(_tag: String) {
  private val MAX_TAG_LEN = 22
  val tag = if (_tag.length < MAX_TAG_LEN) _tag else ":" + _tag.substring(_tag.length - (MAX_TAG_LEN - 1), _tag.length)
}

package object common {

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

  class RichView[V <: View](val view: V) extends TraitView[V]

  trait TraitView[V <: View] {
    def view: V

    @inline def onClick(f: => Unit) {
      view.setOnClickListener(new OnClickListener {
        def onClick(view: View) {
          f
        }
      })
    }

    @inline def onClick(f: View => Unit) {
      view.setOnClickListener(new OnClickListener {
        def onClick(view: View) {
          f(view)
        }
      })
    }

    @inline def onLongClick(f: => Boolean) {
      view.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(view: View): Boolean = {
          f
        }
      })
    }

    @inline def onLongClick(f: View => Boolean) {
      view.setOnLongClickListener(new OnLongClickListener {
        def onLongClick(view: View): Boolean = {
          f(view)
        }
      })
    }

    @inline def onCreateContextMenu(f: => Unit) {
      view.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
          f
        }
      })
    }

    @inline def onCreateContextMenu(f: (ContextMenu, V, ContextMenuInfo) => Unit) {
      view.setOnCreateContextMenuListener(new OnCreateContextMenuListener {
        def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
          f(menu, v.asInstanceOf[V], menuInfo)
        }
      })
    }

    @inline def onFocusChanged(f: => Unit) {
      view.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(v: View, hasFocus: Boolean) {
          f
        }
      })
    }

    @inline def onFocusChanged(f: (View, Boolean) => Unit) {
      view.setOnFocusChangeListener(new OnFocusChangeListener {
        def onFocusChange(v: View, hasFocus: Boolean) {
          f(v, hasFocus)
        }
      })
    }

    @inline def onKey(f: => Boolean) {
      view.setOnKeyListener(new View.OnKeyListener {
        def onKey(v: View, keyCode: Int, event: KeyEvent) = f
      })
    }

    @inline def onKey(f: (View, Int, KeyEvent) => Boolean) {
      view.setOnKeyListener(new View.OnKeyListener {
        def onKey(v: View, keyCode: Int, event: KeyEvent) = f(v, keyCode, event)
      })
    }

    @inline def onTouch(f: => Boolean) {
      view.setOnTouchListener(new OnTouchListener {
        def onTouch(v: View, event: MotionEvent) = f
      })
    }

    @inline def onTouch(f: (View, MotionEvent) => Boolean) {
      view.setOnTouchListener(new OnTouchListener {
        def onTouch(v: View, event: MotionEvent) = f(v, event)
      })
    }

    @inline def layoutParams_=(lp: LayoutParams) = view.setLayoutParams(lp)

    @inline def layoutParams = view.getLayoutParams

    @inline def backgroundColor_=(color: Int) = view.setBackgroundColor(color)

    @noEquivalentGetterExists
    @inline def backgroundColor: Int = 0
  }

  @inline implicit def view2RichView[V <: View](view: V) = new RichView[V](view)

  class $EditText(implicit context: Context) extends android.widget.EditText(context) with TraitTextView {
    def view: TextView = this
  }

  class RichActivity(val activity: Activity) extends TraitActivity

  trait TraitActivity {
    def activity: Activity

    @inline def contentView_=(view: View) = activity.setContentView(view)

    @noEquivalentGetterExists
    @inline def contentView: View = null
  }

  @inline implicit def activity2RichActivity(activity: Activity) = new RichActivity(activity)

  class RichTextView(val view: TextView) extends TraitTextView

  trait TraitTextView extends TraitView[TextView] {
    def view: TextView

    @inline def beforeTextChanged(f: (CharSequence, Int, Int, Int) => Unit) {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          f(s, start, before, count)
        }

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {}
      })
    }

    @inline def beforeTextChanged(f: => Unit) {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          f
        }

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {}
      })
    }

    @inline def onTextChanged(f: => Unit) {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {
          f
        }

        def afterTextChanged(p1: Editable) {}
      })
    }


    @inline def onTextChanged(f: (CharSequence, Int, Int, Int) => Unit) {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {
          f(p1, p2, p3, p4)
        }

        def afterTextChanged(p1: Editable) {}
      })
    }

    @inline def afterTextChanged(f: Editable => Unit) {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {
          f(p1)
        }
      })
    }

    @inline def afterTextChanged(f: => Unit) {
      view.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

        def afterTextChanged(p1: Editable) {
          f
        }
      })
    }

    @inline def onEditorAction(f: => Boolean) {
      view.setOnEditorActionListener(new TextView.OnEditorActionListener {
        def onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean = {
          f
        }
      })
    }

    @inline def onEditorAction(f: (TextView, Int, KeyEvent) => Boolean) {
      view.setOnEditorActionListener(new TextView.OnEditorActionListener {
        def onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean = {
          f(view, actionId, event)
        }
      })
    }

    @inline def textSize_=(size: Float) {
      view.setTextSize(size)
    }

    @inline def textSize: Float = view.getTextSize

    @inline def gravity_=(g: Int) = view.setGravity(g)

    @inline def gravity = view.getGravity

    @inline def movementMethod_=(movement: MovementMethod) {
      view.setMovementMethod(movement)
    }

    @inline def movementMethod: MovementMethod = view.getMovementMethod

    @inline def text_=(txt: CharSequence) {
      view.setText(txt)
    }

    @inline def text: CharSequence = view.getText

    @inline def maxHeight_=(height: Int) = view.setMaxHeight(height)

    @noEquivalentGetterExists
    @inline def maxHeight: Int = 0 // view.getMaxHeight // higher than API Level 16

    @inline def maxLines_=(line: Int) = view.setMaxLines(line)

    @noEquivalentGetterExists
    @inline def maxLines: Int = 0 // view.getMaxLines  // higher than API Level 16

    @inline def linkTextColor_=(color: Int) = view.setLinkTextColor(color)

    @noEquivalentGetterExists
    @inline def linkTextColor: Int = 0
  }

  @inline implicit def view2RichTextView(view: TextView) = new RichTextView(view)


  class RichMenu(menu: Menu) {
    @inline def +=(txt: CharSequence) = menu.add(txt)
  }

  @inline implicit def menu2RichMenu(menu: Menu) = new RichMenu(menu)


  class RichContextMenu(menu: ContextMenu) {
    @inline def headerTitle_=(txt: CharSequence) = menu.setHeaderTitle(txt)

    @noEquivalentGetterExists
    @inline def headerTitle: CharSequence = ""
  }

  @inline implicit def contextMenu2RichContextMenu(menu: ContextMenu) = new RichContextMenu(menu)

  @getter
  @beanGetter
  class noEquivalentGetterExists extends annotation.StaticAnnotation

  class $ListView(implicit context: Context) extends ListView(context) with TraitListView {
    def view = this
  }

  class RichListView(val view: ListView) extends TraitListView

  trait TraitAbsListView extends TraitView[AbsListView] {
    def view: AbsListView

    @inline def cacheColorHint_=(color: Int) = view.setCacheColorHint(color)

    @inline def cacheColorHint = view.getCacheColorHint

    @inline def transcriptMode_=(mode: Int) = view.setTranscriptMode(mode)

    @inline def transcriptMode: Int = view.getTranscriptMode
  }

  trait TraitListView extends TraitAbsListView {
    def view: ListView

    @inline def adapter_=(ad: ListAdapter) = view.setAdapter(ad)

    @inline def adapter = view.getAdapter

    @inline def selection_=(position: Int) = view.setSelection(position)

    @noEquivalentGetterExists
    @inline def selection: Int = 0

    @inline def dividerHeight_=(height: Int) = view.setDividerHeight(height)

    @inline def dividerHeight: Int = view.getDividerHeight

    @inline def divider_=(divider: Drawable) = view.setDivider(divider)

    @inline def divider: Drawable = view.getDivider
  }

  @inline implicit def listView2RichListView(lv: android.widget.ListView) = new RichListView(lv)

  class RichViewGroup(val view: ViewGroup) extends TraitViewGroup

  trait TraitViewGroup extends TraitView[ViewGroup] {
    def view: ViewGroup

    @inline def +=(v: View) = view.addView(v)
  }

  @inline implicit def viewGroup2RichViewGroup(viewGroup: ViewGroup) = new RichViewGroup(viewGroup)

  class RichLinearLayout(val view: LinearLayout) extends TraitLinearLayout

  trait TraitLinearLayout extends TraitViewGroup {
    def view: LinearLayout

    @inline def orientation_=(orient: Int) = view.setOrientation(orient)

    @inline def orientation = view.getOrientation
  }

  @inline implicit def linearLaout2RichLinearLayout(linearLayout: LinearLayout) = new RichLinearLayout(linearLayout)

  @inline class $LinearLayout(implicit context: Context) extends LinearLayout(context) with TraitLinearLayout {
    def view = this
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

  @inline def $Intent[T](implicit context: Context, mt: ClassManifest[T]) = new content.Intent(context, mt.erasure)

  @inline def $Intent[T](action: String)(implicit context: Context, mt: ClassManifest[T]) = $Intent[T].setAction(action)

  class $TextView(implicit context: Context) extends TextView(context) with TraitTextView {
    def view = this
  }

  object $Button {
    def apply(text: CharSequence, onClickListener: OnClickListener)(implicit context: Context): Button = {
      val button = new Button(context)
      button.text = text
      button.setOnClickListener(onClickListener)
      button
    }
  }

  class $Button(implicit context: Context) extends Button(context) with TraitTextView {
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

