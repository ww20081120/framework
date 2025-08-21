/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.fromInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.jmanus.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.fromInput <br>
 */

/**
 * LLM form input tool: supports multiple input items with labels and descriptions.
 */
public class FormInputTool extends AbstractBaseTool<FormInputTool.UserFormInput> {

	private final ObjectMapper objectMapper;

	private String getToolParameters() {
		return """
				{
				    "type": "object",
				    "properties": {
				        "description": {
				            "type": "string",
				            "description": "Description of the form and what information is being collected"
				        },
				        "inputs": {
				            "type": "array",
				            "items": {
				                "type": "object",
				                "properties": {
				                    "name": {
				                        "type": "string",
				                        "description": "Name/ID of the input field"
				                    },
				                    "label": {
				                        "type": "string",
				                        "description": "Display label for the input field"
				                    },
				                    "type": {
				                        "type": "string",
				                        "enum": ["text", "number", "email", "password", "textarea", "select", "checkbox", "radio"],
				                        "description": "Type of input field"
				                    },
				                    "required": {
				                        "type": "boolean",
				                        "description": "Whether this field is required"
				                    },
				                    "placeholder": {
				                        "type": "string",
				                        "description": "Placeholder text for the input field"
				                    },
				                    "options": {
				                        "type": "array",
				                        "items": {
				                            "type": "string"
				                        },
				                        "description": "Options for select, checkbox, or radio inputs"
				                    }
				                },
				                "required": ["name", "label", "type"]
				            },
				            "description": "Array of input field definitions"
				        }
				    },
				    "required": ["description", "inputs"]
				}
				""";
	}

	private String getToolDescription() {
		return """
				Create interactive forms to collect user input. This tool allows you to define form fields and collect structured data from users through a web interface.
				""";
	}

	private static final String LEGACY_PARAMETERS = """
			{
			  "type": "object",
			  "properties": {
			    "inputs": {
			      "type": "array",
			      "description": "List of input items, each containing label and value fields",
			      "items": {
			        "type": "object",
			        "properties": {
			          "label": { "type": "string", "description": "Input item label" },
			          "value": { "type": "string", "description": "Input content" }
			        },
			        "required": ["label"]
			      }
			    },
			    "description": {
			      "type": "string",
			      "description": "Instructions on how to fill these input items"
			    }
			  },
			  "required": [ "description"]
			}
			""";

	public static final String name = "form_input";

	private static final String LEGACY_DESCRIPTION = """
			Provides a labeled multi-input form tool.

			LLM can use this tool to let users submit 0 or more input items (each with label and content), along with filling instructions.
			Allows users to submit 0 input items.
			Suitable for scenarios requiring structured input and can also be used when the model needs to wait for user input before continuing.
			""";

	public OpenAiApi.FunctionTool getToolDefinition() {
		try {
			OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(getToolDescription(), name,
					getToolParameters());
			return new OpenAiApi.FunctionTool(function);
		}
		catch (Exception e) {
			LoggerUtil.warn("Failed to load prompt-based tool definition, using legacy configuration", e);
			OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(LEGACY_DESCRIPTION, name,
					LEGACY_PARAMETERS);
			return new OpenAiApi.FunctionTool(function);
		}
	}

	// Data structures:
	/**
	 * Form input item containing label and corresponding value.
	 */
	public static class InputItem {

		private String name;

		private String label;

		private String value;

		private String type;

		private Boolean required;

		private String placeholder;

		private List<String> options;

		public InputItem() {
		}

		public InputItem(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public InputItem(String name, String label, String type) {
			this.name = name;
			this.label = label;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Boolean getRequired() {
			return required;
		}

		public void setRequired(Boolean required) {
			this.required = required;
		}

		public String getPlaceholder() {
			return placeholder;
		}

		public void setPlaceholder(String placeholder) {
			this.placeholder = placeholder;
		}

		public List<String> getOptions() {
			return options;
		}

		public void setOptions(List<String> options) {
			this.options = options;
		}

	}

	/**
	 * User-submitted form data containing list of input items and description.
	 */
	public static class UserFormInput {

		private List<InputItem> inputs;

		private String description;

		public UserFormInput() {
		}

		public UserFormInput(List<InputItem> inputs, String description) {
			this.inputs = inputs;
			this.description = description;
		}

		public List<InputItem> getInputs() {
			return inputs;
		}

		public void setInputs(List<InputItem> inputs) {
			this.inputs = inputs;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	public FormInputTool(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public enum InputState {

		AWAITING_USER_INPUT, INPUT_RECEIVED, INPUT_TIMEOUT

	}

	private InputState inputState = InputState.INPUT_RECEIVED; // Default state

	private UserFormInput currentFormDefinition; // Stores the form structure defined by

	// LLM and its current values

	public InputState getInputState() {
		return inputState;
	}

	public void setInputState(InputState inputState) {
		this.inputState = inputState;
	}

	@Override
	public ToolExecuteResult run(UserFormInput formInput) {
		LoggerUtil.info("FormInputTool input: {0}", formInput);

		this.currentFormDefinition = formInput;
		// Initialize values to empty string if null, to ensure they are present for
		// form binding
		if (this.currentFormDefinition != null && this.currentFormDefinition.getInputs() != null) {
			for (InputItem item : this.currentFormDefinition.getInputs()) {
				if (item.getValue() == null) {
					item.setValue(""); // Initialize with empty string
				}
			}
		}
		setInputState(InputState.AWAITING_USER_INPUT);

		// Return form definition as a structured result
		try {
			String formJson = objectMapper.writeValueAsString(formInput);
			return new ToolExecuteResult(formJson);
		}
		catch (Exception e) {
			LoggerUtil.error("Error serializing form input", e);
			return new ToolExecuteResult("{\"error\": \"Failed to process form input: " + e.getMessage() + "\"}");
		}
	}

	/**
	 * Get the latest form structure defined by LLM (including description and input item
	 * labels and current values). This form structure will be used to present to users in
	 * the frontend.
	 * @return latest UserFormInput object, or null if not yet defined.
	 */
	public UserFormInput getLatestUserFormInput() {
		return this.currentFormDefinition;
	}

	/**
	 * Set user-submitted form input values. These values will update the value of
	 * corresponding input items in currentFormDefinition.
	 * @param submittedItems list of input items submitted by user (label-value pairs).
	 */
	public void setUserFormInputValues(List<InputItem> submittedItems) {
		if (this.currentFormDefinition == null || this.currentFormDefinition.getInputs() == null) {
			LoggerUtil.warn("Cannot set user form input values: form definition is missing or has no inputs.");
			return;
		}
		if (submittedItems == null) {
			LoggerUtil.warn("Submitted items are null. No values to update.");
			return;
		}

		Map<String, String> submittedValuesMap = new HashMap<>();
		for (InputItem submittedItem : submittedItems) {
			if (submittedItem.getLabel() != null) {
				submittedValuesMap.put(submittedItem.getLabel(), submittedItem.getValue());
			}
		}

		for (InputItem definitionItem : this.currentFormDefinition.getInputs()) {
			if (definitionItem.getLabel() != null && submittedValuesMap.containsKey(definitionItem.getLabel())) {
				definitionItem.setValue(submittedValuesMap.get(definitionItem.getLabel()));
			}
		}
		// The caller (UserInputService) is responsible for calling
		// markUserInputReceived()
	}

	public void markUserInputReceived() {
		setInputState(InputState.INPUT_RECEIVED);
	}

	public void handleInputTimeout() {
		LoggerUtil.warn("Input timeout occurred. No input received from the user for form: {0}",
				this.currentFormDefinition != null ? this.currentFormDefinition.getDescription() : "N/A");
		setInputState(InputState.INPUT_TIMEOUT);
		this.currentFormDefinition = null; // Clear form definition on timeout
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return getToolDescription();
	}

	@Override
	public String getParameters() {
		return getToolParameters();
	}

	@Override
	public Class<UserFormInput> getInputType() {
		return UserFormInput.class;
	}

	@Override
	public boolean isReturnDirect() {
		return true;
	}

	@Override
	public void cleanup(String planId) {
		// Optional implementation
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	/**
	 * Get current tool state, including form description and input items (including
	 * user-entered values if any)
	 */
	@Override
	public String getCurrentToolStateString() {
		if (currentFormDefinition == null) {
			return String.format("FormInputTool Status: No form defined. Current input state: %s",
					inputState.toString());
		}
		try {
			StringBuilder stateBuilder = new StringBuilder("FormInputTool Status:\n");
			stateBuilder
				.append(String.format("Description: %s\nInput Items: %s\n", currentFormDefinition.getDescription(),
						objectMapper.writeValueAsString(currentFormDefinition.getInputs())));
			stateBuilder.append(String.format("Current input state: %s\n", inputState.toString()));
			return stateBuilder.toString();
		}
		catch (JsonProcessingException e) {
			LoggerUtil.error("Error serializing currentFormDefinition for state string", e);
			return String.format("FormInputTool Status: Error serializing input items. Current input state: %s",
					inputState.toString());
		}
	}

}
