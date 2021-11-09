package com.example.motionlayoutexample

import android.os.Build
import java.util.concurrent.TimeUnit

val Int.convertSecondsToMinutes: String
    get() {
        val minutes = TimeUnit.SECONDS.toMinutes(this.toLong())
        val remainderSeconds = this % 60
        val minutesStr = minutes.toInt().makeTwoDigitsStringForTime
        val secondsStr = remainderSeconds.makeTwoDigitsStringForTime
        return "$minutesStr:$secondsStr"
    }

val Int.makeTwoDigitsStringForTime: String
    get() {
        return if (this < 10) {
            "0$this"
        } else {
            "$this"
        }
    }

fun apiIsSmallerThanTheAndroidR() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R