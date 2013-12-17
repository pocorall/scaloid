$license()$

package org.scaloid.support.v4

import org.scaloid.common._


$wholeClassDef(android.support.v4.view.ViewPager)$
$wholeClassDef(android.support.v4.view.PagerAdapter)$


trait ViewImplicits {
  import scala.language.implicitConversions
  $implicitConversion(android.support.v4.view.ViewPager)$
  $ImplicitConversion(android.support.v4.view.PagerAdapter)$
}
object ViewImplicits extends ViewImplicits
