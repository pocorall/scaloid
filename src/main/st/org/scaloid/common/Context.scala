$license()$

package org.scaloid.common

import android.app._
import admin.DevicePolicyManager
import android.view._
import android.net._
import android.os._
import android.media._
import collection.mutable.ArrayBuffer
import android.util.Log

import android.text._
import android.view.accessibility._
import android.accounts._
import android.view.inputmethod._
import android.location._
import android.hardware._
import android.telephony._
import android.net.wifi._
import android.content._
import android.widget._
import android.inputmethodservice._
import android.preference._
import android.preference.Preference._
import android.view.WindowManager.LayoutParams._
import android.view.View._
import android.graphics.drawable.Drawable
import java.lang.CharSequence
import scala.Int
import android.view.ContextMenu.ContextMenuInfo
import android.text.method._
import android.gesture._
import android.appwidget._
import annotation.target.{beanGetter, getter}
import android.view.ViewGroup.LayoutParams
import android.widget.TextView.OnEditorActionListener
import android.graphics._
import android.opengl._


@getter
@beanGetter
class noEquivalentGetterExists extends annotation.StaticAnnotation

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

trait SContext extends Context with TagUtil {
  implicit val ctx = this

  def startActivity[T: ClassManifest] {
    startActivity(SIntent[T])
  }

  def startService[T: ClassManifest] {
    startService(SIntent[T])
  }

  def stopService[T: ClassManifest] {
    stopService(SIntent[T])
  }
}

trait TraitActivity[V <: Activity] {

  @inline def contentView_=(p: View) = {
    basis.setContentView(p)
    basis
  }

  @inline def contentView(p: View) = contentView_=(p)

  $noGetter("contentView")$

  def basis: Activity

  def find[V <: View](id: Int): V = basis.findViewById(id).asInstanceOf[V]

  def runOnUiThread (f: => Unit)  {
    if(uiThread == Thread.currentThread) {
      f
    } else {
      handler.post(new Runnable() {
        def run() {
          f
        }
      })
    }
  }
}

trait SActivity extends Activity with SContext with TraitActivity[SActivity] with Destroyable with Creatable with Registerable {

  def basis = this
  override implicit val ctx = this

  def onRegister(body: => Any) = onResume(body)
  def onUnregister(body: => Any) = onPause(body)

  val onStartStop = new Registerable {
    def onRegister(body: => Any) = onStart(body)
    def onUnregister(body: => Any) = onStop(body)
  }

  val onCreateDestroy = new Registerable {
    def onRegister(body: => Any) = onCreate(body)
    def onUnregister(body: => Any) = onDestroy(body)
  }

  protected override def onCreate(b: Bundle) {
    super.onCreate(b)
    onCreateBodies.foreach(_ ())
  }

  override def onStart {
    super.onStart()
    onStartBodies.foreach(_ ())
  }

  protected val onStartBodies = new ArrayBuffer[() => Any]

  def onStart(body: => Any) = {
    val el = (() => body)
    onStartBodies += el
    el
  }

  override def onResume {
    super.onResume()
    onResumeBodies.foreach(_ ())
  }

  protected val onResumeBodies = new ArrayBuffer[() => Any]

  def onResume(body: => Any) = {
    val el = (() => body)
    onResumeBodies += el
    el
  }

  override def onPause {
    onPauseBodies.foreach(_ ())
    super.onPause()
  }

  protected val onPauseBodies = new ArrayBuffer[() => Any]

  def onPause(body: => Any) = {
    val el = (() => body)
    onPauseBodies += el
    el
  }

  override def onStop {
    onStopBodies.foreach(_ ())
    super.onStop()
  }

  protected val onStopBodies = new ArrayBuffer[() => Any]

  def onStop(body: => Any) = {
    val el = (() => body)
    onStopBodies += el
    el
  }

  override def onDestroy {
    onDestroyBodies.foreach(_ ())
    super.onDestroy()
  }
}

trait SService extends Service with SContext with Destroyable with Creatable with Registerable {
  def basis = this
  override implicit val ctx = this

  def onRegister(body: => Any) = onCreate(body)
  def onUnregister(body: => Any) = onDestroy(body)

  override def onCreate() {
    super.onCreate()
    onCreateBodies.foreach(_ ())
  }

  override def onDestroy() {
    onDestroyBodies.foreach(_ ())
    super.onDestroy()
  }
}

/**
 * Follows a parent's action of onBackPressed().
 * When an activity is a tab that hosted by TabActivity, you may want a common back-button action for each tab.
 *
 * Please refer http://stackoverflow.com/questions/2796050/key-events-in-tabactivities
 */
trait FollowParentBackButton extends SActivity {
  override def onBackPressed() {
    val p = getParent
    if (p != null) p.onBackPressed()
  }
}

/**
 * Turn screen on and show the activity even if the screen is locked.
 * This is useful when notifying some important information.
 */
trait ScreenOnActivity extends SActivity {
  onCreate {
    getWindow.addFlags(FLAG_DISMISS_KEYGUARD | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON)
  }
}

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

trait LocalService extends SService {
  private val binder = new ScaloidServiceBinder

  def onBind(intent: Intent): IBinder = binder

  class ScaloidServiceBinder extends Binder {
    def service: LocalService = LocalService.this
  }

}
