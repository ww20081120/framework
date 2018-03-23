/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.annotation;

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
 * @CreateDate 2018年3月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.annotation <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DulplicateLock {

    /**
     * Description: 锁名称<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String value();

    /**
     * Description: 附属关键字<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String key() default GlobalConstants.BLANK;

    /**
     * Description: 锁过期时间<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    int expireTime() default 10;
}
