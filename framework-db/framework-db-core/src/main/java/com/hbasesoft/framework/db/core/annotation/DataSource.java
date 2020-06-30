/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db <br>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE, ElementType.METHOD
})
public @interface DataSource {

    String value();

    String enhanceDynamicDataSource() default "";
}
