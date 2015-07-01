/**
 * 
 */
package com.fccfc.framework.db.core.config;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月27日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.dao.config <br>
 */
public class DaoConfig {

    /** 是否开启缓存 */
    private boolean cache = true;

    /** 数据库类型 */
    private String dbType;

    /** 基础dao的类型 */
    private Object baseDao;

    /** 结果集类型转换回调函数 */
    private Class<?> callBackType;

    /**
     * isCache
     * @return the cache
     */
    public boolean isCache() {
        return cache;
    }

    /**
     * setCache
     * @param cache the cache to set
     */
    public void setCache(boolean cache) {
        this.cache = cache;
    }

    /**
     * getDbType
     * @return the dbType
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * setDbType
     * @param dbType the dbType to set
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    /**
     * getBaseDao
     * @return the baseDao
     */
    public Object getBaseDao() {
        return baseDao;
    }

    /**
     * setBaseDao
     * @param baseDao the baseDao to set
     */
    public void setBaseDao(Object baseDao) {
        this.baseDao = baseDao;
    }

    public Class<?> getCallBackType() {
        return callBackType;
    }

    public void setCallBackType(Class<?> callBackType) {
        this.callBackType = callBackType;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param typeName <br>
     * @throws ClassNotFoundException <br>
     */
    public void setCallBackType(String typeName) throws ClassNotFoundException {
        this.callBackType = Class.forName(typeName);
    }
}
