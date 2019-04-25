package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.STARTPAGE_SEARCH

/**
 * The StartPage search engine.
 */
class StartPageSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        STARTPAGE_SEARCH,
        R.string.search_engine_startpage
)
