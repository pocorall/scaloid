/*
 * Copyright 2012 Sung-Ho Lee
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
package net.pocorall.android.util

import android.content.{Intent, DialogInterface, Context}
import android.app.{Activity, AlertDialog}
import android.view.View
import android.net.Uri

object ScalaUtils {
  def alert(context: Context)(titleId: Int, textId: Int) {
    val builder: AlertDialog.Builder = new AlertDialog.Builder(context)
    builder.setTitle(titleId)
    builder.setMessage(textId)
    builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
      }
    })
    builder.show()
  }

  /**
   * Launches a new activity for a give uri. For example, opens a web browser for http protocols.
   */
  def openUri(activity: Activity)(uri: String) {
    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
  }

  implicit def function2ViewOnClickListener[F](f: View => F): View.OnClickListener =
    new View.OnClickListener() {
      def onClick(view: View) {
        f(view)
      }
    }

  implicit def function02ViewOnClickListener[F](f: => F): View.OnClickListener =
    new View.OnClickListener() {
      def onClick(view: View) {
        f
      }
    }

  implicit def function02runnable[F](f: => F): Runnable =
    new Runnable() {
      def run() {
        f
      }
    }

  implicit def function2runnable[F](f: () => F): Runnable =
    new Runnable() {
      def run() {
        f()
      }
    }
}