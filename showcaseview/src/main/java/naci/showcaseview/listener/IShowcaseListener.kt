package naci.showcaseview.listener

import naci.showcaseview.ShowcaseViewBuilder

interface IShowcaseListener {
    fun onShowcaseDisplayed(showcaseView: ShowcaseViewBuilder)
    fun onShowcaseDismissed(showcaseView: ShowcaseViewBuilder)
    fun onShowcaseSkipped(showcaseView: ShowcaseViewBuilder)
}