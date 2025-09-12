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
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.IUnifiedDirectoryManager;
import com.hbasesoft.framework.ai.agent.tool.text.textInnerOperator.TextFileService;

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

    public TextFileCountWordsTool(TextFileService textFileService, IUnifiedDirectoryManager unifiedDirectoryManager) {
        super(textFileService, unifiedDirectoryManager);
    }

    @Override
    public ToolExecuteResult run(TextFileCountWordsInput input) {
        log.info("TextFileCountWordsTool input: filePath={}", input.getFilePath());
        try {
            String filePath = input.getFilePath();

            // Basic parameter validation
            if (filePath == null) {
                return new ToolExecuteResult("Error: file_path parameter is required");
            }

            return countWords(filePath);
        }
        catch (Exception e) {
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    private ToolExecuteResult countWords(String filePath) {
        try {
            // Automatically open file
            ToolExecuteResult openResult = ensureFileOpen(filePath);
            if (!openResult.getOutput().toLowerCase().contains("success")) {
                return openResult;
            }

            Path absolutePath = Paths.get(filePath);
            String content = Files.readString(absolutePath);
            int wordCount = content.isEmpty() ? 0 : content.split("\\s+").length;

            return new ToolExecuteResult(String.format("Total word count (including Markdown symbols): %d", wordCount));
        }
        catch (IOException e) {
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
