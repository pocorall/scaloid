$license()$
 
package org.scaloid.common

import android.util.Log


case class LoggerTag(_tag: String) {
  private val MAX_TAG_LEN = 22
  val tag = if (_tag.length < MAX_TAG_LEN) _tag else ":" + _tag.substring(_tag.length - (MAX_TAG_LEN - 1), _tag.length)
}

trait TagUtil {
  implicit val tag = LoggerTag(this.getClass.getName)
}

/**
 * f
 */
trait Logger {
  @inline private def loggingText(str: String, t: Throwable) = str + (if (t == null) "" else "\n" + Log.getStackTraceString(t))

  @inline def verbose (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.VERBOSE)) Log.v  (tag.tag, loggingText(str, t))}
  @inline def debug   (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.DEBUG  )) Log.d  (tag.tag, loggingText(str, t))}
  @inline def info    (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.INFO   )) Log.i  (tag.tag, loggingText(str, t))}
  @inline def warn    (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.WARN   )) Log.w  (tag.tag, loggingText(str, t))}
  @inline def error   (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.ERROR  )) Log.e  (tag.tag, loggingText(str, t))}
  @inline def wtf     (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.ASSERT )) Log.wtf(tag.tag, loggingText(str, t))}
}
