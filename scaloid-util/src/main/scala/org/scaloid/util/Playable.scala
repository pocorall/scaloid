package org.scaloid.util

import android.content.Context
import android.telephony.TelephonyManager
import org.scaloid.common._

/**
 * A general abstraction of something that can be start and stop.
 */
trait Playable {
  protected var _startTime = 0L
  val NOT_STARTED = 0L

  final def running: Boolean = _startTime != NOT_STARTED

  final def startTime: Long = _startTime

  final def timeElapsed: Long = {
    val t = _startTime
    if (t == NOT_STARTED) 0L else System.currentTimeMillis() - t
  }

  def start(): Unit = synchronized { _startTime = System.currentTimeMillis() }

  def stop(): Long = synchronized {
    val elapsed = timeElapsed
    _startTime = NOT_STARTED
    elapsed
  }
}

/**
 * Pause the running during the incoming call.
 */
trait PauseOnCall extends Playable {
  implicit def ctx: Context

  implicit def reg: Registerable

  private var _paused = false

  protected def paused: Boolean = _paused

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
  def playable: Option[Playable]

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
    if (playable.fold(false)(_.running)) {
      startTimer()
    }
  }

  def updateUI(event: UpdateEvent): Unit

  private def start(p: Playable): Unit = {
    if (p.running) return
    p.start()
    runOnUiThread(updateUI(ON_STARTED))
    startTimer()
  }

  def start(): Unit = playable.foreach(start)

  private def stop(p: Playable): Unit = {
    if (!p.running) return
    p.stop()
    runOnUiThread(updateUI(ON_STOPPED))
    timer.cancel()
  }

  def stop(): Unit = playable.foreach(stop)

  def toggle(): Unit = playable.foreach { p =>
    if (p.running) stop(p) else start(p)
  }
}