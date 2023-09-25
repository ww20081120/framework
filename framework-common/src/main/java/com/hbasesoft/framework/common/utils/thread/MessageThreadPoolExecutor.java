/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.thread;

/**
 * <Description> 消息线程处理工具<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年12月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.util <br>
 */
public final class MessageThreadPoolExecutor {

    /**
     * Description: 通过线程池来处理消息<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param message
     * @throws InterruptedException <br>
     */
    public static void execute(final String channel, final Runnable message) {
        Thread.ofVirtual().name(channel + Thread.currentThread().threadId()).start(message);
    }

}
