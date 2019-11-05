/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import lombok.NoArgsConstructor;

/**
 * <Description> 动态数据源和事务管理类<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年5月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
@NoArgsConstructor
public final class DynamicDataSourceManager {

    public static final String DEFAULT_DATASOURCE_CODE = "master";

    private static ThreadLocal<String> dataSourceCodeHolder = new ThreadLocal<String>() {
        protected synchronized String initialValue() {
            return DEFAULT_DATASOURCE_CODE;
        }
    };

    public static String getDataSourceCode() {
        return dataSourceCodeHolder.get();
    }

    public static void setDataSourceCode(String code) {
        dataSourceCodeHolder.set(code);
        LoggerUtil.debug("datasource change to " + code);
    }
}
