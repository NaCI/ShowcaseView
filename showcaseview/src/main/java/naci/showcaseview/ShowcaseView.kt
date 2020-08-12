package naci.showcaseview

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import naci.showcaseview.animation.AnimationFactory
import naci.showcaseview.listener.IDetachedListener
import naci.showcaseview.listener.IShowcaseListener
import java.util.*


class ShowcaseView : View, View.OnTouchListener {

    companion object {
        private const val TAG = "SHOWCASE_VIEW"

        const val SHAPE_CIRCLE = 0
        const val SHAPE_SKEW = 1

        private const val DEFAULT_DELAY = 0L
        private const val DEFAULT_FADE_DURATION: Long = 700
        private const val DEFAULT_DISTANCE_BETWEEN_CIRCLES = 48

        @JvmStatic
        fun init(activity: Activity): ShowcaseView {
            val showcaseViewBuilder = ShowcaseView(activity)
            showcaseViewBuilder.mActivity = activity
            showcaseViewBuilder.isClickable = true
            showcaseViewBuilder.mHandler = Handler()
            return showcaseViewBuilder
        }
    }

    private var mActivity: Activity? = null

    private var mTargetView: View? = null

    private var mHandler: Handler? = null

    private val idsRectMap: HashMap<Rect, Int> = HashMap()
    private val idsClickListenerMap: HashMap<Int, OnClickListener> = HashMap()
    private var showcaseListener: IShowcaseListener? = null
    private var detachedListener: IDetachedListener? = null

    private val mCustomView: MutableList<View> = ArrayList()

    private val mCustomViewGravity: MutableList<Int> = ArrayList()
    private val mCustomViewLeftMargins: MutableList<Float> = ArrayList()
    private val mCustomViewTopMargins: MutableList<Float> = ArrayList()
    private val mCustomViewRightMargins: MutableList<Float> = ArrayList()
    private val mCustomViewBottomMargins: MutableList<Float> = ArrayList()

    private var screenHeight = 0
    private var screenWidth = 0

    private var backgroundOverlayColor = 0
    private var ringColor = 0
    private var mRingWidth = 10f
    private var mShowcaseMargin = 12f
    private var mDistanceBetweenCircles = DEFAULT_DISTANCE_BETWEEN_CIRCLES
    private var mInnerCircleMargin = 24

    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mRadius = 0f

    private var mDelayInMillis = DEFAULT_DELAY

    private var mShape = SHAPE_CIRCLE

    private var tempCanvas: Canvas? = null

    private var backgroundPaint: Paint? = null
    private var transparentPaint: Paint? = null
    private var circleOverlayPaint: Paint? = null
    private var ringPaint: Paint? = null

    private var mTargetViewGlobalRect: Rect? = null

    private var mHideOnTouchOutside: Boolean = false
    private var mShowCircles: Boolean = true

    //    private var mShowcaseDismissed = false
    private var mShowcaseSkipped = false

    private constructor(context: Context) : super(context)

    private constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    // SETTERS
    fun setTargetView(view: View?) {
        mTargetView = view
    }

    fun setBackgroundOverlayColor(color: Int) {
        backgroundOverlayColor = color
    }

    fun setRingColor(color: Int) {
        ringColor = color
    }

    fun setRingWidth(ringWidth: Float) {
        mRingWidth = ringWidth
    }

    fun setShowcaseShape(shape: Int) {
        mShape = shape
    }

    fun setHideOnTouchOutside(value: Boolean) {
        mHideOnTouchOutside = value
    }

    fun setShowcaseMargin(showcaseMargin: Float) {
        mShowcaseMargin = showcaseMargin
    }

    fun setDistanceBetweenShowcaseCircles(distanceBetweenCircles: Int) {
        mDistanceBetweenCircles = distanceBetweenCircles
    }

    fun setDelay(delayInMillis: Long) {
        mDelayInMillis = delayInMillis
    }

    fun setShowCircles(isShow: Boolean) {
        mShowCircles = isShow
    }

    fun setShowcaseListener(showcaseListener: IShowcaseListener) {
        this.showcaseListener = showcaseListener
    }

    fun removeShowcaseListener() {
        showcaseListener = null
    }

    fun setDetachedListener(detachedListener: IDetachedListener) {
        this.detachedListener = detachedListener
    }

    fun removeDetachedListener() {
        detachedListener = null
    }

    fun showcaseSkipped() {
        mShowcaseSkipped = true
    }

    private fun addCustomView(
        view: View,
        gravity: Int = Gravity.NO_GRAVITY,
        leftMargin: Float = 0f,
        topMargin: Float = 0f,
        rightMargin: Float = 0f,
        bottomMargin: Float = 0f
    ): ShowcaseView {
        val metrics = DisplayMetrics()
        mActivity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val rect = Rect()
        rect[0, 0, metrics.widthPixels] = metrics.heightPixels
        val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
        view.measure(widthSpec, heightSpec)
        mCustomView.add(view)
        mCustomViewGravity.add(gravity)
        mCustomViewLeftMargins.add(leftMargin)
        mCustomViewTopMargins.add(topMargin)
        mCustomViewRightMargins.add(rightMargin)
        mCustomViewBottomMargins.add(bottomMargin)
        return this
    }

    private fun addCustomView(
        @LayoutRes layoutId: Int,
        gravity: Int = Gravity.NO_GRAVITY,
        leftMargin: Float = 0f,
        topMargin: Float = 0f,
        rightMargin: Float = 0f,
        bottomMargin: Float = 0f
    ): ShowcaseView {
        val view = LayoutInflater.from(mActivity).inflate(layoutId, null)

        val linearLayout = LinearLayout(mActivity)
        linearLayout.addView(view)
        linearLayout.gravity = Gravity.CENTER

        val metrics = DisplayMetrics()
        mActivity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val rect = Rect()
        rect[0, 0, metrics.widthPixels] = metrics.heightPixels

        val widthSpec =
            MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
        val heightSpec =
            MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
        linearLayout.measure(widthSpec, heightSpec)

        mCustomView.add(linearLayout)
        mCustomViewGravity.add(gravity)
        mCustomViewLeftMargins.add(leftMargin)
        mCustomViewTopMargins.add(topMargin)
        mCustomViewRightMargins.add(rightMargin)
        mCustomViewBottomMargins.add(bottomMargin)
        return this
    }

    private fun calculateContentAreaWidthAndHeight() {
        val displayMetrics = DisplayMetrics()
        mActivity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
    }

    fun show() {
        transparentPaint = Paint()
        ringPaint = Paint()
        backgroundPaint = Paint()
        circleOverlayPaint = Paint()
        if (mTargetView != null) {
            calculateContentAreaWidthAndHeight()
            if (mTargetView!!.width == 0 || mTargetView!!.height == 0) {
                mTargetView!!.viewTreeObserver
                    .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            invalidate()
                            addShowcaseView()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mTargetView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            } else {
                                mTargetView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            }
                        }
                    })
            } else {
                invalidate()
                addShowcaseView()
            }
        }
        setOnTouchListener(this)
    }

    /**
     * Stop showcasing the targetView
     */
    fun hide() {
        mCustomView.clear()
        mCustomViewGravity.clear()
        mCustomViewLeftMargins.clear()
        mCustomViewRightMargins.clear()
        mCustomViewTopMargins.clear()
        mCustomViewBottomMargins.clear()
        idsClickListenerMap.clear()
        idsRectMap.clear()
        mShowCircles = true
        mShape = SHAPE_CIRCLE
        mDelayInMillis = DEFAULT_DELAY
        mDistanceBetweenCircles = DEFAULT_DISTANCE_BETWEEN_CIRCLES
        mHideOnTouchOutside = false
//        mShowcaseDismissed = false
//        mShowcaseSkipped = false
//        showcaseListener = null

        mHandler?.post {
            AnimationFactory.animateFadeOut(
                this@ShowcaseView,
                DEFAULT_FADE_DURATION
            ) {
                visibility = GONE
                (mActivity!!.window.decorView as ViewGroup).removeView(this)
            }
        }
    }

    private fun addShowcaseView() {
        mHandler?.postDelayed({
            (mActivity!!.window.decorView as ViewGroup).addView(this)
            AnimationFactory.animateFadeIn(this@ShowcaseView, DEFAULT_FADE_DURATION) {
                visibility = VISIBLE
                notifyOnDisplayed()
            }
        }, mDelayInMillis)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mTargetView != null) {
            setShowcase(canvas)
            addCustomView(canvas)
        }
    }

    private fun setShowcase(canvas: Canvas) {

        calculateRadiusAndCenter()

        val bitmap = Bitmap.createBitmap(
            if (screenWidth > 0) screenWidth else canvas.width,
            if (screenHeight > 0) screenHeight else canvas.height,
            Bitmap.Config.ARGB_8888
        )

        tempCanvas = Canvas(bitmap)

        backgroundPaint!!.color = backgroundOverlayColor
        backgroundPaint!!.isAntiAlias = true

        transparentPaint!!.color = ContextCompat.getColor(context, android.R.color.transparent)
        transparentPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        transparentPaint!!.isAntiAlias = true

        circleOverlayPaint!!.color = backgroundOverlayColor
        circleOverlayPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        circleOverlayPaint!!.isAntiAlias = true

        ringPaint!!.color = ringColor
        ringPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        ringPaint!!.isAntiAlias = true

        tempCanvas!!.drawRect(
            0f,
            0f,
            tempCanvas!!.width.toFloat(),
            tempCanvas!!.height.toFloat(),
            backgroundPaint!!
        )

        if (mShape == SHAPE_SKEW) {
            val r = Rect()
            val ring = Rect()
            mTargetView?.getGlobalVisibleRect(r)
            mTargetView?.getGlobalVisibleRect(ring);
            //Showcase rect
            r.top -= mShowcaseMargin.toInt()
            r.left -= mShowcaseMargin.toInt()
            r.right += mShowcaseMargin.toInt()
            r.bottom += mShowcaseMargin.toInt()
            //Showcase ring rect
            ring.top -= (mShowcaseMargin + mRingWidth).toInt()
            ring.left -= (mShowcaseMargin + mRingWidth).toInt()
            ring.right += (mShowcaseMargin + mRingWidth).toInt()
            ring.bottom += (mShowcaseMargin + mRingWidth).toInt()
            tempCanvas!!.drawRect(ring, ringPaint!!);
            tempCanvas!!.drawRect(r, transparentPaint!!);
        } else {
            if (mShowCircles) {
                tempCanvas!!.drawCircle(
                    mCenterX,
                    mCenterY,
                    (mRadius * 1.2).toInt() + (mInnerCircleMargin + mDistanceBetweenCircles) + (mRingWidth * 2),
                    ringPaint!!
                )
                tempCanvas!!.drawCircle(
                    mCenterX,
                    mCenterY,
                    (mRadius * 1.2).toFloat() + (mInnerCircleMargin + mDistanceBetweenCircles) + mRingWidth,
                    circleOverlayPaint!!
                )

                tempCanvas!!.drawCircle(
                    mCenterX,
                    mCenterY,
                    (mRadius * 1.2).toInt() + mInnerCircleMargin + mRingWidth,
                    ringPaint!!
                )
                tempCanvas!!.drawCircle(
                    mCenterX,
                    mCenterY,
                    (mRadius * 1.2).toFloat() + mInnerCircleMargin,
                    circleOverlayPaint!!
                )
            }

            tempCanvas!!.drawCircle(
                mCenterX,
                mCenterY,
                (mRadius * 1.1).toFloat(),
                transparentPaint!!
            )
        }
        canvas.drawBitmap(bitmap, 0f, 0f, Paint())
    }

    private fun calculateRadiusAndCenter() {
        val width = mTargetView!!.measuredWidth
        val height = mTargetView!!.measuredHeight
        val xy = intArrayOf(0, 0)
        mTargetView!!.getLocationInWindow(xy)
        mCenterX = xy[0] + (width / 2).toFloat()
        mCenterY = xy[1] + (height / 2).toFloat()
        mRadius = if (width > height) {
            8 * width / 12.toFloat()
        } else {
            8 * height / 12.toFloat()
        }
    }

    private fun addCustomView(canvas: Canvas) {
        if (mCustomView.size != 0) {
            for (i in mCustomView.indices) {
                val cy = mCustomView[i].measuredHeight / 2.toFloat()
                val cx = mCustomView[i].measuredWidth / 2.toFloat()
                var diffY: Float
                var diffX: Float
                val marginTop = mCustomViewTopMargins[i]
                val marginLeft = mCustomViewLeftMargins[i]
                val marginRight = mCustomViewRightMargins[i]
                val marginBottom = mCustomViewBottomMargins[i]
                mTargetViewGlobalRect = Rect()
                mTargetView!!.getGlobalVisibleRect(mTargetViewGlobalRect)
                val view = mCustomView[i]
                when (mCustomViewGravity[i]) {
                    Gravity.START, Gravity.LEFT -> {
                        diffY = mCenterY - cy
                        diffX = mCenterX - cx
                        if (diffX < 0) {
                            view.layout(
                                0,
                                0,
                                (mCenterX - view.measuredWidth - 2 * marginRight).toInt(),
                                (mCustomView[i]
                                    .measuredHeight + 2 * (diffY + marginTop)).toInt()
                            )
                        } else {
                            view.layout(
                                diffX.toInt(),
                                0,
                                (view.measuredWidth - diffX - 2 * marginRight).toInt(),
                                (mCustomView[i]
                                    .measuredHeight + 2 * (diffY + marginTop)).toInt()
                            )
                        }
                    }
                    Gravity.TOP -> {
                        diffY = mCenterY - cy - 2 * mTargetView!!.measuredHeight
                        view.layout(
                            (-marginLeft).toInt(), 0,
                            (view.measuredWidth + marginLeft).toInt(),
                            (mCustomView[i]
                                .measuredHeight + 2 * (diffY + marginTop)).toInt()
                        )
                    }
                    Gravity.END, Gravity.RIGHT -> {
                        diffY = mCenterY - cy
                        view.layout(
                            -2 * mTargetViewGlobalRect!!.right,
                            0,
                            (view.measuredWidth + 4 * marginLeft).toInt(),
                            (mCustomView[i]
                                .measuredHeight + 2 * (diffY + marginTop)).toInt()
                        )
                    }
                    Gravity.BOTTOM -> {
                        diffY = mCenterY - cy + 2 * mTargetView!!.measuredHeight
                        view.layout(
                            (-marginLeft).toInt(), 0,
                            (view.measuredWidth + marginLeft).toInt(),
                            (mCustomView[i]
                                .measuredHeight + 2 * (diffY + marginTop)).toInt()
                        )
                    }
                    else -> mCustomView[i].layout(
                        0,
                        0,
                        mCustomView[i].measuredWidth,
                        mCustomView[i].measuredHeight
                    )
                }
                mCustomView[i].draw(canvas)
            }
        } else {
            Log.d(TAG, "No Custom View defined")
        }
    }

    /**
     * Sets clicklistener on the components of the customView(s) added
     */
    fun setClickListenerOnView(id: Int, clickListener: OnClickListener?) {
        idsClickListenerMap[id] = clickListener!!
    }

    private fun getAbsoluteLeft(myView: View?): Int {
        if (myView == null) {
            return 0
        }
        return if (myView.parent === myView.rootView) myView.left else myView.left + getAbsoluteLeft(
            myView.parent as View?
        )
    }

    private fun getAbsoluteTop(myView: View?): Int {
        if (myView == null) {
            return 0
        }
        return if (myView.parent === myView.rootView) myView.top else myView.top + getAbsoluteTop(
            myView.parent as View?
        )
    }

    private fun getAllChildren(v: View): ArrayList<View>? {
        if (v !is ViewGroup) {
            val viewArrayList: ArrayList<View> = ArrayList()
            viewArrayList.add(v)
            return viewArrayList
        }
        val result: ArrayList<View> = ArrayList()
        for (i in 0 until v.childCount) {
            val child = v.getChildAt(i)
            val viewArrayList: ArrayList<View> = ArrayList()
            viewArrayList.add(v)
            viewArrayList.addAll(getAllChildren(child)!!)
            result.addAll(viewArrayList)
        }
        return result
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (idsRectMap.isEmpty()) {
            for (parentView in mCustomView) {
                val childrenViews: ArrayList<View>? = getAllChildren(parentView)
                for (view in childrenViews!!) {
                    val rect = Rect()
                    rect[getAbsoluteLeft(view), getAbsoluteTop(view), getAbsoluteLeft(view) + view.measuredWidth] =
                        getAbsoluteTop(view) + view.measuredHeight
                    if (view.id > 0) {
                        idsRectMap[rect] = view.id
                    }
                }
            }
        }

        if (event!!.action == MotionEvent.ACTION_UP) {
            val eventX = event.x
            val eventY = event.y
            val keys: Array<Any> = idsRectMap.keys.toTypedArray()
            for (i in 0 until idsRectMap.size) {
                val r = keys[i] as Rect
                if (r.contains(eventX.toInt(), eventY.toInt())) {
                    val id = idsRectMap[r]!!
                    if (idsClickListenerMap[id] != null) {
                        idsClickListenerMap[id]!!.onClick(v)
                        return true
                    }
                }
            }

            if (mHideOnTouchOutside) {
                hide()
                return true
            }
        }
        return false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        notifyOnDetached()
    }

    private fun notifyOnDetached() {
        showcaseListener?.let {
            if (mShowcaseSkipped) {
                it.onShowcaseSkipped(this)
            } else {
                it.onShowcaseDismissed(this)
            }
        }

        detachedListener?.let {
            if (mShowcaseSkipped) {
                it.onShowcaseSkipped(this)
            } else {
                it.onShowcaseDismissed(this)
            }
        }

        mShowcaseSkipped = false
        showcaseListener = null
    }

    private fun notifyOnDisplayed() {
        showcaseListener?.onShowcaseDisplayed(this)
    }

    class Builder(private val activity: Activity) {
        private val showcaseView: ShowcaseView = init(activity)

        /**
         * Set view to be highlighted
         *
         * Note : **This method must be set**
         */
        fun setTargetView(view: View?): Builder {
            showcaseView.setTargetView(view)
            return this
        }

        /**
         * Set background color of showcase view
         */
        fun setBackgroundOverlayColor(color: Int): Builder {
            showcaseView.setBackgroundOverlayColor(color)
            return this
        }

        /**
         * Set color of the rings which helps to highlight target view
         */
        fun setRingColor(color: Int): Builder {
            showcaseView.setRingColor(color)
            return this
        }

        /**
         * Set width of the rings which helps to highlight target view
         */
        fun setRingWidth(ringWidth: Float): Builder {
            showcaseView.setRingWidth(ringWidth)
            return this
        }

        /**
         * Set shape of showcase focus
         * @param shape SHAPE_SKEW or SHAPE_CIRCLE
         *
         * Default value is SHAPE_CIRCLE
         */
        fun setShowcaseShape(shape: Int): Builder {
            showcaseView.setShowcaseShape(shape)
            return this
        }

        /**
         * Set whether hide or not on touch showcase area
         * @param value true or false
         *
         * Default value is false
         */
        fun setHideOnTouchOutside(value: Boolean): Builder {
            showcaseView.setHideOnTouchOutside(value)
            return this
        }

        /**
         * Set the margin between target view and showcase layout
         *
         * Default value is 12f
         */
        fun setShowcaseMargin(showcaseMargin: Float): Builder {
            showcaseView.setShowcaseMargin(showcaseMargin)
            return this
        }

        /**
         * Set distance value between 2 highlight circles
         *
         * Default value is 48
         */
        fun setDistanceBetweenShowcaseCircles(distanceBetweenCircles: Int): Builder {
            showcaseView.setDistanceBetweenShowcaseCircles(distanceBetweenCircles)
            return this
        }

        /**
         * Set delay before show Showcaseview
         * @param delayInMillis Delay as milliseconds.
         *
         * Default value is 0
         */
        fun setDelay(delayInMillis: Long): Builder {
            showcaseView.setDelay(delayInMillis)
            return this
        }

        /**
         * Set whether or not show the highlight circles
         * @param isShow true or false
         *
         * Default value is true
         */
        fun setShowCircles(isShow: Boolean): Builder {
            showcaseView.setShowCircles(isShow)
            return this
        }

        /**
         * Set listener to listen showcase states
         *
         * Showcase states: onShowcaseDisplayed, onShowcaseDismissed, onShowcaseSkipped
         */
        fun setShowcaseListener(showcaseListener: IShowcaseListener): Builder {
            showcaseView.setShowcaseListener(showcaseListener)
            return this
        }

        /**
         * Add view to be shown on the screen while showcase active
         */
        @JvmOverloads
        fun addCustomView(
            view: View,
            gravity: Int = Gravity.NO_GRAVITY,
            leftMargin: Float = 0f,
            topMargin: Float = 0f,
            rightMargin: Float = 0f,
            bottomMargin: Float = 0f
        ): Builder {
            showcaseView.addCustomView(
                view,
                gravity,
                leftMargin,
                topMargin,
                rightMargin,
                bottomMargin
            )
            return this
        }

        /**
         * Add view to be shown on the screen while showcase active
         */
        @JvmOverloads
        fun addCustomView(
            @LayoutRes layoutId: Int,
            gravity: Int = Gravity.NO_GRAVITY,
            leftMargin: Float = 0f,
            topMargin: Float = 0f,
            rightMargin: Float = 0f,
            bottomMargin: Float = 0f
        ): Builder {
            showcaseView.addCustomView(
                layoutId,
                gravity,
                leftMargin,
                topMargin,
                rightMargin,
                bottomMargin
            )
            return this
        }

        fun build(): ShowcaseView {
            return showcaseView
        }

        /**
         * Start showcasing the targetView
         */
        fun show(): ShowcaseView {
            build().show()
            return showcaseView
        }
    }

}