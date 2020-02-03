/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.consumer.rocketmq;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.tx.core.TxClientInfoFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 3, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.client.consumer.rocketmq <br>
 */
public class RocketMQClientInfoFactory implements TxClientInfoFactory {

    private final String topic = PropertyHolder.getProperty("tx.rocketmq.topic",
        PropertyHolder.getProperty("project.name"));

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getClientInfo() {
        return topic;
    }

}
