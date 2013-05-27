$license()$

package org.scaloid.common

import android.app._
import android.content._
import android.media._
import android.net._
import android.preference._
import android.widget._


trait AppHelpers {
  
  @inline def alert(title: CharSequence, text: CharSequence, clickCallback: => Unit = {})(implicit context: Context) {
    new AlertDialogBuilder(title, text) {
      neutralButton(android.R.string.ok, clickCallback)
    }.show()
  }

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   */
  def openUri(uri: Uri)(implicit context: Context) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, uri))
  }

  @inline def pendingService(intent: Intent)(implicit context: Context) =
    PendingIntent.getService(context, 0, intent, 0)

  @inline def pendingActivity(intent: Intent)(implicit context: Context) =
    PendingIntent.getActivity(context, 0, intent, 0)

  @inline def pendingActivity[T](implicit context: Context, mt: ClassManifest[T]) =
    PendingIntent.getActivity(context, 0, SIntent[T], 0)

}
object AppHelpers extends AppHelpers


trait ContentHelpers {

  def broadcastReceiver(filter: IntentFilter)(onReceiveBody: (Context, Intent) => Any)(implicit ctx: Context, reg: Registerable) {
    val receiver = new BroadcastReceiver {
      def onReceive(context: Context, intent: Intent) {
        onReceiveBody(context, intent)
      }
    }
    reg.onRegister(ctx.registerReceiver(receiver, filter))
    reg.onUnregister(ctx.unregisterReceiver(receiver))
  }

}
object ContentHelpers extends ContentHelpers


trait MediaHelpers {

  def play(uri: Uri = notificationSound)(implicit context: Context) {
    val r = RingtoneManager.getRingtone(context, uri)
    if (r != null) {
      r.play()
    }
  }

  @inline def alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

  @inline def notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

  @inline def ringtoneSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

}
object MediaHelpers extends MediaHelpers


trait PreferenceHelpers {

  @inline def defaultSharedPreferences(implicit context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

}
object PreferenceHelpers extends PreferenceHelpers


trait WidgetHelpers {

  @inline def toast(message: CharSequence)(implicit context: Context) {
    runOnUiThread(Toast.makeText(context, message, Toast.LENGTH_SHORT).show())
  }

  @inline def longToast(message: CharSequence)(implicit context: Context) {
    runOnUiThread(Toast.makeText(context, message, Toast.LENGTH_LONG).show())
  }

  @inline def spinnerDialog(title: String, message: String)(implicit context: Context): ProgressDialog =
    runOnUiThread(ProgressDialog.show(context, title, message, true))

}
object WidgetHelpers extends WidgetHelpers


trait Helpers extends AppHelpers with ContentHelpers with MediaHelpers with PreferenceHelpers with WidgetHelpers
object Helpers extends Helpers
