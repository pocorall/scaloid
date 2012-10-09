/*
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
package net.pocorall.android.util

import android.content._
import android.app.{NotificationManager, Activity, AlertDialog}
import android.view.View
import android.net.Uri
import android.os.{Looper, Handler, Vibrator}
import android.media.RingtoneManager
import collection.mutable.ArrayBuffer
import android.util.Log
import android.app.Service


object ScalaUtils {
  def alert(context: Context)(titleId: Int, textId: Int) {
    val builder: AlertDialog.Builder = new AlertDialog.Builder(context)
    builder.setTitle(titleId)
    builder.setMessage(textId)
    builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
      }
    })
    builder.show()
  }

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   */
  def openUri(activity: Activity)(uri: String) {
    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
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


}

trait ContextUtil extends Context {
  def vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]

  def notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  def playNotificationRing() {
    val notificationRing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val r = RingtoneManager.getRingtone(this, notificationRing)
    if (r != null) {
      r.play()
    }
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

/**
 * Automatically unregisters BroadcastReceiver when onDestroy() called
 */
trait UnregisterReceiver extends Service {
  val receiverList = new ArrayBuffer[BroadcastReceiver]()

  override def registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter): Intent = {
    receiverList += receiver
    super.registerReceiver(receiver, filter)
  }

  override def onDestroy() {
    Log.i("tocplus", "Unregister " + receiverList.size + " BroadcastReceivers.")
    for (receiver <- receiverList) try {
      unregisterReceiver(receiver)
    } catch {
      // Suppress "Receiver not registered" exception
      // Refer to http://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android
      case e: IllegalArgumentException => e.printStackTrace()
    }

    super.onDestroy()
  }
}