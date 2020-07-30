package naci.showcaseview

import android.app.Activity
import naci.showcaseview.listener.IDetachedListener
import java.util.*

class ShowcaseSequence(private val mActivity: Activity, val sequenceID: String? = null) :
    IDetachedListener {

    private val mShowcaseQueue: Queue<ShowcaseViewBuilder>

    init {
        mShowcaseQueue = LinkedList<ShowcaseViewBuilder>()
    }

    fun addSequenceItem(showcaseView: ShowcaseViewBuilder): ShowcaseSequence {
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

    override fun onShowcaseDismissed(showcaseView: ShowcaseViewBuilder) {
        showcaseView.removeShowcaseListener()
        showNextItem()
        // TODO : Shared Prefs ilerletilmeli
    }

    override fun onShowcaseSkipped(showcaseView: ShowcaseViewBuilder) {
        showcaseView.removeShowcaseListener()
        // TODO : Shared Prefs ilerletilmeli
    }

}