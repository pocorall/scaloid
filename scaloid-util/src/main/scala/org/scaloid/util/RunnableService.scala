package org.scaloid.util

/**
 * A service that can be start and stop.
 */
@deprecated("Use PlayableConnecter instead.", "3.6")
trait RunnableService {
  var running: Boolean = false
  var startTime = 0L

  def timeElapsed: Long = System.currentTimeMillis() - startTime

  def start(): Unit = {
    running = true
    startTime = System.currentTimeMillis()
  }

  def stop(): Unit = {
    running = false
  }
}

import org.scaloid.common._
import java.util.{ TimerTask, Timer }

import UpdateEvent._

@deprecated("Use PlayableConnecter instead.", "3.6")
abstract class RunnableServiceConnector(activity: SActivity) {
  def runnableService: RunnableService

  protected var timer: Timer = new Timer()

  var timerInterval = 1000

  private def startTimer(): Unit = {
    timer = new Timer()
    timer.schedule(new TimerTask {
      def run(): Unit = {
        runOnUiThread(updateUI(ON_HEARTBEAT))
      }
    }, timerInterval, timerInterval)
  }

  activity.onPause {
    timer.cancel()
  }

  def onServiceConnected(): Unit = {
    runOnUiThread(updateUI(ON_CONNECTED))
    if (runnableService.running) {
      startTimer()
    }
  }

  def updateUI(event: UpdateEvent): Unit

  def start(): Unit = {
    if (runnableService.running) return
    runnableService.start()
    runOnUiThread(updateUI(ON_STARTED))
    startTimer()
  }

  def stop(): Unit = {
    if (!runnableService.running) return
    runnableService.stop()
    runOnUiThread(updateUI(ON_STOPPED))
    timer.cancel()
  }

  def toggle(): Unit = {
    if (runnableService.running) stop() else start()
  }
}