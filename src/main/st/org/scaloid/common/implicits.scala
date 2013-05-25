$license()$

package org.scaloid.common

import android.content._
import android.view._
import language.implicitConversions


trait ListenerImpliciits {

  implicit def func2ViewOnClickListener[F](f: (View) => F): View.OnClickListener =
    new View.OnClickListener() {
      def onClick(view: View) {
        f(view)
      }
    }

  implicit def lazy2ViewOnClickListener[F](f: => F): View.OnClickListener =
    new View.OnClickListener() {
      def onClick(view: View) {
        f
      }
    }

  implicit def func2DialogOnClickListener[F](f: (DialogInterface, Int) => F): DialogInterface.OnClickListener =
    new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        f(dialog, which)
      }
    }

  implicit def lazy2DialogOnClickListener[F](f: => F): DialogInterface.OnClickListener =
    new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        f
      }
    }
}
object ListenerImpliciits extends ListenerImpliciits

trait ViewImplicits {
  $implicitConversion(android.view.Menu)$
  $implicitConversion(android.view.ContextMenu)$
  $implicitConversion(android.view.View)$
  $implicitConversion(android.view.ViewGroup)$
  $implicitConversion(android.view.SurfaceView)$
  $implicitConversion(android.view.ViewStub)$
}
object ViewImplicits extends ViewImplicits

trait WidgetImplicits {
  $implicitConversions(android.widget)$
}
object WidgetImplicits extends WidgetImplicits

trait Implicits extends ListenerImpliciits with ViewImplicits with WidgetImplicits
object Implicits extends Implicits
