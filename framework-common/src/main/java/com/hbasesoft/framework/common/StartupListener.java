/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import org.springframework.context.ApplicationContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.init <br>
 */
public interface StartupListener {

    default LoadOrder getOrder() {
        return LoadOrder.LAST;
    }

    default void init() {
    }

    default void complete(ApplicationContext context) {
    }

    default void destory() {
    }

    enum LoadOrder {
        FIRST, MIDDLE, LAST
    }

}
