package naci.showcaseview

import android.app.Activity
import java.util.*

class ShowcaseSequence(val mActivity: Activity, val sequenceID: String? = null) {

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
            sequenceItem.show()
        } else {
            //TODO: single use eklenmeli
        }

        //TODO : onItemShownListener ShowcaseViewBuilder içine taşınmalı
    }

}