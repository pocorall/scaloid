/*
 *
 *
 *
 *
 * Less painful Android development with Scala
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 *
 * Copyright 2013 Sung-Ho Lee
 *
 * Sung-Ho Lee licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
 
package org.scaloid.common

import android.util.Log

case class LoggerTag(_tag: String) {
  private val MAX_TAG_LEN = 22
  val tag = if (_tag.length < MAX_TAG_LEN) _tag else ":" + _tag.substring(_tag.length - (MAX_TAG_LEN - 1), _tag.length)
}

trait TagUtil {
  implicit val tag = LoggerTag(this.getClass.getName)
}

trait Logger {
  @inline private def loggingText(str: String, t: Throwable) = str + (if (t == null) "" else "\n" + Log.getStackTraceString(t))

  @inline def verbose (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.VERBOSE)) Log.v  (tag.tag, loggingText(str, t))}
  @inline def debug   (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.DEBUG  )) Log.d  (tag.tag, loggingText(str, t))}
  @inline def info    (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.INFO   )) Log.i  (tag.tag, loggingText(str, t))}
  @inline def warn    (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.WARN   )) Log.w  (tag.tag, loggingText(str, t))}
  @inline def error   (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.ERROR  )) Log.e  (tag.tag, loggingText(str, t))}
  @inline def wtf     (str: => String, t: Throwable = null)(implicit tag: LoggerTag) { if (Log.isLoggable(tag.tag, Log.ASSERT )) Log.wtf(tag.tag, loggingText(str, t))}
}
