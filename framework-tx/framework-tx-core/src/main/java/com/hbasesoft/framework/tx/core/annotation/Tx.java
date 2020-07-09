/**
 * 
 */
package com.hbasesoft.framework.tx.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.dao.annotation <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD, ElementType.TYPE
})
public @interface Tx {

    /**
     * Description: 名称<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String name() default GlobalConstants.BLANK;

    /**
     * Description: 最大重试几次<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    int maxRetryTimes() default 5;

    /**
     * Description: 重试的配置，单位为分钟<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String retryConfigs() default "5,10,30,60,120,720";
}
