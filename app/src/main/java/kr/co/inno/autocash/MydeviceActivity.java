package kr.co.inno.autocash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import reina.coffee.ngotronghoangpo.R;

/**
 * Created by byapps on 2016. 3. 16..
 */
public class MydeviceActivity extends Activity {

    private Context context;
    private Dialog dialog;

    private String loginID = "";
    private String memtype = "";
    private String device_name = "";
    private boolean isLogin = false;

    private ImageButton deviceConfirm, deviceCancel;
    private TextView deviceName;

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_mydevice);
        context = this;

        dialog = new Dialog(context, R.style.NewDialog);
        View v = LayoutInflater.from(context).inflate(R.layout.progress_circle,null);
        dialog.setContentView(v);

        deviceName = (TextView)findViewById(R.id.deviceName);

        Bundle bundle = getIntent().getExtras();
        device_name = bundle.getString("device_name");
        deviceName.setText(device_name);

        user_info();

        Log.d("MydeviceActivity : ", "LoginActivity01 : " + device_name);

        deviceConfirm = (ImageButton)findViewById(R.id.deviceConfirm);
        deviceConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                send_device();
            }
        });

        deviceConfirm.setOnTouchListener(new View.OnTouchListener() { //버튼 터치시 이벤트
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) // 버튼을 누르고 있을 때
                if(event.getAction() == MotionEvent.ACTION_UP){ //버튼에서 손을 떼었을 때
                }
                return false;
            }
        });

        deviceCancel = (ImageButton)findViewById(R.id.deviceCancel);
        deviceCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                close_mydevice();
            }
        });

        deviceCancel.setOnTouchListener(new View.OnTouchListener() { //버튼 터치시 이벤트
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) // 버튼을 누르고 있을 때
                if(event.getAction() == MotionEvent.ACTION_UP){ //버튼에서 손을 떼었을 때
                }
                return false;
            }
        });
    }

    private void user_info(){
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        loginID = prefs.getString("loginID", "");
        memtype = prefs.getString("memType", "");
        device_name = prefs.getString("device_name", "");

        boolean chkLogin = (loginID.equals("")||memtype.equals("")) ? false:true;

        if ( isLogin != chkLogin ) {
            isLogin = chkLogin;
        }
    }

    private void send_device(){
        SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("device_name", device_name);
        editor.commit();

        RequestBody formBody = new FormBody.Builder()
                .add("mb_id", loginID)
                .add("device_name", device_name)
                .build();

        send_get_data("device_send", QuickstartPreferences.DEVICE_URL, formBody, false);
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
    private void get_data_response(String op, String data) {
        if ( op.equals("device_send") ) {
            try {
                JSONObject jsonObj = new JSONObject(data);
                JSONArray responsJson = jsonObj.getJSONArray("result");
                JSONObject c = responsJson.getJSONObject(0);
                Log.d("msg", "msg : " + c.getString("msg"));
                if ( c.getString("code").equals("1") ) {
                    String msg = c.getString("msg");
                    Log.d("Service", "device_send : " + msg);
                    finish();
                } else {
                    Log.d("msg", "msg : no action");
                    finish();
                }
            } catch (JSONException e) {
            }
        }
    }

    private void close_mydevice(){
        finish();
        overridePendingTransition(R.anim.scale_up, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        close_mydevice();
    }
}
