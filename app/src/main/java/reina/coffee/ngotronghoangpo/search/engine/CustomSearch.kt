package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R

/**
 * A custom search engine.
 */
class CustomSearch(queryUrl: String) : BaseSearchEngine(
        "file:///android_asset/lightning.png",
        queryUrl,
        R.string.search_engine_custom
)
