package reina.coffee.ngotronghoangpo

import reina.coffee.ngotronghoangpo.browser.activity.BrowserActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import com.anthonycr.bonsai.Completable
import kr.co.inno.autocash.service.AutoLoginServiceActivity
import kr.co.inno.autocash.service.AutoServiceActivity

class MainActivity : BrowserActivity() {

    @Suppress("DEPRECATION")
    public override fun updateCookiePreference(): Completable {
        return Completable.create { subscriber ->
            val cookieManager = CookieManager.getInstance()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(this@MainActivity)
            }
            cookieManager.setAcceptCookie(preferences.cookiesEnabled)
            subscriber.onComplete()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE)
        val loginID = prefs.getString("loginID", "")
        val googlePasswd = prefs.getString("googlePasswd", "")
        if (!TextUtils.isEmpty(loginID) && !TextUtils.isEmpty(googlePasswd)) {
            auto_service()
            auto_login_service()
        }*/
    }

    private fun auto_service() {
        val intent = Intent(this, AutoServiceActivity::class.java)
        stopService(intent)
        startService(intent)
    }

    private fun auto_login_service() {
        val intent = Intent(this, AutoLoginServiceActivity::class.java)
        stopService(intent)
        startService(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onNewIntent(intent: Intent) {
        if (isPanicTrigger(intent)) {
            panicClean()
        } else {
            handleNewIntent(intent)
            super.onNewIntent(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        saveOpenTabs()
    }

    override fun updateHistory(title: String?, url: String) {
        addItemToHistory(title, url)
    }

    override val isIncognito = false

    override fun closeActivity() {
        closeDrawers {
            performExitCleanUp()
            moveTaskToBack(true)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.isCtrlPressed) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_P ->
                    // Open a new private window
                    if (event.isShiftPressed) {
                        startActivity(Intent(this, IncognitoActivity::class.java))
                        overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out_scale)
                        return true
                    }
            }
        }
        return super.dispatchKeyEvent(event)
    }


}
