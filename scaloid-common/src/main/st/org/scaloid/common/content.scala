$license()$

package org.scaloid.common

import android.app.Activity
import android.content._
import android.util.Log
import android.os._
import scala.reflect._
import scala.language.experimental.macros

class EventSource0[T] {
  var events: Vector[() => T] = Vector()
  def apply(e: => T): Unit = {
    require(e != null)
    events :+= (() => e)
  }

  def run(): Unit = events.map(_())

  def clear(): Unit = events = Vector()
}

class EventSource1[Arg1, Ret] {
  var events: Vector[Arg1 => Ret] = Vector()
  def apply(e: Arg1 => Ret): Unit = {
    require(e != null)
    events :+= e
  }

  def run(arg: Arg1): Unit = events.map(_(arg))

  def clear(): Unit = events = Vector()
}

class EventSource2[Arg1, Arg2, Ret] {
  var events: Vector[(Arg1, Arg2) => Ret] = Vector()
  def apply(e: (Arg1, Arg2) => Ret): Unit = {
    require(e != null)
    events :+= e
  }

  def run(arg1: Arg1, arg2: Arg2): Unit = events.map(_(arg1, arg2))

  def clear(): Unit = events = Vector()
}

/**
 * Callback handler for classes that can be destroyed.
 */
trait Destroyable {
  protected var onDestroyBodies = Vector[() => Any]()

  def onDestroy(body: => Any) = {
    val el = body _
    onDestroyBodies :+= el
    el
  }
}

/**
 * Callback handler for classes that can be created.
 */
trait Creatable {
  protected var onCreateBodies = Vector[() => Any]()

  def onCreate(body: => Any) = {
    val el = body _
    onCreateBodies :+= el
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


$android.content.Context; format="whole"$

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

$android.content.ContextWrapper; format="whole"$

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
@deprecated("Use ContentHelper.registerReceiver instead", "4.0")
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

class RichIntent(val intent: Intent) {
  @inline def start[T <: Activity](implicit context: Context, mt: ClassTag[T]) = {
    val clazz = mt.runtimeClass
    intent.setClass(context, clazz)
    clazz match {
      case c if classOf[Activity].isAssignableFrom(c) =>
        context.startActivity(intent)
      case c if classOf[android.app.Service].isAssignableFrom(c) =>
        context.startService(intent)
    }
  }

  def put(values :Any*): Intent = macro org.scaloid.util.MacroImpl.put_impl
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
  var onConnected = new EventSource1[S, Unit]
  var onDisconnected = new EventSource1[S, Unit]

  /**
   * Execute given function with the connected service. If the service is not connected yet, the function
   * is enqueued and be called when the service is connected.
   * For example:
   * val service = new LocalServiceConnection[MyService]
   * //...
   * service(_.doSomeJob())
   */
  def apply[T](f: S => Unit): Unit = service.fold(onConnected(f))(f)

  /**
   * Execute given function with the connected service. If the service is not connected yet,
   * this returns ifEmpty value
   * for example:
   * val service = new LocalServiceConnection[MyService]
   * //...
   * val foo = service(_.foo, defaultVal)
   */
  def apply[T](f: S => T, ifEmpty: => T): T = service.fold(ifEmpty)(f)

  /**
   * Execute given function with the connected service. If the service is not connected yet,
   * this does nothing
   * for example:
   * val service = new LocalServiceConnection[MyService]
   * //...
   * val foo = service.ifAvailable(_.foo)
   */
  def ifAvailable[T](f: S => T): Unit = if(service.nonEmpty) f(service.get)

  /**
   * for example:
   * val service = new LocalServiceConnection[MyService]
   * //...
   * val result = service(_.foo > 3, "3 < " + _.foo, "fail")
   */
  def apply[T](test: S => Boolean, ifTrue: S => T, ifFalse: => T) = if(service.nonEmpty && test(service.get)) ifTrue(service.get) else ifFalse

  /**
   * Internal implementation for handling the service connection. You do not need to call this method.
   */
  def onServiceConnected(p1: ComponentName, b: IBinder) {
    val svc = b.asInstanceOf[LocalService#ScaloidServiceBinder].service.asInstanceOf[S]
    service = Option(svc)
    componentName = p1
    binder = b
    onConnected.run(svc)
    onConnected.clear()
  }

  /**
   * Internal implementation for handling the service connection. You do not need to call this method.
   */
  def onServiceDisconnected(p1: ComponentName) {
    service.foreach(onDisconnected.run)
    onDisconnected.clear()
    service = None
  }

  /**
   * Returns true if the service is currently connected.
   */
  def connected: Boolean = service.isDefined

  /**
    * Creating an intent for ctx.bindService(). Extend this when you need to to something other.
    */
  def createIntent(): Intent = SIntent[S]

  reg.onRegister {
    ctx.bindService(createIntent(), this, bindFlag)
  }

  reg.onUnregister {
    if(connected) {
      service = None // prevents apply(...) methods access this after unbound
      ctx.unbindService(this)
      onConnected.clear() // not to be called at the next binding
    }
  }
}
