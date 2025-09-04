/**
 * 
 */
package com.hbasesoft.framework.db.core;

import com.alibaba.fastjson2.JSON;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.core.bean <br>
 */
public abstract class BaseEntity implements IBaseEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 540091732380370744L;

    /**
     * toString
     * 
     * @see java.lang.Object#toString()
     * @return <br>
     */
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
