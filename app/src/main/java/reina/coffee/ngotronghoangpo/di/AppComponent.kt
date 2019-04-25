package reina.coffee.ngotronghoangpo.di

import reina.coffee.ngotronghoangpo.BrowserApp
import reina.coffee.ngotronghoangpo.adblock.AssetsAdBlocker
import reina.coffee.ngotronghoangpo.adblock.NoOpAdBlocker
import reina.coffee.ngotronghoangpo.browser.BrowserPresenter
import reina.coffee.ngotronghoangpo.browser.SearchBoxModel
import reina.coffee.ngotronghoangpo.browser.TabsManager
import reina.coffee.ngotronghoangpo.browser.activity.BrowserActivity
import reina.coffee.ngotronghoangpo.browser.activity.ThemableBrowserActivity
import reina.coffee.ngotronghoangpo.browser.fragment.BookmarksFragment
import reina.coffee.ngotronghoangpo.browser.fragment.TabsFragment
import reina.coffee.ngotronghoangpo.dialog.LightningDialogBuilder
import reina.coffee.ngotronghoangpo.download.DownloadHandler
import reina.coffee.ngotronghoangpo.download.LightningDownloadListener
import reina.coffee.ngotronghoangpo.html.bookmark.BookmarkPage
import reina.coffee.ngotronghoangpo.html.download.DownloadsPage
import reina.coffee.ngotronghoangpo.html.history.HistoryPage
import reina.coffee.ngotronghoangpo.html.homepage.StartPage
import reina.coffee.ngotronghoangpo.network.NetworkObservable
import reina.coffee.ngotronghoangpo.reading.activity.ReadingActivity
import reina.coffee.ngotronghoangpo.search.SearchEngineProvider
import reina.coffee.ngotronghoangpo.search.SuggestionsAdapter
import reina.coffee.ngotronghoangpo.settings.activity.ThemableSettingsActivity
import reina.coffee.ngotronghoangpo.settings.fragment.*
import reina.coffee.ngotronghoangpo.utils.ProxyUtils
import reina.coffee.ngotronghoangpo.view.LightningChromeClient
import reina.coffee.ngotronghoangpo.view.LightningView
import reina.coffee.ngotronghoangpo.view.LightningWebClient
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(activity: BrowserActivity)

    fun inject(fragment: BookmarksFragment)

    fun inject(fragment: BookmarkSettingsFragment)

    fun inject(builder: LightningDialogBuilder)

    fun inject(fragment: TabsFragment)

    fun inject(lightningView: LightningView)

    fun inject(activity: ThemableBrowserActivity)

    fun inject(fragment: LightningPreferenceFragment)

    fun inject(app: BrowserApp)

    fun inject(proxyUtils: ProxyUtils)

    fun inject(activity: ReadingActivity)

    fun inject(webClient: LightningWebClient)

    fun inject(activity: ThemableSettingsActivity)

    fun inject(listener: LightningDownloadListener)

    fun inject(fragment: PrivacySettingsFragment)

    fun inject(startPage: StartPage)

    fun inject(historyPage: HistoryPage)

    fun inject(bookmarkPage: BookmarkPage)

    fun inject(downloadsPage: DownloadsPage)

    fun inject(presenter: BrowserPresenter)

    fun inject(manager: TabsManager)

    fun inject(fragment: DebugSettingsFragment)

    fun inject(suggestionsAdapter: SuggestionsAdapter)

    fun inject(chromeClient: LightningChromeClient)

    fun inject(downloadHandler: DownloadHandler)

    fun inject(searchBoxModel: SearchBoxModel)

    fun inject(searchEngineProvider: SearchEngineProvider)

    fun inject(generalSettingsFragment: GeneralSettingsFragment)

    fun inject(networkObservable: NetworkObservable)

    fun provideAssetsAdBlocker(): AssetsAdBlocker

    fun provideNoOpAdBlocker(): NoOpAdBlocker

}
