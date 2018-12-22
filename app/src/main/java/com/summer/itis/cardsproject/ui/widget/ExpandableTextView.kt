
package com.summer.itis.cardsproject.ui.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseBooleanArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView

import com.summer.itis.cardsproject.R

//ТЕКСТВЬЮ ДЛЯ ОПИСАНИЯ(КОТОРОГО МОЖЕТ БЫТЬ МНОГО). МОЖЕТ СКРЫВАТЬ И ОТКРЫВАТЬ БОЛЬШЕ ИНФЫ.
class ExpandableTextView : LinearLayout, View.OnClickListener {

    protected var mTv: TextView? = null

    protected lateinit var mButton: TextView // Button to expand/collapse

    private var mRelayout: Boolean = false

    private var mCollapsed = true // Show short version as default.

    private var mCollapsedHeight: Int = 0

    private var mTextHeightWithMaxLines: Int = 0

    private var mMaxCollapsedLines: Int = 0

    private var mMarginBetweenTxtAndBottom: Int = 0

    private var mExpandText: String? = null

    private var mCollapseText: String? = null

    private var mAnimationDuration: Int = 0

    private var mAnimAlphaStart: Float = 0.toFloat()

    private var mAnimating: Boolean = false

    /* Listener for callback */
    private var mListener: OnExpandStateChangeListener? = null

    /* For saving collapsed status when used in ListView */
    private var mCollapsedStatus: SparseBooleanArray? = null
    private var mPosition: Int = 0

    var text: CharSequence?
        get() = if (mTv == null) {
            ""
        } else mTv!!.text
        set(text) {
            mRelayout = true
            mTv!!.text = text
            visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
        }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    override fun setOrientation(orientation: Int) {
        if (LinearLayout.HORIZONTAL == orientation) {
            throw IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.")
        }
        super.setOrientation(orientation)
    }

    override fun onClick(view: View) {
        if (mButton.visibility != View.VISIBLE) {
            return
        }

        mCollapsed = !mCollapsed
        mButton.text = if (mCollapsed) mExpandText else mCollapseText

        if (mCollapsedStatus != null) {
            mCollapsedStatus!!.put(mPosition, mCollapsed)
        }

        // mark that the animation is in progress
        mAnimating = true

        val animation: Animation
        if (mCollapsed) {
            animation = ExpandCollapseAnimation(this, height, mCollapsedHeight)
        } else {
            animation = ExpandCollapseAnimation(this, height, height + mTextHeightWithMaxLines - mTv!!.height)
        }

        animation.setFillAfter(true)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                applyAlphaAnimation(mTv, mAnimAlphaStart)
            }

            override fun onAnimationEnd(animation: Animation) {
                // clear animation here to avoid repeated applyTransformation() calls
                clearAnimation()
                // clear the animation flag
                mAnimating = false

                // notify the listener
                if (mListener != null) {
                    mListener!!.onExpandStateChanged(mTv, !mCollapsed)
                }

                if (mCollapsed) {
                    mTv!!.maxLines = mMaxCollapsedLines
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        clearAnimation()
        startAnimation(animation)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // while an animation is in progress, intercept all the touch events to children to
        // prevent extra clicks during the animation
        return mAnimating
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        findViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // If no change, measure and return
        if (!mRelayout || visibility == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        mRelayout = false

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mButton.visibility = View.GONE
        mTv!!.maxLines = Integer.MAX_VALUE

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // If the text fits in collapsed mode, we are done.
        if (mTv!!.lineCount <= mMaxCollapsedLines) {
            return
        }

        // Saves the text height w/ max lines
        mTextHeightWithMaxLines = getRealTextViewHeight(mTv!!)

        // Doesn't fit in collapsed mode. Collapse text view as needed. Show
        // button.
        if (mCollapsed) {
            mTv!!.maxLines = mMaxCollapsedLines
        }
        mButton.visibility = View.VISIBLE

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTv!!.post { mMarginBetweenTxtAndBottom = height - mTv!!.height }
            // Saves the collapsed height of this ViewGroup
            mCollapsedHeight = measuredHeight
        }
    }

    fun setOnExpandStateChangeListener(listener: OnExpandStateChangeListener?) {
        mListener = listener
    }

    fun setText(text: CharSequence?, collapsedStatus: SparseBooleanArray, position: Int) {
        mCollapsedStatus = collapsedStatus
        mPosition = position
        val isCollapsed = collapsedStatus.get(position, true)
        clearAnimation()
        mCollapsed = isCollapsed
        mButton.text = if (mCollapsed) mExpandText else mCollapseText
        this@ExpandableTextView.text = text
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        requestLayout()
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_maxCollapsedLines, MAX_COLLAPSED_LINES)
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_animDuration, DEFAULT_ANIM_DURATION)
        mAnimAlphaStart = typedArray.getFloat(R.styleable.ExpandableTextView_animAlphaStart, DEFAULT_ANIM_ALPHA_START)
        mExpandText = typedArray.getString(R.styleable.ExpandableTextView_expandText)
        mCollapseText = typedArray.getString(R.styleable.ExpandableTextView_collapseText)

        if (mExpandText == null) {
            mExpandText = EXPAND_BUTTON_TEXT
        }
        if (mCollapseText == null) {
            mCollapseText = COLLAPS_BUTTON_TEXT
        }

        typedArray.recycle()

        // enforces vertical orientation
        orientation = LinearLayout.VERTICAL

        // default visibility is gone
        visibility = View.GONE
    }

    private fun findViews() {
        mTv = findViewById<View>(R.id.expandable_text) as TextView

        mButton = findViewById<View>(R.id.expand_collapse) as TextView
        mButton.text = if (mCollapsed) mExpandText else mCollapseText
        mButton.setOnClickListener(this)
    }

    internal inner class ExpandCollapseAnimation(private val mTargetView: View, private val mStartHeight: Int, private val mEndHeight: Int) : Animation() {

        init {
            duration = mAnimationDuration.toLong()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val newHeight = ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight).toInt()
            mTv!!.maxHeight = newHeight - mMarginBetweenTxtAndBottom

            if (java.lang.Float.compare(mAnimAlphaStart, 1.0f) != 0) {
                applyAlphaAnimation(mTv, mAnimAlphaStart + interpolatedTime * (1.0f - mAnimAlphaStart))
            }

            mTargetView.layoutParams.height = newHeight
            mTargetView.requestLayout()
        }

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    interface OnExpandStateChangeListener {

        fun onExpandStateChanged(textView: TextView?, isExpanded: Boolean)
    }

    companion object {

        private val TAG = ExpandableTextView::class.java.simpleName

        /* The default number of lines */
        private val MAX_COLLAPSED_LINES = 8

        /* The default animation duration */
        private val DEFAULT_ANIM_DURATION = 300

        /* The default alpha value when the animation starts */
        private val DEFAULT_ANIM_ALPHA_START = 0.7f

        /* The default text of collapse button */
        private val COLLAPS_BUTTON_TEXT = "show less"

        /* The default text of expand button  */
        private val EXPAND_BUTTON_TEXT = "show more"

        private val isPostHoneycomb: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private fun applyAlphaAnimation(view: View?, alpha: Float) {
            if (isPostHoneycomb) {
                view!!.alpha = alpha
            } else {
                val alphaAnimation = AlphaAnimation(alpha, alpha)
                // make it instant
                alphaAnimation.duration = 0
                alphaAnimation.fillAfter = true
                view!!.startAnimation(alphaAnimation)
            }
        }

        private fun getRealTextViewHeight(textView: TextView): Int {
            val textHeight = textView.layout.getLineTop(textView.lineCount)
            val padding = textView.compoundPaddingTop + textView.compoundPaddingBottom
            return textHeight + padding
        }
    }
}