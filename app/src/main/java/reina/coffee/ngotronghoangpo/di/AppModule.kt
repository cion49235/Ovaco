package reina.coffee.ngotronghoangpo.di

import reina.coffee.ngotronghoangpo.BrowserApp
import reina.coffee.ngotronghoangpo.database.bookmark.BookmarkDatabase
import reina.coffee.ngotronghoangpo.database.bookmark.BookmarkModel
import reina.coffee.ngotronghoangpo.database.downloads.DownloadsDatabase
import reina.coffee.ngotronghoangpo.database.downloads.DownloadsModel
import reina.coffee.ngotronghoangpo.database.history.HistoryDatabase
import reina.coffee.ngotronghoangpo.database.history.HistoryModel
import reina.coffee.ngotronghoangpo.download.DownloadHandler
import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import net.i2p.android.ui.I2PAndroidHelper
import javax.inject.Singleton

@Module
class AppModule(private val app: BrowserApp) {

    @Provides
    fun provideApplication(): Application = app

    @Provides
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideBookmarkModel(): BookmarkModel = BookmarkDatabase(app)

    @Provides
    @Singleton
    fun provideDownloadsModel(): DownloadsModel = DownloadsDatabase(app)

    @Provides
    @Singleton
    fun providesHistoryModel(): HistoryModel = HistoryDatabase(app)

    @Provides
    @Singleton
    fun provideDownloadHandler(): DownloadHandler = DownloadHandler()

    @Provides
    @Singleton
    fun provideI2PAndroidHelper(): I2PAndroidHelper = I2PAndroidHelper(app.applicationContext)

}
