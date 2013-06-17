$license()$

package org.scaloid.common

import android.app._
import android.content._
import android.os._
import android.view._
import android.view.WindowManager.LayoutParams._
import scala.collection.mutable.ArrayBuffer
import Implicits._
import scala.deprecated


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

/**
 * Enriched trait for the class android.app.Activity.
 * To enable Scaloid, inherit this trait instead of the class Activity.
 */
trait SActivity extends Activity with SContext with TraitActivity[SActivity] with Destroyable with Creatable with Registerable {

  override def basis = this
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

/**
 * Follows a parent's action of onBackPressed().
 * When an activity is a tab that hosted by TabActivity, you may want a common back-button action for each tab.
 *
 * Please refer to [[http://stackoverflow.com/questions/2796050/key-events-in-tabactivities]]
 */
@deprecated("Use org.scaloid.util package instead. This will be removed from Scaloid 3.0.", "2.0")
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
@deprecated("Use org.scaloid.util package instead. This will be removed from Scaloid 3.0.", "2.0")
trait ScreenOnActivity extends SActivity {
  onCreate {
    getWindow.addFlags(FLAG_DISMISS_KEYGUARD | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON)
  }
}


/**
 * Enriched trait of the class android.app.Service. To enable Scaloid support for subclasses of android.app.Service, extend this trait.
 */
trait SService extends Service with SContext with Destroyable with Creatable with Registerable{
  override def basis = this
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

trait LocalService extends SService {
  private val binder = new ScaloidServiceBinder

  def onBind(intent: Intent): IBinder = binder

  class ScaloidServiceBinder extends Binder {
    def service: LocalService = LocalService.this
  }

}

/**
 * A Scala-style builder for AlertDialog.
 * {{{
 *  new AlertDialogBuilder(R.string.title, R.string.message) {
 *    neutralButton()
 *  }.show()
 * }}}
 * This displays an alert dialog with given string resources.
 *
 * Please refer to the URL below for more details.
 *
 * [[https://github.com/pocorall/scaloid/wiki/Basics#class-alertdialogbuilder]]
 */
class AlertDialogBuilder(_title: CharSequence = null, _message: CharSequence = null)(implicit context: Context) extends AlertDialog.Builder(context) {
  if (_title != null) setTitle(_title)
  if (_message != null) setMessage(_message)


  @inline def positiveButton(name: CharSequence = android.R.string.yes, onClick: => Unit = {}): AlertDialogBuilder =
    positiveButton(name, (_, _) => {
      onClick
    })

  @inline def positiveButton(name: CharSequence, onClick: (DialogInterface, Int) => Unit): AlertDialogBuilder = {
    setPositiveButton(name, func2DialogOnClickListener(onClick))
    this
  }

  @inline def neutralButton(name: CharSequence = android.R.string.ok, onClick: => Unit = {}): AlertDialogBuilder =
    neutralButton(name, (_, _) => {
      onClick
    })

  @inline def neutralButton(name: CharSequence, onClick: (DialogInterface, Int) => Unit): AlertDialogBuilder = {
    setNeutralButton(name, func2DialogOnClickListener(onClick))
    this
  }

  @inline def negativeButton(name: CharSequence, onClick: => Unit): AlertDialogBuilder =
    negativeButton(name, (_, _) => {
      onClick
    })

  @inline def negativeButton(name: CharSequence = android.R.string.no, onClick: (DialogInterface, Int) => Unit = (d, _) => {
    d.cancel()
  }): AlertDialogBuilder = {
    setNegativeButton(name, func2DialogOnClickListener(onClick))
    this
  }

  var tit: CharSequence = null

  @inline def title_=(str: CharSequence) = {
    tit = str
    setTitle(str)
  }

  @inline def title = tit

  var msg: CharSequence = null

  @inline def message_=(str: CharSequence) = {
    tit = str
    setMessage(str)
  }

  @inline def message = tit

  override def show():AlertDialog = runOnUiThread(super.show())
}
