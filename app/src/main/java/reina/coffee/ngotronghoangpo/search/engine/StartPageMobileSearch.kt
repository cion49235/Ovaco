package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.STARTPAGE_MOBILE_SEARCH

/**
 * The StartPage mobile search engine.
 */
class StartPageMobileSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        STARTPAGE_MOBILE_SEARCH,
        R.string.search_engine_startpage_mobile
)
