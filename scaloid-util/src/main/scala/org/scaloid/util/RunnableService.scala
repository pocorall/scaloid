package org.scaloid.util

trait RunnableService {
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

abstract class RunnableServiceConnector(activity: SActivity) {
  def runnableService: RunnableService

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
    if (runnableService.running) {
      startTimer()
    }
  }

  def updateUI(event: UpdateEvent)

  def start() {
    runnableService.start()
    runOnUiThread(updateUI(ON_STARTED))
    startTimer()
  }

  def stop() {
    runnableService.stop()
    runOnUiThread(updateUI(ON_STOPPED))
    timer.cancel()
  }

  def toggle() {
    if (runnableService.running) stop() else start()
  }
}