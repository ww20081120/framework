/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * <Description> Agent注解，用于标识和配置智能代理 <br>
 * 该注解用于标记智能代理类，并提供代理的基本配置信息
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.core <br>
 */
@Target({
    ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Agent {

    /**
     * 代理名称
     * 
     * @return 代理名称
     */
    String name();

    /**
     * 代理描述
     * 
     * @return 代理描述
     */
    String description() default "";

    /**
     * Description: 系统提示词<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String systemPrompt() default "";

    /**
     * 代理使用的模型
     * 
     * @return 模型名称
     */
    String model() default "";

    /**
     * Description: 记忆<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String memery() default "";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String[] hooks() default {};

    /**
     * Description: 工具 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String[] tools() default {};
}
