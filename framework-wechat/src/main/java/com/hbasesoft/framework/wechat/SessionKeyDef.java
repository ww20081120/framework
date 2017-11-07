package com.hbasesoft.framework.wechat;

/**
 * Session存储 key
 */
public interface SessionKeyDef {

    /** 短信验证码 */
    String SESSION_SMS_VALIDATE = "sms.validate";

    /** session中存放的openid */
    String SESSION_OPEN_ID = "wx.openid";

    /** 上次访问的URL */
    String LAST_ACCESS_URL = "url.last_access";

    /** session 获取上期地址 */
    String SESSION_OPENID_LASTURL = "__SESSION_OPENID_LASTURL";

    String SESSION_ORG_CODE = "__SESSION_ORG_CODE";
    
    String SESSION_APP_ID = "__SESSION_APP_ID";
}
