package com.example.motionlayoutexample.custom

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily


class CornersTransition : Transition() {

    private val CORNER_RADIUS = "motionlayoutexample:imageCornerTransition:shapeAppearanceModel"

    var startCornerRadius = 0f
    var endCornerRadius = 0f
    private val animator: ValueAnimator = ValueAnimator.ofFloat()

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view is ShapeableImageView) {
            val view = transitionValues.view as ShapeableImageView
            val corner = view.shapeAppearanceModel.bottomLeftCornerSize
            transitionValues.values[CORNER_RADIUS] = corner
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun createAnimator(
            sceneRoot: ViewGroup,
            startValues: TransitionValues?,
            endValues: TransitionValues?,
    ): Animator? {
        val image = targets[0]

        animator.setObjectValues(startCornerRadius, endCornerRadius)
        return if (targets[0] == null) {
            null
        } else {
            animator.duration = duration
            animator.addUpdateListener {
                val value: Float = it.animatedValue as Float
                (image as ShapeableImageView).shapeAppearanceModel =
                        image.shapeAppearanceModel
                                .toBuilder()
                                .setAllCorners(CornerFamily.ROUNDED, value)
                                .build()
            }
            animator
        }
    }

    fun setValues(startCorner: Float, endCorner: Float) {
        startCornerRadius = startCorner
        endCornerRadius = endCorner
    }
}