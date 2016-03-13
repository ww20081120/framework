/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.task;

import java.util.List;

import javax.annotation.Resource;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.api.MessageService;
import com.hbasesoft.framework.message.core.service.TaskService;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.hbasesoft.framework.message.task <br>
 */
public class ResendMessageTask {

    /**
     * logger
     */
    private static Logger logger = new Logger(ResendMessageTask.class);

    /**
     * messageService
     */
    @Resource
    private MessageService.Iface messageService;

    /**
     * resendService
     */
    @Resource
    private TaskService resendService;

    /**
     * Description: 重发<br>
     * 
     * @author 王伟 <br>
     * <br>
     */
    public void invoke() {
        logger.info("------------>开始执行发送消息的消息任务<-------------------");
        List<Long> messageBoxList = null;
        try {
            messageBoxList = resendService.selectResendMessage();
        }
        catch (ServiceException e) {
            logger.warn("查询准备发送的消息失败", e);
        }

        if (CommonUtil.isNotEmpty(messageBoxList)) {
            for (Long messageId : messageBoxList) {
                try {
                    messageService.resendMessage(messageId);
                }
                catch (Exception e) {
                    logger.warn("发送消息失败", e);
                }
            }
        }

        logger.info("------------>执行发送消息的消息任务结束<-------------------");
    }
}
