package reina.coffee.ngotronghoangpo;


import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import kr.co.inno.autocash.AutoLayoutGoogleActivity;
import kr.co.inno.autocash.cms.BaseModel;
import kr.co.inno.autocash.cms.BaseResponse;
import kr.co.inno.autocash.cms.JsonClient;
import kr.co.inno.autocash.cms.MemberLogin;

import reina.coffee.ngotronghoangpo.utils.PreferenceUtil;

import static android.os.Build.VERSION.SDK_INT;


public class IntroActivity extends Activity{
	public Handler handler;
	public Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(reina.coffee.ngotronghoangpo.R.layout.activity_intro);
        context = this;
		SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
		final  String loginID = prefs.getString("loginID", "");
		String googlePasswd = prefs.getString("googlePasswd", "");
		/*if(!TextUtils.isEmpty(googlePasswd)){
			handler = new Handler();
			handler.postDelayed(runnable, 2000);
		}else{
			if (SDK_INT >= Build.VERSION_CODES.M){
				checkPermission();
			}else{
				check_google_account();
			}
		}*/
		client = JsonClient.getInstance(this);
		handler = new Handler();
		handler.postDelayed(runnable, 0);
//		version_check();
		app_url();
    }

	int versionCode;
	private JsonClient client;
	public String m_mac;
	private void version_check(){
		if (SDK_INT > Build.VERSION_CODES.LOLLIPOP){
			m_mac = getMacAddress_v6();
		}else{
			m_mac = getMACAddress("wlan0");
		}
		PackageInfo pi=null;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
		} catch (NullPointerException e){
		} catch (Exception e){
		}
		client.init(reina.coffee.ngotronghoangpo.R.string.version_check, m_mac);
		client.post(new BaseResponse<MemberLogin>(this) {
			@Override
			public void onResponse(MemberLogin response) {
				// 시스템에러
				if (!response.code.equals("000")) {
					Toast.makeText(IntroActivity.this, response.msg, Toast.LENGTH_LONG).show();
					return;
				}
				Log.i("dsu", "서버버전 : " + response.app_version);
				if ( (versionCode < Integer.parseInt(response.app_version)) && (versionCode > 0) ) {
					new android.app.AlertDialog.Builder(context)
							.setTitle(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_03))
//							.setIcon(R.mipmap.icon128)
							.setMessage(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_04))
							.setCancelable(false)
							.setPositiveButton(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_05), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_06)));
									startActivity(intent);
									//moveTaskToBack(true);
									finish();
								}
							})
							.setNegativeButton(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_07), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							})
							.create().show();
				} else {
					handler = new Handler();
					handler.postDelayed(runnable, 2000);
				}
			}
		});
	}

	private void app_url(){
		String adver_id = context.getString(reina.coffee.ngotronghoangpo.R.string.adver_id);
		client.init(reina.coffee.ngotronghoangpo.R.string.app_url, m_mac, Build.VERSION.RELEASE, adver_id);
		client.post(new BaseResponse<BaseModel>(this) {
			@Override
			public void onResponse(BaseModel response) {
				// 시스템에러
				if (!response.code.equals("000")) {
					Toast.makeText(IntroActivity.this, response.msg, Toast.LENGTH_LONG).show();
					return;
				}
				String url = response.url;
				PreferenceUtil.setStringSharedData(context,PreferenceUtil.PREF_URL, url);
			}
		});
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

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSION_REQUEST_STORAGE:
				if ( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
					check_google_account();
				} else {
					finish();
				}
				break;
		}
	}

	private final int MY_PERMISSION_REQUEST_STORAGE = 100;
	private void checkPermission() {
		if ( SDK_INT >= Build.VERSION_CODES.M ) {
			if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_PHONE_STATE)) {
					// Explain to the user why we need to write the permission.
					Toast.makeText(this, context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_12), Toast.LENGTH_SHORT).show();
				}
				requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.GET_ACCOUNTS}, MY_PERMISSION_REQUEST_STORAGE);
			} else {
				check_google_account();
			}
		}else{
			Intent intent = new Intent(context, AutoLayoutGoogleActivity.class);
			intent.putExtra("googleType", "2");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
		final  String loginID = prefs.getString("loginID", "");
		String googlePasswd = prefs.getString("googlePasswd", "");
		/*if(!TextUtils.isEmpty(googlePasswd)){
			handler = new Handler();
			handler.postDelayed(runnable, 2000);
		}else{
			if (SDK_INT >= Build.VERSION_CODES.M){
				checkPermission();
			}else{
				check_google_account();
			}
		}*/
	}

	public void check_google_account(){
		try{
			Account[] accounts = AccountManager.get(this).getAccounts();
			String possibleEmail = "";
			for (Account account : accounts) {
				// TODO: Check possibleEmail against an email regex or treat
				// account.name as an email address only for certain account.type values.
				Log.i("dsu", "account_type : " + account.type);
				if (account.type.equals("com.google")) {        //이러면 구글 계정 구분 가능
					possibleEmail = account.name;
					Log.i("dsu", "account.name : " + possibleEmail);
				}
			}
			new android.app.AlertDialog.Builder(context)
//					.setIcon(R.mipmap.icon128)
					.setCancelable(false)
					.setTitle(possibleEmail)
					.setMessage(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_09 ) + " " + possibleEmail + context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_13))
					.setPositiveButton(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_10), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							Intent intent = new Intent(context, AutoLayoutGoogleActivity.class);
							intent.putExtra("googleType", "2");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							dialogInterface.dismiss();
						}
					})
					.setNegativeButton(context.getString(reina.coffee.ngotronghoangpo.R.string.introAcitivty_11), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
							finish();
						}
					})
					.create().show();
		}catch (Exception e){
		}
	}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(handler != null){
    		handler.removeCallbacks(runnable);
    	}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
			//fade_animation
//			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		}
	};
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(handler != null) handler.removeCallbacks(runnable);
		finish();
	}
}
