
/*
 *
 *
 *
 *
 * Less painful Android development with Scala
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 *
 * Copyright 2013 Sung-Ho Lee
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

package org.scaloid

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


package object common extends Logger with SystemService with Widget {

  @getter
  @beanGetter
  class noEquivalentGetterExists extends annotation.StaticAnnotation

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   */
  def openUri(uri: Uri)(implicit context: Context) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, uri))
  }

  def play(uri: Uri = notificationSound)(implicit context: Context) {
    val r = RingtoneManager.getRingtone(context, uri)
    if (r != null) {
      r.play()
    }
  }

implicit def func2ViewOnClickListener[F](f: (View) => F): View.OnClickListener =
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

def defaultValue[U]: U = {
  class Default[W] {
    var default: W = _
  }
  new Default[U].default
}

  trait ConstantsSupport {
    // android:inputType constants for TextView

    import android.text.InputType._

    val NONE = 0
    val TEXT = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL
    val TEXT_CAP_CHARACTERS = TYPE_TEXT_FLAG_CAP_CHARACTERS
    val TEXT_CAP_WORDS = TYPE_TEXT_FLAG_CAP_WORDS
    val TEXT_CAP_SENTENCES = TYPE_TEXT_FLAG_CAP_SENTENCES
    val TEXT_AUTO_CORRECT = TYPE_TEXT_FLAG_AUTO_CORRECT
    val TEXT_AUTO_COMPLETE = TYPE_TEXT_FLAG_AUTO_COMPLETE
    val TEXT_MULTI_LINE = TYPE_TEXT_FLAG_MULTI_LINE
    val TEXT_IME_MULTI_LINE = TYPE_TEXT_FLAG_IME_MULTI_LINE
    val TEXT_NO_SUGGESTIONS = TYPE_TEXT_FLAG_NO_SUGGESTIONS
    val TEXT_URI = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_URI
    val TEXT_EMAIL_ADDRESS = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    val TEXT_EMAIL_SUBJECT = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_SUBJECT
    val TEXT_SHORT_MESSAGE = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_SHORT_MESSAGE
    val TEXT_LONG_MESSAGE = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_LONG_MESSAGE
    val TEXT_PERSION_NAME = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME
    val TEXT_POSTAL_ADDRESS = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS
    val TEXT_PASSWORD = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
    // TODO: write more (http://developer.android.com/reference/android/widget/TextView.html#attr_android:inputType)
  }


  val idSequence = new java.util.concurrent.atomic.AtomicInteger(0)

  def getUniqueId(implicit activity:Activity): Int = {
    var candidate:Int = 0
    do {
      candidate = idSequence.incrementAndGet
    } while(activity.findViewById(candidate) != null)
    candidate
  }
  
  
  trait Destroyable {
    protected val onDestroyBodies = new ArrayBuffer[() => Unit]
  	  
    def onDestroy(body: => Unit) {
      onDestroyBodies += (() => body)
    }
  }
  
  trait Creatable {
    protected val onCreateBodies = new ArrayBuffer[() => Unit]
  	  
    def onCreate(body: => Unit) {
      onCreateBodies += (() => body)
    }
  }

  class RichActivity[V <: Activity](val basis: V) extends TraitActivity[V]
  implicit def activity2RichActivity[V <: Activity](activity: V) = new RichActivity[V](activity)

  trait TraitActivity[V <: Activity] {

    @inline def contentView_=(p: View) = {
      basis.setContentView(p)
      basis
    }

    @inline def contentView(p: View) = contentView_=(p)

    @noEquivalentGetterExists
    @inline def contentView: View = null

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

  trait SActivity extends Activity with SContext with TraitActivity[SActivity] with Destroyable with Creatable {
    def basis = this
    override implicit val ctx = this
     
    protected override def onCreate(b: Bundle) {
      super.onCreate(b)
      onCreateBodies.foreach(_ ())
    }

    override def onRestart {
      super.onRestart()
      onRestartBody()
    }

    var onRestartBody: () => Unit = () => {}

    def onRestart(body: => Unit) {
      onRestartBody = (() => body)
    }

    override def onResume {
      super.onResume()
      onResumeBody()
    }

    var onResumeBody: () => Unit = () => {}

    def onResume(body: => Unit) {
      onResumeBody = (() => body)
    }

    override def onPause {
      super.onPause()
      onPauseBody()
    }

    var onPauseBody: () => Unit = () => {}

    def onPause(body: => Unit) {
      onPauseBody = (() => body)
    }

    override def onStop {
      super.onStop()
      onStopBody()
    }

    var onStopBody: () => Unit = () => {}

    def onStop(body: => Unit) {
      onStopBody = (() => body)
    }

    override def onDestroy {
      super.onDestroy()
      onDestroyBodies.foreach(_ ())
    }
  }

  trait SService extends Service with SContext with Destroyable with Creatable {
    def basis = this

    override def onCreate() {
      super.onCreate()
      onCreateBodies.foreach(_ ())
    }
    
    override def onDestroy() {
      super.onDestroy()
      onDestroyBodies.foreach(_ ())
    }
  }
   

  class RichMenu(menu: Menu) {
    @inline def +=(txt: CharSequence) = menu.add(txt)
	    
    @inline def inflate(id: Int)(implicit activity: Activity) = {
      val inflater = activity.getMenuInflater
      inflater.inflate(id, menu)
      true
    }
  }

  @inline implicit def menu2RichMenu(menu: Menu) = new RichMenu(menu)


  class RichContextMenu(basis: ContextMenu) {
    @inline def headerTitle_=(p: CharSequence) = {
      basis.setHeaderTitle(p)
      basis
    }

    @inline def headerTitle(p: CharSequence) = headerTitle_=(p)

    @noEquivalentGetterExists
    @inline def headerTitle: CharSequence = ""

  }

  @inline implicit def contextMenu2RichContextMenu(menu: ContextMenu) = new RichContextMenu(menu)

  class UnitConversion(val ext: Double)(implicit context: Context) {
    def dip: Int = (ext * context.getResources().getDisplayMetrics().density).toInt
    def sp : Int = (ext * context.getResources().getDisplayMetrics().scaledDensity).toInt
  }

  @inline implicit def Double2unitConversion(ext: Double)(implicit context: Context): UnitConversion = new UnitConversion(ext)(context)
  @inline implicit def Long2unitConversion  (ext: Long)  (implicit context: Context): UnitConversion = new UnitConversion(ext)(context)
  @inline implicit def Int2unitConversion   (ext: Int)   (implicit context: Context): UnitConversion = new UnitConversion(ext)(context)

  class ResourceConversion(val id: Int)(implicit context: Context) {
    def r2Text         : CharSequence        = context.getText(id)
    def r2TextArray    : Array[CharSequence] = context.getResources.getTextArray(id)
    def r2String       : String              = context.getResources.getString(id)
    def r2StringArray  : Array[String]       = context.getResources.getStringArray(id)
    def r2Drawable     : Drawable            = context.getResources.getDrawable(id)
    def r2Movie        : Movie               = context.getResources.getMovie(id)
  }

  @inline implicit def Int2resource(ext: Int)(implicit context: Context): ResourceConversion = new ResourceConversion(ext)(context)

  // r2String is not provided because it is ambiguous with r2Text
  @inline implicit def r2Text       (id: Int)(implicit context: Context): CharSequence        = context.getText(id)
  @inline implicit def r2TextArray  (id: Int)(implicit context: Context): Array[CharSequence] = context.getResources.getTextArray(id)
  @inline implicit def r2StringArray(id: Int)(implicit context: Context): Array[String]       = context.getResources.getStringArray(id)
  @inline implicit def r2Drawable   (id: Int)(implicit context: Context): Drawable            = context.getResources.getDrawable(id)
  @inline implicit def r2Movie      (id: Int)(implicit context: Context): Movie               = context.getResources.getMovie(id)

  @inline implicit def string2Uri           (str: String): Uri            = Uri.parse(str)
  @inline implicit def string2IntentFilter  (str: String): IntentFilter   = new IntentFilter(str)


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

  class RichEditTextPreference[V <: EditTextPreference](val basis: V) extends TraitEditTextPreference[V]
  implicit def editTextPreference2RichEditTextPreference[V <: EditTextPreference](editTextPreference: V) = new RichEditTextPreference[V](editTextPreference)

  trait TraitEditTextPreference[V <: EditTextPreference] {

    @inline def onPreferenceChange(f:  => Boolean): V = {
      basis.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(p1: Preference, p2: Object): Boolean = { f }
      })
      basis
    }

    @inline def onPreferenceChange(f: (Preference, Object) => Boolean): V = {
      basis.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(p1: Preference, p2: Object): Boolean = { f(p1, p2) }
      })
      basis
    }

    @inline def onPreferenceClick(f:  => Boolean): V = {
      basis.setOnPreferenceClickListener(new OnPreferenceClickListener {
        def onPreferenceClick(p1: Preference): Boolean = { f }
      })
      basis
    }

    @inline def onPreferenceClick(f: (Preference) => Boolean): V = {
      basis.setOnPreferenceClickListener(new OnPreferenceClickListener {
        def onPreferenceClick(p1: Preference): Boolean = { f(p1) }
      })
      basis
    }

    def basis: V
  }

  class SEditTextPreference(implicit context: Context) extends EditTextPreference(context) with TraitEditTextPreference[SEditTextPreference] {
    def basis = this
  }

  object SEditTextPreference {
    def apply()(implicit context: Context): SEditTextPreference = new SEditTextPreference
  }

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

  @inline def alert(title: CharSequence, text: CharSequence, clickCallback: => Unit = {})(implicit context: Context) {
    new AlertDialogBuilder(title, text) {
      neutralButton(android.R.string.ok, clickCallback)
    }.show()
  }



  object SIntent {
    @inline def apply[T]()(implicit context: Context, mt: ClassManifest[T]) = new Intent(context, mt.erasure)

    @inline def apply[T](action: String)(implicit context: Context, mt: ClassManifest[T]): Intent = SIntent[T].setAction(action)
  }


  @inline def toast(message: CharSequence)(implicit context: Context) {
    runOnUiThread(Toast.makeText(context, message, Toast.LENGTH_SHORT).show())
  }

  @inline def longToast(message: CharSequence)(implicit context: Context) {
    runOnUiThread(Toast.makeText(context, message, Toast.LENGTH_LONG).show())
  }

  @inline def spinnerDialog(title: String, message: String)(implicit context: Context): ProgressDialog =
    runOnUiThread(ProgressDialog.show(context, title, message, true))

  @inline def pendingService(intent: Intent)(implicit context: Context) =
    PendingIntent.getService(context, 0, intent, 0)

  @inline def pendingActivity(intent: Intent)(implicit context: Context) =
    PendingIntent.getActivity(context, 0, intent, 0)

  @inline def pendingActivity[T](implicit context: Context, mt: ClassManifest[T]) =
    PendingIntent.getActivity(context, 0, SIntent[T], 0)

  @inline def notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

  @inline def ringtoneSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

  @inline def alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

  @inline def defaultSharedPreferences(implicit context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)


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

  /**
   * Provides handler instance and runOnUiThread() utility method.
   */
    lazy val handler = new Handler(Looper.getMainLooper)

    lazy val uiThread = Looper.getMainLooper.getThread

    def runOnUiThread[T >: Null](f: => T):T = {
      if(uiThread == Thread.currentThread) {
        return f
      } else {
        handler.post(new Runnable() {
          def run() {
            f
          }
        })
        return null
      }
    }

  trait UnregisterReceiver extends ContextWrapper with Destroyable {
    val receiverList = new ArrayBuffer[BroadcastReceiver]()

    onDestroy {
      Log.i("ScalaUtils", "Unregister " + receiverList.size + " BroadcastReceivers.")
      for (receiver <- receiverList) try {
        unregisterReceiver(receiver)
      } catch {
        // Suppress "Receiver not registered" exception
        // Refer to http://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android
        case e: IllegalArgumentException => e.printStackTrace()
      }
    }

    override def registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter): android.content.Intent = {
      receiverList += receiver
      super.registerReceiver(receiver, filter)
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
    override def onCreate(savedInstanceState: Bundle) {
      super.onCreate(savedInstanceState)
      getWindow.addFlags(FLAG_DISMISS_KEYGUARD | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON)
    }
  }

  implicit def func2DialogOnClickListener[F](f: (DialogInterface, Int) => F): DialogInterface.OnClickListener =
    new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        f(dialog, which)
      }
    }

  implicit def lazy2DialogOnClickListener[F](f: => F): DialogInterface.OnClickListener =
    new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        f
      }
    }

  class SArrayAdapter[T <: AnyRef](items: Array[T])(implicit context: Context) extends ArrayAdapter[T](context, android.R.layout.simple_spinner_item, items) {
    def setItem(view: TextView, pos: Int): TextView = {
      getItem(pos) match {
        case i: CharSequence => view.setText(i)
        case i => view.setText(i.toString)
      }
      view
    }

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      val v = super.getView(position, convertView, parent)
      if (_style != null) _style(v.asInstanceOf[TextView]) else v
    }

    private var _style: TextView => TextView = null

    def style(v: TextView => TextView): SArrayAdapter[T] = {
      _style = v
      this
    }

    override def getDropDownView(position: Int, convertView: View, parent: ViewGroup): View = {
      val v = super.getDropDownView(position, convertView, parent)
      if (_dropDownStyle != null) _dropDownStyle(v.asInstanceOf[TextView]) else v
    }

    private var _dropDownStyle: TextView => TextView = null

    def dropDownStyle(v: TextView => TextView): SArrayAdapter[T] = {
      _dropDownStyle = v
      this
    }
  }
  
  object SArrayAdapter {
    def apply[T <: AnyRef : Manifest](items:T*)(implicit context: Context) = new SArrayAdapter(items.toArray)
	
    def apply[T <: AnyRef](items:Array[T])(implicit context: Context) = new SArrayAdapter(items)	
  }  


}


