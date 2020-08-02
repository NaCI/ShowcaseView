package naci.showcaseview.listener

import naci.showcaseview.ShowcaseView

interface IShowcaseListener {
    fun onShowcaseDisplayed(showcaseView: ShowcaseView)
    fun onShowcaseDismissed(showcaseView: ShowcaseView)
    fun onShowcaseSkipped(showcaseView: ShowcaseView)
}