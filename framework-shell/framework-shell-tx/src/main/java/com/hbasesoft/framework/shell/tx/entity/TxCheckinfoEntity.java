/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.shell.tx.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.hbasesoft.framework.shell.core.vo.AbstractVo;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> T_TX_CHECKINFO的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2020年02月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Table("t_tx_check_info")
@Getter
@Setter
public class TxCheckinfoEntity extends AbstractVo {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
    private String id;

    @PrimaryKeyColumn(name = "mark", ordinal = 2, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
    /** mark */
    private String mark;

    /** result */
    private String result;

    /** next_retry_time */
    private java.util.Date createTime;

}
