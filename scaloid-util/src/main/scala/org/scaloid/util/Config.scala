package org.scaloid.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import android.util.DisplayMetrics


object Config {
  @inline def config(implicit context: Context) = context.getResources.getConfiguration

  @inline def orientation(implicit context: Context): Int = config.orientation

  @inline def portrait(implicit context: Context): Boolean = orientation == Configuration.ORIENTATION_PORTRAIT

  @inline def landscape(implicit context: Context): Boolean = orientation == Configuration.ORIENTATION_LANDSCAPE

  @inline def square(implicit context: Context): Boolean = orientation == Configuration.ORIENTATION_SQUARE

  @inline def long(implicit context: Context): Boolean = (config.screenLayout & Configuration.SCREENLAYOUT_LONG_YES) != 0

  @inline def locale(implicit context: Context): Locale = config.locale

  @inline def width(implicit context: Context): Int = context.getResources.getDisplayMetrics.widthPixels

  @inline def height(implicit context: Context): Int = context.getResources.getDisplayMetrics.heightPixels

  @inline def dpi(implicit context: Context): Int = context.getResources.getDisplayMetrics.densityDpi

  val LDPI = DisplayMetrics.DENSITY_LOW

  val MDPI = DisplayMetrics.DENSITY_MEDIUM

  val HDPI = DisplayMetrics.DENSITY_HIGH

  val XHDPI = 320 // DisplayMetrics.DENSITY_XHIGH  // heigher than API level 9

  val XXHDPI = 480 // DisplayMetrics.DENSITY_XHIGH  // heigher than API level 16
}