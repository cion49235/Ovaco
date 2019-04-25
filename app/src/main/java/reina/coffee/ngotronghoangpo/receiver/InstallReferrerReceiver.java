package reina.coffee.ngotronghoangpo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igaworks.IgawReceiver;

import reina.coffee.ngotronghoangpo.utils.PreferenceUtil;


public class InstallReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //IGAW 구글 인스톨 리시버 등록
        IgawReceiver igawReceiver = new IgawReceiver();
        igawReceiver.onReceive(context, intent);
        try{
            String referrer = intent.getStringExtra("referrer");
            Log.i("dsu", "애드브릭스 래퍼러값 : " + referrer);//==>이걸쓰면됨
            if(referrer.indexOf("utm_source=") != -1){
                PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_REWARD_PARAM, "");
                Log.i("dsu", "애드브릭스 레퍼러값저장안함 : ");//==>이걸쓰면됨
            }else{
                PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_REWARD_PARAM, referrer);
                Log.i("dsu", "애드브릭스 레퍼러값저장함 : ");//==>이걸쓰면됨
            }
        }catch (NullPointerException e){
        }catch (Exception e){
        }
//        String referrer = "https://play.google.com/store/apps/details?id=quizmall.app.marketingis&referrer=aaaa_bbbb_cccc";
//        try{
//            String one = "";
//            String two = "";
//            String three = "";
//            String reward_param = "";
//            if(!StringUtil.isEmpty(referrer)){
//                String[] split_referrer = referrer.split("_");
//                /*if ( split_referrer.length > 0 && split_referrer[0] != null ) one = split_referrer[0];
//                if ( split_referrer.length > 1 && split_referrer[1] != null ) two = split_referrer[1];
//                if ( split_referrer.length > 2 && split_referrer[2] != null ) three = split_referrer[2]; //1111|2222|3333*/
//            }
//        }catch (NullPointerException e){
//        }catch (Exception e){
//        }
    }
}