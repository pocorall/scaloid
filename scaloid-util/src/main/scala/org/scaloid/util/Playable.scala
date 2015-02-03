package org.scaloid.util

import android.content.Context
import android.telephony.TelephonyManager
import org.scaloid.common._

/**
 * A general abstraction of something that can be start and stop.
 */
trait Playable {
  protected var _running: Boolean = false
  protected var _startTime = 0L

  def running = _running

  def startTime = _startTime

  def timeElapsed = System.currentTimeMillis() - _startTime

  def start() {
    _running = true
    _startTime = System.currentTimeMillis()
  }

  def stop() {
    _running = false
  }
}

/**
 * Pause the running during the incoming call.
 */
trait PauseOnCall extends Playable {
  implicit def ctx: Context

  implicit def reg: Registerable

  private var _paused = false

  protected def paused = _paused

  onCallStateChanged {
    case (TelephonyManager.CALL_STATE_RINGING, _) =>
      if (running) {
        _paused = true
        stop()
      }
    case (TelephonyManager.CALL_STATE_IDLE, _) =>
      if (paused) {
        _paused = false
        start()
      }
    case _ =>
  }
}

/**
 * Pause the running during the incoming call.
 */
trait StopOnCall extends PauseOnCall {
  override protected def paused = false
}

import java.util.{ TimerTask, Timer }

object UpdateEvent extends Enumeration {
  type UpdateEvent = Value
  val OTHERS, ON_CONNECTED, ON_HEARTBEAT, ON_STOPPED, ON_STARTED = Value
}

import UpdateEvent._

/**
 * Provides event notifications of a Playable.
 */
abstract class PlayableConnector(activity: SActivity) {
  def playable: Playable

  protected var timer: Timer = new Timer()

  var timerInterval = 1000

  private def startTimer() {
    timer = new Timer()
    timer.schedule(new TimerTask {
      def run() {
        runOnUiThread(updateUI(ON_HEARTBEAT))
      }
    }, timerInterval, timerInterval)
  }

  activity.onPause {
    timer.cancel()
  }

  def onServiceConnected() {
    runOnUiThread(updateUI(ON_CONNECTED))
    if (playable.running) {
      startTimer()
    }
  }

  def updateUI(event: UpdateEvent)

  def start() {
    if (playable.running) return
    playable.start()
    runOnUiThread(updateUI(ON_STARTED))
    startTimer()
  }

  def stop() {
    if (!playable.running) return
    playable.stop()
    runOnUiThread(updateUI(ON_STOPPED))
    timer.cancel()
  }

  def toggle() {
    if (playable.running) stop() else start()
  }
}