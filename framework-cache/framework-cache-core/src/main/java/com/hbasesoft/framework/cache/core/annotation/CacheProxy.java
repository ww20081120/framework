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
 * <Description> 缓存代理 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.annotation <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CacheProxy {

    /**
     * Description: 代理对象的名称<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String name() default GlobalConstants.BLANK;

    /**
     * Description: Cache超时时间<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    int expireTime() default 0;

    /**
     * Description: 代理方法的配置 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    CacheMethodConfig[] value() default {};
}
