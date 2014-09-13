package org.scaloid.util

import org.scaloid.common._


/**
 * A general abstraction of something that can be start and stop.
 */
trait Playable {
  private var _running: Boolean = false
  private var _startTime = 0L

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
trait TelephonePause extends Playable {
  implicit def ctx: Context

  implicit def reg: Registerable

  private var paused = false

  onCallStateChanged {
    case (TelephonyManager.CALL_STATE_RINGING, _) =>
      if (running) {
        paused = true
        stop()
      }
    case (TelephonyManager.CALL_STATE_IDLE, _) =>
      if (paused) {
        paused = false
        start()
      }
    case _ =>
  }
}

import org.scaloid.common._
import java.util.{TimerTask, Timer}

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