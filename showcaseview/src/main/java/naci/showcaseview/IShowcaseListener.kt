package naci.showcaseview

interface IShowcaseListener {
    fun onShowcaseDisplayed(showcaseView: ShowcaseViewBuilder)
    fun onShowcaseDismissed(showcaseView: ShowcaseViewBuilder)
    fun onShowcaseSkipped(showcaseView: ShowcaseViewBuilder)
}