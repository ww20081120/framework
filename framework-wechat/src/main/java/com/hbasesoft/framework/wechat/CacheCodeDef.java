/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat;

import com.hbasesoft.framework.cache.core.CacheConstant;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.common <br>
 */
public interface CacheCodeDef extends CacheConstant {

    /** 客户端缓存时间 */
    int CLIENT_CACHE_TIME = 60;

    /** 用户信息缓存时间 10 分钟 */
    int USERINFO_SAVE_TIME = 60 * 10;

    /** 配置信息缓存一个小时 */
    int CONFIG_SAVE_TIME = 60 * 60;

    /** 开放API信息缓存2个小时 */
    int OPEN_API_SAVE_TIME = 2 * 60 * 60;

    /** 最大缓存时间 */
    int MAX_CACHE_TIME = 60 * 60 * 24;
    
    /** 缓存一个月 */
    int MONTH_CACHE_TIME = 60 * 60 * 24 * 30;

    /** access token */
    String OPEN_API_ACCESS_TOKEN = "openapi.access_token";

    /** 最后一次访问的时间戳 */
    String OPEN_API_LAST_ACCESS_TIME = "openapi.last_access_timestap";

    /** 根据客户证件查询 */
    String CUST_BY_CERT = "cust.cert_type_code";

    // /** 微信AccessToken */
    // String WX_ACCESS_TOKEN = "wechat.access_token";

    /** 微信JsApi ticket */
    String WX_JS_API_TICKET = "wechat.js_api_ticket";

    /** 微信AppId */
    String WX_ACCOUNT_INFO = "wechat.account_info";

    /** 微信JsApiTicket */
    String WX_JSAPI_TICKET = "wechat.JsApiTicket";

    /** 微信JsApiTicketTime */
    String WX_JSAPI_TICKET_TIME = "wechat.JsApiTicket_time";

    /** 微信JsApiTicket_error */
    String WX_JSAPI_TICKET_ERR_CODE = "wechat.JsApiTicket_err_code";

    /** 微信JsApiTicket_errormsg */
    String WX_JSAPI_TICKET_ERR_MSG = "wechat.JsApiTicket_err_msg";

    /** 获取token错误信息 */
    String WX_ACCESS_TOKEN_ERROR = "wechat.access_token_error";

    /** 扩展数据缓存 */
    String EXPAND_CONFIG_CACHE = "expand_config_cache";

    /** 第三方扩展插件 */
    String WX_OPENAPI_CACHE = "wechat.openapi_cache";

    /** 微信api第三方授权参数 */
    String WX_OAUTH_STATE_PARAM = "WECHAT.WX_OAUTH_STATE_PARAM";
    
    /**微信userid对应的appId*/
    String WX_USERID_APPID = "wechat.userid_appid";
    
    /**获取员工推广二维码URL缓存*/
    String WX_SPREAD_QC_CACHE = "wechat.spread_qc_cache";
    
    /**获取员工l临时推广二维码URL缓存*/
    String WX_TEMP_SPREAD_QC_CACHE = "wechat.temp_spread_qc_cache";

    /**获取会员员工l临时推广二维码URL缓存*/
    String WX_MEMBER_TEMP_SPREAD_QC_CACHE = "wechat.temp_member_spread_qc_cache";
    
    /**客服信息对象*/
    String WX_MESSAGE = "wechat.message";

    /**客服信息对象*/
    String WX_USER_EXISTENCE = "wechat.wx_user_existence";
}
