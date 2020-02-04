/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.entity;

import javax.persistence.Column;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> T_TX_CHECKINFO的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2020年02月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
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

    public byte[] getResult() {
        return this.result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

}
