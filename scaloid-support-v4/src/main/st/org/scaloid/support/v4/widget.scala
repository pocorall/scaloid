$license()$

package org.scaloid.support.v4

import org.scaloid.common._


$wrapAllPackageMembers(android.support.v4.widget)$


trait WidgetImplicits {
  import scala.language.implicitConversions

  $implicitConversions(android.support.v4.widget)$
}
object WidgetImplicits extends WidgetImplicits
