package kr.co.inno.autocash.cms;

/**
 * Created by leekh on 2017. 1. 6..
 */

public class BaseModel {
    public String code;
    public String msg;
    public String url;

    @Override
    public String toString() {
        return "BaseModel{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
