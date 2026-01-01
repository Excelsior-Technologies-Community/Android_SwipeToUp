package com.ext.android_swipe_to_up

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs
import kotlin.math.min

class SwipeToUpView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var currentIndex = 0
    private var dragOffset = 0f
    private var isAnimating = false

    private val swipeThresholdRatio = 0.25f
    private val animationDuration = 300L

    private var startY = 0f
    private var listener: OnSwipeListener? = null

    init {
        clipChildren = false
        clipToPadding = false
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    // ---------------- MEASURE ----------------
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        for (i in 0 until childCount) {
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
            )
        }
        setMeasuredDimension(w, h)
    }

    // ---------------- LAYOUT ----------------
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutPages()
    }


    // ---------------- TOUCH ----------------
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isAnimating || childCount == 0) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val delta = event.y - startY
                startY = event.y

                dragOffset = (dragOffset + delta)
                    .coerceIn(-height.toFloat(), height.toFloat())

                layoutPages()
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                releaseSwipe()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // ---------------- RELEASE ----------------
    private fun releaseSwipe() {
        val threshold = height * swipeThresholdRatio

        when {
            dragOffset < -threshold && currentIndex < childCount - 1 -> {
                animateTo(-height.toFloat()) {
                    currentIndex++
                    reset()
                    listener?.onSwipeUp(currentIndex)
                }
            }

            dragOffset > threshold && currentIndex > 0 -> {
                animateTo(height.toFloat()) {
                    currentIndex--
                    reset()
                    listener?.onSwipeDown(currentIndex)
                }
            }

            else -> {
                animateTo(0f) { reset() }
            }
        }
    }

    // ---------------- ANIMATION ----------------
    private fun animateTo(target: Float, end: () -> Unit) {
        isAnimating = true

        ValueAnimator.ofFloat(dragOffset, target).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                dragOffset = it.animatedValue as Float
                layoutPages()
            }
            doOnEnd {
                isAnimating = false
                end()
            }
            start()
        }
    }

    private fun reset() {
        dragOffset = 0f
        layoutPages()
    }

    // ---------------- OVERLAY EFFECT ----------------
    private fun layoutPages() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            // Base position relative to current index
            val baseTop = (i - currentIndex) * height
            child.layout(0, baseTop, width, baseTop + height)
        }

        // Apply overlay effect including dragOffset
        applyOverlayEffect()
    }

    private fun applyOverlayEffect() {
        val progress = min(1f, abs(dragOffset) / height)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.translationY = 0f // reset first

            when {
                i == currentIndex -> {
                    child.scaleX = 1f - 0.05f * progress
                    child.scaleY = 1f - 0.05f * progress
                    child.alpha = 1f - 0.3f * progress
                    child.translationY = dragOffset // current page moves with drag
                }

                i == currentIndex + 1 && dragOffset < 0 -> {
                    child.scaleX = 0.95f + 0.05f * progress
                    child.scaleY = 0.95f + 0.05f * progress
                    child.alpha = 0.7f + 0.3f * progress
                    child.translationY = height * 0.3f * (1 - progress) + dragOffset
                }

                i == currentIndex - 1 && dragOffset > 0 -> {
                    child.scaleX = 0.95f + 0.05f * progress
                    child.scaleY = 0.95f + 0.05f * progress
                    child.alpha = 0.7f + 0.3f * progress
                    child.translationY = -height * 0.3f * (1 - progress) + dragOffset
                }

                else -> {
                    child.scaleX = 0.95f
                    child.scaleY = 0.95f
                    child.alpha = 0.7f
                }
            }
        }
    }

    // ---------------- API ----------------
    interface OnSwipeListener {
        fun onSwipeUp(newPosition: Int) {}
        fun onSwipeDown(newPosition: Int) {}
    }

    fun setOnSwipeListener(l: OnSwipeListener) {
        listener = l
    }

    fun getCurrentPosition(): Int = currentIndex

    private fun ValueAnimator.doOnEnd(action: () -> Unit) {
        addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationEnd(animation: android.animation.Animator) = action()
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
    }
}
