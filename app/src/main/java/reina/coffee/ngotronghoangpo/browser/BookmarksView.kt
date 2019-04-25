package reina.coffee.ngotronghoangpo.browser

import reina.coffee.ngotronghoangpo.database.HistoryItem

interface BookmarksView {

    fun navigateBack()

    fun handleUpdatedUrl(url: String)

    fun handleBookmarkDeleted(item: HistoryItem)

}
