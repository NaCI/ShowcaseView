package naci.showcaseview.animation;

public interface AnimationListener {

    /**
     * We need to make MaterialIntroView visible
     * before fade in animation starts
     */
    interface OnAnimationStartListener {
        void onAnimationStart();
    }

    /**
     * We need to make MaterialIntroView invisible
     * after fade out animation ends.
     */
    interface OnAnimationEndListener {
        void onAnimationEnd();
    }

}
