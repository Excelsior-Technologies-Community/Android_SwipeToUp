package com.ext.android_swipe_to_up

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
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
    private var startY = 0f
    private var isAnimating = false

    private var stackOffset = 90f
    private var minScale = 0.96f
    private var minAlpha = 0.85f
    private var swipeThresholdRatio = 0.22f
    private var animationDuration = 480L

    private var listener: OnSwipeListener? = null

    init {
        clipChildren = false
        clipToPadding = false

        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.SwipeToUpView)
            stackOffset = ta.getDimension(R.styleable.SwipeToUpView_stackOffset, stackOffset)
            minScale = ta.getFloat(R.styleable.SwipeToUpView_minScale, minScale)
            minAlpha = ta.getFloat(R.styleable.SwipeToUpView_minAlpha, minAlpha)
            swipeThresholdRatio = ta.getFloat(
                R.styleable.SwipeToUpView_autoSwipeThreshold,
                swipeThresholdRatio
            )
            animationDuration = ta.getInt(
                R.styleable.SwipeToUpView_animationDuration,
                animationDuration.toInt()
            ).toLong()
            ta.recycle()
        }
    }

    override fun onMeasure(wSpec: Int, hSpec: Int) {
        val w = MeasureSpec.getSize(wSpec)
        val h = MeasureSpec.getSize(hSpec)
        setMeasuredDimension(w, h)
        measureChildren(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).layout(0, 0, width, height)
        }
        applyStackEffect()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isAnimating || childCount < 2) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dy = event.y - startY
                startY = event.y
                dragOffset += dy
                dragOffset = dragOffset.coerceIn(-height.toFloat(), height.toFloat())
                applyStackEffect()
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                releaseSwipe()
                return true
            }
        }
        return false
    }

    private fun releaseSwipe() {
        val threshold = height * swipeThresholdRatio
        when {
            dragOffset < -threshold && currentIndex < childCount - 1 -> swipeUp()
            dragOffset > threshold && currentIndex > 0 -> swipeDown()
            else -> reset()
        }
    }

    private fun swipeUp() {
        animateTo(-height.toFloat()) {
            currentIndex++
            dragOffset = 0f
            applyStackEffect()
            listener?.onSwipeUp(currentIndex)
        }
    }

    private fun swipeDown() {
        animateTo(height.toFloat()) {
            currentIndex--
            dragOffset = 0f
            applyStackEffect()
            listener?.onSwipeDown(currentIndex)
        }
    }

    private fun reset() {
        animateTo(0f) {
            dragOffset = 0f
            applyStackEffect()
        }
    }

    private fun animateTo(target: Float, end: () -> Unit) {
        isAnimating = true
        ValueAnimator.ofFloat(dragOffset, target).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator(1.8f)
            addUpdateListener {
                dragOffset = it.animatedValue as Float
                applyStackEffect()
            }
            doOnEnd {
                isAnimating = false
                end()
            }
            start()
        }
    }
    private fun applyStackEffect() {
        val progressUp = if (dragOffset < 0) min(1f, abs(dragOffset) / height) else 0f
        val progressDown = if (dragOffset > 0) min(1f, dragOffset / height) else 0f

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val position = i - currentIndex

            when (position) {

                // ---------------- CURRENT PAGE ----------------
                0 -> {
                    child.visibility = View.VISIBLE

                    if (dragOffset < 0) {
                        // Swipe down → up: top page moving out
                        child.translationY = dragOffset
                        child.scaleX = 1f
                        child.scaleY = 1f
                        child.alpha = 1f

                        // ✅ REMOVE overlay from the top page
                        child.foreground = null

                    } else if (dragOffset > 0) {
                        // Swipe up → down: top page
                        child.translationY = 0f
                        child.scaleX = 1f - (progressDown * 0.05f)
                        child.scaleY = 1f - (progressDown * 0.05f)
                        child.alpha = 1f - (progressDown * 0.6f)

                        // Overlay while swipe up → down (same as before)
                        if (progressDown < 0.6f) {
                            val overlayAlpha = ((0.6f - progressDown) / 0.6f * 200).toInt()
                            child.foreground = android.graphics.drawable.ColorDrawable(
                                android.graphics.Color.argb(overlayAlpha, 0, 0, 0)
                            )
                        } else {
                            child.foreground = null
                        }
                    } else {
                        child.foreground = null
                    }

                    child.elevation = if (dragOffset > 0) 5f else 10f
                }

// ---------------- NEXT PAGE BELOW ----------------
                1 -> {
                    child.visibility = View.VISIBLE
                    child.pivotX = child.width / 2f
                    child.pivotY = 0f

                    if (dragOffset < 0) {
                        // Swipe down → up: page revealed underneath
                        val scale = 0.75f + 0.25f * progressUp
                        val alpha = 0.5f + 0.5f * progressUp
                        val translateY = stackOffset * (1 - progressUp)

                        child.scaleX = scale
                        child.scaleY = scale
                        child.alpha = alpha
                        child.translationY = translateY
                        child.elevation = 6f

                        // ✅ Overlay only on the page that is coming in
                        if (progressUp < 0.6f) {
                            val overlayAlpha = ((0.6f - progressUp) / 0.6f * 200).toInt()
                            child.foreground = android.graphics.drawable.ColorDrawable(
                                android.graphics.Color.argb(overlayAlpha, 0, 0, 0)
                            )
                        } else {
                            child.foreground = null
                        }

                    } else if (dragOffset > 0) {
                        // Swipe up → down: page revealed underneath
                        child.scaleX = minScale
                        child.scaleY = minScale
                        child.translationY = stackOffset
                        child.elevation = 0f
                        child.alpha = 0f  // hide text completely

                        // ✅ Overlay same as before
                        if (progressDown < 0.6f) {
                            val overlayAlpha = ((0.6f - progressDown) / 0.6f * 200).toInt()
                            child.foreground = android.graphics.drawable.ColorDrawable(
                                android.graphics.Color.argb(overlayAlpha, 0, 0, 0)
                            )
                        } else {
                            child.foreground = null
                        }
                    } else {
                        child.scaleX = minScale
                        child.scaleY = minScale
                        child.alpha = minAlpha
                        child.translationY = stackOffset
                        child.elevation = 6f
                        child.foreground = null
                    }
                }


                // ---------------- PREVIOUS PAGE ABOVE ----------------
                -1 -> {
                    child.visibility = View.VISIBLE
                    child.translationY = -height + dragOffset.coerceAtLeast(0f)
                    child.scaleX = 1f
                    child.scaleY = 1f
                    child.alpha = 1f
                    child.elevation = 15f
                    child.foreground = null
                }

                // ---------------- OTHER PAGES ----------------
                else -> child.visibility = View.GONE
            }
        }
    }




    interface OnSwipeListener {
        fun onSwipeUp(newIndex: Int) {}
        fun onSwipeDown(newIndex: Int) {}
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



