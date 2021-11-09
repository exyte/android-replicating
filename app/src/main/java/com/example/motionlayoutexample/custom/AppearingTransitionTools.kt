package com.example.motionlayoutexample.custom

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.transition.Transition
import androidx.transition.TransitionValues

class AppearingTransitionTools : Transition() {

    private val HEIGHT = "ru.alexydenkov.customtransition:CustomTransition:height"
    private val WiDTH = "ru.alexydenkov.customtransition:CustomTransition:width"
    private var endHeight = 0
    private var endWidth = 0

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view

        transitionValues.values[HEIGHT] = view.height
        transitionValues.values[WiDTH] = view.width
    }

    fun setEndValues(endHeight1: Int, endWidth1: Int) {
        endHeight = endHeight1
        endWidth = endWidth1
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        var startWidth = 0

        if (startValues != null) {
            startWidth = startValues.values[WiDTH] as Int
        }

        if (endValues == null) {
            return null
        } else {

            endHeight = endValues.values[HEIGHT] as Int
            endWidth = endValues.values[WiDTH] as Int

            val image1 = targets[0]

            val startImageY = image1.y

            val animator = ValueAnimator.ofFloat(startWidth.toFloat(), endWidth.toFloat())
            if (targets[0] == null) {
                return null
            } else {
                animator.addUpdateListener {
                    val y: Float
                    val x: Float
                    val value: Float = it.animatedValue as Float
                    x = if (value > endWidth) {
                        endWidth.toFloat()
                    } else {
                        image1.x = endWidth - value
                        value
                    }
                    y = if (value > endHeight) {
                        image1.y = startImageY - endHeight / 2
                        endHeight.toFloat()
                    } else {
                        if (image1.y > startImageY - endHeight / 2) {
                            image1.y = startImageY - value / 2
                        }
                        value
                    }
                    image1.layoutParams = FrameLayout.LayoutParams(x.toInt(), y.toInt())
                }
                return animator
            }
        }
    }
}