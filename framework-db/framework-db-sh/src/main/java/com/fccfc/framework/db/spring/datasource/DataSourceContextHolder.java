/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.db.spring.datasource;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.fccfc.framework.dao.datasource <br>
 */
public class DataSourceContextHolder {
    
    /**
     * contextHolder
     */
    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER;

    static {
        CONTEXT_HOLDER = new ThreadLocal<DataSourceType>();
    }

    /**
     * Description: 设置数据源名称<br>
     * 
     * @author 王伟 <br>
     * @param dataSourceType <br>
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    /**
     * Description: 获取数据源名称<br>
     * 
     * @author 王伟 <br>
     * @return <br>
     */
    public static DataSourceType getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟 <br> <br>
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

}
