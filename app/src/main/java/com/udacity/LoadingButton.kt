package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Rectangles
    private lateinit var buttonRect: Rect
    private lateinit var animatedButtonRect: Rect
    private var buttonWidth = 0
    private var buttonHeight = 0
    private var animatedButtonRight = 0

    // Button dimens
    private var buttonTextX: Int = 0
    private var buttonTextY: Int = 0
    private var buttonTextSize: Float = 0.0F

    // Button colors
    private var buttonColor: Int = -1
    private var animatedButtonColor: Int = -1

    // Button Text
    private val buttonLoadingText = context.getString(R.string.button_loading)
    private val buttonCompleteText = context.getString(R.string.button_name)

    // Value animator
    private val buttonAnimator = ValueAnimator()
    private val arcAnimator = ValueAnimator()
    private val buttonAnimationDuration = 600L

    // Loading Circle
    private var arcSweepingAngle = 0f
    private var arcRadius = 50
    private val arcAnimationDuration = 800L
    private val arcMargin = 60
    private var arcColor: Int = -1

    // Button State
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, old, new ->
        if (old == ButtonState.Completed && new == ButtonState.Loading) {
            // Button Animator
            buttonAnimator.setIntValues(0, buttonWidth)
            buttonAnimator.duration = buttonAnimationDuration
            buttonAnimator.repeatCount = ValueAnimator.INFINITE
            buttonAnimator.repeatMode = ValueAnimator.RESTART
            buttonAnimator.addUpdateListener {
                animatedButtonRect.right = buttonAnimator.animatedValue as Int
                invalidate()
            }
            // Arc Animator
            arcAnimator.setFloatValues(0f, 360f)
            arcAnimator.duration = arcAnimationDuration
            arcAnimator.repeatCount = ValueAnimator.INFINITE
            arcAnimator.repeatMode = ValueAnimator.RESTART
            arcAnimator.addUpdateListener {
                arcSweepingAngle = arcAnimator.animatedValue as Float
                invalidate()
            }
            buttonAnimator.start()
            arcAnimator.start()
        } else if (old == ButtonState.Loading && new == ButtonState.Completed) {
            buttonAnimator.end()
            arcAnimator.end()
            animatedButtonRect.right = 0
            arcSweepingAngle = 0f
        }
        invalidate()
    }

    // Paint object
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LoadingButton).apply {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            animatedButtonColor = getColor(R.styleable.LoadingButton_animationColor, 0)
            arcColor = getColor(R.styleable.LoadingButton_arcColor, 0)
            recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        buttonRect = Rect(
            0, 0,
            w, h
        )
        animatedButtonRect = Rect(
            0, 0,
            animatedButtonRight, h
        )
        buttonWidth = w
        buttonHeight = h


        buttonTextX = w / 2
        buttonTextY = h / 2
        buttonTextSize = (w * h * 0.21995 / 1000).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonColor
        canvas?.drawRect(buttonRect, paint)

        paint.color = animatedButtonColor
        canvas?.drawRect(animatedButtonRect, paint)

        paint.color = Color.BLACK
        paint.textSize = buttonTextSize
        paint.textAlign = Paint.Align.CENTER
        canvas?.drawText(
            if (buttonState == ButtonState.Completed) buttonCompleteText else buttonLoadingText,
            buttonTextX.toFloat(),
            buttonTextY.toFloat(),
            paint
        )

        paint.color = arcColor
        canvas?.drawArc(
            (buttonWidth - (arcRadius * 2) - arcMargin).toFloat(), ((buttonHeight / 2) - arcRadius).toFloat(),
            (buttonWidth - arcMargin).toFloat(), ((buttonHeight / 2) + arcRadius).toFloat(),
            0f, arcSweepingAngle, true, paint
        )
    }
}