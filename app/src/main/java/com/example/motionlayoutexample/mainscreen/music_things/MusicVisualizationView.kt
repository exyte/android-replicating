package com.example.motionlayoutexample.mainscreen.music_things


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import com.example.motionlayoutexample.R
import kotlin.random.Random


class MusicVisualizationView : FrameLayout {

    companion object {
        const val STRIPES_QUANTITY = 40
        const val CORNER_RADIUS = 5f
        const val DELAY = 16L
    }

    var isNeedToAnim = true
    var playButtonSize = 50
        set(value) {
            field = value
            play.layoutParams.width = field
            play.layoutParams.height = field
            this.layoutParams.height = field
        }
    var animateSpeed = 0.6f

    val rectangle = RectF(0f, 0f, 0f, 0f)

    private var data: ArrayList<Float> = (1..40).map { Random.nextFloat() * 50 } as ArrayList<Float>
    private var newData: ArrayList<Float> = (1..40).map { Random.nextFloat() * 50 } as ArrayList<Float>
    private var middleLine = height / 2
    private val paint = Paint()
    private var musicEvents: MusicEvents? = null
    private lateinit var play: AppCompatImageButton


    interface MusicEvents {
        fun onStart()
        fun onStop()
    }

    constructor(context: Context) : super(context) {
        setWillNotDraw(false)
        initAnim()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
        initAnim()
        setPlayButton(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        initAnim()
        setPlayButton(attrs)
    }

    fun registerListener(musicListener: MusicEvents) {
        musicEvents = musicListener
    }


    private fun setPlayButton(attrs: AttributeSet?) {
        play = AppCompatImageButton(context, attrs)
        play.layoutParams = LayoutParams(
                playButtonSize,
                playButtonSize,
                Gravity.CENTER
        )

        setButtonImage(play)
        play.setOnClickListener {
            isNeedToAnim = !isNeedToAnim
            if (isNeedToAnim) {
                musicEvents?.onStart()
            } else {
                musicEvents?.onStop()
            }
            setButtonImage(play)
        }
        addView(play)
    }

    private fun setButtonImage(play: AppCompatImageButton) {
        val typedValue = TypedValue()
        val theme = context.theme

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setSize(playButtonSize, playButtonSize)
        if (isNeedToAnim) {
            theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
            @ColorInt val primaryColorVariant = typedValue.data

            play.setImageResource(R.drawable.ic_pause)
            shape.setColor(primaryColorVariant)
            play.background = shape
        } else {
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            @ColorInt val primaryColor = typedValue.data

            play.setImageResource(R.drawable.ic_play_arrow)
            shape.setColor(primaryColor)
            play.background = shape
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

    private fun animateItems() {
        if (isNeedToAnim) {
            data.forEachIndexed { index, fl ->
                when {
                    (fl - newData[index] > 0) -> {
                        data[index] -= animateSpeed
                    }
                    (fl - newData[index] < 0) -> {
                        data[index] += animateSpeed
                    }
                }
            }
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        middleLine = height / 2
    }

    fun startPlaying() {
        isNeedToAnim = true
        setButtonImage(play)
    }

    fun stopPlaying() {
        isNeedToAnim = false
        setButtonImage(play)
    }

    fun play(newRectFromEqualizer: ArrayList<Float>) {
        newData = newRectFromEqualizer
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            paint.color = Color.WHITE
            val defWidth = width / (2 * STRIPES_QUANTITY).toFloat()
            data.forEachIndexed { index, data ->
                val x = index * defWidth * 3
                rectangle.top = data / 2 + middleLine
                rectangle.right = x + defWidth
                rectangle.left = x
                rectangle.bottom = middleLine - (data / 2)
                canvas.drawRoundRect(rectangle, CORNER_RADIUS, CORNER_RADIUS, paint)
            }
        }
        super.onDraw(canvas)
    }

}