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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.innerStorage.ISmartContentSavingService;

public abstract class AbstractTextFileTool<T> extends AbstractBaseTool<T> {

    protected static final Logger log = LoggerFactory.getLogger(AbstractTextFileTool.class);

    protected final TextFileService textFileService;

    protected final ISmartContentSavingService innerStorageService;

    public AbstractTextFileTool(TextFileService textFileService, ISmartContentSavingService innerStorageService) {
        this.textFileService = textFileService;
        this.innerStorageService = innerStorageService;
    }

    /**
     * Ensure file is opened, create if it doesn't exist
     */
    protected ToolExecuteResult ensureFileOpen(String planId, String filePath) {
        try {
            // Check file type
            if (!textFileService.isSupportedFileType(filePath)) {
                textFileService.updateFileState(planId, filePath, "Error: Unsupported file type");
                return new ToolExecuteResult("Unsupported file type. Only text-based files are supported.");
            }

            // Use TextFileService to validate and get the absolute path
            Path absolutePath = textFileService.validateFilePath(planId, filePath);

            // If file doesn't exist, create parent directory first
            if (!Files.exists(absolutePath)) {
                try {
                    Files.createDirectories(absolutePath.getParent());
                    Files.createFile(absolutePath);
                    textFileService.updateFileState(planId, filePath, "Success: New file created");
                    return new ToolExecuteResult("New file created successfully: " + absolutePath);
                }
                catch (IOException e) {
                    textFileService.updateFileState(planId, filePath,
                        "Error: Failed to create file: " + e.getMessage());
                    return new ToolExecuteResult("Failed to create file: " + e.getMessage());
                }
            }

            textFileService.updateFileState(planId, filePath, "Success: File opened");
            return new ToolExecuteResult("File opened successfully: " + absolutePath);
        }
        catch (IOException e) {
            textFileService.updateFileState(planId, filePath, "Error: " + e.getMessage());
            return new ToolExecuteResult("Error opening file: " + e.getMessage());
        }
    }

    protected void forceFlushFile(Path absolutePath) throws IOException {
        try (FileChannel channel = FileChannel.open(absolutePath, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
    }

    @Override
    public String getCurrentToolStateString() {
        String planId = this.currentPlanId;
        try {
            Path workingDir = textFileService.getAbsolutePath(planId, "");
            return String.format(
                """
                    Current Text File Operation State:
                    - working Directory:
                    %s

                    - Operations are automatically handled (no manual file opening/closing required)
                    - All file operations (open, save) are performed automatically
                    - Supported file types: txt, md, html, css, java, py, js, ts, xml, json, yaml, properties, sh, bat, log, etc.

                    - Last Operation Result:
                    %s
                    """,
                workingDir.toString(),
                textFileService.getLastOperationResult(planId).isEmpty() ? "No operation performed yet"
                    : textFileService.getLastOperationResult(planId));
        }
        catch (Exception e) {
            return String.format("""
                Current Text File Operation State:
                - Error getting working directory: %s

                - Last Operation Result:
                %s
                """, e.getMessage(),
                textFileService.getLastOperationResult(planId).isEmpty() ? "No operation performed yet"
                    : textFileService.getLastOperationResult(planId));
        }
    }

    @Override
    public String getServiceGroup() {
        return "default-service-group";
    }

    @Override
    public void cleanup(String planId) {
        if (planId != null) {
            log.info("Cleaning up text file resources for plan: {}", planId);
            textFileService.cleanupPlanDirectory(planId);
        }
    }
}
