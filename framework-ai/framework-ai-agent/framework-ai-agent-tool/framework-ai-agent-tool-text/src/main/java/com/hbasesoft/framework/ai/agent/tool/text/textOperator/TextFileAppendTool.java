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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.IUnifiedDirectoryManager;
import com.hbasesoft.framework.ai.agent.tool.text.textInnerOperator.TextFileService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class TextFileAppendTool extends AbstractTextFileTool<TextFileAppendTool.TextFileAppendInput> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextFileAppendInput {
        private String filePath;

        private String content;
    }

    public TextFileAppendTool(TextFileService textFileService, IUnifiedDirectoryManager unifiedDirectoryManager) {
        super(textFileService, unifiedDirectoryManager);
    }

    @Override
    public ToolExecuteResult run(TextFileAppendInput input) {
        log.info("TextFileAppendTool input: filePath={}", input.getFilePath());
        try {
            String filePath = input.getFilePath();
            String content = input.getContent();

            // Basic parameter validation
            if (filePath == null) {
                return new ToolExecuteResult("Error: filePath parameter is required");
            }
            if (content == null || content.isEmpty()) {
                return new ToolExecuteResult("Error: append operation requires content parameter");
            }

            return appendToFile(filePath, content);
        }
        catch (Exception e) {
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    private ToolExecuteResult appendToFile(String filePath, String content) {
        try {
            if (content == null || content.isEmpty()) {
                return new ToolExecuteResult("Error: No content to append");
            }

            // Automatically open file
            ToolExecuteResult openResult = ensureFileOpen(filePath);
            if (!openResult.getOutput().toLowerCase().contains("success")) {
                return openResult;
            }

            Path absolutePath = Paths.get(filePath);
            Files.writeString(absolutePath, "\n" + content, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

            // Automatically save file
            try (FileChannel channel = FileChannel.open(absolutePath, StandardOpenOption.WRITE)) {
                channel.force(true);
            }

            return new ToolExecuteResult("Content appended and saved successfully");
        }
        catch (IOException e) {
            return new ToolExecuteResult("Error appending to file: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "text_file_append";
    }

    @Override
    public String getDescription() {
        return """
            Append content to file.

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
                    "filePath": {
                        "type": "string",
                        "description": "File path to operate on"
                    },
                    "content": {
                        "type": "string",
                        "description": "Content to append to the file"
                    }
                },
                "required": ["filePath", "content"],
                "additionalProperties": false
            }
            """;
    }

    @Override
    public Class<TextFileAppendInput> getInputType() {
        return TextFileAppendInput.class;
    }
}
