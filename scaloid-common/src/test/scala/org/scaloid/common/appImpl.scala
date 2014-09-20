package org.scaloid.common

import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.os.{IBinder, Bundle}
import android.view.View
import android.widget.Button

class SActivityImpl extends SActivity {
  var current:LifeCycle = Initialized

  override def onCreate(savedInstanceState: Bundle): Unit = {
    onCreate(current = OnCreate)
    onStart(current = OnStart)
    onStop(current = OnStop)
    onResume(current = OnResume)
    onPause(current = OnPause)
    onDestroy(current = OnDestroy)
    super.onCreate(savedInstanceState)
    val sld = new StateListDrawable()
    sld.addState(Array(android.R.attr.state_pressed),android.R.drawable.btn_star_big_on)
    sld.addState(Array.empty,android.R.drawable.btn_star_big_off)
    contentView = new SVerticalLayout {
      STextView("Hello").id(1)
      SButton("Button").id(2).onClick{v:View => v.asInstanceOf[Button].setText("Pressed")}
      SImageView(stateListDrawable).id(3)
    }
  }

  def alertDialog():AlertDialogBuilder =
    new AlertDialogBuilder("TITLE","MESSAGE")

  def stateListDrawable():StateListDrawable = new SStateListDrawable{
    +=(android.R.drawable.btn_star_big_on, PRESSED)
    +=(android.R.drawable.btn_star_big_off)
  }
}

class SServiceImpl extends SService{
  var current:LifeCycle = Initialized

  override def onBind(p1: Intent): IBinder = null

  override def onCreate(): Unit = {
    onCreate(current = OnCreate)
    onDestroy(current = OnDestroy)
    super.onCreate()
  }
}

sealed abstract class LifeCycle(val name:String)
case object OnCreate extends LifeCycle("onCreate")
case object OnStart extends LifeCycle("onStart")
case object OnStop extends LifeCycle("onStop")
case object OnResume extends LifeCycle("onResume")
case object OnPause extends LifeCycle("onPause")
case object OnDestroy extends LifeCycle("onDestroy")
case object Initialized extends LifeCycle("N/A")