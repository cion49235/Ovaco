package reina.coffee.ngotronghoangpo.search.engine

import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.constant.YANDEX_SEARCH

/**
 * The Yandex search engine.
 *
 * See http://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex.svg/600px-Yandex.svg.png
 * for the icon.
 */
class YandexSearch : BaseSearchEngine(
        "file:///android_asset/yandex.png",
        YANDEX_SEARCH,
        R.string.search_engine_yandex
)
