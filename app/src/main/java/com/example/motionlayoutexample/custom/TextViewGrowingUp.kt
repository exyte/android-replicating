package com.example.motionlayoutexample.custom

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.util.AttributeSet
import androidx.core.widget.addTextChangedListener
import kotlin.math.abs

class TextViewGrowingUp(
        context: Context,
        attrs: AttributeSet?,
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    companion object {
        const val DELAY = 16L
    }

    private var letterList = arrayListOf<LetterProperties>()
    private var isAnimationRequired = true
    private var isAnimatingNow = false

    var animationSpeed = 10f
    var letterOffset = 10
    private var initialHeight = (paint.fontMetrics.bottom - paint.fontMetrics.top) * 2

    init {
        setWillNotDraw(false)
        addTextChangedListener(afterTextChanged = ::textChanged)
        initAnim()
    }

    private fun textChanged(editable: Editable?) {
        val text = text.toString().toCharArray()
        letterList.clear()
        text.forEachIndexed { index, char ->
            val width: Float = paint.measureText(char.toString(), 0, 1)
            letterList.add(
                    LetterProperties(
                            index.calculatePosition,
                            width + paint.letterSpacing,
                            char
                    )
            )
        }
    }

    private fun initAnim() {
        post(object : Runnable {
            override fun run() {
                animateItems()
                postDelayed(this, DELAY)
            }
        })
    }

    fun playAnimation() {
        if (!isAnimatingNow) {
            getReadyForAnimation()
            isAnimationRequired = true
        }
    }

    private fun getReadyForAnimation() {
        letterList.forEachIndexed { index, letterProperties ->
            letterList[index] = letterProperties.copy(position = index.calculatePosition)
        }
    }

    val Int.calculatePosition: Float
        get() {
            return initialHeight + this * letterOffset
        }

    private fun animateItems() {
        if (isAnimationRequired) {
            letterList.forEach { letterProperties ->
                if (letterProperties.position >= abs(paint.fontMetrics.top) + animationSpeed) {
                    letterProperties.position -= animationSpeed
                    if (abs(letterProperties.position - abs(paint.fontMetrics.top)) < animationSpeed) {
                        letterProperties.position = abs(paint.fontMetrics.top)
                    }
                }
                if (letterList.last().position <= abs(paint.fontMetrics.top)) {
                    isAnimatingNow = false
                    isAnimationRequired = false
                }
            }
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        var x = 0f
        letterList.forEach { letterProperties ->
            val width: Float = paint.measureText(letterProperties.Letter.toString(), 0, 1)
            val paint = paint
            paint.color = currentTextColor
            canvas?.drawText(letterProperties.Letter.toString(), x, letterProperties.position, paint)
            x += width
        }
    }

    fun readyForAnimation() {
        isAnimationRequired = false
        getReadyForAnimation()
        invalidate()
    }

    data class LetterProperties(
            var position: Float,
            val width: Float,
            var Letter: Char,
    )
}