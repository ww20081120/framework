/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.delay;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.delay <br>
 */
public interface StepDelayMessageQueue {

    /**
     * Description: 获取检查队列 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    int getLevel();

    void add(DelayMessage delayMessage);

    DelayMessage remove(String msgId);

    void check();

}
