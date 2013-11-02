package org.scaloid.util

import android.content.Context
import java.util.Locale
import android.util.DisplayMetrics

/**
 * Shortcuts for various device support.
 *
 * {{{
 *   import org.scaloid.util.Configuration._
 *
 *   if(long) SButton("This button is shown only for a long screen dimension (" + width + ", " + height + ")")
 *   if(landscape) SLinearLayout {
 *     SButton("Buttons for")
 *     SButton("landscape")
 *     SButton("layout")
 *   }
 *   if(dpi >= HDPI) SButton("You have a high resolution display!")
 * }}}
 */
object Configuration {
  @inline def conf(implicit context: Context) = context.getResources.getConfiguration

  @inline def orientation(implicit context: Context): Int = conf.orientation

  @inline def portrait(implicit context: Context): Boolean = orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

  @inline def landscape(implicit context: Context): Boolean = orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

  @inline def square(implicit context: Context): Boolean = orientation == android.content.res.Configuration.ORIENTATION_SQUARE

  @inline def long(implicit context: Context): Boolean = (conf.screenLayout & android.content.res.Configuration.SCREENLAYOUT_LONG_YES) != 0

  @inline def locale(implicit context: Context): Locale = conf.locale

  @inline def displayMetrics(implicit context: Context) = context.getResources.getDisplayMetrics

  @inline def width(implicit context: Context): Int = displayMetrics.widthPixels

  @inline def height(implicit context: Context): Int = displayMetrics.heightPixels

  @inline def dpi(implicit context: Context): Int = displayMetrics.densityDpi

  val LDPI = DisplayMetrics.DENSITY_LOW

  val MDPI = DisplayMetrics.DENSITY_MEDIUM

  val HDPI = DisplayMetrics.DENSITY_HIGH

  val XHDPI = 320 // DisplayMetrics.DENSITY_XHIGH  // heigher than API level 9

  val XXHDPI = 480 // DisplayMetrics.DENSITY_XHIGH  // heigher than API level 16
}