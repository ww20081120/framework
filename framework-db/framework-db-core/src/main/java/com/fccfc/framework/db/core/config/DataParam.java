/**
 * 
 */
package com.fccfc.framework.db.core.config;

import java.util.Map;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月26日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.dao.datasource <br>
 */
public class DataParam {
    private int pageIndex = -1;

    private int pageSize = -1;

    private Map<String, Object> paramMap;

    private Class<?> beanType;

    private Class<?> returnType;

    private Object callback;

    private String dbId;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Object getCallback() {
        return callback;
    }

    public void setCallback(Object callback) {
        this.callback = callback;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DataParam [pageIndex=" + pageIndex + ", pageSize=" + pageSize + ", paramMap=" + paramMap
            + ", beanType=" + beanType + ", returnType=" + returnType + ", callback=" + callback + ", dbId=" + dbId
            + "]";
    }
}
