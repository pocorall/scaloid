$license()$

package org.scaloid.common

import android.content.{Context, SharedPreferences}
import scala.collection.JavaConversions._
import scala.language.dynamics
import scala.reflect._

@deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
class StringPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: String = _

  def selectDynamic(name: String): String = preferences.getString(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: String): String = preferences.getString(name, defaultVal)
}

@deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
class IntPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Int = _

  def selectDynamic(name: String): Int = preferences.getInt(name, defaultValue)

  def updateDynamic(name: String)(value: Int) {
    preferences.edit().putInt(name, value).commit()
  }

  def applyDynamic(name: String)(defaultVal: Int): Int = preferences.getInt(name, defaultVal)
}

@deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
class LongPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Long = _

  def selectDynamic(name: String): Long = preferences.getLong(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: Long): Long = preferences.getLong(name, defaultVal)
}

@deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
class FloatPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Float = _

  def selectDynamic(name: String): Float = preferences.getFloat(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: Float): Float = preferences.getFloat(name, defaultVal)

}

@deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
class BooleanPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Boolean = _

  def selectDynamic(name: String): Boolean = preferences.getBoolean(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: Boolean): Boolean = preferences.getBoolean(name, defaultVal)
}

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
  type SS = Set[String]

  def updateDynamic(name: String)(value: Any) {
    value match {
      case v: String => preferences.edit().putString(name, v).commit()
      case v: Int => preferences.edit().putInt(name, v).commit()
      case v: Long => preferences.edit().putLong(name, v).commit()
      case v: Boolean => preferences.edit().putBoolean(name, v).commit()
      case v: Float => preferences.edit().putFloat(name, v).commit()
      case v: SS => preferences.edit().putStringSet(name, v).commit()
    }
  }

  def applyDynamic[T](name: String)(defaultVal: T): T = defaultVal match {
    case v: String => preferences.getString(name, v).asInstanceOf[T]
    case v: Int => preferences.getInt(name, v).asInstanceOf[T]
    case v: Long => preferences.getLong(name, v).asInstanceOf[T]
    case v: Boolean => preferences.getBoolean(name, v).asInstanceOf[T]
    case v: Float => preferences.getFloat(name, v).asInstanceOf[T]
    case v: SS => preferences.getStringSet(name, v).toSet.asInstanceOf[T]
  }

  def remove(name: String) {
    preferences.edit().remove(name).commit()
  }

  @deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
  val String = new StringPreferences(preferences)
  @deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
  val Int = new IntPreferences(preferences)
  @deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
  val Long = new LongPreferences(preferences)
  @deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
  val Float = new FloatPreferences(preferences)
  @deprecated("Use Preferences instead. This will be removed from Scaloid 3.0.", "2.0")
  val Boolean = new BooleanPreferences(preferences)
}

object Preferences {
  def apply()(implicit ctx: Context) = new Preferences(defaultSharedPreferences)
}


$wholeClassDef(android.preference.Preference)$
$richClassDef(android.preference.DialogPreference)$
$wholeClassDef(android.preference.EditTextPreference)$
