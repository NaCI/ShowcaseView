package naci.showcaseview.listener

import naci.showcaseview.ShowcaseViewBuilder

interface IDetachedListener {
    fun onShowcaseDismissed(showcaseView: ShowcaseViewBuilder)
    fun onShowcaseSkipped(showcaseView: ShowcaseViewBuilder)
}