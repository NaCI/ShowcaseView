package naci.showcaseview

import android.app.Activity
import naci.showcaseview.listener.IDetachedListener
import java.util.*

class ShowcaseSequence(private val mActivity: Activity, val sequenceID: String? = null) :
    IDetachedListener {

    private val mShowcaseQueue: Queue<ShowcaseView>

    init {
        mShowcaseQueue = LinkedList<ShowcaseView>()
    }

    fun addSequenceItem(showcaseView: ShowcaseView): ShowcaseSequence {
        mShowcaseQueue.add(showcaseView)
        return this
    }

    fun start() {
        //TODO: single use eklenmeli

        if (mShowcaseQueue.isNotEmpty()) {
            showNextItem()
        }
    }

    private fun showNextItem() {
        if (mShowcaseQueue.isNotEmpty() && !mActivity.isFinishing) {
            val sequenceItem = mShowcaseQueue.remove()
            sequenceItem.setDetachedListener(this)
            sequenceItem.show()
        } else {
            //TODO: single use eklenmeli
        }

        //TODO : onItemShownListener ShowcaseViewBuilder içine taşınmalı
    }

    override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
        showcaseView.removeShowcaseListener()
        showNextItem()
        // TODO : Shared Prefs ilerletilmeli
    }

    override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
        showcaseView.removeShowcaseListener()
        // TODO : Shared Prefs ilerletilmeli
    }

}