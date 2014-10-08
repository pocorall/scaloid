package org.scaloid.common

import android.content.{Context, DialogInterface, Intent, IntentFilter}
import android.net.Uri
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.robolectric.annotation.Config
import org.robolectric.shadows.{ShadowAlertDialog, ShadowToast}
import org.robolectric.{Robolectric, RobolectricTestRunner}
import org.scalatest._
import org.scalatest.junit.JUnitSuite

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

@RunWith(classOf[RobolectricTestRunner])
@Config(manifest = Config.NONE, emulateSdk = 16)
class helpersTest extends JUnitSuite with ShouldMatchers {
  implicit var activity:SActivity = _
    @Before
    def createActivity():Unit={
        activity = Robolectric.buildActivity(classOf[SActivityImpl]).create.get
    }

    @Test
    def testAlert(): Unit ={
      AppHelpers.alert("TITLE","MESSAGE",()=>{})
      val alert = ShadowAlertDialog.getLatestAlertDialog
      alert shouldBe a ('showing)

      val shadowAlert = Robolectric.shadowOf(alert)
      shadowAlert.getTitle shouldBe "TITLE"
      shadowAlert.getMessage shouldBe "MESSAGE"

      alert.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
      alert shouldNot be a 'showing
    }

  @Test
  def testOpenUri():Unit ={
    val uri = Uri.parse("http://scaloid.org/")
    AppHelpers.openUri(uri)
    val shadowActivity = Robolectric.shadowOf(activity)
    val intent = shadowActivity.getNextStartedActivity
    intent.getAction shouldBe Intent.ACTION_VIEW
    intent.getData shouldBe uri
  }

  @Test
  def testPendingService():Unit = {
    implicit val tag = ClassTag(classOf[SServiceImpl])
    val shadowPI = Robolectric.shadowOf(AppHelpers.pendingService[SServiceImpl])
    val intent = shadowPI.getSavedIntent
    intent.getComponent.getClassName shouldBe classOf[SServiceImpl].getName
  }

  @Test
  def testPendingActivity():Unit = {
    implicit val tag = ClassTag(classOf[SActivityImpl])
    val shadowPI = Robolectric.shadowOf(AppHelpers.pendingActivity[SActivityImpl])
    val intent = shadowPI.getSavedIntent
    intent.getComponent.getClassName shouldBe classOf[SActivityImpl].getName
  }

  @Test
  def testBroadcastReceiver():Unit={
    val controller = Robolectric.buildActivity(classOf[SActivityImpl]).create
    activity = controller.get()
    ContentHelpers.broadcastReceiver(new IntentFilter(Intent.ACTION_VIEW))((c:Context,i:Intent)=> c.startActivity(SIntent[SActivityImpl]))
    activity = controller.start.resume.get

    val registered = Robolectric.getShadowApplication.getRegisteredReceivers.asScala
    registered shouldNot be a 'empty
    registered.foreach{r=>
      r.getIntentFilter.getAction(0) shouldBe Intent.ACTION_VIEW
    }

    val intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://scaloid.org/"))
    val received = Robolectric.getShadowApplication.getReceiversForIntent(intent).asScala
    received.size shouldBe 1
    received(0).onReceive(Robolectric.getShadowApplication.getApplicationContext,intent)
    val nextIntent = Robolectric.getShadowApplication.peekNextStartedActivity
    nextIntent.getComponent.getClassName shouldBe classOf[SActivityImpl].getName
  }

  @Test
  def testSharedPreference():Unit = {
    PreferenceHelpers.defaultSharedPreferences.edit.putString("foo","bar").commit
    PreferenceHelpers.defaultSharedPreferences.getString("foo","") shouldBe "bar"
  }

  @Test
  def testToast():Unit={
    WidgetHelpers.toast("Hello")
    ShadowToast.getTextOfLatestToast shouldBe "Hello"
  }
  @Test
  def testSpinnerDialog():Unit={
    val dialog = WidgetHelpers.spinnerDialog("TITLE","MESSAGE")
    dialog shouldBe a ('showing)

    val shadowDialog = Robolectric.shadowOf(dialog)
    shadowDialog.getTitle shouldBe "TITLE"

    /* ShadowProgressDialog.getMessage returns "null" wrongly at Robolectric 2.3 .
     *
     * Please refer to the URL below for more details.
     * [[https://github.com/robolectric/robolectric/blob/robolectric-2.3/src/test/java/org/robolectric/shadows/ProgressDialogTest.java#L78]]
     */
    //shadowDialog.getMessage shouldBe "MESSAGE"

    dialog.cancel()
    dialog shouldNot be a 'showing
  }
}
