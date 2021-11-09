package com.example.motionlayoutexample.custom

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues

class AlphaTransition:Transition() {

    companion object{
        const val START_ALPHA = 0f
    }

    private val animator: ValueAnimator = ValueAnimator.ofFloat()

    private val ALPHA = "motionlayoutexample:alphaTransition:alpha"

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        transitionValues.values[ALPHA] = view.alpha
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        var startAlpha = START_ALPHA

        if (startValues != null) {
            startAlpha = startValues.values[ALPHA] as Float
        }

        val view = targets[0]

        return if(endValues==null){
            null
        } else {
            val endAlpha = endValues.values[ALPHA] as Float
            animator.setObjectValues(startAlpha,endAlpha)
            animator.addUpdateListener {
                val value:Float = it.animatedValue as Float
                view.alpha = value
            }
            animator
        }
    }
}