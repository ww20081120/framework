/**
 * 
 */
package com.hbasesoft.framework.db.core.config;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.dao.datasource <br>
 */
@Getter
@Setter
public class DataParam {

    /**
     * pageIndex
     */
    private Integer pageIndex = -1;

    /**
     * pageSize
     */
    private Integer pageSize = -1;

    /**
     * paramMap
     */
    private Map<String, Object> paramMap;

    /**
     * beanType
     */
    private Class<?> beanType;

    /**
     * returnType
     */
    private Class<?> returnType;

    /**
     * callback
     */
    private Object callback;

    /**
     * dbId
     */
    private String dbId;

    /**
     * toString
     * 
     * @see java.lang.Object#toString()
     * @return <br>
     */
    @Override
    public String toString() {
        return "DataParam [pageIndex=" + pageIndex + ", pageSize=" + pageSize + ", paramMap=" + paramMap + ", beanType="
            + beanType + ", returnType=" + returnType + ", callback=" + callback + ", dbId=" + dbId + "]";
    }
}
