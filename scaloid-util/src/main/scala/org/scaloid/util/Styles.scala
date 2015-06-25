package org.scaloid.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import org.scaloid.common.SStateListDrawable

/**
 * Provides minimalistic button styles.
 * {{{
 *   Button("Simple").backgroundDrawable(Sytles.yellow)
 *   Button("Styles").backgroundDrawable(Sytles.btn(0xff68B8FC))
 * }}}
 */
trait Styles {
  import Color._

  @inline def c(color: Int) = new ColorDrawable(color)

  @inline def transform(color: Int, f: Int => Int): Int = {
    val a = color & 0xff000000
    val r = (color >> 16) & 0xff
    val g = (color >> 8) & 0xff
    val b = (color >> 0) & 0xff
    a + (f(r) << 16) + (f(g) << 8) + f(b)
  }

  @inline def average(color: Int): Double = {
    val r = (color >> 16) & 0xff
    val g = (color >> 8) & 0xff
    val b = (color >> 0) & 0xff
    (r + g + b) / 3
  }

  @inline def invert(color: Int): Int = transform(color, 255 - _)

  @inline def clamp(c: Int) = if(c > 255) 255 else if(c < 0) 0 else c

  def pressedColor(color: Int): Int = {
    val avg = average(color)
    if(avg < 80) return invert(pressedColor(invert(color)))
    def f(c: Int) = clamp((c * 0.7 * (c/(avg+0.01))).asInstanceOf[Int])
    transform(color, f)
  }

  def btn(normal:Int, pressed:Int) = new SStateListDrawable {
    +=(c(pressed), PRESSED)
    +=(c(LTGRAY), -ENABLED)
    +=(c(normal))
  }

  def btn(normal:Int): SStateListDrawable = btn(normal, pressedColor(normal))

  def white = btn(WHITE)

  def black = btn(BLACK)

  def selectable = new SStateListDrawable {
    +=(c(DKGRAY), SELECTED)
    +=(c(BLACK))
  }

  def clickable = new SStateListDrawable {
    +=(c(DKGRAY), SELECTED)
    +=(c(DKGRAY), PRESSED)
    +=(c(TRANSPARENT))
  }

  def gray = btn(LTGRAY)

  def blue = btn(0xff68B8FC)

  def green = btn(0xff65CA60)

  def yellow = btn(0xfff3d05d)
}

object Styles extends Styles