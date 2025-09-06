/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.file.recorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public RecorderServiceImpl(UnifiedDirectoryManager unifiedDirectoryManager) {
        this.unifiedDirectoryManager = unifiedDirectoryManager;
        this.objectMapper = new ObjectMapper();
        // 注册JavaTimeModule以支持Java 8的时间类型序列化
        this.objectMapper.registerModule(new JavaTimeModule());
        // 禁用时间戳序列化，使用标准格式
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Value("${namespace.value:default}")
    private String namespace;

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

}
