/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.handler;

import java.util.Map;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.wechat.bean.AccountPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年6月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.wx.service <br>
 */
public interface WechatMessageHandler {

    String process(String msgId, String toUserName, AccountPojo entity, String content, Map<String, String> requestMap, String imagePath, String serverPath, String message)
        throws FrameworkException;
    
    void asynProcess(String msgId, String toUserName, AccountPojo entity, String content, Map<String, String> requestMap, String imagePath, String serverPath, String message)
            throws FrameworkException;
}
