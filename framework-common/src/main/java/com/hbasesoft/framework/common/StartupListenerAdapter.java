/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import org.springframework.context.ApplicationContext;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class StartupListenerAdapter implements StartupListener {

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.LAST;
    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @throws FrameworkException <br>
     */
    @Override
    public void init() throws FrameworkException {
    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @throws FrameworkException <br>
     */
    @Override
    public void complete(ApplicationContext context) throws FrameworkException {
    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br> <br>
     */
    @Override
    public void destory() {
    }

}
