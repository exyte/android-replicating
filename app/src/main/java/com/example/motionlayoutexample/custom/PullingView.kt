package com.example.motionlayoutexample.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class PullingView(
        context: Context,
        attrs: AttributeSet?,
) : FrameLayout(context, attrs), View.OnTouchListener {

    private var dx = 0f
    private var dy = 0f
    private var oldX = 0f

    private var onChange: OnStateChange? = null

    init {
        setOnTouchListener(this)
    }

    interface OnStateChange {
        fun onPositionChange(x: Int)
        fun onFingerRelease(x: Float)
    }

    fun registerListener(sensor: OnStateChange) {
        onChange = sensor
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dx = view.x - event.rawX
                dy = view.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                oldX = event.rawX + dx
                onChange?.onPositionChange((event.rawX + dx).toInt())
            }
            MotionEvent.ACTION_UP -> {
                onChange?.onFingerRelease(event.rawX)
            }
            else -> {
                return false
            }
        }
        return false
    }

}