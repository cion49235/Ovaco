package kr.co.inno.autocash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import reina.coffee.ngotronghoangpo.R;


/**
 * Created by byapps on 2016. 3. 16..
 */
public class NotifyConfirmActivity extends Activity {

    private Context context;
    private Dialog dialog;
    private String msg;
    private EditText notifyText;

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_notify_confirm);
        context = this;

        Intent i = getIntent();
        msg = i.getStringExtra("msg");

        notifyText = (EditText)findViewById(R.id.notifyText);
        notifyText.setFocusable(false);
        notifyText.setClickable(false);
        notifyText.setText(msg);

        dialog = new Dialog(context, R.style.NewDialog);
        View v = LayoutInflater.from(context).inflate(R.layout.progress_circle,null);
        dialog.setContentView(v);

        Button notifyConfirm = (Button)findViewById(R.id.notifyConfirm);
        notifyConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                close_notify_confirm();
            }
        });
    }

    private void close_notify_confirm(){
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
        close_notify_confirm();
    }
}
