/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.entity;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;

/**
 * <Description> T_TX_CHECKINFO的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2020年02月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.BaseEntity.bean.BaseEntity <br>
 */

public class TxCheckinfoEntity extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** id */
    @Column(name = "id")
    private String id;

    /** mark */
    @Column(name = "mark")
    private String mark;

    /** result */
    @Column(name = "result")
    private byte[] result;

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
    public byte[] getResult() {
        return this.result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result <br>
     */
    public void setResult(final byte[] result) {
        this.result = result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return createTime <br>
     */
    public java.util.Date getCreateTime() {
        return createTime;
    }

    /**
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
