/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年4月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.wechat <br>
 */
public interface WechatConstant {

    /** token缓存时间 */
    int TOKEN_CACHE_TIME = 60 * 60 * 2;

    /** token缓存时间 */
    int TOKEN_TIME = 60 * 60 * 1000;
    
    /**
     * 微信接口请求成功返回码
     */
    String SUCCESS = "0";

    /**
     * 错误代码
     */
    String FAIL_CODE = "1";

    /**
     * 获取access_token请求URL,get
     */
    String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";

    /**
     * 获取jsapi_ticket请求URL,get
     */
    String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={0}&type=jsapi";

    /**
     * 获取media下载请求
     */
    String MEDIA_DOWN_URL = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token={0}&media_id={1}";

    /**
     * 获取code_url,其他redirect_uri,需要urlencode,state 非必填字段,
     */
    String OAUTH2_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state={3}#wechat_redirect";

    /**
     * 根据code获取openid,oauth2 access_token URL
     */
    String OAUTH2_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";

    /**
     * 发送模板消息URL
     */
    String TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={0}";

    /**
     * 获取用户信息URL
     */
    String USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}&lang=zh_CN";

    /**
     * 微信的统一下单地址
     */
    String ORDER_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 退费接口
     */
    String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 不弹出授权页面，直接跳转，只能获取用户openid）
     */
    String scope_snsapi_base = "snsapi_base";

    /**
     * 弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
     */
    String scope_snsapi_userinfo = "snsapi_userinfo";

    /**
     * 获取用户增减数据
     */
    String get_user_summary = "https://api.weixin.qq.com/datacube/getusersummary?access_token={0}&begin_date={1}&end_date={2}";

    /**
     * 获取累计用户数据
     */
    String get_user_cumulate = "https://api.weixin.qq.com/datacube/getusercumulate?access_token={0}&begin_date={1}&end_date={2}";

    /** 请求参数以json格式提交过来 */
    String APPLICATION_JSON_UTF_8 = "application/json; charset=UTF-8";

    /**
     * 发送客户消息请求,access_token为必填
     */
    String CUSTOM_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token={0}";

    /** 需要微信第三方回复 */
    String WECHAT_OPENAPI_NEED_RESP = "WECHAT_OPENAPI_NEED_RESP";

    /** 不需要微信第三方回复 */
    String WECHAT_OPENAPI_UN_NEED_RESP = "WECHAT_OPENAPI_UN_NEED_RESP";

    /** 获取地理信息的行政区划代码 */
    String WEBSERVICE_LOCATION = "http://apis.map.qq.com/ws/geocoder/v1/?location={0}&key={1}";

    /** 微信下载对账单 */
    String WEIXIN_MCH_DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    
    /**
     * 获取永久推广二维码请求URL,get
     */
    String SPREAD_QRCODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token={0}";
}
