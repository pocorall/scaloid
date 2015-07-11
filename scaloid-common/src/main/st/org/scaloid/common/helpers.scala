$license() $

package org.scaloid.common

import android.app._
import android.content._
import android.media._
import android.net._
import android.preference._
import android.widget._
import android.view._
import scala.concurrent.Future
import scala.reflect._
import scala.util.DynamicVariable

trait AppHelpers {

  /**
   * Displays a simple alert dialog.
   *
   * Although this builder displays some UI element, this builder can be called from any thread, because the method `show()` handles threading internally.
   *
   * @param clickCallback This callback is run when the button is clicked. Does nothing by default.
   */
  @inline def alert(title: CharSequence, text: CharSequence, clickCallback: => Unit = {})(implicit context: Context) {
    new AlertDialogBuilder(title, text) {
      neutralButton(android.R.string.ok, clickCallback)
    }.show()
  }

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   *
   * {{{
   *   openUri("http://scaloid.org")
   * }}}
   */
  @inline def openUri(uri: Uri)(implicit context: Context) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, uri))
  }

  @inline def pendingService(intent: Intent, flags: Int = 0)(implicit context: Context) =
    PendingIntent.getService(context, 0, intent, flags)

  @inline def pendingService[T](implicit context: Context, ct: ClassTag[T]) =
    PendingIntent.getService(context, 0, SIntent[T], 0)

  @inline def pendingActivity(intent: Intent, flags: Int = 0)(implicit context: Context) =
    PendingIntent.getActivity(context, 0, intent, flags)

  @inline def pendingActivity[T](implicit context: Context, ct: ClassTag[T]) =
    PendingIntent.getActivity(context, 0, SIntent[T], 0)

  private[scaloid] val createBundle = new DynamicVariable[Option[android.os.Bundle]](None)

}

object AppHelpers extends AppHelpers


trait ContentHelpers {
  /**
   * When you register BroadcastReceiver with Context.registerReceiver() you have to unregister it to prevent memory leak.
   * Trait UnregisterReceiver handles these chores for you.
   * All you need to do is append the trait to your class.
     {{{
   class MyService extends SService with UnregisterReceiver {
     def func() {
       // ...
       registerReceiver(receiver, intentFilter)
       // Done! automatically unregistered at UnregisterReceiverService.onDestroy()
     }
   }
     }}}
   */
  def broadcastReceiver(filter: IntentFilter)(onReceiveBody: (Context, Intent) => Any)(implicit ctx: Context, reg: Registerable) {
    val receiver = new BroadcastReceiver {
      def onReceive(context: Context, intent: Intent) {
        onReceiveBody(context, intent)
      }
    }
    reg.onRegister(ctx.registerReceiver(receiver, filter))
    reg.onUnregister(ctx.unregisterReceiver(receiver))
  }
  /**
   * When you register BroadcastReceiver with Context.registerReceiver() you have to unregister it to prevent memory leak.
   * Trait UnregisterReceiver handles these chores for you.
   * All you need to do is append the trait to your class.
     {{{
   class MyService extends SService with UnregisterReceiver {
     def func() {
       // ...
       registerReceiver(receiver, intentFilter)
       // Done! automatically unregistered at UnregisterReceiverService.onDestroy()
     }
   }
     }}}
   */
  def broadcastReceiver(filterString: String)(onReceiveBody: => Any)(implicit ctx: Context, reg: Registerable) {
    val receiver = new BroadcastReceiver {
      def onReceive(context: Context, intent: Intent) {
        onReceiveBody
      }
    }
    val filter = new IntentFilter()
    filter.addAction(filterString)
    reg.onRegister(ctx.registerReceiver(receiver, filter))
    reg.onUnregister(ctx.unregisterReceiver(receiver))
  }

}

object ContentHelpers extends ContentHelpers


trait MediaHelpers {
  /**
   * Plays a sound from a given Uri.
   * {{{
   *   play("content://media/internal/audio/media/50")
   * }}}
   */
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

trait PreferenceVal[T] {
  val defaultValue: T

  def apply(value: T)(implicit pref: SharedPreferences): T

  def apply()(implicit pref: SharedPreferences): T = apply(defaultValue)

  def update(value: T)(implicit pref: SharedPreferences): Unit
}

trait PreferenceHelpers {
  /**
   * Returns DefaultSharedPreferences object for given implicit context.
   */
  @inline implicit def defaultSharedPreferences(implicit context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

  @inline def preferenceVal[T](key: String, defaultVal: T): PreferenceVal[T] = defaultVal match {
    case v: String => new PreferenceVal[T] {
      val defaultValue = defaultVal

      override def apply(value: T)(implicit pref: SharedPreferences): T = pref.getString(key, value.asInstanceOf[String]).asInstanceOf[T]

      def update(value: T)(implicit pref: SharedPreferences): Unit = pref.edit().putString(key, value.asInstanceOf[String]).commit()
    }
    case v: Set[String] => new PreferenceVal[T] {
      val defaultValue = defaultVal
      import scala.collection.JavaConversions._
      override def apply(value: T)(implicit pref: SharedPreferences): T = pref.getStringSet(key, value.asInstanceOf[Set[String]]).asInstanceOf[T]

      def update(value: T)(implicit pref: SharedPreferences): Unit = pref.edit().putStringSet(key, value.asInstanceOf[Set[String]]).commit()
    }
    case v: Int => new PreferenceVal[T] {
      val defaultValue = defaultVal

      override def apply(value: T)(implicit pref: SharedPreferences): T = pref.getInt(key, value.asInstanceOf[Int]).asInstanceOf[T]

      def update(value: T)(implicit pref: SharedPreferences): Unit = pref.edit().putInt(key, value.asInstanceOf[Int]).commit()
    }
    case v: Long => new PreferenceVal[T] {
      val defaultValue = defaultVal

      override def apply(value: T)(implicit pref: SharedPreferences): T = pref.getLong(key, value.asInstanceOf[Long]).asInstanceOf[T]

      def update(value: T)(implicit pref: SharedPreferences): Unit = pref.edit().putLong(key, value.asInstanceOf[Long]).commit()
    }
    case v: Float => new PreferenceVal[T] {
      val defaultValue = defaultVal

      override def apply(value: T)(implicit pref: SharedPreferences): T = pref.getFloat(key, value.asInstanceOf[Float]).asInstanceOf[T]

      def update(value: T)(implicit pref: SharedPreferences): Unit = pref.edit().putFloat(key, value.asInstanceOf[Float]).commit()
    }
    case v: Boolean => new PreferenceVal[T] {
      val defaultValue = defaultVal

      override def apply(value: T)(implicit pref: SharedPreferences): T = pref.getBoolean(key, value.asInstanceOf[Boolean]).asInstanceOf[T]

      def update(value: T)(implicit pref: SharedPreferences): Unit = pref.edit().putBoolean(key, value.asInstanceOf[Boolean]).commit()
    }
    case _ => throw new Exception("Invalid type for SharedPreferences")
  }

  import scala.language.experimental.macros

  def preferenceVal[T](defaultVal: T): PreferenceVal[T] = macro PreferenceHelpers.preferenceValImpl[T]

}

object PreferenceHelpers extends PreferenceHelpers {
  import scala.language.experimental.macros
  import scala.reflect.macros.blackbox.Context

  private def getShortName(str: String) = {
    val pos = str.lastIndexOf(".")
    if(pos < 0) str else str.substring(pos+1)
  }

  def preferenceValImpl[T](c: Context)(defaultVal: c.Expr[T]): c.Expr[PreferenceVal[T]] = {
    import c.universe._

    val enclosingName = getShortName(c.internal.enclosingOwner.fullName)
    val name = c.Expr[String](Literal(Constant(enclosingName)))
    reify {
      preferenceVal(name.splice, defaultVal.splice)
    }
  }
}

/**
 * Contains helper methods that displaying some UI elements.
 */
trait WidgetHelpers {
  @inline private[this] def _toast(message: CharSequence, duration: Int, gravity: Int, view: View)(implicit context: Context) {
    runOnUiThread {
      val toast = Toast.makeText(context, message, duration)
      toast.setGravity(gravity, 0, 0)
      if(view != null) toast.setView(view)
      toast.show()
    }
  }
  /**
   * Displays a toast message.
   * This method can be called from any threads.
   */
  @inline def toast(message: CharSequence, gravity: Int = Gravity.BOTTOM, view: View = null)(implicit context: Context) {
    _toast(message, Toast.LENGTH_SHORT, gravity, view)
  }

  /**
   * Displays a toast message for a longer time.
   * This method can be called from any threads.
   */
  @inline def longToast(message: CharSequence, gravity: Int = Gravity.BOTTOM, view: View = null)(implicit context: Context) {
    _toast(message, Toast.LENGTH_LONG, gravity, view)
  }

  /**
   * Displays a dialog with spinner icon.
   * This method can be called from any threads.
   */
  @inline def spinnerDialog(title: CharSequence, message: CharSequence)(implicit context: Context): Future[ProgressDialog] =
    evalOnUiThread(ProgressDialog.show(context, title, message, true))

}

/**
 * Contains helper methods that displaying some UI elements.
 */
object WidgetHelpers extends WidgetHelpers

/**
 * Aggregate trait for helpers.
 */
trait Helpers extends AppHelpers with ContentHelpers with MediaHelpers with PreferenceHelpers with WidgetHelpers

/**
 * Aggregate object for helpers.
 */
object Helpers extends Helpers
