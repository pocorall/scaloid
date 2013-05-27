$license()$

package org.scaloid.support.v4

import language.implicitConversions


trait AppImplicits {
  $implicitConversion(android.support.v4.app.Fragment)$
  $implicitConversion(android.support.v4.app.FragmentActivity)$
  $implicitConversion(android.support.v4.app.ListFragment)$
  $implicitConversion(android.support.v4.app.DialogFragment)$

}
object AppImplicits extends AppImplicits


trait Implicits extends AppImplicits
object Implicits extends Implicits
