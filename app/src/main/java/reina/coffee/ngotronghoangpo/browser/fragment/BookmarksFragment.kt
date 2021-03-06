package reina.coffee.ngotronghoangpo.browser.fragment

import reina.coffee.ngotronghoangpo.BrowserApp
import reina.coffee.ngotronghoangpo.R
import reina.coffee.ngotronghoangpo.animation.AnimationUtils
import reina.coffee.ngotronghoangpo.browser.BookmarksView
import reina.coffee.ngotronghoangpo.browser.TabsManager
import reina.coffee.ngotronghoangpo.browser.bookmark.BookmarkUiModel
import reina.coffee.ngotronghoangpo.constant.LOAD_READING_URL
import reina.coffee.ngotronghoangpo.controller.UIController
import reina.coffee.ngotronghoangpo.database.HistoryItem
import reina.coffee.ngotronghoangpo.database.bookmark.BookmarkModel
import reina.coffee.ngotronghoangpo.dialog.LightningDialogBuilder
import reina.coffee.ngotronghoangpo.favicon.FaviconModel
import reina.coffee.ngotronghoangpo.preference.PreferenceManager
import reina.coffee.ngotronghoangpo.reading.activity.ReadingActivity
import reina.coffee.ngotronghoangpo.utils.Preconditions
import reina.coffee.ngotronghoangpo.utils.SubscriptionUtils
import reina.coffee.ngotronghoangpo.utils.ThemeUtils
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.anthonycr.bonsai.Schedulers
import com.anthonycr.bonsai.SingleOnSubscribe
import com.anthonycr.bonsai.Subscription
import kotlinx.android.synthetic.main.bookmark_drawer.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class BookmarksFragment : Fragment(), View.OnClickListener, View.OnLongClickListener, BookmarksView {

    // Managers
    @Inject internal lateinit var bookmarkModel: BookmarkModel

    // Dialog builder
    @Inject internal lateinit var bookmarksDialogBuilder: LightningDialogBuilder

    @Inject internal lateinit var preferenceManager: PreferenceManager

    @Inject internal lateinit var faviconModel: FaviconModel

    private lateinit var uiController: UIController

    private val tabsManager: TabsManager
        get() = uiController.getTabModel()

    // Adapter
    private var bookmarkAdapter: BookmarkListAdapter? = null

    // Preloaded images
    private var webpageBitmap: Bitmap? = null
    private var folderBitmap: Bitmap? = null

    // Colors
    private var iconColor: Int = 0
    private var scrollIndex: Int = 0

    private var isIncognito: Boolean = false

    private var bookmarksSubscription: Subscription? = null
    private var foldersSubscription: Subscription? = null
    private var bookmarkUpdateSubscription: Subscription? = null

    private val uiModel = BookmarkUiModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BrowserApp.appComponent.inject(this)

        uiController = context as UIController
        isIncognito = arguments.getBoolean(INCOGNITO_MODE, false)
        val darkTheme = preferenceManager.useTheme != 0 || isIncognito
        webpageBitmap = ThemeUtils.getThemedBitmap(context, R.drawable.ic_webpage, darkTheme)
        folderBitmap = ThemeUtils.getThemedBitmap(context, R.drawable.ic_folder, darkTheme)
        iconColor = if (darkTheme) {
            ThemeUtils.getIconDarkThemeColor(context)
        } else {
            ThemeUtils.getIconLightThemeColor(context)
        }
    }

    // Handle bookmark click
    private val itemClickListener = object : OnItemClickListener {
        override fun onItemClick(item: HistoryItem) {
            if (item.isFolder) {
                scrollIndex = (bookmark_list_view.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                setBookmarksShown(item.title, true)
            } else {
                uiController.bookmarkItemClicked(item)
            }
        }
    }

    private val itemLongClickListener = object : OnItemLongClickListener {
        override fun onItemLongClick(item: HistoryItem): Boolean {
            handleLongPress(item)
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        if (bookmarkAdapter != null) {
            setBookmarksShown(null, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.bookmark_drawer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmark_back_button_image?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
        val backView = view.findViewById<View>(R.id.bookmark_back_button)
        backView.setOnClickListener {
            if (!uiModel.isRootFolder) {
                setBookmarksShown(null, true)
                bookmark_list_view?.layoutManager?.scrollToPosition(scrollIndex)
            }
        }
        setupNavigationButton(view, R.id.action_add_bookmark, R.id.action_add_bookmark_image)
        setupNavigationButton(view, R.id.action_reading, R.id.action_reading_image)
        setupNavigationButton(view, R.id.action_toggle_desktop, R.id.action_toggle_desktop_image)


        bookmarkAdapter = BookmarkListAdapter(faviconModel, folderBitmap!!, webpageBitmap!!).apply {
            onItemClickListener = itemClickListener
            onItemLongCLickListener = itemLongClickListener
        }

        bookmark_list_view?.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = bookmarkAdapter
        }


        setBookmarksShown(null, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        SubscriptionUtils.safeUnsubscribe(bookmarksSubscription)
        SubscriptionUtils.safeUnsubscribe(foldersSubscription)
        SubscriptionUtils.safeUnsubscribe(bookmarkUpdateSubscription)

        bookmarkAdapter?.cleanupSubscriptions()
    }

    override fun onDestroy() {
        super.onDestroy()

        SubscriptionUtils.safeUnsubscribe(bookmarksSubscription)
        SubscriptionUtils.safeUnsubscribe(foldersSubscription)
        SubscriptionUtils.safeUnsubscribe(bookmarkUpdateSubscription)

        bookmarkAdapter?.cleanupSubscriptions()
    }

    fun reinitializePreferences() {
        val activity = activity ?: return
        val darkTheme = preferenceManager.useTheme != 0 || isIncognito
        webpageBitmap = ThemeUtils.getThemedBitmap(activity, R.drawable.ic_webpage, darkTheme)
        folderBitmap = ThemeUtils.getThemedBitmap(activity, R.drawable.ic_folder, darkTheme)
        iconColor = if (darkTheme)
            ThemeUtils.getIconDarkThemeColor(activity)
        else
            ThemeUtils.getIconLightThemeColor(activity)
    }

    private fun updateBookmarkIndicator(url: String) {
        SubscriptionUtils.safeUnsubscribe(bookmarkUpdateSubscription)
        bookmarkUpdateSubscription = bookmarkModel.isBookmark(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(object : SingleOnSubscribe<Boolean>() {
                    override fun onItem(item: Boolean?) {
                        bookmarkUpdateSubscription = null
                        Preconditions.checkNonNull(item)
                        val activity = activity
                        if (action_add_bookmark_image == null || activity == null) {
                            return
                        }
                        if (item != true) {
                            action_add_bookmark_image?.setImageResource(R.drawable.ic_action_star)
                            action_add_bookmark_image?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
                        } else {
                            action_add_bookmark_image?.setImageResource(R.drawable.ic_bookmark)
                            action_add_bookmark_image?.setColorFilter(ThemeUtils.getAccentColor(activity), PorterDuff.Mode.SRC_IN)
                        }
                    }
                })
    }

    override fun handleBookmarkDeleted(item: HistoryItem) {
        if (item.isFolder) {
            setBookmarksShown(null, false)
        } else {
            bookmarkAdapter?.deleteItem(item)
        }
    }

    private fun setBookmarksShown(folder: String?, animate: Boolean) {
        SubscriptionUtils.safeUnsubscribe(bookmarksSubscription)
        bookmarksSubscription = bookmarkModel.getBookmarksFromFolderSorted(folder)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(object : SingleOnSubscribe<List<HistoryItem>>() {
                    override fun onItem(item: List<HistoryItem>?) {
                        bookmarksSubscription = null
                        val list = requireNotNull(item) { "setBookmarksShown: bookmark list must not be null" }.toMutableList()

                        uiModel.currentFolder = folder
                        if (folder == null) {
                            SubscriptionUtils.safeUnsubscribe(foldersSubscription)
                            foldersSubscription = bookmarkModel.getFoldersSorted()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.main())
                                    .subscribe(object : SingleOnSubscribe<List<HistoryItem>>() {
                                        override fun onItem(folders: List<HistoryItem>?) {
                                            foldersSubscription = null
                                            val folderList = requireNotNull(folders) { "setBookmarksShown: folder list must not be null" }
                                            list.addAll(folderList)
                                            setBookmarkDataSet(list, animate)
                                        }
                                    })
                        } else {
                            setBookmarkDataSet(list, animate)
                        }
                    }
                })
    }

    private fun setBookmarkDataSet(items: List<HistoryItem>, animate: Boolean) {
        bookmarkAdapter?.updateItems(items)
        val resource = if (uiModel.isRootFolder) {
            R.drawable.ic_action_star
        } else {
            R.drawable.ic_action_back
        }

        if (animate) {
            bookmark_back_button_image?.let {
                val transition = AnimationUtils.createRotationTransitionAnimation(it, resource)
                it.startAnimation(transition)
            }
        } else {
            bookmark_back_button_image?.setImageResource(resource)
        }
    }

    private fun setupNavigationButton(view: View, @IdRes buttonId: Int, @IdRes imageId: Int) {
        val frameButton = view.findViewById<FrameLayout>(buttonId)
        frameButton.setOnClickListener(this)
        frameButton.setOnLongClickListener(this)
        val buttonImage = view.findViewById<ImageView>(imageId)
        buttonImage.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
    }

    private fun handleLongPress(item: HistoryItem) {
        if (item.isFolder) {
            bookmarksDialogBuilder.showBookmarkFolderLongPressedDialog(activity, uiController, item)
        } else {
            bookmarksDialogBuilder.showLongPressedDialogForBookmarkUrl(activity, uiController, item)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.action_add_bookmark -> uiController.bookmarkButtonClicked()
            R.id.action_reading -> {
                val currentTab = tabsManager.currentTab
                if (currentTab != null) {
                    val read = Intent(activity, ReadingActivity::class.java)
                    read.putExtra(LOAD_READING_URL, currentTab.url)
                    startActivity(read)
                }
            }
            R.id.action_toggle_desktop -> {
                val current = tabsManager.currentTab
                if (current != null) {
                    current.toggleDesktopUA(activity)
                    current.reload()
                    // TODO add back drawer closing
                }
            }
            else -> {
            }
        }
    }

    override fun onLongClick(v: View) = false

    override fun navigateBack() {
        if (uiModel.isRootFolder) {
            uiController.closeBookmarksDrawer()
        } else {
            setBookmarksShown(null, true)
            bookmark_list_view?.layoutManager?.scrollToPosition(scrollIndex)
        }
    }

    override fun handleUpdatedUrl(url: String) {
        updateBookmarkIndicator(url)
        val folder = uiModel.currentFolder
        setBookmarksShown(folder, false)
    }

    private class BookmarkViewHolder(
            itemView: View,
            private val adapter: BookmarkListAdapter,
            private val onItemLongClickListener: OnItemLongClickListener?,
            private val onItemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        var txtTitle: TextView = itemView.findViewById(R.id.textBookmark)
        var favicon: ImageView = itemView.findViewById(R.id.faviconBookmark)

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val index = adapterPosition
            if (onItemClickListener != null && index.toLong() != RecyclerView.NO_ID) {
                onItemClickListener.onItemClick(adapter.itemAt(index))
            }
        }

        override fun onLongClick(v: View): Boolean {
            val index = adapterPosition
            return index != RecyclerView.NO_POSITION && onItemLongClickListener != null &&
                    onItemLongClickListener.onItemLongClick(adapter.itemAt(index))
        }
    }

    internal interface OnItemLongClickListener {
        fun onItemLongClick(item: HistoryItem): Boolean
    }

    internal interface OnItemClickListener {
        fun onItemClick(item: HistoryItem)
    }

    private class BookmarkListAdapter(
            private val faviconModel: FaviconModel,
            private val folderBitmap: Bitmap,
            private val webpageBitmap: Bitmap
    ) : RecyclerView.Adapter<BookmarkViewHolder>() {

        private var bookmarks: List<HistoryItem> = ArrayList()
        private val faviconFetchSubscriptions = ConcurrentHashMap<String, Subscription>()

        var onItemLongCLickListener: OnItemLongClickListener? = null
        var onItemClickListener: OnItemClickListener? = null

        internal fun itemAt(position: Int): HistoryItem = bookmarks[position]

        internal fun deleteItem(item: HistoryItem) {
            val newList = ArrayList(bookmarks)
            newList.remove(item)
            updateItems(newList)
        }

        internal fun updateItems(newList: List<HistoryItem>) {
            val oldList = bookmarks
            bookmarks = newList

            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = oldList.size

                override fun getNewListSize() = bookmarks.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        oldList[oldItemPosition] == bookmarks[newItemPosition]

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        oldList[oldItemPosition] == bookmarks[newItemPosition]
            })

            diffResult.dispatchUpdatesTo(this)
        }

        internal fun cleanupSubscriptions() {
            for (subscription in faviconFetchSubscriptions.values) {
                subscription.unsubscribe()
            }
            faviconFetchSubscriptions.clear()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder? {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.bookmark_list_item, parent, false)

            return BookmarkViewHolder(itemView, this, onItemLongCLickListener, onItemClickListener)
        }

        override fun onViewRecycled(holder: BookmarkViewHolder?) {
            super.onViewRecycled(holder)
        }

        override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
            holder.itemView.jumpDrawablesToCurrentState()

            val web = bookmarks[position]
            holder.txtTitle.text = web.title
            when {
                web.isFolder -> holder.favicon.setImageBitmap(folderBitmap)
                web.bitmap == null -> {
                    holder.favicon.setImageBitmap(webpageBitmap)
                    holder.favicon.tag = web.url.hashCode()

                    val url = web.url

                    val oldSubscription = faviconFetchSubscriptions[url]
                    SubscriptionUtils.safeUnsubscribe(oldSubscription)

                    val faviconSubscription = faviconModel.faviconForUrl(url, web.title)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.main())
                            .subscribe(object : SingleOnSubscribe<Bitmap>() {
                                override fun onItem(item: Bitmap?) {
                                    faviconFetchSubscriptions.remove(url)
                                    val tag = holder.favicon.tag
                                    if (tag != null && tag == url.hashCode()) {
                                        holder.favicon.setImageBitmap(item)
                                    }

                                    web.bitmap = item
                                }
                            })

                    faviconFetchSubscriptions.put(url, faviconSubscription)
                }
                else -> holder.favicon.setImageBitmap(web.bitmap)
            }

        }

        override fun getItemCount() = bookmarks.size
    }

    companion object {

        @JvmStatic
        fun createFragment(isIncognito: Boolean): BookmarksFragment {
            val bookmarksFragment = BookmarksFragment()
            val bookmarksFragmentArguments = Bundle()
            bookmarksFragmentArguments.putBoolean(BookmarksFragment.INCOGNITO_MODE, isIncognito)
            bookmarksFragment.arguments = bookmarksFragmentArguments

            return bookmarksFragment
        }

        private const val TAG = "BookmarksFragment"

        private const val INCOGNITO_MODE = "$TAG.INCOGNITO_MODE"
    }

}
