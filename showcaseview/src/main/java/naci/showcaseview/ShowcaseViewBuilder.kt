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
import java.util.*

class ShowcaseViewBuilder : View, View.OnTouchListener {

    companion object {

        private const val DEFAULT_FADE_DURATION: Long = 700

        private const val TAG = "SHOWCASE_VIEW"

        const val SHAPE_CIRCLE = 0
        const val SHAPE_SKEW = 1

        @JvmStatic
        fun init(activity: Activity): ShowcaseViewBuilder {
            val showcaseViewBuilder =
                ShowcaseViewBuilder(
                    activity
                )
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
    private var mOuterCircleMargin = 48
    private var mInnerCircleMargin = 24

    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mRadius = 0f

    private var mShape = SHAPE_CIRCLE

    private var tempCanvas: Canvas? = null

    private var backgroundPaint: Paint? = null
    private var transparentPaint: Paint? = null
    private var circleOverlayPaint: Paint? = null
    private var ringPaint: Paint? = null

    private var mTargetViewGlobalRect: Rect? = null

    private var mHideOnTouchOutside: Boolean = false

    private constructor(context: Context) : super(context)

    private constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setTargetView(view: View?): ShowcaseViewBuilder {
        mTargetView = view
        return this
    }

    fun setBackgroundOverlayColor(color: Int): ShowcaseViewBuilder {
        backgroundOverlayColor = color
        return this
    }

    fun setRingColor(color: Int): ShowcaseViewBuilder {
        ringColor = color
        return this
    }

    fun setRingWidth(ringWidth: Float): ShowcaseViewBuilder {
        mRingWidth = ringWidth
        return this
    }

    fun setShowcaseShape(shape: Int): ShowcaseViewBuilder {
        mShape = shape
        return this
    }

    fun setHideOnTouchOutside(value: Boolean): ShowcaseViewBuilder {
        mHideOnTouchOutside = value
        return this
    }

    fun setShowcaseMargin(showcaseMargin: Float): ShowcaseViewBuilder {
        mShowcaseMargin = showcaseMargin
        return this
    }

    @JvmOverloads
    fun addCustomView(
        view: View,
        gravity: Int = Gravity.NO_GRAVITY,
        leftMargin: Float = 0f,
        topMargin: Float = 0f,
        rightMargin: Float = 0f,
        bottomMargin: Float = 0f
    ): ShowcaseViewBuilder {
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

    @JvmOverloads
    fun addCustomView(
        @LayoutRes layoutId: Int,
        gravity: Int = Gravity.NO_GRAVITY,
        leftMargin: Float = 0f,
        topMargin: Float = 0f,
        rightMargin: Float = 0f,
        bottomMargin: Float = 0f
    ): ShowcaseViewBuilder {
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

    fun hide() {
        mCustomView.clear()
        mCustomViewGravity.clear()
        mCustomViewLeftMargins.clear()
        mCustomViewRightMargins.clear()
        mCustomViewTopMargins.clear()
        mCustomViewBottomMargins.clear()
        idsClickListenerMap.clear()
        idsRectMap.clear()
        mHideOnTouchOutside = false

        mHandler?.post {
            AnimationFactory.animateFadeOut(
                this@ShowcaseViewBuilder,
                DEFAULT_FADE_DURATION
            ) {
                visibility = GONE
                (mActivity!!.window.decorView as ViewGroup).removeView(this)
            }
        }
    }

    private fun addShowcaseView() {
        mHandler?.post {
            (mActivity!!.window.decorView as ViewGroup).addView(this)
            AnimationFactory.animateFadeIn(this@ShowcaseViewBuilder, DEFAULT_FADE_DURATION) {
                visibility = VISIBLE
            }
        }
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
            tempCanvas!!.drawCircle(
                mCenterX,
                mCenterY,
                (mRadius * 1.2).toInt() + mOuterCircleMargin + (mRingWidth * 2),
                ringPaint!!
            )
            tempCanvas!!.drawCircle(
                mCenterX,
                mCenterY,
                (mRadius * 1.2).toFloat() + mOuterCircleMargin + mRingWidth,
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
}