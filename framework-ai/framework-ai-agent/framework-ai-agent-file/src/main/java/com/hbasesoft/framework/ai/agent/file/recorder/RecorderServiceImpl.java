/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.file.recorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hbasesoft.framework.ai.agent.recorder.RecorderService;
import com.hbasesoft.framework.ai.agent.recorder.model.vo.RecorderVo;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.recorder <br>
 */
@Service
public class RecorderServiceImpl implements RecorderService {

    private final UnifiedDirectoryManager unifiedDirectoryManager;

    private final ObjectMapper objectMapper;

    private final ExecutorService cleanupExecutor = Executors.newSingleThreadExecutor();

    @Value("${namespace.value:default}")
    private String namespace;

    @Value("${recorder.max.files:100}")
    private int maxFileCount = 100;

    public RecorderServiceImpl(UnifiedDirectoryManager unifiedDirectoryManager) {
        this.unifiedDirectoryManager = unifiedDirectoryManager;
        this.objectMapper = new ObjectMapper();
        // 注册JavaTimeModule以支持Java 8的时间类型序列化
        this.objectMapper.registerModule(new JavaTimeModule());
        // 禁用时间戳序列化，使用标准格式
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param planId <br>
     */
    @Override
    public void deleteById(String planId) {
        try {
            Path filePath = getFilePath(planId);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                LoggerUtil.info("已删除记录文件: {0}", filePath);
            }
        }
        catch (IOException e) {
            LoggerUtil.error(e, "删除记录文件失败: {0}", planId);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param rootPlanId
     * @return <br>
     */
    @Override
    public RecorderVo getByRootPlanId(String rootPlanId) {
        try {
            Path filePath = getFilePath(rootPlanId);
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                return objectMapper.readValue(content, RecorderVo.class);
            }
        }
        catch (IOException e) {
            LoggerUtil.error(e, "读取记录文件失败: {0}", rootPlanId);
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void save(RecorderVo entity) {
        try {
            Path filePath = getFilePath(entity.getPlanId());

            // 确保目录存在
            Path directory = filePath.getParent();
            if (directory != null) {
                unifiedDirectoryManager.ensureDirectoryExists(directory);
            }

            // 将实体写入JSON文件
            String content = objectMapper.writeValueAsString(entity);
            Files.writeString(filePath, content);
            LoggerUtil.info("已保存记录文件: {0}", filePath);

            // 异步清理旧文件
            cleanupExecutor.submit(this::cleanupOldFiles);
        }
        catch (IOException e) {
            LoggerUtil.error(e, "保存记录文件失败: {0}", entity.getPlanId());
        }
    }

    /**
     * 获取文件路径：工作目录/{namespace}/{planId}.json
     * 
     * @param planId 计划ID
     * @return 文件路径
     */
    private Path getFilePath(String planId) {
        return unifiedDirectoryManager.getWorkingDirectory().resolve(namespace).resolve(planId + ".json");
    }

    /**
     * 清理旧文件，保留最新的maxFileCount个文件
     */
    private void cleanupOldFiles() {
        try {
            Path directory = unifiedDirectoryManager.getWorkingDirectory().resolve(namespace);
            if (!Files.exists(directory)) {
                return;
            }

            // 获取目录中的计划文件（以planId命名的JSON文件）并按修改时间排序（从最新到最旧）
            try (Stream<Path> files = Files.list(directory)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        // 只处理计划文件，即以.json结尾但不是其他配置文件的文件
                        // 计划文件格式为 {planId}.json，所以我们排除特定的配置文件名
                        return fileName.endsWith(".json") && 
                               !fileName.equals("prompt.json") && 
                               !fileName.equals("config.json") && 
                               !fileName.equals("settings.json") && 
                               !fileName.equals("metadata.json");
                    })
                    .sorted(Comparator.comparingLong((Path path) -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis();
                        } catch (IOException e) {
                            LoggerUtil.error(e, "获取文件修改时间失败: {0}", path);
                            return 0L;
                        }
                    }).reversed())) { // 按修改时间从新到旧排序

                // 跳过最新的maxFileCount个文件，删除其余的旧文件
                files.skip(maxFileCount)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                LoggerUtil.info("已删除旧记录文件: {0}", path);
                            } catch (IOException e) {
                                LoggerUtil.error(e, "删除旧记录文件失败: {0}", path);
                            }
                        });
            }
        } catch (IOException e) {
            LoggerUtil.error(e, "清理旧记录文件时发生错误");
        }
    }

    /**
     * 销毁方法，关闭清理线程池
     */
    @PreDestroy
    public void destroy() {
        if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
            cleanupExecutor.shutdown();
        }
    }

}
