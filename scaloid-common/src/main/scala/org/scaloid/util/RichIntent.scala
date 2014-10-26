package org.scaloid.util

import android.app.Activity
import android.content._
import scala.reflect._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.{Context => MacroCtx}

object MacroImpl {

  def toName(c: MacroCtx)(value: c.Expr[_]): c.Expr[String] = {
    import c.universe._
    val valRep = show(value.tree)
    val valueRepTree = Literal(Constant(valRep))
    c.Expr[String](valueRepTree)
  }

  def put_impl(c: MacroCtx)(values: c.Expr[Any]*): c.Expr[Intent] = {
    import c.universe._
    var result:c.Tree = q"${c.prefix.tree}.intent"
    values.foreach { value =>
      result = q"$result.putExtra(${toName(c)(value)}, $value)"
    }
    c.Expr(result)
  }
}
