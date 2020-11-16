/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.cassandra.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> T_TX_CLIENTINFO的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2020年02月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Table("t_tx_clientinfo")
@Getter
@Setter
public class TxClientinfoEntity extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** id */
    @PrimaryKey
    private String id;

    /** mark */
    private String mark;

    /** context */
    private String context;

    /** args */
    private String args;

    /** max_retry_times */
    private Integer maxRetryTimes;

    /** current_retry_times */
    private Integer currentRetryTimes;

    /** retry_configs */
    private String retryConfigs;

    /** next_retry_time */
    private java.util.Date nextRetryTime;

    /** client_info */
    private String clientInfo;

    /** next_retry_time */
    private java.util.Date createTime;

}
