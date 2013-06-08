$license()$

package org.scaloid.common

import android.content._
import android.util.Log
import android.os._
import scala.collection.mutable.ArrayBuffer


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

trait Destroyable {
  protected val onDestroyBodies = new ArrayBuffer[() => Any]

  def onDestroy(body: => Any) = {
    val el = (() => body)
    onDestroyBodies += el
    el
  }
}

trait Creatable {
  protected val onCreateBodies = new ArrayBuffer[() => Any]

  def onCreate(body: => Any) = {
    val el = (() => body)
    onCreateBodies += el
    el
  }
}

trait Registerable {
  def onRegister(body: => Any): () => Any
  def onUnregister(body: => Any): () => Any
}


$wholeClassDef(android.content.Context)$

trait SContext extends Context with TraitContext[SContext] with TagUtil {
}

$wholeClassDef(android.content.ContextWrapper)$


trait UnregisterReceiver extends ContextWrapper with Destroyable {
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


object SIntent {
  @inline def apply[T](implicit context: Context, mt: ClassManifest[T]) = new Intent(context, mt.erasure)

  @inline def apply[T](action: String)(implicit context: Context, mt: ClassManifest[T]): Intent = SIntent[T].setAction(action)
}


class LocalServiceConnection[S <: LocalService](bindFlag: Int = Context.BIND_AUTO_CREATE)(implicit ctx: Context, reg: Registerable, ev: Null <:< S, mf: ClassManifest[S]) extends ServiceConnection {
  var service: S = null
  var componentName:ComponentName = _
  var binder: IBinder = _
  var onConnected = new EventSource0[Unit]
  var onDisconnected = new EventSource0[Unit]

  def onServiceConnected(p1: ComponentName, b: IBinder) {
    service = (b.asInstanceOf[LocalService#ScaloidServiceBinder]).service.asInstanceOf[S]
    componentName = p1
    binder = b
    onConnected.run()
  }

  def onServiceDisconnected(p1: ComponentName) {
    service = null
    onDisconnected.run()
  }

  def connected: Boolean = service != null

  reg.onRegister {
    ctx.bindService(SIntent[S], this, bindFlag)
  }

  reg.onUnregister {
    ctx.unbindService(this)
  }
}
