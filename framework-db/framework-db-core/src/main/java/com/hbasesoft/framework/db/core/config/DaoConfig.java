/**
 * 
 */
package com.hbasesoft.framework.db.core.config;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.dao.config <br>
 */
@Getter
@Setter
public class DaoConfig {

    /** 数据库类型 */
    private String dbType;

    /** 基础dao的类型 */
    private Class<?> baseDaoType;

    /** 结果集类型转换回调函数 */
    private Class<?> callBackType;

    public Class<?> getCallBackType() {
        return callBackType;
    };
}
