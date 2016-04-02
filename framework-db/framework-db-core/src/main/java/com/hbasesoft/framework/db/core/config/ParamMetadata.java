/**
 * 
 */
package com.hbasesoft.framework.db.core.config;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.dao.metadata <br>
 */
public class ParamMetadata extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4716201082587162232L;

    /**
     * indexPosition
     */
    private Integer indexPosition = -1;

    /**
     * sizePosition
     */
    private Integer sizePosition = -1;

    /**
     * callBackPosition
     */
    private Integer callBackPosition = -1;

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
    public Integer getIndexPosition() {
        return indexPosition;
    }

    /**
     * setIndexPosition
     * 
     * @param indexPosition <br>
     */
    public void setIndexPosition(Integer indexPosition) {
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
    public void setSizePosition(Integer sizePosition) {
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
    public void setParamName(Integer index, String name) {
        this.paramNames[index] = name;
    }

    public int getCallBackPosition() {
        return callBackPosition;
    }

    public void setCallBackPosition(Integer callBackPosition) {
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
