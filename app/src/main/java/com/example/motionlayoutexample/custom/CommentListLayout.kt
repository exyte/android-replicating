package com.example.motionlayoutexample.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.contains
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.motionlayoutexample.entities.CommentEntity


class CommentListLayout(
        context: Context, attrs: AttributeSet?,
) : FrameLayout(context, attrs) {

    companion object{
        const val START_ALPHA = 0f
    }

    var animDuration = 300L
    var tollMarginsPx = 5

    var isTollAnimating = false
    private lateinit var layout: ConstraintLayout

    fun setLayout(layout: ConstraintLayout) {
        this.layout = layout
        setupCommentToolView()
    }

    private fun setupCommentToolView() {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        layout.layoutParams = params
        layout.setOnClickListener {
            hideToolView(true)
        }
    }

    fun hideToolView(isAnimating: Boolean = true) {
        layout.alpha = START_ALPHA
        if (isAnimating) {
            val transitionSet = makeTransitionDisappearSet()
            TransitionManager.beginDelayedTransition(this, transitionSet)
        } else {
            removeView(layout)
        }
    }

    private fun makeTransitionDisappearSet(): TransitionSet {
        val transition = AlphaTransition()
        val transitionDisappear = AppearingTransitionTools()

        layout.let {
            transition.addTarget(it)
            transitionDisappear.addTarget(it)
        }

        val transitionSet = TransitionSet()
        transitionSet.apply {
            duration = animDuration
            addTransition(transition)
            addTransition(transitionDisappear)
        }
        addRemoveViewListener(transition)
        return transitionSet
    }

    fun clickOnSection(item: CommentEntity, originalPos: ToolSizes) {
        if (!isTollAnimating) {
            if (this.contains(layout)) {
                hideToolView(true)
            } else {
                val rootLayoutPosition = getRootLayoutPosition()

                val y = (originalPos.y - rootLayoutPosition[1]).toFloat()
                val x = originalPos.x.toFloat()

                val transitionSet = makeTransitionAppearSet(originalPos)

                TransitionManager.beginDelayedTransition(this, transitionSet)
                layout.y = y
                layout.x = x
                setAppearParams(originalPos.height, tollMarginsPx)

                if (!this.contains(layout)) {
                    this.addView(layout)
                }
            }
        }
    }

    private fun setAppearParams(height: Int, margins: Int) {
        val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                height + margins)
        layout.layoutParams = params
        layout.alpha = 1f
    }

    private fun makeTransitionAppearSet(originalPos: ToolSizes): TransitionSet {
        val transition = AlphaTransition()
        val transitionAppear = AppearingTransitionTools()
        transitionAppear.setEndValues(originalPos.height, originalPos.width)
        transitionAppear.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                isTollAnimating = true
            }

            override fun onTransitionEnd(transition: Transition) {
                isTollAnimating = false
            }

            override fun onTransitionCancel(transition: Transition) {
                isTollAnimating = false
            }

            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}

        })

        layout.let {
            transition.addTarget(it)
            transitionAppear.addTarget(it)
        }

        val transitionSet = TransitionSet()
        transitionSet.apply {
            addTransition(transition)
            addTransition(transitionAppear)
            duration = animDuration
        }
        return transitionSet
    }

    private fun getRootLayoutPosition(): IntArray {
        val rootLayoutPosition = IntArray(2)
        this.getLocationInWindow(rootLayoutPosition)
        return rootLayoutPosition
    }

    private fun addRemoveViewListener(transition: Transition) {
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                isTollAnimating = true
            }

            override fun onTransitionEnd(transition: Transition) {
                isTollAnimating = false
                this@CommentListLayout.removeView(layout)
            }

            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}
        })
    }

    data class ToolSizes(
            val height: Int,
            val width: Int,
            val x: Int,
            val y: Int,
    )
}