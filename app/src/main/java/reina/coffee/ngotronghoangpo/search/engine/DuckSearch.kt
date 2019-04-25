package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.DUCK_SEARCH

/**
 * The DuckDuckGo search engine.
 *
 * See https://duckduckgo.com/assets/logo_homepage.normal.v101.png for the icon.
 */
class DuckSearch : BaseSearchEngine(
        "file:///android_asset/duckduckgo.png",
        DUCK_SEARCH,
        R.string.search_engine_duckduckgo
)
