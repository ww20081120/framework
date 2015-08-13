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
     * serialVersionUID
     */
    private static final long serialVersionUID = -4716201082587162232L;

    /**
     * indexPosition
     */
    private int indexPosition = -1;

    /**
     * sizePosition
     */
    private int sizePosition = -1;

    /**
     * callBackPosition
     */
    private int callBackPosition = -1;

    /**
     * paramNames
     */
    private String[] paramNames;

    /**
     * beanType
     */
    private Class<?> beanType;

    /**
     * returnType
     */
    private Class<?> returnType;

    /**
     * dbId
     */
    private String dbId;

    /**
     * ParamMetadata
     */
    public ParamMetadata() {
        paramNames = new String[1];
    }

    /**
     * ParamMetadata
     * 
     * @param size <br>
     */
    public ParamMetadata(int size) {
        paramNames = new String[size];
    }

    /**
     * getIndexPosition
     * 
     * @return <br>
     */
    public int getIndexPosition() {
        return indexPosition;
    }

    /**
     * setIndexPosition
     * 
     * @param indexPosition <br>
     */
    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    /**
     * getSizePosition
     * 
     * @return the sizePosition
     */
    public int getSizePosition() {
        return sizePosition;
    }

    /**
     * setSizePosition
     * 
     * @param sizePosition <br>
     */
    public void setSizePosition(int sizePosition) {
        this.sizePosition = sizePosition;
    }

    /**
     * getParamNames
     * 
     * @return the paramNames <br>
     */
    public String[] getParamNames() {
        return paramNames;
    }

    /**
     * setParamName
     * 
     * @param index <br>
     * @param name <br>
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

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }

}
