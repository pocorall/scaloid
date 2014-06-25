$license()$

package org.scaloid.common

import android.content.{Context, SharedPreferences}
import scala.collection.JavaConversions._
import scala.language.dynamics
import scala.reflect._

/**
 * An accessor of SharedPreferences.
 *
 * {{{
 *   val pref = Preferences
 *   val ec = pref.executionCount(0)
 *   pref.executionCount = ec + 1
 * }}}
 *
 * Refer to the URL below for more details.
 *
 * [[http://blog.scaloid.org/2013/03/dynamicly-accessing-sharedpreferences.html]]
 *
 * @param preferences
 */
class Preferences(val preferences: SharedPreferences) extends Dynamic {
  def updateDynamic(name: String)(value: Any) {
    value match {
      case v: String => preferences.edit().putString(name, v).commit()
      case v: Int => preferences.edit().putInt(name, v).commit()
      case v: Long => preferences.edit().putLong(name, v).commit()
      case v: Boolean => preferences.edit().putBoolean(name, v).commit()
      case v: Float => preferences.edit().putFloat(name, v).commit()
$if(ver.gte_11)$
      case v: Set[String] => preferences.edit().putStringSet(name, v).commit()
$endif$
    }
  }

  def applyDynamic[T](name: String)(defaultVal: T): T = defaultVal match {
    case v: String => preferences.getString(name, v).asInstanceOf[T]
    case v: Int => preferences.getInt(name, v).asInstanceOf[T]
    case v: Long => preferences.getLong(name, v).asInstanceOf[T]
    case v: Boolean => preferences.getBoolean(name, v).asInstanceOf[T]
    case v: Float => preferences.getFloat(name, v).asInstanceOf[T]
$if(ver.gte_11)$
    case v: Set[String] => preferences.getStringSet(name, v).toSet.asInstanceOf[T]
$endif$
  }

  def remove(name: String) {
    preferences.edit().remove(name).commit()
  }

  abstract class TypedPreferences[T] extends Dynamic {
    def get(name: String): T
    def selectDynamic(name: String): Option[T] = if(preferences.contains(name)) Some(get(name)) else None
  }

  val String = new TypedPreferences[String] {
    override def get(name: String): String = preferences.getString(name, "")
  }

  val Int = new TypedPreferences[Int] {
    override def get(name: String): Int = preferences.getInt(name, 0)
  }

  val Long = new TypedPreferences[Long] {
    override def get(name: String): Long = preferences.getLong(name, 0L)
  }

  val Boolean = new TypedPreferences[Boolean] {
    override def get(name: String): Boolean = preferences.getBoolean(name, true)
  }

  val Float = new TypedPreferences[Float] {
    override def get(name: String): Float = preferences.getFloat(name, 0f)
  }

$if(ver.gte_11)$
  val StringSet = new TypedPreferences[Set[String]] {
    override def get(name: String): Set[String] = preferences.getStringSet(name, null).toSet
  }
$endif$
}

object Preferences {
  def apply()(implicit ctx: Context) = new Preferences(defaultSharedPreferences)
}

class Extra(val activity: SActivity) extends Dynamic {
  def intent = activity.intent
  def extra = intent match {
    case Some(i) if i.getExtras != null => Option(i.getExtras)
    case _ => None
  }

  def updateDynamic[T](name: String)(value: T) {
    intent.map(i=> value match {
      case v:Boolean => i.putExtra(name, v)
      case v:Byte => i.putExtra(name, v)
      case v:Char => i.putExtra(name, v)
      case v:Short => i.putExtra(name, v)
      case v:Int => i.putExtra(name, v)
      case v:Long => i.putExtra(name, v)
      case v:Float => i.putExtra(name, v)
      case v:Double => i.putExtra(name, v)
      case v:String => i.putExtra(name, v)
      case v:CharSequence => i.putExtra(name, v)
      case v:android.os.Parcelable => i.putExtra(name, v)
      //        case v: => i.putExtra(name, v)
      // TODO: more types to add...
    })
  }

  def selectDynamic[T](name: String): Option[T] = {
    extra.map(x => if (x.containsKey(name)) {
      try {
        return Some(x.get(name).asInstanceOf[T])
      } catch { case e:Exception => println(e.printStackTrace()) }
    })
    None
  }

  def remove(name: String) = intent match { case Some(i) => i.removeExtra(name) }

}
object Extra {
  def apply()(implicit basis: SActivity) = new Extra(basis)
}


$wholeClassDef(android.preference.Preference)$
$richClassDef(android.preference.DialogPreference)$
$wholeClassDef(android.preference.EditTextPreference)$
