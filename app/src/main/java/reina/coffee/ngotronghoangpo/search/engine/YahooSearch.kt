package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.YAHOO_SEARCH

/**
 * The Yahoo search engine.
 *
 * See http://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Yahoo%21_logo.svg/799px-Yahoo%21_logo.svg.png
 * for the icon.
 */
class YahooSearch : BaseSearchEngine(
        "file:///android_asset/yahoo.png",
        YAHOO_SEARCH,
        R.string.search_engine_yahoo
)
