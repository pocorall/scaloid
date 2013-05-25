$license()$

package org.scaloid.common

import language.implicitConversions


trait WidgetImplicits {
  $implicitConversion(android.view.View)$
  $implicitConversion(android.view.ViewGroup)$
  $implicitConversion(android.view.SurfaceView)$
  $implicitConversion(android.view.ViewStub)$
  $implicitConversions(android.widget)$
}

object WidgetImplicits extends WidgetImplicits
