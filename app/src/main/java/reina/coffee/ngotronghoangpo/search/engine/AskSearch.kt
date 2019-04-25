package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.ASK_SEARCH

/**
 * The Ask search engine.
 */
class AskSearch : BaseSearchEngine(
        "file:///android_asset/ask.png",
        ASK_SEARCH,
        R.string.search_engine_ask
)
