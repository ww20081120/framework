/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;

import com.hbasesoft.framework.common.Bootstrap;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo <br>
 */
@TestConfiguration
public class GlobalConfig implements ApplicationListener<ContextRefreshedEvent> {

    static {
        Bootstrap.before();
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param event <br>
     */
    @Override
    public void onApplicationEvent(final @NonNull ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) { // 确保是根上下文初始化完成
            Bootstrap.after(event.getApplicationContext());
        }
    }

}
