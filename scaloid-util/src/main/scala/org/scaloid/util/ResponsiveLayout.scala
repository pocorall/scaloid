package org.scaloid.util

import android.content.Context
import org.scaloid.common._
import android.view.View

/**
 * Responsive layout.
 *
 * Refer to the URL below for more details:
 *
 * [[http://blog.scaloid.org/2013/02/android-multiple-layout-directory.html]]
 */
class ResponsiveLayout(implicit context: Context) extends SLinearLayout {
  if (Configuration.portrait) orientation = VERTICAL
  private var first = true

  override def +=(v: View) = {
    if (first) {
      val rv = v: RichView[View]
      if (Configuration.portrait) rv.fw else rv.wf
      first = false
    }
    super.+=(v)
  }
}
