/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo.bean;

import com.hbasesoft.rule.plugin.statemachine.StateMachineFlowBean;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.demo.bean <br>
 */
@Getter
@Setter
public class TestStateMachineBean extends FlowBean implements StateMachineFlowBean {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1L;

    /** state */
    private String state;

    /** event */
    private String event;

}
