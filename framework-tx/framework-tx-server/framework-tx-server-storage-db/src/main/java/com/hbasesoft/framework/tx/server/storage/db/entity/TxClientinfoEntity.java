/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

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
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public byte[] getArgs() {
        return this.args;
    }

    public void setArgs(byte[] args) {
        this.args = args;
    }

    public Integer getMaxRetryTimes() {
        return this.maxRetryTimes;
    }

    public void setMaxRetryTimes(Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public String getRetryConfigs() {
        return this.retryConfigs;
    }

    public void setRetryConfigs(String retryConfigs) {
        this.retryConfigs = retryConfigs;
    }

    public java.util.Date getNextRetryTime() {
        return this.nextRetryTime;
    }

    public void setNextRetryTime(java.util.Date nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    public Integer getCurrentRetryTimes() {
        return currentRetryTimes;
    }

    public void setCurrentRetryTimes(Integer currentRetryTimes) {
        this.currentRetryTimes = currentRetryTimes;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

}
