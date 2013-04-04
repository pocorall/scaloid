package org.scaloid.common

import scala.language.dynamics
import android.content.{Context, SharedPreferences}

class StringPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: String = _

  def selectDynamic(name: String): String = preferences.getString(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: String): String = preferences.getString(name, defaultVal)
}

class IntPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Int = _

  def selectDynamic(name: String): Int = preferences.getInt(name, defaultValue)

  def updateDynamic(name: String)(value: Int) {
    preferences.edit().putInt(name, value).commit()
  }

  def applyDynamic(name: String)(defaultVal: Int): Int = preferences.getInt(name, defaultVal)
}

class LongPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Long = _

  def selectDynamic(name: String): Long = preferences.getLong(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: Long): Long = preferences.getLong(name, defaultVal)
}

class FloatPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Float = _

  def selectDynamic(name: String): Float = preferences.getFloat(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: Float): Float = preferences.getFloat(name, defaultVal)

}

class BooleanPreferences(preferences: SharedPreferences) extends Dynamic {
  var defaultValue: Boolean = _

  def selectDynamic(name: String): Boolean = preferences.getBoolean(name, defaultValue)

  def applyDynamic(name: String)(defaultVal: Boolean): Boolean = preferences.getBoolean(name, defaultVal)
}

class Preferences(val preferences: SharedPreferences) extends Dynamic {
  def updateDynamic(name: String)(value: Any) {
    value match {
      case v: String => preferences.edit().putString(name, v).commit()
      case v: Int => preferences.edit().putInt(name, v).commit()
      case v: Long => preferences.edit().putLong(name, v).commit()
      case v: Boolean => preferences.edit().putBoolean(name, v).commit()
      case v: Float => preferences.edit().putFloat(name, v).commit()
    }
  }

  def applyDynamic[T](name: String)(defaultVal: T): T = defaultVal match {
    case v: String => preferences.getString(name, v).asInstanceOf[T]
    case v: Int => preferences.getInt(name, v).asInstanceOf[T]
    case v: Long => preferences.getLong(name, v).asInstanceOf[T]
    case v: Boolean => preferences.getBoolean(name, v).asInstanceOf[T]
    case v: Float => preferences.getFloat(name, v).asInstanceOf[T]
  }

  val String = new StringPreferences(preferences)
  val Int = new IntPreferences(preferences)
  val Long = new LongPreferences(preferences)
  val Float = new FloatPreferences(preferences)
  val Boolean = new BooleanPreferences(preferences)
}

object Preferences {
  def apply()(implicit ctx: Context) = new Preferences(defaultSharedPreferences)
}
