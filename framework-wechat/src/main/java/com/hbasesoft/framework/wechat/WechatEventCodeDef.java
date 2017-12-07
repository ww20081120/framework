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
public interface WechatEventCodeDef {

    /** 微信关注 */
    String WECHAT_SUBSCRIBER = "WECHAT_SUBSCRIBER";
    
    /** 微信推送事件 */
    String WECHAT_TEMPLATE_PUSH = "WECHAT_TEMPLATE_PUSH";
    
    /** 微信第三方推送事件 */
    String WECHAT_OPENAPI_PUSH = "WECHAT_OPENAPI_PUSH";
    
    /** 微信文件下载 */
    String WECHAT_FILE_UPLOAD = "WECHAT_FILE_UPLOAD";
    
    /** 微信回复*/
    String WECHAT_CALL = "WECHAT_CALL";
    
    /** 微信回复*/
    String WECHAT_EXPAND_CLASS_NAME = "WECHAT_EXPAND_CLASS_NAME";

    /** 在线客服回复 */
    String WECHAT_KF_ONLINE_REPLY = "WECHAT_KF_ONLINE_REPLY";
    
    /** 绑卡事件 */
    String BIND_CARD_PUSH = "BIND_CARD_PUSH";
    
    /** 微信扫描带参二维码*/
    String WECHAT_SCAN = "WECHAT_SCAN";
}
