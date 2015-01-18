$license()$

package org.scaloid.support.v4

import org.scaloid.common._


$android.support.v4.widget; format="wrap-all-classes"$


trait WidgetImplicits {
  import scala.language.implicitConversions

  $android.support.v4.widget; format="package-implicit-conversions"$
}
object WidgetImplicits extends WidgetImplicits
