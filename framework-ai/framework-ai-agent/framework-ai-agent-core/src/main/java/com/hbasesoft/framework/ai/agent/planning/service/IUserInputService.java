/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.planning.service;

import java.util.Map;

import com.hbasesoft.framework.ai.agent.planning.model.vo.UserInputWaitState;
import com.hbasesoft.framework.ai.agent.tool.fromInput.FormInputTool;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.planning.service <br>
 */
public interface IUserInputService {

    /**
     * Store form input tool
     * 
     * @param planId Plan ID
     * @param tool Form input tool
     */
    void storeFormInputTool(String planId, FormInputTool tool);

    /**
     * Get form input tool
     * 
     * @param planId Plan ID
     * @return Form input tool
     */
    FormInputTool getFormInputTool(String planId);

    /**
     * Remove form input tool
     * 
     * @param planId Plan ID
     */
    void removeFormInputTool(String planId);

    /**
     * Create user input waiting state
     * 
     * @param planId Plan ID
     * @param message Message
     * @param formInputTool Form input tool
     * @return User input waiting state
     */
    UserInputWaitState createUserInputWaitState(String planId, String message, FormInputTool formInputTool);

    /**
     * Get waiting state
     * 
     * @param planId Plan ID
     * @return User input waiting state
     */
    UserInputWaitState getWaitState(String planId);

    /**
     * Submit user input
     * 
     * @param planId Plan ID
     * @param inputs Input data
     * @return Whether submission was successful
     */
    boolean submitUserInputs(String planId, Map<String, String> inputs);
}
