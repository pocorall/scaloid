/*
 *
 *
 *
 *
 * Less painful Android development with Scala
 *
 * https://github.com/pocorall/android-scala-common
 *
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
import android.widget.{Button, TextView, Toast}
import android.preference.PreferenceManager
import android.view.WindowManager.LayoutParams._
import android.view.View.{OnClickListener, OnFocusChangeListener}
import android.graphics.drawable.Drawable


package object common {

  implicit def resourceIdToTextResource(id: Int)(implicit context: Context): CharSequence = context.getText(id)

  def alert(titleId: CharSequence, textId: CharSequence, clickCallback: => Unit = {})(implicit context: Context) {
    val builder: AlertDialog.Builder = new AlertDialog.Builder(context)
    builder.setTitle(titleId)
    builder.setMessage(textId)
    builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        clickCallback
      }
    })
    builder.show()
  }

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   */
  def openUri(uri: String)(implicit context: Context) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
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


  implicit def func2OnEditorActionListener(f: (TextView, Int, KeyEvent) => Boolean): TextView.OnEditorActionListener =
    new TextView.OnEditorActionListener {
      def onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean = {
        f(view, actionId, event)
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

  implicit def func2TextWatcher[F](f: (CharSequence, Int, Int, Int) => F): TextWatcher =
    new TextWatcher {
      def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        f(s, start, before, count)
      }

      def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

      def afterTextChanged(p1: Editable) {}
    }

  implicit def f2TextWatcher[F](f: (Editable) => F): TextWatcher =
    new TextWatcher {
      def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

      def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

      def afterTextChanged(editable: Editable) {
        f(editable)
      }
    }

  implicit def lazy2TextWatcher[F](f: => F): TextWatcher =
    new TextWatcher {
      def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        f
      }

      def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

      def afterTextChanged(p1: Editable) {}
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

  class AlertDialogBuilder(title: CharSequence, message: CharSequence)(implicit context: Context) extends AlertDialog.Builder(context) {
    setTitle(title)
    setMessage(message)

    def positiveButton(name: CharSequence, onClick: (DialogInterface, Int) => Unit): AlertDialogBuilder = {
      setPositiveButton(name, func2DialogOnClickListener(onClick))
      this
    }

    def negativeButton(name: CharSequence, onClick: (DialogInterface, Int) => Unit = (d, _) => {
      d.cancel()
    }): AlertDialogBuilder = {
      setNegativeButton(name, func2DialogOnClickListener(onClick))
      this
    }
  }

  implicit def stringToUri(str: String): Uri = Uri.parse(str)

  def newIntent[T](implicit context: Context, mt: ClassManifest[T]) = new content.Intent(context, mt.erasure)

  def newTextView(implicit context: Context): TextView = new TextView(context)

  def newButton(text: CharSequence, onClickListener: OnClickListener)(implicit context: Context): Button = {
    val btn = new Button(context)
    btn.setText(text)
    btn.setOnClickListener(onClickListener)
    btn
  }

  def toast(message: String)(implicit context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  def longToast(message: String)(implicit context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
  }

  def spinnerDialog(title: String, message: String)(implicit context: Context): ProgressDialog =
    ProgressDialog.show(context, title, message, true)

  def pendingService(intent: Intent)(implicit context: Context) =
    PendingIntent.getService(context, 0, intent, 0)

  def pendingActivity(intent: Intent)(implicit context: Context) =
    PendingIntent.getActivity(context, 0, intent, 0)

  def pendingActivity[T](implicit context: Context, mt: ClassManifest[T]) =
    PendingIntent.getActivity(context, 0, newIntent[T], 0)

  def notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

  def ringtoneSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

  def alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

  def defaultSharedPreferences(implicit context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

  trait ContextUtil extends Context {
    def accessibilityManager: AccessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE).asInstanceOf[AccessibilityManager]

    def accountManager: AccountManager = getSystemService(Context.ACCOUNT_SERVICE).asInstanceOf[AccountManager]

    def activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]

    def alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE).asInstanceOf[AlarmManager]

    def audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE).asInstanceOf[AudioManager]

    def clipboardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]

    def connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]

    def devicePolicyManager: DevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE).asInstanceOf[DevicePolicyManager]

    def downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE).asInstanceOf[DownloadManager]

    def dropBoxManager: DropBoxManager = getSystemService(Context.DROPBOX_SERVICE).asInstanceOf[DropBoxManager]

    def inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]

    def keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE).asInstanceOf[KeyguardManager]

    def layoutInflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    def locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]

    def nfcManager: NfcManager = getSystemService(Context.NFC_SERVICE).asInstanceOf[NfcManager]

    def notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

    def powerManager: PowerManager = getSystemService(Context.POWER_SERVICE).asInstanceOf[PowerManager]

    def searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE).asInstanceOf[SearchManager]

    def sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]

    def storageManager: StorageManager = getSystemService(Context.STORAGE_SERVICE).asInstanceOf[StorageManager]

    def telephonyManager: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE).asInstanceOf[TelephonyManager]

    def uiModeManager: UiModeManager = getSystemService(Context.UI_MODE_SERVICE).asInstanceOf[UiModeManager]

    def vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]

    def wallpaperManager: WallpaperManager = getSystemService(Context.WALLPAPER_SERVICE).asInstanceOf[WallpaperManager]

    def wifiManager: WifiManager = getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]

    def windowManager: WindowManager = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]

    def play(uri: Uri = notificationSound) {
      val r = RingtoneManager.getRingtone(this, uri)
      if (r != null) {
        r.play()
      }
    }

    implicit val context = this

    def startActivity[T: ClassManifest] {
      startActivity(newIntent[T])
    }

    def startService[T: ClassManifest] {
      startService(newIntent[T])
    }

    def stopService[T: ClassManifest] {
      stopService(newIntent[T])
    }
  }

  /**
   * Provides utility methods for Activity
   */
  trait ActivityUtil extends Activity {
    def find[V <: android.view.View](id: Int): V = findViewById(id).asInstanceOf[V]
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
      getWindow.addFlags(FLAG_DISMISS_KEYGUARD | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON | FLAG_KEEP_SCREEN_ON)
    }
  }

}

