package com.bitop.otcapi.constant;

public interface RedisConstants {
    /**
     * 登录角色权限
     */
    public static final String LOGIN_USER_KEY = "login_user_key:";


    /**
     * 管理员权限
     */
    public static final String LOGIN_MENU_CODE = "login_menu_code:";

    /**
     * 短信配置
     */
    public static final String SMS_CONFIG_KEY = "sms_config_key:";

    /**
     * 取消订单次数 key
     */
    public static final String CANCEL_ORDER_COUNT_KEY = "cancel_order_count_key:";








    /**
     * Verification code
     */
    public static final String PHONE_REGISTER_SMS_KEY = "phone_register_sms_key:";//注册发送手机验证
    public static final String EMAIL_REGISTER_SMS_KEY = "email_register_sms_key:";//注册发送邮箱验证

    public static final String PHONE_RETRIEVE_PASSWORD_SMS_KEY = "phone_retrieve_password_sms_key:";//找回发送手机验证
    public static final String EMAIL_RETRIEVE_PASSWORD_SMS_KEY = "email_retrieve_password_sms_key:";//找回发送邮箱验证

    public static final String PHONE_BIND_INFO_SMS_KEY = "phone_bind_info_sms_key:";//绑定手机
    public static final String EMAIL_BIND_INFO_SMS_KEY = "email_bind_info_sms_key:";//绑定邮箱

    public static final String PHONE_RETRIEVE_SECURITY_PWD = "phone_retrieve_security_pwd:";//手机找回安全密码
    public static final String EMAIL_RETRIEVE_SECURITY_PWD = "email_retrieve_security_pwd:";//邮箱找回安全密码

}