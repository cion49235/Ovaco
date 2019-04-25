package kr.co.inno.autocash.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import kr.co.inno.autocash.Autoapp_DBopenHelper;
import kr.co.inno.autocash.QuickstartPreferences;
import kr.co.inno.autocash.cms.AutoInstallList;
import kr.co.inno.autocash.cms.AutoInstallListItem;
import kr.co.inno.autocash.cms.BaseModel;
import kr.co.inno.autocash.cms.BaseResponse;
import kr.co.inno.autocash.cms.JsonClient;
import kr.co.inno.autocash.cms.PreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import reina.coffee.ngotronghoangpo.R;

import static android.os.Build.VERSION.SDK_INT;

public class AutoCompleteServiceActivity extends Service
{
    public static Context context;

    private String loginID = "";
    private String memtype = "";
    private String google_id = "";
    private String uid = "";
    private String device = "";
    private boolean isLogin = false;
    public static int isApp = 0;

    String ev_type, ev_app_pkg;
    private boolean mLockListView;
    private JsonClient client = null;
    public String app_uid;
    public IBinder onBind(Intent paramIntent)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();

        context = this;
        client = JsonClient.getInstance(context);
        Log.d("AutoComplete", "AutoServiceActivity : Service is Created");
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        int i = super.onStartCommand(intent, flags, startId);

        user_info();


//        if ( isNetWork() == 1 || isNetWork() == 2 ) {
            try {
                if ( intent.getStringExtra("ev_type") != null && !intent.getStringExtra("ev_type").equals("") ) {
                    ev_type = intent.getStringExtra("ev_type");
                    ev_app_pkg = intent.getStringExtra("ev_app_pkg");
                    Log.d("AutoComplete", "AutoServiceActivity : AutoCashCompleteService Excute");
                    Log.d("AutoComplete", "AutoServiceActivity : AutoCashCompleteService ev_type : " + ev_type);
                    Log.d("AutoComplete", "AutoServiceActivity : AutoCashCompleteService ev_app_pkg : " + ev_app_pkg);
                    if (ev_type.equals("packageADD") ) {
                        Log.i("dsu", "자동설치 전송==================>" + ev_type);
                        //자동설치성공시 서버로 정보전송
                        getData(ev_app_pkg);
                    } else {
                        Log.i("dsu", "앱삭제==================>" + ev_type);
                        appDataCheck(ev_app_pkg);
                    }
                }
            } catch (NullPointerException e) {
                Log.d("AutoComplete", "AutoServiceActivity : AutoCashCompleteService " + e.toString());
            }
//        }
        return i;
    }

    // 데이터 가져오기
    public static Autoapp_DBopenHelper autoapp_mydb;
    public String aai_seq = "empty";
    private void getData(final String ev_app_pkg) {
        mLockListView = true;
        /** 통신처리 */
        client.init(R.string.auto_install_list, app_uid);
        client.post(new BaseResponse<AutoInstallList>(context) {
            @Override
            public void onResponse(AutoInstallList response) {
                try{
                    if (!response.code.equals("000")) {
//                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ( response.list != null ) {
                        for (int i = 0; i < response.list.size(); i++) {
                            AutoInstallListItem frItem = response.list.get(i);
                            if(response.list.get(i).aai_link_url.equals(ev_app_pkg)){
                                install_data_send(ev_app_pkg);
                                Log.i("dsu", "패키지명 전송===>" + ev_app_pkg);
                                return;
                            }
                        }
                        mLockListView = false;
                    } else {
                        Toast.makeText(context, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                }finally {
                    if(autoapp_mydb != null){
                        autoapp_mydb.close();
                    }
                }
            }
        });
    }

    private void appDataCheck(final String ev_app_pkg) {
        mLockListView = true;
        /** 통신처리 */
        client.init(R.string.auto_install_list, app_uid);
        client.post(new BaseResponse<AutoInstallList>(context) {
            @Override
            public void onResponse(AutoInstallList response) {
                try{
                    if (!response.code.equals("000")) {
//                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ( response.list != null ) {
                        for (int i = 0; i < response.list.size(); i++) {
                            AutoInstallListItem frItem = response.list.get(i);
                            if(response.list.get(i).aai_link_url.equals(ev_app_pkg)){
                                autoapp_mydb = new Autoapp_DBopenHelper(context);
                                Cursor cursor = autoapp_mydb.getReadableDatabase().rawQuery(
                                        "select * from auto_app_list where aai_seq = '"+response.list.get(i).aai_seq+"'", null);
                                if(null != cursor && cursor.moveToFirst()){
                                    aai_seq = cursor.getString(cursor.getColumnIndex("aai_seq"));
                                }else{
                                    aai_seq = "empty";
                                }
                                if(aai_seq.equals("empty")){
                                    ContentValues cv = new ContentValues();
                                    cv.put("aai_seq", response.list.get(i).aai_seq);
                                    cv.put("aai_title", response.list.get(i).aai_title);
                                    cv.put("aai_link_url", response.list.get(i).aai_link_url);
                                    cv.put("aai_status", response.list.get(i).aai_status);
                                    autoapp_mydb.getWritableDatabase().insert("auto_app_list", null, cv);
                                    Log.i("dsu", "처음자동설치해서 깔린앱지운다===>" + ev_app_pkg);
                                }else{
                                    Log.i("dsu", "이미한번 깔린 앱입니다===>" + ev_app_pkg);
                                }
                                return;
                            }
                        }
                        mLockListView = false;
                    } else {
                        Toast.makeText(context, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                }
            }
        });
    }

    // 데이터 가져오기
    private void install_data_send(final String ev_app_pkg) {
        Log.i("dsu", "서버전송 통신처리 1 : ===>" + app_uid);
        mLockListView = true;
        /** 통신처리 */
        client.init(R.string.auto_install_list_upd, app_uid, ev_app_pkg);
        Log.i("dsu", "서버전송 통신처리 2 : ===>" + ev_app_pkg);
        client.post(new BaseResponse<BaseModel>(this) {
            @Override
            public void onResponse(BaseModel response) {
//                Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show();
                if (!response.code.equals("000")) { // 시스템 에러
                    Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ev_app_pkg.equals("air.kr.co.gifttv2")){
                    Intent intent = getPackageManager().getLaunchIntentForPackage(ev_app_pkg);
                    startActivity(intent);
                }
                mLockListView = false;
            }
        });
    }

    private void user_info(){
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        loginID = prefs.getString("loginID", "");
        memtype = prefs.getString("memType", "");
        google_id = prefs.getString("google_id", "");
        uid = prefs.getString("uid", "");

        SharedPreferences preferences = PreferencesHelper.getInstance(this).getPreferences();
        if (SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            app_uid = getMacAddress_v6();
        }else{
            app_uid = getMACAddress("wlan0");
        }

        Log.i("dsu", "app_uid(maccaddress) : " + app_uid);

        boolean chkLogin = (loginID.equals("")||memtype.equals("")) ? false:true;
        if ( isLogin != chkLogin ) {
            isLogin = chkLogin;
        }
    }

    private String getMacAddress_v6() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }
        return "02:00:00:00:00:00";
    }

    private String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }
        return "";
    }

    private int isNetWork(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        /*boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();*/
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect)) {
            Log.d("AutoComplete", "AutoServiceActivity : isWifi : true");
            return 1;
        }
        /*else  if ((isMobileAvailable && isMobileConnect)) {
            Log.d("AutoComplete", "AutoServiceActivity : isMobile : true");
            return 2;
        }*/
        else{
            Log.d("AutoComplete", "AutoServiceActivity : isWifi : false");
            return 3;
        }
    }

    private void send_complete(){
        device = Build.MODEL;
        RequestBody formBody = new FormBody.Builder()
                .add("mb_id", loginID)
                .add("ev_type", ev_type)
                .add("ev_app_pkg", ev_app_pkg)
                .add("adid", google_id)
                .add("uid", uid)
                .add("device", device)
                .build();

        send_get_data("complete_send", QuickstartPreferences.COMPLETE_URL, formBody, false);
    }

    private void send_fail(){
        RequestBody formBody = new FormBody.Builder()
                .add("mb_id", loginID)
                .add("ev_type", ev_type)
                .add("ev_app_pkg", ev_app_pkg)
                .build();

        send_get_data("fail_send", QuickstartPreferences.COMPLETE_URL, formBody, false);
    }

    private void send_get_data(final String op, String url, RequestBody formBody, boolean isShow){
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        CookieManager cookieManager = new CookieManager((CookieStore) cookieStore, CookiePolicy.ACCEPT_ALL);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String res = response.body().string();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            get_data_response(op, res);
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("NewApi")
    private void get_data_response(String op, String data){
        if(op.equals("complete_send")) {
            try {
                JSONObject jsonObj = new JSONObject(data);
                JSONArray responsJson = jsonObj.getJSONArray("result");
                JSONObject c = responsJson.getJSONObject(0);
                Log.d("msg", "msg : " + c.getString("msg"));
                if(c.getString("code").equals("1")){
                    String msg = c.getString("msg");
                    String result = c.getString("ev_result");
                    String result2 = c.getString("ev_result2");
                    String adid = c.getString("adid");
                    String app_id = c.getString("app_id");
                    String ev_idx = c.getString("ev_idx");
                    String uid = c.getString("uid");
                    String gid = c.getString("gid");
                    String ev_type_auto = c.getString("ev_type_auto");
                    String ev_app_pkg = c.getString("ev_app_pkg");
                    int cf_6 = Integer.parseInt(c.getString("cf_6"));

                    Log.d("AutoComplete", "AutoServiceActivity : Complete - msg : " + msg);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - result : " + result);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - result2 : " + result2);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - adid : " + adid);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - app_id : " + app_id);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - ev_idx : " + ev_idx);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - uid : " + uid);
                    Log.d("AutoComplete", "AutoServiceActivity : omplete - gid : " + gid);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - ev_type_auto : " + ev_type_auto);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - ev_app_pkg : " + ev_app_pkg);
                    Log.d("AutoComplete", "AutoServiceActivity : Complete - cf_6 : " + cf_6);
                    if ( ev_type_auto.equals("CPE") ) {
                        Log.d("AutoComplete", "AutoServiceActivity : Complete - ev_type_auto : " + ev_type_auto);
                        Log.d("AutoComplete", "AutoServiceActivity : Complete - ev_app_pkg : " + ev_app_pkg);

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(ev_app_pkg);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Log.d("AutoComplete", "AutoServiceActivity : Complete - cf_6_1 : " + cf_6);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Log.d("AutoComplete", "AutoServiceActivity : Complete - cf_6_2 : " + cf_6);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Log.d("AutoComplete", "AutoServiceActivity : Complete - cf_6_3 : " + cf_6);
                        try {
                            Thread.sleep(1000 * cf_6);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("AutoComplete", "AutoServiceActivity : Complete - cf_6_4 : " + cf_6);
                        Log.d("AutoComplete", "CAutoServiceActivity : omplete - ev_app_pkg : " + ev_app_pkg);
                        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                        am.killBackgroundProcesses(ev_app_pkg);
                    }

                } else {
                    Log.d("AutoComplete", "AutoServiceActivity : msg : no action");
                }
            } catch (JSONException e) {
                //CommonLib.show_msg(context, getString(R.string.network_error));
            }
        } else if(op.equals("fail_send")) {
            try {
                JSONObject jsonObj = new JSONObject(data);
                JSONArray responsJson = jsonObj.getJSONArray("result");
                JSONObject c = responsJson.getJSONObject(0);
                Log.d("AutoComplete", "AutoServiceActivity : msg : " + c.getString("msg"));
                if(c.getString("code").equals("1")){
                    String msg = c.getString("msg");

                    Log.d("AutoComplete", "AutoServiceActivity : fail_send : " + msg);

                    /*if ( isApp == 1 ) {
                        ((PartActivity)PartActivity.context).load_refresh();
                        isApp = 0;
                    }*/

                } else {
                    Log.d("AutoComplete", "AutoServiceActivity : msg : no action");
                }
            } catch (JSONException e) {
                //CommonLib.show_msg(context, getString(R.string.network_error));
            }
        }
    }
}