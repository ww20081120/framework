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
 * @CreateDate 2016年8月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.wx <br>
 */
public interface WebConstant {

    /** session 中存储的ticket */
    String SESSION_TICKET = "__SESSION_TICKET";

    /** cookie 中存储的ticket */
    String COOKIE_TICKET = "__ticket";

    String SESSION_USER_INFO = "__WEB_SESSION_USER_INFO";

    /** 验证码 */
    String USER_VERIFICATION_CODE = "USER_VERIFICATION_CODE";

    /** 上次访问的地址 */
    String LAST_ACCESS_URL = "url.last_access";

    /** 登录页面地址 */
    String LOGIN_URI = "/user/to/login";

    /** 户号绑定页面地址 */
    String BIND_URI = "/user/to/gasNew";

    /** 燃气缴费 户号绑定页面地址 */
    String R_BIND_URL = "/user/to/r_bindsubs";

    /** 缴费首页 */
    String CHARGE_INDEX_URL = "/charge/to/chargeIndex";

    String PAY_URL = "/charge/to/chargeIndex";

    String OATH_CALLBACK_URI = "/oath2callback";

    String OATH_PAY_OATH_URL = "/charge/oath2callback";

    /** 页面js全局变量 */
    String JS_PARAMS = "js_global_parameters";
    
    String MEMBER_OATH_CALLBACK_URI = "member/oath2callback";

}
