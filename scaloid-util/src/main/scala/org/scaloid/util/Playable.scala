package org.scaloid.util


/**
 * A general abstraction of something that can be start and stop.
 */
trait Playable {
  var running: Boolean = false
  var startTime = 0L

  def timeElapsed = System.currentTimeMillis() - startTime

  def start() {
    running = true
    startTime = System.currentTimeMillis()
  }

  def stop() {
    running = false
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