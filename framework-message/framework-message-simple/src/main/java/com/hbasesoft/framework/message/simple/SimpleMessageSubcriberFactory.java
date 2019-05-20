/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.simple;

import com.hbasesoft.framework.message.core.MessageSubcriberFactory;
import com.hbasesoft.framework.message.core.MessageSubscriber;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月18日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.simple <br>
 */
public class SimpleMessageSubcriberFactory implements MessageSubcriberFactory {

    public static final String SIMPLE = "SIMPLE";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return SIMPLE;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param broadcast
     * @param subscriber <br>
     */
    @Override
    public void registSubscriber(String channel, boolean broadcast, MessageSubscriber subscriber) {
        EventManager.getInstance().regist(channel, broadcast, subscriber);
    }

}
