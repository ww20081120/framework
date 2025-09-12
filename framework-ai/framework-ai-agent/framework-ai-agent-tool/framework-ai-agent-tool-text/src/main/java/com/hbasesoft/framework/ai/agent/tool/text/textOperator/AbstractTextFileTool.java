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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.filesystem.IUnifiedDirectoryManager;
import com.hbasesoft.framework.ai.agent.tool.text.textInnerOperator.TextFileService;

public abstract class AbstractTextFileTool<T> extends AbstractBaseTool<T> {

    protected static final Logger log = LoggerFactory.getLogger(AbstractTextFileTool.class);

    protected final TextFileService textFileService;

    protected final IUnifiedDirectoryManager unifiedDirectoryManager;

    public AbstractTextFileTool(TextFileService textFileService, IUnifiedDirectoryManager unifiedDirectoryManager) {
        this.textFileService = textFileService;
        this.unifiedDirectoryManager = unifiedDirectoryManager;
    }

    /**
     * Ensure file is opened, create if it doesn't exist
     */
    protected ToolExecuteResult ensureFileOpen(String filePath) {
        // Check file type
        if (!textFileService.isSupportedFileType(filePath)) {
            return new ToolExecuteResult("Unsupported file type. Only text-based files are supported.");
        }
        Path path = Paths.get(filePath);

        if (!unifiedDirectoryManager.isPathAllowed(path)) {
            return new ToolExecuteResult("File path not allowed. " + path.toAbsolutePath().toString());
        }

        // If file doesn't exist, create parent directory first
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return new ToolExecuteResult("New file created successfully: " + path.toString());
            }
            catch (IOException e) {
                return new ToolExecuteResult("Failed to create file: " + e.getMessage());
            }
        }
        return new ToolExecuteResult("File opened successfully: " + path.toAbsolutePath().toString());

    }

    protected void forceFlushFile(Path absolutePath) throws IOException {
        try (FileChannel channel = FileChannel.open(absolutePath, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
    }

    @Override
    public String getCurrentToolStateString() {
        try {
            return String.format(
                """
                    Current Text File Operation State:
                    - working Directory:
                    %s

                    - Operations are automatically handled (no manual file opening/closing required)
                    - All file operations (open, save) are performed automatically
                    - Supported file types: txt, md, html, css, java, py, js, ts, xml, json, yaml, properties, sh, bat, log, etc.
                    """,
                unifiedDirectoryManager.getWorkingDirectory().toString());
        }
        catch (Exception e) {
            return String.format("""
                Current Text File Operation State:
                - Error getting working directory: %s
                """, e.getMessage());
        }
    }

    @Override
    public String getServiceGroup() {
        return "default-service-group";
    }

    @Override
    public void cleanup(String planId) {
    }
}
