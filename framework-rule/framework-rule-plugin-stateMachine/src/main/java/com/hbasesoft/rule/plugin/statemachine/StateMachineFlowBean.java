/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.rule.plugin.statemachine;

import java.io.Serializable;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.rule.plugin.statemachine <br>
 */
public interface StateMachineFlowBean extends Serializable {

    String getAction();

    String getState();

    void setState(String state);
}
