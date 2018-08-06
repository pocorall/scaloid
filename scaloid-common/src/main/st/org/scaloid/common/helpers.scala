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

abstract class PreferenceVar[T](val key: String, val defaultValue: T) {
  def apply(value: T)(implicit pref: SharedPreferences): T

  def apply()(implicit pref: SharedPreferences): T = apply(defaultValue)

  def update(value: T)(implicit pref: SharedPreferences): this.type = {
    val editor = pref.edit()
    put(value, editor)
    editor.apply()
    this
  }

  protected def put(value: T, editor: SharedPreferences.Editor): Unit

  def remove()(implicit pref: SharedPreferences): this.type = {
    pref.edit().remove(key).apply()
    this
  }
}

trait PreferenceHelpers {
  /**
   * Returns DefaultSharedPreferences object for given implicit context.
   */
  @inline implicit def defaultSharedPreferences(implicit context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

  @inline def preferenceVar[T](key: String, defaultVal: T): PreferenceVar[T] = defaultVal match {
    case v: String => new PreferenceVar[String](key, v) {
      override def apply(value: String)(implicit pref: SharedPreferences): String = pref.getString(key, value)

      def put(value: String, editor: SharedPreferences.Editor): Unit = editor.putString(key, value)
    }.asInstanceOf[PreferenceVar[T]]
    case v: Set[String] => new PreferenceVar[Set[String]](key, v) {
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      override def apply(value: Set[String])(implicit pref: SharedPreferences): Set[String] = pref.getStringSet(key, value).asScala.toSet

      def put(value: Set[String], editor: SharedPreferences.Editor): Unit = editor.putStringSet(key, value)
    }.asInstanceOf[PreferenceVar[T]]
    case v: Int => new PreferenceVar[Int](key, v) {
      override def apply(value: Int)(implicit pref: SharedPreferences): Int = pref.getInt(key, value)

      def put(value: Int, editor: SharedPreferences.Editor): Unit = editor.putInt(key, value)
    }.asInstanceOf[PreferenceVar[T]]
    case v: Long => new PreferenceVar[Long](key, v) {
      override def apply(value: Long)(implicit pref: SharedPreferences): Long = pref.getLong(key, value)

      def put(value: Long, editor: SharedPreferences.Editor): Unit = editor.putLong(key, value)
    }.asInstanceOf[PreferenceVar[T]]
    case v: Float => new PreferenceVar[Float](key, v) {
      override def apply(value: Float)(implicit pref: SharedPreferences): Float = pref.getFloat(key, value)

      def put(value: Float, editor: SharedPreferences.Editor): Unit = editor.putFloat(key, value)
    }.asInstanceOf[PreferenceVar[T]]
    case v: Boolean => new PreferenceVar[Boolean](key, v) {
      override def apply(value: Boolean)(implicit pref: SharedPreferences): Boolean = pref.getBoolean(key, value)

      def put(value: Boolean, editor: SharedPreferences.Editor): Unit = editor.putBoolean(key, value)
    }.asInstanceOf[PreferenceVar[T]]
    case _ => throw new Exception("Invalid type for SharedPreferences")
  }

  import scala.language.experimental.macros

  def preferenceVar[T](defaultVal: T): PreferenceVar[T] = macro PreferenceHelpers.preferenceVarImpl[T]

}

object PreferenceHelpers extends PreferenceHelpers {
  import scala.language.experimental.macros
  import scala.reflect.macros.blackbox.Context

  private def getShortName(str: String) = {
    val pos = str.lastIndexOf(".")
    if(pos < 0) str else str.substring(pos+1)
  }

  def preferenceVarImpl[T](c: Context)(defaultVal: c.Expr[T]): c.Expr[PreferenceVar[T]] = {
    import c.universe._

    val enclosingName = getShortName(c.internal.enclosingOwner.fullName)
    val name = c.Expr[String](Literal(Constant(enclosingName)))
    reify {
      preferenceVar(name.splice, defaultVal.splice)
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
