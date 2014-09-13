package org.scaloid.util

import android.content.pm.ActivityInfo._
import org.scaloid.common._
import android.view.WindowManager.LayoutParams._

/**
 * Follows a parent's action of onBackPressed().
 * When an activity is a tab that hosted by TabActivity, you may want a common back-button action for each tab.
 *
 * Please refer to [[http://stackoverflow.com/questions/2796050/key-events-in-tabactivities]]
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

/**
 * Prevent the activity is rotated.
 */
trait PreventRotateActivity extends SActivity {
  onResume {
    setRequestedOrientation(if (Configuration.portrait) SCREEN_ORIENTATION_PORTRAIT else SCREEN_ORIENTATION_LANDSCAPE)
  }

  onPause {
    setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED)
  }
}