/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat;

/**
 * <Description> <br>
 * 
 * @author 查思玮<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年4月18日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.wechat <br>
 */
public interface ErrorCodeDef extends com.hbasesoft.framework.common.ErrorCodeDef {

    /************** wechat 专用参数 **************************/

    /** appId不能为空 */
    int APPID_NULL = 30001;
    
    /** redirect_uri不能为空 */
    int REDICECT_URI_NULL = 30002;
    
    /** response_type不能为空 */
    int RESPONSE_TYPE_NULL = 30003;
    
    /** scope不能为空 */
    int SCOPE_NULL = 30004;
    
    /** state不能为空 */
    int STATE_NULL = 30005;
    
    /** code不能为空 */
    int CODE_NULL = 30005;
    
    /** secret不能为空 */
    int SECTRET_NULL = 30006;
    
    /** access_token不能为空 */
    int ACCESS_TOKEN_NULL = 30007;
    
    /** redirect_uri 参数错误 */
    int REDIRECT_URI_ERROR = 30008;
    
    /** 退费HASH错误 */
    int REFUND_HASH_ERROR = 50011;

    /** APPID、AppSecret错误 */
    int APPID_SECRET_ERROR = 30009;
    
    /** ACCESS_TOKEN错误 */
    int ACCESS_TOKEN_ERROR = 30010;
    
    /** 永久二维码达到上限错误 */
    int QRCODE_MAX_ERROR = 45029;
    
    /** 退费错误 */
    int REFUND_ERROR = 50007;
    
}
