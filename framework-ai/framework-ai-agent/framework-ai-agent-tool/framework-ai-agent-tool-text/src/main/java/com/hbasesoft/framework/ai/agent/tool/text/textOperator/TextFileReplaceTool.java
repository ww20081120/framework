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

@Component
public class TextFileReplaceTool extends AbstractTextFileTool<TextFileReplaceTool.TextFileReplaceInput> {

    public static class TextFileReplaceInput {
        private String filePath;

        private String sourceText;

        private String targetText;

        public TextFileReplaceInput() {
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getSourceText() {
            return sourceText;
        }

        public void setSourceText(String sourceText) {
            this.sourceText = sourceText;
        }

        public String getTargetText() {
            return targetText;
        }

        public void setTargetText(String targetText) {
            this.targetText = targetText;
        }
    }

    public TextFileReplaceTool(TextFileService textFileService, IUnifiedDirectoryManager unifiedDirectoryManager) {
        super(textFileService, unifiedDirectoryManager);
    }

    @Override
    public ToolExecuteResult run(TextFileReplaceInput input) {
        log.info("TextFileReplaceTool input: filePath={}", input.getFilePath());
        try {
            String filePath = input.getFilePath();
            String sourceText = input.getSourceText();
            String targetText = input.getTargetText();

            // Basic parameter validation
            if (filePath == null) {
                return new ToolExecuteResult("Error: filePath parameter is required");
            }
            if (sourceText == null || targetText == null) {
                return new ToolExecuteResult(
                    "Error: replace operation requires sourceText and targetText parameters");
            }

            return replaceText(filePath, sourceText, targetText);
        }
        catch (Exception e) {
            return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
        }
    }

    private ToolExecuteResult replaceText(String filePath, String sourceText, String targetText) {
        try {
            // Automatically open file
            ToolExecuteResult openResult = ensureFileOpen(filePath);
            if (!openResult.getOutput().toLowerCase().contains("success")) {
                return openResult;
            }

            Path absolutePath = Paths.get(filePath);
            String content = Files.readString(absolutePath);
            String newContent = content.replace(sourceText, targetText);
            Files.writeString(absolutePath, newContent);

            // Automatically save file
            try (FileChannel channel = FileChannel.open(absolutePath, StandardOpenOption.WRITE)) {
                channel.force(true);
            }

            return new ToolExecuteResult("Text replaced and saved successfully");
        }
        catch (IOException e) {
            return new ToolExecuteResult("Error replacing text: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "text_file_replace";
    }

    @Override
    public String getDescription() {
        return """
            Replace specific text in a file.

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
                    "sourceText": {
                        "type": "string",
                        "description": "Text to be replaced"
                    },
                    "targetText": {
                        "type": "string",
                        "description": "Replacement text"
                    }
                },
                "required": ["filePath", "sourceText", "targetText"],
                "additionalProperties": false
            }
            """;
    }

    @Override
    public Class<TextFileReplaceInput> getInputType() {
        return TextFileReplaceInput.class;
    }
}
