$license()$

package org.scaloid.support.v4

import org.scaloid.common._


$android.support.v4.view.ViewPager; format="whole"$
$android.support.v4.view.PagerAdapter; format="whole"$


trait ViewImplicits {
  import scala.language.implicitConversions
  $android.support.v4.view.ViewPager; format="implicit-conversion"$
  $android.support.v4.view.PagerAdapter; format="implicit-conversion"$
}
object ViewImplicits extends ViewImplicits
