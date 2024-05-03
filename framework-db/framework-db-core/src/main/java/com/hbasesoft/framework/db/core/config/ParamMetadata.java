/**
 * 
 */
package com.hbasesoft.framework.db.core.config;

import com.hbasesoft.framework.db.core.BaseDto;

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
public class ParamMetadata extends BaseDto {

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
    public ParamMetadata(final int size) {
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
    public void setIndexPosition(final Integer indexPosition) {
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
    public void setSizePosition(final Integer sizePosition) {
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
    public void setParamName(final Integer index, final String name) {
        this.paramNames[index] = name;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getCallBackPosition() {
        return callBackPosition;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param callBackPosition <br>
     */
    public void setCallBackPosition(final Integer callBackPosition) {
        this.callBackPosition = callBackPosition;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Class<?> getBeanType() {
        return beanType;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beanType <br>
     */
    public void setBeanType(final Class<?> beanType) {
        this.beanType = beanType;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param returnType <br>
     */
    public void setReturnType(final Class<?> returnType) {
        this.returnType = returnType;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getDbId() {
        return dbId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param dbId <br>
     */
    public void setDbId(final String dbId) {
        this.dbId = dbId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param paramNames <br>
     */
    public void setParamNames(final String[] paramNames) {
        this.paramNames = paramNames;
    }

}
