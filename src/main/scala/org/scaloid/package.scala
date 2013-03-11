package org

/**
 * Scaloid marries Android code with Scala resulting in easier to understand
 * and maintain code.
 *
 *
 * @example
 *
 * {{{
 * package com.example.hello
 *
 * import android.os.Bundle
 * import android.widget.Button
 * import org.scaloid.common._
 *
 * class MainActivity extends SActivity {
 *
 *   override def onCreate( savedInstanceState:Bundle ) {
 *     super.onCreate(savedInstanceState)
 *
 *     contentView = new SVerticalLayout {
 *
 *       setTheme(android.R.style.Theme_Holo_NoActionBar)
 *
 *       style {
 *         case b:SButton => b.textSize(22 dip)
 *       }
 *
 *       STextView("Welcome").textSize(22 sp).<<.marginBottom(22 dip).>>
 *
 *       val name = SEditText()
 *       STextView("What is your name?").<<.marginBottom(22 dip).>>
 *
 *       SButton("GO").onClick(longToast(s"Hello, ${name.getText}"))
 *
 *     }.padding(20 dip)
 *   }
 * }
 * }}}
 *
 * @see [[org.scaloid.TermsAndConditions]]
 *
 * @author Sung-Ho Lee
 */

package object scaloid
