/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.entity;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * <Description> T_TX_CLIENTINFO的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2020年02月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "T_TX_CLIENTINFO")
public class TxClientinfoEntity extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** id */
    @Id
    @Column(name = "id")
    private String id;

    /** mark */
    @Column(name = "mark")
    private String mark;

    /** context */
    @Column(name = "context")
    private String context;

    /** args */
    @Column(name = "args")
    private byte[] args;

    /** max_retry_times */
    @Column(name = "max_retry_times")
    private Integer maxRetryTimes;

    /** current_retry_times */
    @Column(name = "current_retry_times")
    private Integer currentRetryTimes;

    /** retry_configs */
    @Column(name = "retry_configs")
    private String retryConfigs;

    /** next_retry_time */
    @Column(name = "next_retry_time")
    private java.util.Date nextRetryTime;

    /** client_info */
    @Column(name = "client_info")
    private String clientInfo;

    /** next_retry_time */
    @Column(name = "create_time")
    private java.util.Date createTime;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getId() {
        return this.id;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getMark() {
        return this.mark;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param mark <br>
     */
    public void setMark(final String mark) {
        this.mark = mark;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getContext() {
        return this.context;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    public void setContext(final String context) {
        this.context = context;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public byte[] getArgs() {
        return this.args;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args <br>
     */
    public void setArgs(final byte[] args) {
        this.args = args;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Integer getMaxRetryTimes() {
        return this.maxRetryTimes;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param maxRetryTimes <br>
     */
    public void setMaxRetryTimes(final Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getRetryConfigs() {
        return this.retryConfigs;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param retryConfigs <br>
     */
    public void setRetryConfigs(final String retryConfigs) {
        this.retryConfigs = retryConfigs;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public java.util.Date getNextRetryTime() {
        return this.nextRetryTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nextRetryTime <br>
     */
    public void setNextRetryTime(final java.util.Date nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Integer getCurrentRetryTimes() {
        return currentRetryTimes;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param currentRetryTimes <br>
     */
    public void setCurrentRetryTimes(final Integer currentRetryTimes) {
        this.currentRetryTimes = currentRetryTimes;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getClientInfo() {
        return clientInfo;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    public void setClientInfo(final String clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public java.util.Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param createTime <br>
     */
    public void setCreateTime(final java.util.Date createTime) {
        this.createTime = createTime;
    }

}
