package kr.co.inno.autocash;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by automoney on 2016-12-28
 */
public class AutoLoginService extends Service {

    public static Activity prevActivity;

    public static Context context;
    private Dialog dialog;
    WebView webview;

    private MyJavaScriptInterface myJavaScriptInterface;

    private static Thread thread = null;
    String get_data;  // 파싱해서 가져온 데이터를 저장할 스트링 변수
    ArrayList<String> array;  // get_data 변수의 값을 순차적으로 저장할 배열
    String account_email2, googleType;
    private int isConfirm = 0;
    private int isStep = 0;
    private int isResult = 0;

    private String mb_id = "";
    private String mb_id_send = "0";

    private String device = "";
    private String device_name = "";
    private String googlePasswd = "";
    private String googleId = "";
    private RelativeLayout whiteLayout;

    // 자동로그인 체크
    private int isLogin = 0;
    private int isNext = 0;

    public static boolean start_call_state = true;
    @Override
    @SuppressLint("NewApi")
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    // 서비스가 호출될때마다 매번 실행(onResume()과 비슷)
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);
        try {
            googleType = intent.getStringExtra("googleType");
            Log.i("dsu", "");
            this.deleteDatabase("webview.db");
            this.deleteDatabase("webviewCache.db");

            if ( googleType.equals("2") ) {
                android.webkit.CookieManager.getInstance().removeAllCookie();
            }
            sendURL();
        } catch ( NullPointerException e ) {
            Log.d("AutoCash", "AutoWebviewServiceActivity NullPointerException : " + e.toString());
        }
        return START_STICKY;
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public void sendURL()
    {
        Log.i("dsu", "새로운 로그인을 시도중입니다!======================>");

        if (webview == null)
        {
            webview = new WebView(context);
        }
        WebSettings set = webview.getSettings();
        webview.clearHistory();
        webview.clearCache(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setJavaScriptEnabled(true);
        set.setSaveFormData(false);
        webview.setWebViewClient(new ProxyWebViewClient());
        webview.setWebChromeClient(new ProxyWebChromeClient());
//        myJavaScriptInterface = new MyJavaScriptInterface(AutoLayoutGoogleActivity2.this, webview); //JavascriptInterface 객체화
//        webview.addJavascriptInterface(myJavaScriptInterface, "Android"); //웹뷰에 JavascriptInterface를 연결

        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accountGoogle", "");
        editor.commit();

        if ( googleType.equals("1") ) {
            webview.loadUrl("https://accounts.google.com/ServiceLogin");
        } else if ( googleType.equals("2") ) {
            webview.loadUrl("https://accounts.google.com/ServiceLogin");
        } else if ( googleType.equals("3") ) {
            webview.loadUrl("https://play.google.com/settings");
        } else if ( googleType.equals("4") ) {
            webview.loadUrl("https://accounts.google.com/ServiceLogin");
        }

        TelephonyManager telephony=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        Log.d("Google", "getCallState : "+telephony.getCallState());
        Log.d("Google", "getDataActivity : "+telephony.getDataActivity());
        Log.d("Google", "getDataState : "+telephony.getDataState());
        Log.d("Google", "getDeviceId : "+telephony.getDeviceId());
        Log.d("Google", "getDeviceSoftwareVersion : "+telephony.getDeviceSoftwareVersion());
        Log.d("Google", "getLine1Number : "+telephony.getLine1Number());
        Log.d("Google", "getNetworkCountryIso : "+telephony.getNetworkCountryIso());
        Log.d("Google", "getNetworkOperator : "+telephony.getNetworkOperator());
        Log.d("Google", "getNetworkOperatorName : "+telephony.getNetworkOperatorName());
        Log.d("Google", "getNetworkType : "+telephony.getNetworkType());
        Log.d("Google", "getPhoneType : "+telephony.getPhoneType());
        Log.d("Google", "getSimCountryIso : "+telephony.getSimCountryIso());
        Log.d("Google", "getSubscriberId : "+telephony.getSubscriberId());
        Log.d("Google", "getVoiceMailAlphaTag : "+telephony.getVoiceMailAlphaTag());
        Log.d("Google", "getVoiceMailNumber : "+telephony.getVoiceMailNumber());
        Log.d("Google", "isNetworkRoaming : "+telephony.isNetworkRoaming());
        Log.d("Google", "hasIccCard : "+telephony.hasIccCard());
        Log.d("Google", "hashCode : "+telephony.hashCode());
        Log.d("Google", "toString : "+telephony.toString());

        // device 계정 정보 가져오기
        Account[] accounts =  AccountManager.get(getApplicationContext()).getAccounts();
        Account account = null;

        for(int i=0;i<accounts.length;i++) {
            account = accounts[i];
            if(account.type.equals("com.google")){     //이러면 구글 계정 구분 가능
                Log.d("Google", "Account - name: " + account.name + ", type :" + account.type);
            }
        }
        start_call_state = false;
    }



    //helper method for clearCache() , recursive
    //returns number of deleted files
    static int clearCacheFolder(final File dir, final int numDays) {

        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {

                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            }
            catch(Exception e) {
                Log.e("ERROR", String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }
        return deletedFiles;
    }

    /*
     * Delete the files older than numDays days from the application cache
     * 0 means all files.
     */
    public static void clearCache(final Context context, final int numDays) {
        Log.i("ERROR", String.format("Starting cache prune, deleting files older than %d days", numDays));
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
        Log.i("ERROR", String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
    }


    public void googleLogin(String account_email, String account_passwd) {
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("loginID", account_email);
        editor.putString("googlePasswd", account_passwd);
        editor.commit();
        Log.d("dsu", "구글아이디 : " + account_email + "\n구글패스워드 : " + account_passwd);
    }

    private class ProxyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("URL1 : ", "URL1 : " + url);
            Uri uri = Uri.parse(url);

            if( url.startsWith("https://play.google.com") ) {
                Log.d("URL : ", "URL : " + url);
                return true;
            } else {
                Log.d("URL : ", "URL : " + url);
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.d("Autocash", "Autocash isStep : " + isStep);
            Log.i("dsu", "재로그인url========> : " + googleId);
            if( url.startsWith("https://accounts.google.com/ServiceLogin?elo=1#identifier") && mb_id_send.equals("0") ) {
                mb_id_send = "1";
                String js = "javascript:document.getElementById('Email').value = '" + mb_id + "';";
                if ( Build.VERSION.SDK_INT >= 19 ) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                        }
                    });
                } else {
                    view.loadUrl(js);
                }
                webview.loadUrl("javascript:document.getElementById('gaia_loginform').submit();");
                googleId = mb_id;

                Log.i("dsu", "재로그인googleId : " + googleId);
            }

            if ( url.startsWith("https://accounts.google.com/ServiceLogin?elo=1") ) {
                isNext++;
            }

            if ( url.startsWith("https://accounts.google.com/ServiceLogin?elo=1") && isNext == 2 ) {
                webview.loadUrl("javascript:document.getElementById('identifierLink').click();");
            }

            if( url.startsWith("https://accounts.google.com/ServiceLogin") && isStep > 0  ) {
                if ( googleType.equals("3") ) {
                } else {
                    if ( isStep < 10 ) {
                        Log.d("Autocash", "Autocash LoginActivity07_01 : " + account_email2);
                        //view.loadUrl("https://accounts.google.com/ServiceLogin");
                        //view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        isStep++;
                    }
                }
            }
            if( url.startsWith("https://myaccount.google.com") || url.startsWith("https://myaccount.google.com/?pli") || url.startsWith("https://accounts.google.com/ManageAccount") ) {
                if ( isLogin == 0 ) {
                    dialog.show();
                }
                Log.d("Autocash", "Autocash AutoLayoutGoogleActivity url 1 : " + url);
                Log.d("Autocash", "Autocash AutoLayoutGoogleActivity googleType 1 : " + googleType);
                isLogin = 1;

                if ( googleType.equals("1") ) {
                    if ( account_email2 != null && !account_email2.equals("") ) {
//                        ((LoginActivity) LoginActivity.context).googleLogin(account_email2, googlePasswd);
                    } else {
                        Log.d("LoginActivity", "LoginActivity09 : " + account_email2);
                        if ( isStep < 10 ) {
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        }

                        isStep++;
                    }
                }
                if ( googleType.equals("2") ) {
                    Log.d("Autocash", "Autocash account_email : " + account_email2);

                    if ( account_email2 != null && !account_email2.equals("") ) {
                        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("accountGoogle", account_email2);
                        editor.commit();
                        Log.d("Autocash", "재로그인 이메일 : " + account_email2);
                        googleLogin(account_email2, googlePasswd);
                        /*Intent intent = new Intent(context, IntroActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);*/
                        close_autolayout();
                    } else {
                        Log.d("Autocash", "Autocash account_email : " + account_email2);
                        if ( isStep < 10 ) {
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        }

                        isStep++;
                    }
                }

                if ( googleType.equals("4") ) {
                    if ( account_email2 != null && !account_email2.equals("") ) {
//                        ((HomeActivity) HomeActivity.context).googleLogin(account_email2, googlePasswd);
                    } else {
                        Log.d("LoginActivity", "LoginActivity09 : " + account_email2);

                        if ( isStep > 5 ) {
                            view.loadUrl(url);
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        } else {
                            view.loadUrl("https://accounts.google.com/ServiceLogin");
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        }

                        isStep++;
                    }
                }
            } else {
                if ( googleType.equals("3") ) {
                } else {
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                }
            }
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Log.d("URL3 : ", "LoginActivity04 : " + url);
            //Log.d("URL3 : ", "LoginActivity05 : " + googleType);
            Log.d("Autocash", "Autocash isStep : " + isStep);

            if( url.startsWith("https://accounts.google.com/ServiceLogin?elo=1#identifier") && mb_id_send.equals("0") ) {
                mb_id_send = "1";
                String js = "javascript:document.getElementById('Email').value = '" + mb_id + "';";
                if ( Build.VERSION.SDK_INT >= 19 ) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                        }
                    });
                } else {
                    view.loadUrl(js);
                }
                webview.loadUrl("javascript:document.getElementById('gaia_loginform').submit();");
                googleId = mb_id;

                Log.i("dsu", "googleId : " + googleId);
            }

            if ( url.startsWith("https://accounts.google.com/ServiceLogin?elo=1") ) {
                isNext++;
            }

            if ( url.startsWith("https://accounts.google.com/ServiceLogin?elo=1") && isNext == 2 ) {
                webview.loadUrl("javascript:document.getElementById('identifierLink').click();");
            }

            if( url.startsWith("https://accounts.google.com/ServiceLogin") && isStep > 0  ) {
                if ( googleType.equals("3") ) {
                } else {
                    if ( isStep < 10 ) {
                        Log.d("Autocash", "Autocash LoginActivity07_01 : " + account_email2);
                        //view.loadUrl("https://accounts.google.com/ServiceLogin");
                        //view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");

                        isStep++;
                    }
                }
            }

            if( url.startsWith("https://myaccount.google.com") || url.startsWith("https://myaccount.google.com/?pli") || url.startsWith("https://accounts.google.com/ManageAccount") ) {
                if ( isLogin == 0 ) {
                    dialog.show();
                }
                Log.d("Autocash", "Autocash AutoLayoutGoogleActivity url 1 : " + url);
                Log.d("Autocash", "Autocash AutoLayoutGoogleActivity googleType 1 : " + googleType);
                isLogin = 1;

                if ( googleType.equals("1") ) {
                    if ( account_email2 != null && !account_email2.equals("") ) {
//                        ((LoginActivity) LoginActivity.context).googleLogin(account_email2, googlePasswd);
                    } else {
                        Log.d("LoginActivity", "LoginActivity09 : " + account_email2);
                        if ( isStep < 10 ) {
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        }

                        isStep++;
                    }
                }

                if ( googleType.equals("2") ) {
                    Log.d("Autocash", "Autocash account_email : " + account_email2);

                    if ( account_email2 != null && !account_email2.equals("") ) {
                        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("accountGoogle", account_email2);
                        editor.commit();
                        Log.d("Autocash", "Autocash account_email : " + account_email2);
                        googleLogin(account_email2, googlePasswd);
                        /*Intent intent = new Intent(context, IntroActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);*/
                        close_autolayout();
                    } else {
                        Log.d("Autocash", "Autocash account_email : " + account_email2);
                        if ( isStep < 10 ) {
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        }

                        isStep++;
                    }
                }

                if ( googleType.equals("4") ) {
                    if ( account_email2 != null && !account_email2.equals("") ) {
//                        ((HomeActivity) HomeActivity.context).googleLogin(account_email2, googlePasswd);
                    } else {
                        Log.d("LoginActivity", "LoginActivity09 : " + account_email2);

                        if ( isStep > 5 ) {
                            view.loadUrl(url);
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        } else {
                            view.loadUrl("https://accounts.google.com/ServiceLogin");
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                        }

                        isStep++;
                    }
                }
            } else {
                if ( googleType.equals("3") ) {
                } else {
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
                }
            }
            super.onPageFinished(view, url);
        }
    }

    private class ProxyWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress){
            if (progress == 100) {
                try {
                    //dialog.dismiss();
                } catch (IllegalArgumentException e) {
                    Log.d("dialog.dismiss", "IllegalArgumentException : " + e.toString());
                }
            } else {
                webview.loadUrl("javascript:window.Android.setHtml(document.getElementsByName('password')[0].value);");
                webview.loadUrl("javascript:window.Android.setHtml(document.getElementById('Passwd').value);");
                Log.d("Autocash", "Autocash passwd2 : ing");
                Log.d("Autocash", "Autocash passwd2 : ing" + progress);
                /*if(!dialog.isShowing()) {
                    try {
                        //dialog.show();
                    } catch (WindowManager.BadTokenException e) {
                        Log.d("BadTokenException", "BadTokenException : " + e.toString());
                    }
                }*/
                /*if(dialog.isShowing()) {
                    final ProgressWheel wheel = (ProgressWheel)dialog.findViewById(R.id.progressBar1);
                    wheel.setCallback(new ProgressWheel.ProgressCallback() {
                        @Override
                        public void onProgressUpdate(float progress) {
                            if(progress == 1.0f) {
                                //dialog.dismiss();
                            }
                        }
                    });
                    wheel.setProgress(1.0f);
                }*/
            }
        }
    }

    private void close_autolayout(){
//        onDestroy();
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("authuser", "0");
        editor.commit();
    }


    // 서비스가 종료될때 실행
    public void onDestroy() {
        super.onDestroy();
        start_call_state = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
