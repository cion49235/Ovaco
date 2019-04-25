package kr.co.inno.autocash.cms;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import reina.coffee.ngotronghoangpo.R;


public class JsonClient {

    private static JsonClient current;
    private AsyncHttpClient client;
    private Context context;
    private String prefix;
    private String urlPath;
    private String fullUrl;
    private RequestParams requestParams;

    public static JsonClient getInstance(Context context) {
        if (current == null) {
            current = new JsonClient();
        }

        current.setContext(context);
        current.prefix = current.context.getString(R.string.url_prefix);
        return current;
    }

    private JsonClient() {
        this.client = new AsyncHttpClient();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void post(BaseResponse response) {
        client.post(this.fullUrl, requestParams, response);
    }

    public void post(BaseResponse2 response) {
        client.post(this.fullUrl, requestParams, response);
    }

    public void get(BaseResponse response) {
        client.get(this.fullUrl, requestParams, response);
    }

    public void init(String url, RequestParams params) {
        this.fullUrl = url;
        this.requestParams = params;
    }

    public void init(int url_id, Object... params) {
        // ex) /member/member_login.php?m_imei=%1$s&service_key=app_w
        String url = this.context.getString(url_id, params);

        init(url);
    }

    public void init2(int prefix_id, int url_id, Object... params) {
        // ex) /member/member_login.php?m_imei=%1$s&service_key=app_w
        String url = this.context.getString(prefix_id) + this.context.getString(url_id, params);

        init(url);
    }

    public void init(String url) {

        if (this.requestParams != null) {
            this.requestParams = null;
        }
        this.requestParams = new RequestParams();
        String urlParams = null;

        // URL에 ?가 있는지 검사
        int p = url.indexOf("?");

        if (p > -1) {
            this.urlPath = url.substring(0, p);
            urlParams = url.substring(p+1);
        } else {
            this.urlPath = url;
        }

        if ( this.urlPath.indexOf("http://") == 0 || this.urlPath.indexOf("https://") == 0 ) {
            this.fullUrl = this.urlPath;
        } else {
            this.fullUrl = this.prefix + this.urlPath;
        }

        if (urlParams != null) {
            String[] data = urlParams.split("&");
            for (int i=0; i<data.length; i++) {
                String[] temp = data[i].split("=");

                if (temp.length == 2) {
                    requestParams.put(temp[0], temp[1]);
                }
            }
        } // end if
    }
}
