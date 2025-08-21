/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.docLoader;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.function.FunctionToolCallback;

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
 * @see com.hbasesoft.framework.ai.jmanus.tool.docLoader <br>
 */
public class DocLoaderTool extends AbstractBaseTool<DocLoaderTool.DocLoaderInput> {

	/**
	 * Internal input class for defining input parameters of the document loading
	 * tool
	 */
	public static class DocLoaderInput {

		@com.fasterxml.jackson.annotation.JsonProperty("file_type")
		private String fileType;

		@com.fasterxml.jackson.annotation.JsonProperty("file_path")
		private String filePath;

		public DocLoaderInput() {
		}

		public DocLoaderInput(String fileType, String filePath) {
			this.fileType = fileType;
			this.filePath = filePath;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

	}

	private static final String name = "doc_loader";

	public OpenAiApi.FunctionTool getToolDefinition() {
		String description = getDescription();
		String parameters = getParameters();
		OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(description, name, parameters);
		OpenAiApi.FunctionTool functionTool = new OpenAiApi.FunctionTool(function);
		return functionTool;
	}

	/**
	 * Get FunctionToolCallback for Spring AI
	 */
	public static FunctionToolCallback<DocLoaderInput, ToolExecuteResult> getFunctionToolCallback() {
		return FunctionToolCallback
				.<DocLoaderInput, ToolExecuteResult>builder(name, (DocLoaderInput input,
						org.springframework.ai.chat.model.ToolContext context) -> new DocLoaderTool().run(input))
				.description("""
						Get the content information of a local file at a specified path.
						Use this tool when you want to get some related information asked by the user.
						This tool accepts the file path and gets the related information content.
						""").inputType(DocLoaderInput.class).build();
	}

	public DocLoaderTool() {
	}

	private String lastFilePath = "";

	private String lastOperationResult = "";

	private String lastFileType = "";

	@Override
	public ToolExecuteResult run(DocLoaderInput input) {
		String fileType = input.getFileType();
		String filePath = input.getFilePath();
		LoggerUtil.info("DocLoaderTool fileType: {0}, filePath: {1}", fileType, filePath);
		this.lastFilePath = filePath;
		this.lastFileType = fileType;

		try {
			if (!"pdf".equalsIgnoreCase(fileType)) {
				return new ToolExecuteResult("Unsupported file type: " + fileType);
			}

			try (PDDocument document = PDDocument.load(new File(filePath))) {
				PDFTextStripper pdfStripper = new PDFTextStripper();
				String documentContentStr = pdfStripper.getText(document);

				if (StringUtils.isEmpty(documentContentStr)) {
					this.lastOperationResult = "No content found";
					return new ToolExecuteResult("No Related information");
				} else {
					this.lastOperationResult = "Success";
					return new ToolExecuteResult("Related information: " + documentContentStr);
				}
			}
		} catch (Throwable e) {
			this.lastOperationResult = "Error: " + e.getMessage();
			return new ToolExecuteResult("Error get Related information: " + e.getMessage());
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return """
				Get the content information of a local file at a specified path.
				Use this tool when you want to get some related information asked by the user.
				This tool accepts the file path and gets the related information content.
				""";
	}

	@Override
	public String getParameters() {
		return """
				{
				    "type": "object",
				    "properties": {
				        "file_type": {
				            "type": "string",
				            "description": "(required) File type, only support pdf file."
				        },
				        "file_path": {
				            "type": "string",
				            "description": "(required) Get the absolute path of the file from the user request."
				        }
				    },
				    "required": ["file_type", "file_path"]
				}
				""";
	}

	@Override
	public Class<DocLoaderInput> getInputType() {
		return DocLoaderInput.class;
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	@Override
	public String getCurrentToolStateString() {
		return String.format("""
				            Current File Operation State:
				            - Working Directory:
				%s

				            - Last File Operation:
				%s

				            - Last Operation Result:
				%s

				            """, new File("").getAbsolutePath(),
				lastFilePath.isEmpty() ? "No file loaded yet"
						: String.format("Load %s file from: %s", lastFileType, lastFilePath),
				lastOperationResult.isEmpty() ? "No operation performed yet" : lastOperationResult);
	}

	@Override
	public void cleanup(String planId) {
		// do nothing
	}

}
