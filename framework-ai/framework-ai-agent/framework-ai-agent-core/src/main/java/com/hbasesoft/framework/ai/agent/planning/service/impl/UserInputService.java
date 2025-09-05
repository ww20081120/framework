/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.planning.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.agent.planning.model.vo.UserInputWaitState;
import com.hbasesoft.framework.ai.agent.planning.service.IUserInputService;
import com.hbasesoft.framework.ai.agent.tool.fromInput.FormInputTool;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.planning.service.impl <br>
 */

@Service
public class UserInputService implements IUserInputService {

    private final ConcurrentHashMap<String, FormInputTool> formInputToolMap = new ConcurrentHashMap<>();

    public void storeFormInputTool(String planId, FormInputTool tool) {
        formInputToolMap.put(planId, tool);
    }

    public FormInputTool getFormInputTool(String planId) {
        return formInputToolMap.get(planId);
    }

    public void removeFormInputTool(String planId) {
        formInputToolMap.remove(planId);
    }

    public UserInputWaitState createUserInputWaitState(String planId, String message, FormInputTool formInputTool) {
        UserInputWaitState waitState = new UserInputWaitState(planId, message, true);
        if (formInputTool != null) {
            // Assume FormInputTool has methods getFormDescription() and getFormInputs()
            // to get form information
            // This requires FormInputTool class to support these methods, or other ways
            // to get this information
            // This is indicative code, specific implementation depends on the actual
            // structure of FormInputTool
            FormInputTool.UserFormInput latestFormInput = formInputTool.getLatestUserFormInput();
            if (latestFormInput != null) {
                waitState.setFormDescription(latestFormInput.getDescription());
                if (latestFormInput.getInputs() != null) {
                    List<Map<String, String>> formInputsForState = latestFormInput.getInputs().stream()
                        .map(inputItem -> {
                            Map<String, String> inputMap = new HashMap<>();
                            inputMap.put("label", inputItem.getLabel());
                            inputMap.put("value", inputItem.getValue() != null ? inputItem.getValue() : "");
                            if (inputItem.getName() != null) {
                                inputMap.put("name", inputItem.getName());
                            }
                            if (inputItem.getType() != null) {
                                inputMap.put("type", inputItem.getType());
                            }
                            if (inputItem.getPlaceholder() != null) {
                                inputMap.put("placeholder", inputItem.getPlaceholder());
                            }
                            if (inputItem.getRequired() != null) {
                                inputMap.put("required", inputItem.getRequired().toString());
                            }
                            if (inputItem.getOptions() != null && !inputItem.getOptions().isEmpty()) {
                                inputMap.put("options", String.join(",", inputItem.getOptions()));
                            }

                            return inputMap;
                        }).collect(Collectors.toList());
                    waitState.setFormInputs(formInputsForState);
                }
            }
        }
        return waitState;
    }

    public UserInputWaitState getWaitState(String planId) {
        FormInputTool tool = getFormInputTool(planId);
        if (tool != null && tool.getInputState() == FormInputTool.InputState.AWAITING_USER_INPUT) { // Corrected
            // to
            // use
            // getInputState
            // and
            // InputState
            // Assuming a default message or retrieve from tool if available
            return createUserInputWaitState(planId, "Awaiting user input.", tool);
        }
        return null; // Or a UserInputWaitState with waiting=false
    }

    public boolean submitUserInputs(String planId, Map<String, String> inputs) { // Changed
        // to
        // return
        // boolean
        FormInputTool formInputTool = getFormInputTool(planId);
        if (formInputTool != null && formInputTool.getInputState() == FormInputTool.InputState.AWAITING_USER_INPUT) { // Corrected
            // to
            // use
            // getInputState
            // and
            // InputState
            List<FormInputTool.InputItem> inputItems = inputs.entrySet().stream().map(entry -> {
                return new FormInputTool.InputItem(entry.getKey(), entry.getValue());
            }).collect(Collectors.toList());

            formInputTool.setUserFormInputValues(inputItems);
            formInputTool.markUserInputReceived();
            return true;
        }
        else {
            if (formInputTool == null) {
                throw new IllegalArgumentException("FormInputTool not found for planId: " + planId);
            }
            // If tool exists but not awaiting input
            return false;
        }
    }

}
