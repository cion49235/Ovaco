package kr.co.inno.autocash.cms;

public class MemberLogin extends BaseModel {
    public String app_version;
    public String fg_exists;
    public String m_nickname;
    public String m_my_gender;
    public String m_other_gender;
    public String m_app_uid;
    public String am_fcm_token;
    public String ma_code;

    @Override
    public String toString() {
        return "MemberLogin{" +
                "fg_exists='" + fg_exists + '\'' +
                ", m_nickname='" + m_nickname + '\'' +
                ", m_my_gender='" + m_my_gender + '\'' +
                ", m_other_gender='" + m_other_gender + '\'' +
                ", m_app_uid='" + m_app_uid + '\'' +
                ", ma_code='" + ma_code + '\'' +
                ", am_fcm_token='" + am_fcm_token + '\'' +
                "} " + super.toString();
    }
}
