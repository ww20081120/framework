/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core <br>
 */
public interface FlowComponentInterceptor {

    default int order() {
        return 0;
    }

    default boolean before(FlowBean flowBean, FlowContext flowContext) {
        return true;
    }

    default void after(FlowBean flowBean, FlowContext flowContext) {
    }

    default void error(Exception e, FlowBean flowBean, FlowContext flowContext) {
    }

}
