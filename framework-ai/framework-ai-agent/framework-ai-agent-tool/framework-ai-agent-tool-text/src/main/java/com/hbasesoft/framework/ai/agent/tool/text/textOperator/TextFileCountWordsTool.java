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
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.innerStorage.ISmartContentSavingService;

@Component
public class TextFileCountWordsTool extends AbstractTextFileTool<TextFileCountWordsTool.TextFileCountWordsInput> {

    public static class TextFileCountWordsInput {
        private String filePath;

        public TextFileCountWordsInput() {
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    private final ObjectMapper objectMapper;

    public TextFileCountWordsTool(TextFileService textFileService, ISmartContentSavingService innerStorageService,
        ObjectMapper objectMapper) {
        super(textFileService, innerStorageService);
        this.objectMapper = objectMapper;
    }

    @Override
    public ToolExecuteResult run(TextFileCountWordsInput input) {
        log.info("TextFileCountWordsTool input: filePath={}", input.getFilePath());
        try {
            String planId = this.currentPlanId;
            String filePath = input.getFilePath();

            // Basic parameter validation
            if (filePath == null) {
                return new ToolExecuteResult("Error: file_path parameter is required");
            }

            return countWords(planId, filePath);
        }
        catch (Exception e) {
            String planId = this.currentPlanId;
            textFileService.updateFileState(planId, textFileService.getCurrentFilePath(planId),
                "Error: " + e.getMessage());
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    public ToolExecuteResult run(String toolInput) {
        log.info("TextFileCountWordsTool toolInput:{}", toolInput);
        try {
            TextFileCountWordsInput input = objectMapper.readValue(toolInput, TextFileCountWordsInput.class);
            return run(input);
        }
        catch (Exception e) {
            String planId = this.currentPlanId;
            textFileService.updateFileState(planId, textFileService.getCurrentFilePath(planId),
                "Error: " + e.getMessage());
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    private ToolExecuteResult countWords(String planId, String filePath) {
        try {
            // Automatically open file
            ToolExecuteResult openResult = ensureFileOpen(planId, filePath);
            if (!openResult.getOutput().toLowerCase().contains("success")) {
                return openResult;
            }

            Path absolutePath = textFileService.validateFilePath(planId, filePath);
            String content = Files.readString(absolutePath);
            int wordCount = content.isEmpty() ? 0 : content.split("\\s+").length;

            textFileService.updateFileState(planId, filePath, "Success: Counted words");
            return new ToolExecuteResult(String.format("Total word count (including Markdown symbols): %d", wordCount));
        }
        catch (IOException e) {
            textFileService.updateFileState(planId, filePath, "Error: " + e.getMessage());
            return new ToolExecuteResult("Error counting words: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "text_file_count_words";
    }

    @Override
    public String getDescription() {
        return """
            Count words in current file.

            Supported file types include:
            - Text files (.txt)
            - Markdown files (.md, .markdown)
            - Web files (.html, .css, .scss, .sass, .less)
            - Programming files (.java, .py, .js, .ts, .cpp, .c, .h, .go, .rs, .php, .rb, .swift, .kt, .scala)
            - Configuration files (.json, .xml, .yaml, .yml, .toml, .ini, .conf)
            - Documentation files (.rst, .adoc)
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
                        "description": "File path to count words"
                    }
                },
                "required": ["file_path"],
                "additionalProperties": false
            }
            """;
    }

    @Override
    public Class<TextFileCountWordsInput> getInputType() {
        return TextFileCountWordsInput.class;
    }
}
