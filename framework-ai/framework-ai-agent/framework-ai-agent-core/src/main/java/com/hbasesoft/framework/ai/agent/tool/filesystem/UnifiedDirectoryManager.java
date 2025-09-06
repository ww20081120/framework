/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * 统一目录管理器实现类 <br>
 * 实现了IUnifiedDirectoryManager接口，提供对系统中各种目录的创建、访问和清理操作的具体实现
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.filesystem <br>
 */

@Service
public class UnifiedDirectoryManager implements IUnifiedDirectoryManager {

    private final IManusProperties manusProperties;

    private String workingDirectoryPath;

    // Directory structure constants
    private static final String EXTENSIONS_DIR = "extensions";

    private static final String INNER_STORAGE_DIR = "inner_storage";

    public UnifiedDirectoryManager(IManusProperties manusProperties) {
        this.manusProperties = manusProperties;

    }

    public void init() {
        if (StringUtils.isEmpty(this.workingDirectoryPath)) {
            this.workingDirectoryPath = getWorkingDirectory(manusProperties.getBaseDir());
        }
    }

    /**
     * 获取主工作目录 (baseDir/extensions)
     * 
     * @return 工作目录的绝对路径
     */
    public String getWorkingDirectoryPath() {
        return workingDirectoryPath;
    }

    /**
     * 获取工作目录路径作为Path对象
     * 
     * @return 工作目录的Path对象
     */
    public Path getWorkingDirectory() {
        return Paths.get(workingDirectoryPath);
    }

    /**
     * 获取根计划目录 (baseDir/extensions/inner_storage/rootPlanId) 此目录可被当前任务及其所有子任务访问
     * 
     * @param rootPlanId 根计划ID
     * @return 根计划目录的Path对象
     */
    public Path getRootPlanDirectory(String rootPlanId) {
        if (rootPlanId == null || rootPlanId.trim().isEmpty()) {
            throw new IllegalArgumentException("根计划ID不能为空");
        }
        return getWorkingDirectory().resolve(INNER_STORAGE_DIR).resolve(rootPlanId);
    }

    /**
     * 获取根计划目录下的子任务目录 (baseDir/extensions/inner_storage/rootPlanId/subTaskId)
     * 
     * @param rootPlanId 根计划ID
     * @param subTaskId 子任务ID
     * @return 子任务目录的Path对象
     */
    public Path getSubTaskDirectory(String rootPlanId, String subTaskId) {
        if (subTaskId == null || subTaskId.trim().isEmpty()) {
            throw new IllegalArgumentException("子任务ID不能为空");
        }
        return getRootPlanDirectory(rootPlanId).resolve(subTaskId);
    }

    /**
     * 获取指定目录并进行安全验证。如果ManusProperties配置不允许外部访问， 则只允许访问工作目录内的目录。
     * 
     * @param targetPath 目标目录路径（绝对或相对）
     * @return 验证后的目录Path对象
     * @throws SecurityException 如果访问被拒绝
     * @throws IOException 如果路径验证失败
     */
    public Path getSpecifiedDirectory(String targetPath) throws IOException, SecurityException {
        if (targetPath == null || targetPath.trim().isEmpty()) {
            throw new IllegalArgumentException("目标路径不能为空");
        }

        Path target = Paths.get(targetPath);

        // 如果是相对路径，则相对于工作目录解析
        if (!target.isAbsolute()) {
            target = getWorkingDirectory().resolve(targetPath);
        }

        target = target.normalize();

        // 安全检查：验证目标是否在允许范围内
        if (!isPathAllowed(target)) {
            throw new SecurityException("访问被拒绝：路径超出允许的工作目录范围: " + target);
        }

        return target;
    }

    /**
     * 确保目录存在，如果不存在则创建
     * 
     * @param directory 要确保存在的目录路径
     * @throws IOException 如果目录创建失败
     */
    public void ensureDirectoryExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            LoggerUtil.debug("已创建目录: {0}", directory);
        }
    }

    /**
     * 验证路径是否在允许的工作目录范围内。此方法通过确保所有文件操作 保持在指定的工作目录内来强制执行安全策略，除非明确允许外部访问。
     * 
     * @param targetPath 要验证的路径
     * @return 如果路径被允许返回true，否则返回false
     */
    public boolean isPathAllowed(Path targetPath) {
        try {
            Path workingDir = getWorkingDirectory().toAbsolutePath().normalize();
            Path normalizedTarget = targetPath.toAbsolutePath().normalize();

            // 检查目标是否在工作目录内
            boolean isWithinWorkingDir = normalizedTarget.startsWith(workingDir);

            // 如果在工作目录内，则始终允许
            if (isWithinWorkingDir) {
                return true;
            }

            // 如果在工作目录外，则检查配置
            boolean allowExternal = manusProperties.getAllowExternalAccess();

            LoggerUtil.debug("路径验证 - 工作目录: {0}, 目标: {1}, 是否在内: {2}, 允许外部: {3}, 最终结果: {4}", workingDir, normalizedTarget,
                isWithinWorkingDir, allowExternal, allowExternal);

            return allowExternal;
        }
        catch (Exception e) {
            LoggerUtil.error("验证路径时出错: {0}", targetPath, e);
            return false;
        }
    }

    /**
     * 从基础目录获取工作目录
     * 
     * @param baseDir 基础目录（如果为空，则使用系统属性user.dir）
     * @return 工作目录的绝对路径
     */
    private String getWorkingDirectory(String baseDir) {
        if (baseDir == null || baseDir.isEmpty()) {
            baseDir = System.getProperty("user.dir");
        }
        return Paths.get(baseDir, EXTENSIONS_DIR).toString();
    }

    /**
     * 获取内部存储根目录
     * 
     * @return 内部存储根目录的Path对象
     */
    public Path getInnerStorageRoot() {
        return getWorkingDirectory().resolve(INNER_STORAGE_DIR);
    }

    /**
     * 创建相对于工作目录的路径
     * 
     * @param absolutePath 绝对路径
     * @return 相对于工作目录的路径，如果不在工作目录内则返回原始路径
     */
    public String getRelativePathFromWorkingDirectory(Path absolutePath) {
        try {
            Path workingDir = getWorkingDirectory().toAbsolutePath().normalize();
            Path normalized = absolutePath.toAbsolutePath().normalize();

            if (normalized.startsWith(workingDir)) {
                return workingDir.relativize(normalized).toString();
            }
            else {
                return absolutePath.toString();
            }
        }
        catch (Exception e) {
            LoggerUtil.error(e, "获取相对路径时出错: {0}", absolutePath);
            return absolutePath.toString();
        }
    }

    /**
     * 获取ManusProperties以访问配置
     * 
     * @return ManusProperties实例
     */
    public IManusProperties getManusProperties() {
        return manusProperties;
    }

    /**
     * 仅清理子任务目录
     * 
     * @param rootPlanId 根计划ID
     * @param subTaskId 要清理的子任务ID
     * @throws IOException 如果目录删除失败
     */
    public void cleanupSubTaskDirectory(String rootPlanId, String subTaskId) throws IOException {
        Path subTaskDir = getSubTaskDirectory(rootPlanId, subTaskId);
        if (Files.exists(subTaskDir)) {
            deleteDirectoryRecursively(subTaskDir);
            LoggerUtil.info("已清理子任务目录: {0}", subTaskDir);
        }
    }

    /**
     * 清理整个根计划目录及其所有子任务
     * 
     * @param rootPlanId 要清理的根计划ID
     * @throws IOException 如果目录删除失败
     */
    public void cleanupRootPlanDirectory(String rootPlanId) throws IOException {
        Path rootPlanDir = getRootPlanDirectory(rootPlanId);
        if (Files.exists(rootPlanDir)) {
            deleteDirectoryRecursively(rootPlanDir);
            LoggerUtil.info("已清理根计划目录: {0}", rootPlanDir);
        }
    }

    /**
     * 递归删除目录及其所有内容
     * 
     * @param directory 要删除的目录
     * @throws IOException 如果删除失败
     */
    private void deleteDirectoryRecursively(Path directory) throws IOException {
        Files.walk(directory).sorted((path1, path2) -> path2.compareTo(path1)) // 先删除文件再删除目录
            .forEach(path -> {
                try {
                    Files.delete(path);
                }
                catch (IOException e) {
                    LoggerUtil.error(e, "删除失败: {0}", path);
                }
            });
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param workingDirectoryPath <br>
     */
    @Override
    public void setWorkingDirectory(String workingDirectoryPath) {
        this.workingDirectoryPath = workingDirectoryPath;
    }

}
