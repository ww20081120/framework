/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hbasesoft.framework.ai.jmanus.config.model.enums.ConfigInputType;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.config <br>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {

	/**
	 * Top-level group
	 */
	String group();

	/**
	 * Secondary group
	 */
	String subGroup();

	/**
	 * Configuration item key
	 */
	String key();

	/**
	 * Configuration item YAML full path, used to find specific configuration items as
	 * default values in the database
	 */
	String path();

	/**
	 * Configuration item description
	 * <p>
	 * Supports internationalization key format: config.desc.{group}.{subGroup}.{key}
	 */
	String description() default "";

	/**
	 * Configuration item default value
	 */
	String defaultValue() default "";

	/**
	 * Configuration item input type
	 * <p>
	 * Default is text input box
	 */
	ConfigInputType inputType() default ConfigInputType.TEXT;

	/**
	 * Dropdown box options
	 * <p>
	 * Only effective when inputType = SELECT
	 */
	ConfigOption[] options() default {};

}
