package showcaseview.naci.showcaseviewdemo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import naci.showcaseview.RippleBackground;
import naci.showcaseview.ShowcaseView;
import naci.showcaseview.listener.IShowcaseListener;

public class MainActivity extends AppCompatActivity {

    public ShowcaseView.Builder showcaseViewBuilder;
    public ShowcaseView showcaseView;

    private FloatingActionButton fab;
    private TextView textView;
    private ImageView imageView;
    private Button button;
    private RippleBackground fabHighlighter, tvHighlighter, btnHighlighter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseTextView();
            }
        });

        imageView = (ImageView) findViewById(R.id.imgBtn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseImage();
            }
        });

        button = (Button) findViewById(R.id.magic_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseButton();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcaseFab();
            }
        });

        fabHighlighter = (RippleBackground) findViewById(R.id.fab_highlighter);
        tvHighlighter = (RippleBackground) findViewById(R.id.tv_highlighter);
        btnHighlighter = (RippleBackground) findViewById(R.id.btn_highlighter);

        showcaseViewBuilder = new ShowcaseView.Builder(this);

        showcaseFab();

    }

    private void showcaseFab() {
        if (!fabHighlighter.isRippleAnimationRunning()) {
            fabHighlighter.startRippleAnimation();
        } else {
            fabHighlighter.stopRippleAnimation();

            showcaseViewBuilder.setTargetView(fab)
                    .setBackgroundOverlayColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccentOverlay))
                    .setRingColor(0xcc8e8e8e)
                    .setShowCircles(false)
                    .setShowcaseShape(ShowcaseView.SHAPE_CIRCLE)
                    .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()))
                    .addCustomView(R.layout.fab_description_view, Gravity.LEFT, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -228, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics()), 0);

            showcaseView = showcaseViewBuilder.show();

            showcaseView.setClickListenerOnView(R.id.btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showcaseView.hide();
                }
            });
        }
    }

    private void showcaseTextView() {
        showcaseViewBuilder.setTargetView(textView)
                .setBackgroundOverlayColor(0xcc000000)
                .setRingColor(0xccb9e797)
                .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()))
                .setShowcaseListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(@NotNull ShowcaseView showcaseView) {
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));
                    }

                    @Override
                    public void onShowcaseDismissed(@NotNull ShowcaseView showcaseView) {
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
                    }

                    @Override
                    public void onShowcaseSkipped(@NotNull ShowcaseView showcaseView) {
                        textView.setText("You skipped tutorial");
                    }
                })
                .addCustomView(R.layout.layout_showcase_body, Gravity.CENTER);

        showcaseView = showcaseViewBuilder.show();

        showcaseView.setClickListenerOnView(R.id.image_showcase_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.showcaseSkipped();
                showcaseView.hide();
            }
        });

        showcaseView.setClickListenerOnView(R.id.material_button_showcase, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.hide();
            }
        });
    }

    private void showcaseButton() {
        showcaseViewBuilder.setTargetView(button)
                .setBackgroundOverlayColor(0xcc000000)
                .setRingColor(0xcc8e8e8e)
                .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
                .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()))
                .setShowcaseMargin(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()))
                .addCustomView(R.layout.button_description_view_bottom, Gravity.BOTTOM, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()), 0)
                .addCustomView(R.layout.skip_layout)
                .addCustomView(R.layout.button_description_view_top, Gravity.TOP, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -30, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()), 0);

        showcaseView = showcaseViewBuilder.show();

        showcaseView.setClickListenerOnView(R.id.skip_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.hide();
            }
        });
    }

    private void showcaseImage() {
        View view = LayoutInflater.from(this).inflate(R.layout.image_description_view, null, false);
        view.findViewById(R.id.tv_test).startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_down));
        showcaseViewBuilder.setTargetView(imageView)
                .setBackgroundOverlayColor(0xee4d4d4d)
                .setRingColor(0xcc8e8e8e)
                .setDelay(1000)
                .setDistanceBetweenShowcaseCircles(0)
                .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()))
                .addCustomView(view, Gravity.BOTTOM)
                .setHideOnTouchOutside(true);

        showcaseViewBuilder.show();
    }
}
