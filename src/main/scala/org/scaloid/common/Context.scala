
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
  
trait Destroyable {
  protected val onDestroyBodies = new ArrayBuffer[() => Any]
  	  
  def onDestroy(body: => Any) {
    onDestroyBodies += (() => body)
  }
}
  
trait Creatable {
  protected val onCreateBodies = new ArrayBuffer[() => Any]
  	  
  def onCreate(body: => Any) {
    onCreateBodies += (() => body)
  }
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
      onRestartBodies.foreach(_ ())
    }

    protected val onRestartBodies = new ArrayBuffer[() => Any]

    def onRestart(body: => Any) {
      onRestartBodies += (() => body)
    }

    override def onResume {
      super.onResume()
      onResumeBodies.foreach(_ ())
    }

    protected val onResumeBodies = new ArrayBuffer[() => Any]

    def onResume(body: => Any) {
      onResumeBodies += (() => body)
    }

    override def onPause {
      super.onPause()
      onPauseBodies.foreach(_ ())
    }

    protected val onPauseBodies = new ArrayBuffer[() => Any]

    def onPause(body: => Any) {
      onPauseBodies += (() => body)
    }

    override def onStop {
      super.onStop()
      onStopBodies.foreach(_ ())
    }

    protected val onStopBodies = new ArrayBuffer[() => Any]

    def onStop(body: => Any) {
      onStopBodies += (() => body)
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

