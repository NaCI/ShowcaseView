package naci.showcaseview

import android.app.Activity
import naci.showcaseview.listener.IDetachedListener
import java.util.*

/**
 * Use to make showcases as sequence
 *
 * @param mActivity Activity to be bound
 * @param sequenceID If u want your sequel to show only once give it a sequenceID,
 * otherwise leave it to null
 */
class ShowcaseSequence(private val mActivity: Activity, private val sequenceID: String? = null) :
    IDetachedListener {

    private val mShowcaseQueue: Queue<ShowcaseView>
    private var mPrefsManager: PrefsManager? = null

    init {
        mShowcaseQueue = LinkedList<ShowcaseView>()
        if (!sequenceID.isNullOrEmpty()) {
            mPrefsManager = PrefsManager(mActivity, sequenceID)
        }
    }

    private fun isShowOnce(): Boolean {
        return mPrefsManager != null
    }

    fun addSequenceItem(showcaseView: ShowcaseView): ShowcaseSequence {
        mShowcaseQueue.add(showcaseView)
        return this
    }

    fun start() {
        if (isShowOnce() && mPrefsManager!!.isDisplayed()) {
            return
        }

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
            mPrefsManager?.setDisplayed()
        }
    }

    override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
        showcaseView.removeShowcaseListener()
        showNextItem()
    }

    override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
        showcaseView.removeShowcaseListener()
        mPrefsManager?.setDisplayed()
    }

}