/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

import java.io.Serializable;

import com.hbasesoft.framework.common.annotation.NoTransLog;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.core <br>
 */
@NoTransLog
public interface FlowComponentInterceptor extends Comparable<FlowComponentInterceptor> {

    default boolean before(Serializable flowBean, FlowContext flowContext) {
        return true;
    }

    default void after(Serializable flowBean, FlowContext flowContext) {
    }

    default void error(Exception e, Serializable flowBean, FlowContext flowContext) {
    }

}
