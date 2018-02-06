/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

/**
 * <Description> 流程组件<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.core <br>
 */
public interface FlowComponent<T> {

    /**
     * Description: 方法处理<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @throws Exception <br>
     */
    boolean process(T flowBean, FlowContext flowContext) throws Exception;

    /**
     * Description: 执行完成以后<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext
     * @param e
     * @throws Exception <br>
     */
    default void afterProcess(T flowBean, FlowContext flowContext, Exception e) throws Exception {
    }

}
