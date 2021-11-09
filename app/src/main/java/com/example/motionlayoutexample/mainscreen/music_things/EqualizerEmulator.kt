package com.example.motionlayoutexample.mainscreen.music_things

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class EqualizerEmulator(lifecycleScope: LifecycleCoroutineScope) {

    private var equalizer: Equalizer? = null
    var delay = 500L
    var maxValue = 50

    init {
        lifecycleScope.launch {
            while (true) {
                delay(delay)
                val random = Random
                val randomNumbers: ArrayList<Float> = (1..40).map { random.nextFloat()*maxValue } as ArrayList<Float>
                equalizer?.setNewData(randomNumbers)
            }
        }
    }

    fun registerListener(eq: Equalizer) {
        equalizer = eq
    }


}