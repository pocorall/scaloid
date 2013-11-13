$license()$

package org.scaloid.common

import android.content._
import android.util.Log
import android.os._
import scala.collection.mutable.ArrayBuffer
import scala.reflect._


class EventSource0[T] extends ArrayBuffer[() => T] {
  def apply(e: => T) = append(() => e)

  def run() {
    foreach(_())
  }
}

class EventSource1[T <: Function1[_, _]] extends ArrayBuffer[T] {
  def apply(e: T) = append(e)
}

class EventSource2[T <: Function2[_, _, _]] extends ArrayBuffer[T] {
  def apply(e: T) = append(e)
}

/**
 * Callback handler for classes that can be destroyed.
 */
trait Destroyable {
  protected val onDestroyBodies = new ArrayBuffer[() => Any]

  def onDestroy(body: => Any) = {
    val el = (() => body)
    onDestroyBodies += el
    el
  }
}

/**
 * Callback handler for classes that can be created.
 */
trait Creatable {
  protected val onCreateBodies = new ArrayBuffer[() => Any]

  def onCreate(body: => Any) = {
    val el = (() => body)
    onCreateBodies += el
    el
  }
}

/**
 * Callback handler for classes that can be registered and unregistered.
 */
trait Registerable {
  def onRegister(body: => Any): () => Any
  def onUnregister(body: => Any): () => Any
}


$wholeClassDef(android.content.Context)$

/**
 * Enriched trait of the class android.content.Context. To enable Scaloid support for subclasses android.content.Context, extend this trait.
 *
 * Refer the URL below for more details.
 *
 * [[https://github.com/pocorall/scaloid/?134#trait-scontext]]
 *
 */
trait SContext extends Context with TraitContext[SContext] with TagUtil {
  def basis: SContext = this
}

$wholeClassDef(android.content.ContextWrapper)$

/**
 * When you register BroadcastReceiver with Context.registerReceiver() you have to unregister it to prevent memory leak.
 * Trait UnregisterReceiver handles these chores for you.
 * All you need to do is append the trait to your class.
 *
 * {{{
 *class MyService extends SService with UnregisterReceiver {
   def func() {
     // ...
     registerReceiver(receiver, intentFilter)
     // Done! automatically unregistered at UnregisterReceiverService.onDestroy()
   }
 }
 * }}}
 * See also: [[https://github.com/pocorall/scaloid/wiki/Basics#trait-unregisterreceiver]]
 */
trait UnregisterReceiver extends ContextWrapper with Destroyable {
  /**
    * Internal implementation for (un)registering the receiver. You do not need to call this method.
    */
  override def registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter): android.content.Intent = {
    onDestroy {
      Log.i("ScalaUtils", "Unregister BroadcastReceiver: "+receiver)
      try {
        unregisterReceiver(receiver)
      } catch {
        // Suppress "Receiver not registered" exception
        // Refer to http://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android
        case e: IllegalArgumentException => e.printStackTrace()
      }
    }

    super.registerReceiver(receiver, filter)
  }
}

/**
 * Provides shortcuts for intent creation.
 *
 * {{{
 *   SIntent[MyActivity]
 * }}}
 *
 */
object SIntent {
  @inline def apply[T](implicit context: Context, mt: ClassTag[T]) = new Intent(context, mt.runtimeClass)

  @inline def apply[T](action: String)(implicit context: Context, mt: ClassTag[T]): Intent = SIntent[T].setAction(action)
}


/**
 * An in-process service connector that can bound [[LocalService]]. This yields far more concise code than that uses plain-old Android API.
 *
 * Please refer to the URL below for more details.
 *
 * [[http://blog.scaloid.org/2013/03/introducing-localservice.html]]
 */
class LocalServiceConnection[S <: LocalService](bindFlag: Int = Context.BIND_AUTO_CREATE)(implicit ctx: Context, reg: Registerable, mf: ClassTag[S]) extends ServiceConnection {
  var service: Option[S] = None
  var componentName:ComponentName = _
  var binder: IBinder = _
  var onConnected = new EventSource0[Unit]
  var onDisconnected = new EventSource0[Unit]

  /**
   * Internal implementation for handling the service connection. You do not need to call this method.
   */
  def onServiceConnected(p1: ComponentName, b: IBinder) {
    service = Option(b.asInstanceOf[LocalService#ScaloidServiceBinder].service.asInstanceOf[S])
    componentName = p1
    binder = b
    onConnected.run()
  }

  /**
   * Internal implementation for handling the service connection. You do not need to call this method.
   */
  def onServiceDisconnected(p1: ComponentName) {
    service = None
    onDisconnected.run()
  }

  /**
   * Returns true if the service is currently connected.
   */
  def connected: Boolean = !service.isEmpty

  reg.onRegister {
    ctx.bindService(SIntent[S], this, bindFlag)
  }

  reg.onUnregister {
    ctx.unbindService(this)
  }
}
