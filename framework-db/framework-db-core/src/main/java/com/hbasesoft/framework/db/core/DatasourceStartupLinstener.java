/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.db.core.utils.DataSourceUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月12日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
public class DatasourceStartupLinstener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.MIDDLE;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    public void complete(final ApplicationContext context) {
        DataSourceUtil.init();
    }
}
