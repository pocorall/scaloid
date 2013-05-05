$license()$

package org.scaloid.common

import android.app._
import admin.DevicePolicyManager
import android.view._
import android.net._
import android.os._
import android.media._

import android.view.accessibility._
import android.accounts._
import android.view.inputmethod._
import android.location._
import android.hardware._
import android.telephony._
import android.net.wifi._
import android.content._
import java.lang.CharSequence

import language.implicitConversions

trait SystemService {
  @inline def accessibilityManager  (implicit context: Context): AccessibilityManager    = context.getSystemService(Context.ACCESSIBILITY_SERVICE ).asInstanceOf[AccessibilityManager]
  @inline def accountManager        (implicit context: Context): AccountManager          = context.getSystemService(Context.ACCOUNT_SERVICE       ).asInstanceOf[AccountManager]
  @inline def activityManager       (implicit context: Context): ActivityManager         = context.getSystemService(Context.ACTIVITY_SERVICE      ).asInstanceOf[ActivityManager]
  @inline def alarmManager          (implicit context: Context): AlarmManager            = context.getSystemService(Context.ALARM_SERVICE         ).asInstanceOf[AlarmManager]
  @inline def audioManager          (implicit context: Context): AudioManager            = context.getSystemService(Context.AUDIO_SERVICE         ).asInstanceOf[AudioManager]
  @inline def clipboardManager      (implicit context: Context): android.text.ClipboardManager =context.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[android.text.ClipboardManager]

  class RichClipboardManager(cm: android.text.ClipboardManager) {
    def text_=(txt: CharSequence) = cm.setText(txt)
    def text = cm.getText
  }

  @inline implicit def richClipboardManager(cm: android.text.ClipboardManager): RichClipboardManager = new RichClipboardManager(cm)

  @inline def connectivityManager   (implicit context: Context): ConnectivityManager    = context.getSystemService(Context.CONNECTIVITY_SERVICE   ).asInstanceOf[ConnectivityManager]
  @inline def devicePolicyManager   (implicit context: Context): DevicePolicyManager    = context.getSystemService(Context.DEVICE_POLICY_SERVICE  ).asInstanceOf[DevicePolicyManager]
$if(ver.gte_9)$
  @inline def downloadManager       (implicit context: Context): DownloadManager        = context.getSystemService(Context.DOWNLOAD_SERVICE       ).asInstanceOf[DownloadManager]
$endif$
  @inline def dropBoxManager        (implicit context: Context): DropBoxManager         = context.getSystemService(Context.DROPBOX_SERVICE        ).asInstanceOf[DropBoxManager]
$if(ver.gte_16)$
  import android.hardware.input.InputManager
  @inline def inputManager          (implicit context: Context): InputManager           = context.getSystemService(Context.INPUT_SERVICE          ).asInstanceOf[InputManager]
$endif$
  @inline def inputMethodManager    (implicit context: Context): InputMethodManager     = context.getSystemService(Context.INPUT_METHOD_SERVICE   ).asInstanceOf[InputMethodManager]
  @inline def keyguardManager       (implicit context: Context): KeyguardManager        = context.getSystemService(Context.KEYGUARD_SERVICE       ).asInstanceOf[KeyguardManager]
  @inline def layoutInflater        (implicit context: Context): LayoutInflater         = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
  @inline def locationManager       (implicit context: Context): LocationManager        = context.getSystemService(Context.LOCATION_SERVICE       ).asInstanceOf[LocationManager]
$if(ver.gte_16)$
  import android.media.MediaRouter
  @inline def mediaRouter           (implicit context: Context): MediaRouter            = context.getSystemService(Context.MEDIA_ROUTER_SERVICE   ).asInstanceOf[MediaRouter]
$endif$
$if(ver.gte_10)$
  import android.nfc.NfcManager
  @inline def nfcManager            (implicit context: Context): NfcManager             = context.getSystemService(Context.NFC_SERVICE            ).asInstanceOf[NfcManager]
$endif$
  @inline def notificationManager   (implicit context: Context): NotificationManager    = context.getSystemService(Context.NOTIFICATION_SERVICE   ).asInstanceOf[NotificationManager]
$if(ver.gte_16)$
  import android.net.nsd.NsdManager
  @inline def nsdManager            (implicit context: Context): NsdManager             = context.getSystemService(Context.NSD_SERVICE            ).asInstanceOf[NsdManager]
$endif$
  @inline def powerManager          (implicit context: Context): PowerManager           = context.getSystemService(Context.POWER_SERVICE          ).asInstanceOf[PowerManager]
  @inline def searchManager         (implicit context: Context): SearchManager          = context.getSystemService(Context.SEARCH_SERVICE         ).asInstanceOf[SearchManager]
  @inline def sensorManager         (implicit context: Context): SensorManager          = context.getSystemService(Context.SENSOR_SERVICE         ).asInstanceOf[SensorManager]
$if(ver.gte_10)$
  import storage.StorageManager
  @inline def storageManager        (implicit context: Context): StorageManager         = context.getSystemService(Context.STORAGE_SERVICE        ).asInstanceOf[StorageManager]
$endif$

  $systemServiceHead(android.telephony.TelephonyManager)$

  def onCallForwardingIndicatorChanged(fun: Boolean => Any)(implicit ctx: Context, reg: Registerable) {
    val callStateListener = new PhoneStateListener() {
      override def onCallForwardingIndicatorChanged(cfi: Boolean) {
        fun(cfi)
      }
    }
    reg.onRegister {
      telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR)
    }
    reg.onUnregister {
      telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }
  }

  def onCallStateChanged(fun: (Int, String) => Any)(implicit ctx: Context, reg: Registerable) {
    val callStateListener = new PhoneStateListener() {
      override def onCallStateChanged(state: Int, incomingNumber: String) {
        fun(state, incomingNumber)
      }
    }
    reg.onRegister {
      telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
    reg.onUnregister {
      telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }
  }

  def onCellLocationChanged(fun: CellLocation => Any)(implicit ctx: Context, reg: Registerable) {
    val callStateListener = new PhoneStateListener() {
      override def onCellLocationChanged(cellLocation: CellLocation) {
        fun(cellLocation)
      }
    }
    reg.onRegister {
      telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CELL_LOCATION)
    }
    reg.onUnregister {
      telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }
  }

$if(ver.gte_14)$
  import textservice.TextServicesManager
  @inline def textServicesManager   (implicit context: Context): TextServicesManager    = context.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE).asInstanceOf[TextServicesManager]
$endif$
  @inline def uiModeManager         (implicit context: Context): UiModeManager          = context.getSystemService(Context.UI_MODE_SERVICE        ).asInstanceOf[UiModeManager]
$if(ver.gte_12)$
  import android.hardware.usb.UsbManager
  @inline def usbManager            (implicit context: Context): UsbManager             = context.getSystemService(Context.USB_SERVICE            ).asInstanceOf[UsbManager]
$endif$
  @inline def vibrator              (implicit context: Context): Vibrator               = context.getSystemService(Context.VIBRATOR_SERVICE       ).asInstanceOf[Vibrator]
  @inline def wallpaperManager      (implicit context: Context): WallpaperManager       = context.getSystemService(Context.WALLPAPER_SERVICE      ).asInstanceOf[WallpaperManager]
$if(ver.gte_14)$
  import android.net.wifi.p2p.WifiP2pManager
  @inline def wifiP2pManager        (implicit context: Context): WifiP2pManager         = context.getSystemService(Context.WIFI_P2P_SERVICE       ).asInstanceOf[WifiP2pManager]
$endif$
  @inline def wifiManager           (implicit context: Context): WifiManager            = context.getSystemService(Context.WIFI_SERVICE           ).asInstanceOf[WifiManager]
  @inline def windowManager         (implicit context: Context): WindowManager          = context.getSystemService(Context.WINDOW_SERVICE         ).asInstanceOf[WindowManager]
}
