package showcaseview.naci.showcaseviewdemo

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import naci.showcaseview.RippleBackground
import naci.showcaseview.ShowcaseSequence
import naci.showcaseview.ShowcaseView
import naci.showcaseview.listener.IShowcaseListener

class MainActivityKotlin : AppCompatActivity() {

    var showcaseView1: ShowcaseView? = null
    var showcaseView2: ShowcaseView? = null
    var showcaseView3: ShowcaseView? = null
    var showcaseView4: ShowcaseView? = null

    private lateinit var fab: FloatingActionButton
    private lateinit var textView: TextView
    private lateinit var textSequence: TextView
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var fabHighlighter: RippleBackground
    private lateinit var tvHighlighter: RippleBackground
    private lateinit var btnHighlighter: RippleBackground

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar =
            findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        textView = findViewById(R.id.textView)
        textView.setOnClickListener { showcaseTextView() }
        textSequence = findViewById(R.id.textSequence)
        textSequence.setOnClickListener { showcaseSequence() }
        imageView = findViewById(R.id.imgBtn)
        imageView.setOnClickListener { showcaseImage() }
        button = findViewById(R.id.magic_btn)
        button.setOnClickListener { showcaseButton() }
        fab = findViewById(R.id.fab)
        fab.setOnClickListener { showcaseFab() }
        fabHighlighter = findViewById(R.id.fab_highlighter)
        tvHighlighter = findViewById(R.id.tv_highlighter)
        btnHighlighter = findViewById(R.id.btn_highlighter)
        showcaseFab()
        startupSequence()
    }

    private fun prepareShowcaseViews() {
        prepareFirstShowcase()
        prepareSecondShowcase()
        prepareThirdShowcase()
        prepareFourthShowcase()
    }

    private fun prepareFirstShowcase() {
        showcaseView1 = ShowcaseView.Builder(this)
            .setTargetView(fab)
            .setBackgroundOverlayColor(
                ContextCompat.getColor(
                    baseContext,
                    R.color.colorAccentOverlay
                )
            )
            .setRingColor(-0x33717172)
            .setShowCircles(false)
            .setShowcaseShape(ShowcaseView.SHAPE_CIRCLE)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                )
            )
            .addCustomView(
                R.layout.fab_description_view,
                Gravity.LEFT,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -228f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -80f,
                    resources.displayMetrics
                ),
                0f
            )
            .build()

        showcaseView1!!.setClickListenerOnView(
            R.id.btn,
            View.OnClickListener { showcaseView1!!.hide() })
    }

    private fun prepareSecondShowcase() {
        showcaseView2 = ShowcaseView.Builder(this)
            .setTargetView(textView)
            .setBackgroundOverlayColor(
                ContextCompat.getColor(
                    applicationContext!!,
                    R.color.overlay_color_alternate
                )
            )
            .setRingColor(ContextCompat.getColor(applicationContext!!, R.color.ring_color))
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    3f,
                    resources.displayMetrics
                )
            )
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            android.R.color.holo_red_light
                        )
                    )
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            android.R.color.black
                        )
                    )
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    textView.text = "You skipped tutorial"
                }
            })
            .addCustomView(R.layout.layout_showcase_body, Gravity.CENTER)
            .build()

        showcaseView2!!.setClickListenerOnView(
            R.id.image_showcase_close,
            View.OnClickListener {
                showcaseView2!!.showcaseSkipped()
                showcaseView2!!.hide()
            })
        showcaseView2!!.setClickListenerOnView(
            R.id.material_button_showcase,
            View.OnClickListener { showcaseView2!!.hide() })
    }

    private fun prepareThirdShowcase() {
        showcaseView3 = ShowcaseView.Builder(this)
            .setTargetView(button)
            .setBackgroundOverlayColor(-0x34000000)
            .setRingColor(-0x33717172)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    6f,
                    resources.displayMetrics
                )
            )
            .setShowcaseMargin(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4f,
                    resources.displayMetrics
                )
            )
            .addCustomView(
                R.layout.button_description_view_bottom,
                Gravity.BOTTOM,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    30f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                0f
            )
            .addCustomView(R.layout.skip_layout)
            .addCustomView(
                R.layout.button_description_view_top,
                Gravity.TOP,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -30f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                0f
            )
            .build()

        showcaseView3!!.setClickListenerOnView(
            R.id.skip_btn,
            View.OnClickListener { showcaseView3!!.hide() })
    }

    private fun prepareFourthShowcase() {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.image_description_view, null, false)
        view.findViewById<View>(R.id.tv_test).startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.scale_down
            )
        )
        showcaseView4 = ShowcaseView.Builder(this)
            .setTargetView(imageView)
            .setBackgroundOverlayColor(-0x11b2b2b3)
            .setRingColor(-0x33717172)
            .setDelay(1000)
            .setDistanceBetweenShowcaseCircles(0)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                    resources.displayMetrics
                )
            )
            .addCustomView(view, Gravity.BOTTOM)
            .setHideOnTouchOutside(true)
            .build()
    }

    private fun showcaseFab() {
        if (!fabHighlighter.isRippleAnimationRunning) {
            fabHighlighter.startRippleAnimation()
        } else {
            fabHighlighter.stopRippleAnimation()
            prepareFirstShowcase()
            showcaseView1?.show()
        }
    }

    private fun showcaseTextView() {
        prepareSecondShowcase()
        showcaseView2!!.show()
    }

    private fun showcaseButton() {
        prepareThirdShowcase()
        showcaseView3!!.show()
    }

    private fun showcaseImage() {
        prepareFourthShowcase()
        showcaseView4!!.show()
    }

    private fun showcaseSequence() {
        prepareShowcaseViews()
        val sequence = ShowcaseSequence(this)
        sequence.addSequenceItem(showcaseView1!!)
        sequence.addSequenceItem(showcaseView2!!)
        sequence.addSequenceItem(showcaseView3!!)
        sequence.addSequenceItem(showcaseView4!!)
        sequence.start()
    }

    private fun startupSequence() {
        prepareShowcaseViews()
        val sequence = ShowcaseSequence(this, "FirstSequence")
        sequence.addSequenceItem(showcaseView4!!)
        sequence.addSequenceItem(showcaseView2!!)
        sequence.addSequenceItem(showcaseView1!!)
        sequence.addSequenceItem(showcaseView3!!)
        sequence.start()
    }
}