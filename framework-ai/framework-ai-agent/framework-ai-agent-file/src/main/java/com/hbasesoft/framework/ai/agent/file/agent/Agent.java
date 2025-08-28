/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.file.agent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Agent注解，用于标识和配置智能代理<br>
 * 该注解用于标记智能代理类，并提供代理的基本配置信息
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.file.agent <br>
 */
@Target({ ElementType.TYPE })
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
	String description();

	/**
	 * 代理使用的模型
	 * 
	 * @return 模型名称
	 */
	String model() default "";

	/**
	 * 代理命名空间
	 * 
	 * @return 命名空间
	 */
	String namespace() default "default";

	/**
	 * 是否为内置代理
	 * 
	 * @return 是否为内置代理
	 */
	boolean builtIn() default false;

	/**
	 * 是否为内置代理（备用字段）
	 * 
	 * @return 是否为内置代理
	 */
	boolean isBuiltIn() default false;
}
