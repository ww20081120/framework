/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core <br>
 */
public interface MessageSubcriberFactory {

    /**
     * Description: 注册监听者<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param subscriber <br>
     */
    void registSubscriber(String channel, MessageSubscriber subscriber);

}
