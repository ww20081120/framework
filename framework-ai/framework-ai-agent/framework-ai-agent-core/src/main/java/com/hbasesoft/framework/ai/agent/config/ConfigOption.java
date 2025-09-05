/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.config <br>
 */

/**
 * Configuration option annotation
 * <p>
 * Used to define options for dropdown boxes, radio buttons, etc.
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigOption {

    /**
     * Option value
     */
    String value();

    /**
     * Option label
     * <p>
     * Supports internationalization key format: config.option.{group}.{subGroup}.{key}.{value}
     */
    String label() default "";

    /**
     * Option description
     * <p>
     * Supports internationalization key format: config.option.desc.{group}.{subGroup}.{key}.{value}
     */
    String description() default "";

    /**
     * Option icon (optional)
     */
    String icon() default "";

    /**
     * Whether option is disabled
     */
    boolean disabled() default false;

}
