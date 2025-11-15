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
import java.util.List;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.IUnifiedDirectoryManager;
import com.hbasesoft.framework.ai.agent.tool.text.textInnerOperator.TextFileService;

@Component
public class TextFileGetTextTool extends AbstractTextFileTool<TextFileGetTextTool.TextFileGetTextInput> {

    public static class TextFileGetTextInput {
        private String filePath;

        private Integer startLine;

        private Integer endLine;

        public TextFileGetTextInput() {
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Integer getStartLine() {
            return startLine;
        }

        public void setStartLine(Integer startLine) {
            this.startLine = startLine;
        }

        public Integer getEndLine() {
            return endLine;
        }

        public void setEndLine(Integer endLine) {
            this.endLine = endLine;
        }
    }

    public TextFileGetTextTool(TextFileService textFileService, IUnifiedDirectoryManager unifiedDirectoryManager) {
        super(textFileService, unifiedDirectoryManager);
    }

    @Override
    public ToolExecuteResult run(TextFileGetTextInput input) {
        log.info("TextFileGetTextTool input: filePath={}", input.getFilePath());
        try {
            String filePath = input.getFilePath();
            Integer startLine = input.getStartLine();
            Integer endLine = input.getEndLine();

            // Basic parameter validation
            if (filePath == null) {
                return new ToolExecuteResult("Error: filePath parameter is required");
            }
            if (startLine == null || endLine == null) {
                return new ToolExecuteResult("Error: getText operation requires startLine and endLine parameters");
            }

            return getTextByLines(filePath, startLine, endLine);
        }
        catch (Exception e) {
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    private ToolExecuteResult getTextByLines(String filePath, Integer startLine, Integer endLine) {
        try {
            // Parameter validation
            if (startLine < 1 || endLine < 1) {
                return new ToolExecuteResult("Error: Line numbers must start from 1");
            }
            if (startLine > endLine) {
                return new ToolExecuteResult("Error: Start line number cannot be greater than end line number");
            }

            // Check 500-line limit
            int requestedLines = endLine - startLine + 1;
            if (requestedLines > 500) {
                return new ToolExecuteResult(
                    "Error: Maximum 500 lines per request. Please adjust line range or make multiple calls. Current requested lines: "
                        + requestedLines);
            }

            // Automatically open file
            ToolExecuteResult openResult = ensureFileOpen(filePath);
            if (!openResult.getOutput().toLowerCase().contains("success")) {
                return openResult;
            }

            Path absolutePath = Paths.get(filePath);
            List<String> lines = Files.readAllLines(absolutePath);

            if (lines.isEmpty()) {
                return new ToolExecuteResult("File is empty");
            }

            // Validate line number range
            if (startLine > lines.size()) {
                return new ToolExecuteResult(
                    "Error: Start line number exceeds file range (file has " + lines.size() + " lines)");
            }

            // Adjust end line number (not exceeding total file lines)
            int actualEndLine = Math.min(endLine, lines.size());

            StringBuilder result = new StringBuilder();
            result.append(String.format("File: %s (Lines %d-%d, Total %d lines)\n", filePath, startLine, actualEndLine,
                lines.size()));
            result.append("=".repeat(50)).append("\n");

            for (int i = startLine - 1; i < actualEndLine; i++) {
                result.append(String.format("%4d: %s\n", i + 1, lines.get(i)));
            }

            // If file has more content, prompt user
            if (actualEndLine < lines.size()) {
                result.append("\nNote: File has more content (lines ").append(actualEndLine + 1).append("-")
                    .append(lines.size()).append("), you can continue calling get_text to retrieve.");
            }

            return new ToolExecuteResult(result.toString());
        }
        catch (IOException e) {
            return new ToolExecuteResult("Error retrieving text lines: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "text_file_get_text";
    }

    @Override
    public String getDescription() {
        return """
            Get content from specified line range in file.

            Supported file types include:
            - Text files (.txt)
            - Markdown files (.md, .markdown)
            - Web files (.html, .css, .scss, .sass, .less)
            - Programming files (.java, .py, .js, .ts, .cpp, .c, .h, .go, .rs, .php, .rb, .swift, .kt, .scala)
            - Configuration files (.json, .xml, .yaml, .yml, .toml, .ini, .conf)
            - Documentation files (.rst, .adoc)

            Limitation: Maximum 500 lines per call, use multiple calls for more content
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
                        "description": "File path to read"
                    },
                    "startLine": {
                        "type": "integer",
                        "description": "Starting line number (starts from 1)"
                    },
                    "endLine": {
                        "type": "integer",
                        "description": "Ending line number (inclusive). Note: Maximum 500 lines per call, use multiple calls for more content"
                    }
                },
                "required": ["filePath", "startLine", "endLine"],
                "additionalProperties": false
            }
            """;
    }

    @Override
    public Class<TextFileGetTextInput> getInputType() {
        return TextFileGetTextInput.class;
    }
}
