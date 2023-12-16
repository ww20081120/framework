/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core.annotation <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
@Documented
@Component
public @interface Job {

    /**
     * Description: 任务名称<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String name() default GlobalConstants.BLANK;

    /**
     * Description: 任务执行的表达式<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String cron();

    /**
     * Description: 分片参数：Beijing,Shanghai,Guangzhou<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String shardingParam() default GlobalConstants.BLANK;

    /**
     * Description: 是否启用<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String enable() default "true";

    /**
     * Description: 是否为流式操作<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    boolean streamingProcess() default false;
}
