/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.core;

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
public interface FlowComponent {

    /**
     * Description: 方法处理<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @throws Exception <br>
     */
    boolean process(FlowBean flowBean, FlowContext flowContext) throws Exception;
}
