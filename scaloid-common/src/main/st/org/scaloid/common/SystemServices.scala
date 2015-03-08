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
trait SystemServices {
  $android.view.accessibility.AccessibilityManager; format="system-service"$
  $android.accounts.AccountManager; format="system-service"$
  $android.app.ActivityManager; format="system-service"$
  $android.app.AlarmManager; format="system-service"$
  $android.media.AudioManager; format="system-service"$

  // android.content.ClipboardManager in API 11+, but this is its superclass
  $android.text.ClipboardManager; format="system-service"$

  class RichClipboardManager(cm: android.text.ClipboardManager) {
    def text_=(txt: CharSequence) = cm.setText(txt)
    def text = cm.getText
  }

  @inline implicit def richClipboardManager(cm: android.text.ClipboardManager): RichClipboardManager = new RichClipboardManager(cm)

  $android.net.ConnectivityManager; format="system-service"$
  $android.app.admin.DevicePolicyManager; format="system-service"$
$if(ver.gte_9)$
  $android.app.DownloadManager; format="system-service"$
$endif$
  $android.os.DropBoxManager; format="system-service"$
$if(ver.gte_16)$
  $android.hardware.input.InputManager; format="system-service"$
$endif$
  $android.view.inputmethod.InputMethodManager; format="system-service"$
  $android.app.KeyguardManager; format="system-service"$
  $android.view.LayoutInflater; format="system-service"$
  $android.location.LocationManager; format="system-service"$
$if(ver.gte_16)$
  $android.media.MediaRouter; format="system-service"$
$endif$
$if(ver.gte_10)$
  $android.nfc.NfcManager; format="system-service"$
$endif$
  $android.app.NotificationManager; format="system-service"$
$if(ver.gte_16)$
  $android.net.nsd.NsdManager; format="system-service"$
$endif$
  $android.os.PowerManager; format="system-service"$
  $android.app.SearchManager; format="system-service"$
  $android.hardware.SensorManager; format="system-service"$
$if(ver.gte_10)$
  $android.os.storage.StorageManager; format="system-service"$
$endif$

  $android.telephony.TelephonyManager; format="system-service"$

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
  @inline def textServicesManager(implicit context: Context) =
    context.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE).asInstanceOf[android.view.textservice.TextServicesManager]
$endif$
  $android.app.UiModeManager; format="system-service"$
$if(ver.gte_12)$
  $android.hardware.usb.UsbManager; format="system-service"$
$endif$
  $android.os.Vibrator; format="system-service"$
  $android.app.WallpaperManager; format="system-service"$
$if(ver.gte_14)$
  $android.net.wifi.p2p.WifiP2pManager; format="system-service"$
$endif$
  $android.net.wifi.WifiManager; format="system-service"$
  $android.view.WindowManager; format="system-service"$
}

object SystemServices extends SystemServices