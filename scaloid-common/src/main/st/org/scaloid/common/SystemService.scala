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

/**
 * Shortcuts for Android system services.
 *
 * Refer to the URL below:
 *
 * [[https://github.com/pocorall/scaloid/#system-services]]
 */
trait SystemService {
  $systemServiceHead(android.view.accessibility.AccessibilityManager)$
  $systemServiceHead(android.accounts.AccountManager)$
  $systemServiceHead(android.app.ActivityManager)$
  $systemServiceHead(android.app.AlarmManager)$
  $systemServiceHead(android.media.AudioManager)$
  $systemServiceHead(android.text.ClipboardManager)$

  class RichClipboardManager(cm: android.text.ClipboardManager) {
    def text_=(txt: CharSequence) = cm.setText(txt)
    def text = cm.getText
  }

  @inline implicit def richClipboardManager(cm: android.text.ClipboardManager): RichClipboardManager = new RichClipboardManager(cm)

  $systemServiceHead(android.net.ConnectivityManager)$
  $systemServiceHead(android.app.admin.DevicePolicyManager)$
$if(ver.gte_9)$
  $systemServiceHead(android.app.DownloadManager)$
$endif$
  $systemServiceHead(android.os.DropBoxManager)$
$if(ver.gte_16)$
  $systemServiceHead(android.hardware.input.InputManager)$
$endif$
  $systemServiceHead(android.view.inputmethod.InputMethodManager)$
  $systemServiceHead(android.app.KeyguardManager)$
  $systemServiceHead(android.view.LayoutInflater)$
  $systemServiceHead(android.location.LocationManager)$
$if(ver.gte_16)$
  $systemServiceHead(android.media.MediaRouter)$
$endif$
$if(ver.gte_10)$
  $systemServiceHead(android.nfc.NfcManager)$
$endif$
  $systemServiceHead(android.app.NotificationManager)$
$if(ver.gte_16)$
  $systemServiceHead(android.net.nsd.NsdManager)$
$endif$
  $systemServiceHead(android.os.PowerManager)$
  $systemServiceHead(android.app.SearchManager)$
  $systemServiceHead(android.hardware.SensorManager)$
$if(ver.gte_10)$
  $systemServiceHead(android.os.storage.StorageManager)$
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
  $systemServiceHead(android.view.textservice.TextServicesManager)$
$endif$
  $systemServiceHead(android.app.UiModeManager)$
$if(ver.gte_12)$
  $systemServiceHead(android.hardware.usb.UsbManager)$
$endif$
  $systemServiceHead(android.os.Vibrator)$
  $systemServiceHead(android.app.WallpaperManager)$
$if(ver.gte_14)$
  $systemServiceHead(android.net.wifi.p2p.WifiP2pManager)$
$endif$
  $systemServiceHead(android.net.wifi.WifiManager)$
  $systemServiceHead(android.view.WindowManager)$
}
