package org.scaloid

import android.app._
import android.content._
import android.os._
import android.view._
import annotation.target.{beanGetter, getter}

import language.implicitConversions

/**
 * Scaloid marries Android code with Scala resulting in easier to understand
 * and maintain code.
 *
 *
 * @example
 *
 * {{{
 * import org.scaloid.common._
 *
 * class MainActivity extends SActivity {
 *
 *   onCreate {
 *     contentView = new SVerticalLayout {
 *       setTheme(android.R.style.Theme_Holo_NoActionBar)
 *
 *       style {
 *         case b:SButton => b.textSize(22 dip)
 *       }
 *
 *       STextView("Welcome").textSize(22 sp).<<.marginBottom(22 dip).>>
 *
 *       val name = SEditText()
 *       STextView("What is your name?").<<.marginBottom(22 dip).>>
 *
 *       SButton("GO").onClick(longToast(s"Hello, \${name.getText}"))
 *     }.padding(20 dip)
 *   }
 * }
 * }}}
 *
 * @see [[http://scaloid.org]]
 *
 * @author Sung-Ho Lee
 */
package object common extends Logger with SystemService with Helpers with Implicits {

  val idSequence = new java.util.concurrent.atomic.AtomicInteger(0)

  def getUniqueId(implicit activity: Activity): Int = {
    var candidate: Int = 0
    do {
      candidate = idSequence.incrementAndGet
    } while (activity.findViewById(candidate) != null)
    candidate
  }


  /**
   * Provides handler instance and runOnUiThread() utility method.
   */
  lazy val handler = new Handler(Looper.getMainLooper)

  lazy val uiThread = Looper.getMainLooper.getThread

  def runOnUiThread[T >: Null](f: => T): T = {
    if (uiThread == Thread.currentThread) {
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

  private[scaloid] trait NoGetterForThisProperty

}

