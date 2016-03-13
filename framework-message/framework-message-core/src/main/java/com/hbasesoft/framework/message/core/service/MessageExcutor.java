/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.service;

import java.util.List;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.message.api.Attachment;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.hbasesoft.framework.message.service <br>
 */
public interface MessageExcutor {

    /**
     * Description: 获取渠道标示 <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String getChannelId();

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param title <br>
     * @param content <br>
     * @param sender <br>
     * @param receiver <br>
     * @param attachments <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    String sendMessage(String title, String content, String sender, String[] receiver, List<Attachment> attachments)
        throws ServiceException;
}
