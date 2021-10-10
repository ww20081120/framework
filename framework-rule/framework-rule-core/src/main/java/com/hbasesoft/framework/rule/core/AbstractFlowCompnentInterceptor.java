/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

import com.hbasesoft.framework.common.annotation.NoTransLog;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年11月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.core <br>
 */
@NoTransLog
public abstract class AbstractFlowCompnentInterceptor implements FlowComponentInterceptor {

    /** order */
    private int order = 0;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param o
     * @return <br>
     */
    @Override
    public int compareTo(final FlowComponentInterceptor o) {
        AbstractFlowCompnentInterceptor ao = (AbstractFlowCompnentInterceptor) o;
        return this.order - ao.order;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getOrder() {
        return order;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param order <br>
     */
    public void setOrder(final int order) {
        this.order = order;
    }

}
