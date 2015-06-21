/**
 * 
 */
package com.fccfc.framework.db.core.config;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.dao.metadata <br>
 */
public class ParamMetadata extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -4716201082587162232L;

    private int indexPosition = -1;

    private int sizePosition = -1;

    private int callBackPosition = -1;

    private String[] paramNames;

    private Class<?> beanType;

    private Class<?> returnType;

    private String dbId;

    public ParamMetadata() {
        paramNames = new String[1];
    }

    public ParamMetadata(int size) {
        paramNames = new String[size];
    }

    /**
     * @return the indexPosition
     */
    public int getIndexPosition() {
        return indexPosition;
    }

    /**
     * @param indexPosition the indexPosition to set
     */
    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    /**
     * @return the sizePosition
     */
    public int getSizePosition() {
        return sizePosition;
    }

    /**
     * @param sizePosition the sizePosition to set
     */
    public void setSizePosition(int sizePosition) {
        this.sizePosition = sizePosition;
    }

    /**
     * @return the paramNames
     */
    public String[] getParamNames() {
        return paramNames;
    }

    /**
     * @param paramNames the paramNames to set
     */
    public void setParamName(int index, String name) {
        this.paramNames[index] = name;
    }

    public int getCallBackPosition() {
        return callBackPosition;
    }

    public void setCallBackPosition(int callBackPosition) {
        this.callBackPosition = callBackPosition;
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

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }
}
