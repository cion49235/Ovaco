package kr.co.inno.autocash;

/**
 * Created by byapps on 2016. 2. 5..
 */
public class QuickstartPreferences {

    public static final String APP_VERSION = "1.0";

    // 공통
    public static final String DOMAIN_URL = "http://automoney.co";

    // LoginActiviy.java
    public static final String LOGIN_URL = DOMAIN_URL + "/APP_API/login_send_v2.php";
    public static final String LOGOUT_URL = DOMAIN_URL + "/APP_API/logout_send.php";

    // AdvertActivity.java
    public static final String ADVERT_URL = DOMAIN_URL + "/reward/autocash_add.php";
    public static final String ADVERT_DETAIL_URL = DOMAIN_URL + "/reward/autocash_detail.php";

    // AdvertAutoActivity.java
    public static final String ADVERT_AUTO_URL = DOMAIN_URL + "/sub/advert_auto_list.php";

    // PartActivity.java
    public static final String PART_URL = DOMAIN_URL + "/sub/part_list.php";
    public static final String PART_DELETE_URL = DOMAIN_URL + "/sub/part_list_delete.php";

    // PrivacyActivity.java
    public static final String SERVICE_URL = DOMAIN_URL + "/sub/service.php";
    public static final String PRIVACY_URL = DOMAIN_URL + "/sub/privacy.php";

    // PasswordActivity.java
    public static final String LOST_FOUND_URL = DOMAIN_URL + "/APP_API/lost_found.php";

    // MemberPasswordActivity.java
    public static final String MEMBER_PASSWORD_URL = DOMAIN_URL + "/APP_API/member_password_send.php";

    // SettingActivity.java
    public static final String SETTING_SEND_URL = DOMAIN_URL + "/APP_API/setting_send.php";
    public static final String GET_SETTING_URL = DOMAIN_URL + "/APP_API/get_setting_data.php";

    // PointListActivity.java
    public static final String POINT_LIST_URL = DOMAIN_URL + "/sub/point_list.php";

    // NoticeActivity.java
    public static final String NOTICE_LIST_URL = DOMAIN_URL + "/sub/notice_list.php";

    // HelpActivity.java
    public static final String HELP_LIST_URL = DOMAIN_URL + "/sub/help_list.php";

    // FaqActivity.java
    public static final String FAQ_LIST_URL = DOMAIN_URL + "/sub/faq_list.php";

    // CompanyActivity.java
    public static final String COMPANY_URL = DOMAIN_URL + "/sub/company_write.php";

    // OneListActivity.java
    public static final String ONE_LIST_URL = DOMAIN_URL + "/sub/one_list.php";

    // OneListActivity.java
    public static final String ONE_URL = DOMAIN_URL + "/sub/one_write.php";

    // ShopListActivity.java
    public static final String SHOP_LIST_URL = DOMAIN_URL + "/sub/shop_list.php";

    // ShopBrandActivity.java
    public static final String SHOP_BRAND_LIST_URL = DOMAIN_URL + "/sub/shop_brand_list.php";

    // ShopCategoryActivity.java
    public static final String SHOP_CATEGORY_LIST_URL = DOMAIN_URL + "/sub/shop_category_list.php";

    // EventActivity.java
    public static final String EVENT_LIST_URL = DOMAIN_URL + "/sub/event_list.php";

    // CouponActivity.java
    public static final String COUPON_LIST_URL = DOMAIN_URL + "/sub/coupon_list.php";

    // CouponDetailActivity.java
    public static final String COUPON_DETAIL_URL = DOMAIN_URL + "/sub/coupon_detail.php";
    public static final String SHOP_CANCEL_URL = DOMAIN_URL + "/APP_API/shop_cancel_send.php";

    // PointListActivity.java
    public static final String GET_POINT_URL = DOMAIN_URL + "/APP_API/get_point_data.php";

    // JoinActivity.java
    public static final String JOIN_URL = DOMAIN_URL + "/APP_API/join_info_send_v2.php";
    public static final String MB_NICK_URL = DOMAIN_URL + "/APP_API/mb_nick_send.php";

    // MemberModifyActivity.java
    public static final String MODIFY_URL = DOMAIN_URL + "/APP_API/modify_send.php";
    public static final String GET_INFO_URL = DOMAIN_URL + "/APP_API/get_info_data.php";

    // LeaveActivity.java
    public static final String LEAVE_URL = DOMAIN_URL + "/APP_API/leave_send.php";

    // AdvertCategoryActivity.java
    public static final String GET_CATEGORY_DATA_URL = DOMAIN_URL + "/APP_API/get_category_data.php";

    // ShopDetailActivity.java
    public static final String GET_SHOP_DATA_URL = DOMAIN_URL + "/APP_API/get_shop_data.php";

    // ShopBuyActivity.java
    public static final String SHOP_AUTH_URL = DOMAIN_URL + "/APP_API/shop_auth_send.php";
    public static final String SHOP_BUY_URL = DOMAIN_URL + "/APP_API/shop_buy_send.php";

    // ShopRefundActivity.java
    public static final String GET_REFUND_DATA_URL = DOMAIN_URL + "/APP_API/get_refund_data.php";
    public static final String SHOP_REFUND_URL = DOMAIN_URL + "/APP_API/shop_refund_send.php";

    // SpalshActivity.java
    public static final String PUSH_URL = DOMAIN_URL + "/APP_API/push_send.php";
    public static final String GOOGLE_URL = DOMAIN_URL + "/APP_API/google_send.php";

    // PopupActivity.java
    public static final String GET_POPUP_DATA_URL = DOMAIN_URL + "/APP_API/get_popup_data.php";
    public static final String POPUP_URL = DOMAIN_URL + "/sub/popup_new.php";

    // AutoServiceActivity.java
    public static final String GET_AUTO_URL = DOMAIN_URL + "/APP_API/get_auto_data_v4.php";
    public static final String INSTALL_URL = DOMAIN_URL + "/APP_API/install_send.php";

    // AutoCompleteServiceActivity.java
    public static final String COMPLETE_URL = DOMAIN_URL + "/APP_API/complete_send_add_v4.php";

    public static final String PARSE_URL = DOMAIN_URL + "/APP_API/parse_send.php";

    // AutoLayoutGoogleDeviceActivity.java
    public static final String DEVICE_URL = DOMAIN_URL + "/APP_API/device_send.php";

    // TutorialActivity.java
    public static final String TUTORIAL_LIST_URL = DOMAIN_URL + "/sub/slide_list.php";

    public static final String URI_ENCODE="utf-8";

    public static final int TIME_OUT = 9000;
    public static final String REGISTRATION_READY = "registrationReady";
    public static final String REGISTRATION_GENERATING = "registrationGenerating";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String AES_256 = "aoszmffhwptaoszmffhwptmancloset0";
}
