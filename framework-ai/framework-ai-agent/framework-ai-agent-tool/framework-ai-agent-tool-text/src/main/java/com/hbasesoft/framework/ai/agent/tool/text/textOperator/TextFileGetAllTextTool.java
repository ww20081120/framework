/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.ai.agent.tool.text.textOperator;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.innerStorage.ISmartContentSavingService;
import com.hbasesoft.framework.ai.agent.tool.innerStorage.SmartProcessResult;

@Component
public class TextFileGetAllTextTool extends AbstractTextFileTool<TextFileGetAllTextTool.TextFileGetAllTextInput> {

    public static class TextFileGetAllTextInput {
        private String filePath;

        public TextFileGetAllTextInput() {
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    private final ObjectMapper objectMapper;

    public TextFileGetAllTextTool(TextFileService textFileService, ISmartContentSavingService innerStorageService,
        ObjectMapper objectMapper) {
        super(textFileService, innerStorageService);
        this.objectMapper = objectMapper;
    }

    @Override
    public ToolExecuteResult run(TextFileGetAllTextInput input) {
        log.info("TextFileGetAllTextTool input: filePath={}", input.getFilePath());
        try {
            String planId = this.currentPlanId;
            String filePath = input.getFilePath();

            // Basic parameter validation
            if (filePath == null) {
                return new ToolExecuteResult("Error: file_path parameter is required");
            }

            return getAllText(planId, filePath);
        }
        catch (Exception e) {
            String planId = this.currentPlanId;
            textFileService.updateFileState(planId, textFileService.getCurrentFilePath(planId),
                "Error: " + e.getMessage());
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    public ToolExecuteResult run(String toolInput) {
        log.info("TextFileGetAllTextTool toolInput:{}", toolInput);
        try {
            TextFileGetAllTextInput input = objectMapper.readValue(toolInput, TextFileGetAllTextInput.class);
            return run(input);
        }
        catch (Exception e) {
            String planId = this.currentPlanId;
            textFileService.updateFileState(planId, textFileService.getCurrentFilePath(planId),
                "Error: " + e.getMessage());
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    private ToolExecuteResult getAllText(String planId, String filePath) {
        try {
            // Automatically open file
            ToolExecuteResult openResult = ensureFileOpen(planId, filePath);
            if (!openResult.getOutput().toLowerCase().contains("success")) {
                return openResult;
            }

            // Read file content
            Path absolutePath = textFileService.validateFilePath(planId, filePath);
            String content = Files.readString(absolutePath);

            // Force flush to disk to ensure data consistency
            try (FileChannel channel = FileChannel.open(absolutePath, StandardOpenOption.READ)) {
                channel.force(true);
            }

            textFileService.updateFileState(planId, filePath, "Success: Retrieved all text");

            // Use InnerStorageService to intelligently process content
            SmartProcessResult processedResult = innerStorageService.processContent(planId, content, "get_all_text");

            return new ToolExecuteResult(processedResult.getSummary());
        }
        catch (IOException e) {
            textFileService.updateFileState(planId, filePath, "Error: " + e.getMessage());
            return new ToolExecuteResult("Error retrieving all text: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "text_file_get_all_text";
    }

    @Override
    public String getDescription() {
        return """
            Get all content from file.

            Supported file types include:
            - Text files (.txt)
            - Markdown files (.md, .markdown)
            - Web files (.html, .css, .scss, .sass, .less)
            - Programming files (.java, .py, .js, .ts, .cpp, .c, .h, .go, .rs, .php, .rb, .swift, .kt, .scala)
            - Configuration files (.json, .xml, .yaml, .yml, .toml, .ini, .conf)
            - Documentation files (.rst, .adoc)

            Note: If file content is too long, it will be automatically stored in temporary file and return file path
            """;
    }

    @Override
    public String getParameters() {
        return """
            {
                "type": "object",
                "properties": {
                    "file_path": {
                        "type": "string",
                        "description": "File path to read all content. Note: If file is too long, content will be stored in temporary file and return file path"
                    }
                },
                "required": ["file_path"],
                "additionalProperties": false
            }
            """;
    }

    @Override
    public Class<TextFileGetAllTextInput> getInputType() {
        return TextFileGetAllTextInput.class;
    }
}
