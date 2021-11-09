package com.example.motionlayoutexample.mainscreen.music_things

import android.os.CountDownTimer
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.motionlayoutexample.convertSecondsToMinutes

class MusicManager(
        val player: MusicVisualizationView,
        lifecycleScope: LifecycleCoroutineScope,
) {

    companion object{
        const val TIMER_INTERVAL = 1000L
        const val TIMER_END = 3000000L
    }

    private var newTimeData: TimeData? = null

    var isPlaying = true
    var isCountDownTimerRunning = true
    var trackDuration = 20
    var startTime = 0
    var endTime = 20

    fun registerListener(listener:TimeData){
        newTimeData = listener
    }

    init {
        player.registerListener( object : MusicVisualizationView.MusicEvents {
            override fun onStart() {
                startTimer()
                isPlaying = true
                if (!isCountDownTimerRunning) {
                    timer.start()
                }
            }

            override fun onStop() {
                isPlaying = false
            }
        })
        val equalizerEmulator = EqualizerEmulator(lifecycleScope)
        equalizerEmulator.registerListener(object : Equalizer {
            override fun setNewData(randomNumbers: ArrayList<Float>){
                player.play(randomNumbers)
            }
        }
        )
    }

    private fun startTimer() {
        if (endTime <= 0) {
            startTime = 0
            endTime = trackDuration
        }
    }

    val timer: CountDownTimer = object : CountDownTimer(TIMER_END, TIMER_INTERVAL) {

        override fun onTick(millisUntilFinished: Long) {
            isCountDownTimerRunning = true
            if (isPlaying) {
                val startTimeStr = (startTime++).convertSecondsToMinutes
                val endTimeStr = (endTime--).convertSecondsToMinutes
                newTimeData?.newTimeData(startTimeStr,endTimeStr)

            }
            checkForEnd()
        }

        override fun onFinish() {
            isCountDownTimerRunning = false
            player.stopPlaying()
        }
    }.start()

    private fun checkForEnd() {
        if (endTime <= -1) {
            isPlaying = false
            player.stopPlaying()
        }
    }

    fun endSession() {
        timer.cancel()
    }

}