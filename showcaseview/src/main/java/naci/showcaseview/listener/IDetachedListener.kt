package naci.showcaseview.listener

import naci.showcaseview.ShowcaseView

interface IDetachedListener {
    fun onShowcaseDismissed(showcaseView: ShowcaseView)
    fun onShowcaseSkipped(showcaseView: ShowcaseView)
}