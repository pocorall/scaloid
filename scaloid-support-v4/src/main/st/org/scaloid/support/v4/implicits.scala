$license()$

package org.scaloid.support.v4

import scala.language.implicitConversions


trait AppImplicits {
  $android.support.v4.app.Fragment; format="implicit-conversion"$
  $android.support.v4.app.FragmentActivity; format="implicit-conversion"$
  $android.support.v4.app.FragmentManager; format="implicit-conversion"$
  $android.support.v4.app.FragmentTransaction; format="implicit-conversion"$
  $android.support.v4.app.ListFragment; format="implicit-conversion"$
  $android.support.v4.app.DialogFragment; format="implicit-conversion"$

}
object AppImplicits extends AppImplicits


trait Implicits extends AppImplicits with ViewImplicits with WidgetImplicits
object Implicits extends Implicits
