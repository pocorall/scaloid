package org.scaloid.common

import android.content.DialogInterface
import android.graphics.drawable.StateListDrawable
import android.os.Looper
import android.widget.{ImageView, Button, TextView}
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.{Robolectric, RobolectricTestRunner}
import org.scalatest.ShouldMatchers
import org.scalatest.junit.JUnitSuite

import scala.concurrent.{ExecutionContext, Future}

@RunWith(classOf[RobolectricTestRunner])
@Config(manifest = Config.NONE, emulateSdk = 16)
class appTest extends JUnitSuite with ShouldMatchers {

  @Test
  def testSActivityType(): Unit = {
    val activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    activity shouldBe a [SActivity]
    activity shouldBe a [SContext]
    activity shouldBe a [TraitActivity[_]]
    activity shouldBe a [Destroyable]
    activity shouldBe a [Creatable]
    activity shouldBe a [Registerable]
  }

  @Test
  def testTraitActivityMembers(): Unit = {
    val activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    activity.basis shouldBe activity

    val textView = activity.find[TextView](1)
    textView shouldBe activity.findViewById(1)
    textView.getText.toString shouldBe "Hello"
  }

  @Test
  def testRunOnUIThread():Unit = {
    val activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    val mainThread = Looper.getMainLooper.getThread
    activity.runOnUiThread {
      Thread.currentThread shouldBe mainThread
    }

    Future[Unit] {
      Thread.currentThread() shouldNot be (mainThread)
      activity.runOnUiThread {
        Thread.currentThread shouldBe mainThread
      }
    }(ExecutionContext.global)
  }

  @Test
  def testButtonClick(): Unit = {
    val activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    val button = activity.find[Button](2)
    button.performClick()
    button.getText shouldBe "Pressed"
  }

  @Test
  def testSStateListDrawable(): Unit = {
    val activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    val drawable = activity.find[ImageView](3).getDrawable.asInstanceOf[StateListDrawable]
    val shadowDrawable = Robolectric.shadowOf(drawable)

    val pressedDrawable = shadowDrawable.getDrawableForState(Array(android.R.attr.state_pressed))
    pressedDrawable shouldBe activity.getResources.getDrawable(android.R.drawable.btn_star_big_on)

    val normalDrawable = shadowDrawable.getDrawableForState(Array.empty)
    normalDrawable shouldBe activity.getResources.getDrawable(android.R.drawable.btn_star_big_off)
  }

  @Test
  def testAlertDialog(): Unit = {
    val activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    val alert = activity.alertDialog
    .positiveButton("POS",{(di:DialogInterface,id:Int) => di.dismiss})
    .show()
    alert shouldBe a ('showing)

    val shadowAlert = Robolectric.shadowOf(alert)
    shadowAlert.getTitle shouldBe "TITLE"
    shadowAlert.getMessage shouldBe "MESSAGE"

    alert.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
    alert shouldNot be a 'showing
  }

  @Test
  def testSActivityLifeCycles(): Unit = {
    val c = Robolectric.buildActivity(classOf[SActivityImpl])
    c.create.get.current shouldBe OnCreate
    c.create.start.get.current shouldBe OnStart
    c.create.start.resume.get.current shouldBe OnResume
    c.create.start.resume.pause.get.current shouldBe OnPause
    c.create.start.resume.pause.stop.get.current shouldBe OnStop
    c.create.start.resume.pause.stop.destroy.get.current shouldBe OnDestroy
  }

  @Test
  def testSServiceType(): Unit = {
    val service = Robolectric.buildService(classOf[SServiceImpl]).create.get
    service shouldBe a [SService]
    service shouldBe a [SContext]
    service shouldBe a [Destroyable]
    service shouldBe a [Creatable]
    service shouldBe a [Registerable]
  }

  @Test
  def testSServiceLifeCycles(): Unit = {
    val c = Robolectric.buildService(classOf[SServiceImpl])
    c.create.get.current shouldBe OnCreate
    c.create.destroy.get.current shouldBe OnDestroy
  }
}