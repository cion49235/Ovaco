package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.DUCK_LITE_SEARCH

/**
 * The DuckDuckGo Lite search engine.
 *
 * See https://duckduckgo.com/assets/logo_homepage.normal.v101.png for the icon.
 */
class DuckLiteSearch : BaseSearchEngine(
        "file:///android_asset/duckduckgo.png",
        DUCK_LITE_SEARCH,
        R.string.search_engine_duckduckgo_lite
)
