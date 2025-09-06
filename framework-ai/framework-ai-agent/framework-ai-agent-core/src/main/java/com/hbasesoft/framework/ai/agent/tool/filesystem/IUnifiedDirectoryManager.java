/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.filesystem;

import java.io.IOException;
import java.nio.file.Path;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;

/**
 * 统一目录管理器接口 <br>
 * 用于管理系统中各种目录的创建、访问和清理操作
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.filesystem <br>
 */

public interface IUnifiedDirectoryManager {

    /**
     * 获取工作目录路径
     * 
     * @return 工作目录路径字符串
     */
    String getWorkingDirectoryPath();

    /**
     * 获取工作目录
     * 
     * @return 工作目录Path对象
     */
    Path getWorkingDirectory();

    /**
     * 获取根计划目录
     * 
     * @param rootPlanId 根计划ID
     * @return 根计划目录Path
     */
    Path getRootPlanDirectory(String rootPlanId);

    /**
     * 获取子任务目录
     * 
     * @param rootPlanId 根计划ID
     * @param subTaskId 子任务ID
     * @return 子任务目录Path
     */
    Path getSubTaskDirectory(String rootPlanId, String subTaskId);

    /**
     * 获取指定目录
     * 
     * @param targetPath 目标路径
     * @return 指定目录Path
     * @throws IOException IO异常
     * @throws SecurityException 安全异常
     */
    Path getSpecifiedDirectory(String targetPath) throws IOException, SecurityException;

    /**
     * 确保目录存在
     * 
     * @param directory 目录
     * @throws IOException IO异常
     */
    void ensureDirectoryExists(Path directory) throws IOException;

    /**
     * 检查路径是否被允许
     * 
     * @param targetPath 目标路径
     * @return 是否被允许
     */
    boolean isPathAllowed(Path targetPath);

    /**
     * 获取内部存储根目录
     * 
     * @return 内部存储根目录Path
     */
    Path getInnerStorageRoot();

    /**
     * 获取相对于工作目录的路径
     * 
     * @param absolutePath 绝对路径
     * @return 相对路径字符串
     */
    String getRelativePathFromWorkingDirectory(Path absolutePath);

    /**
     * 获取Manus属性配置
     * 
     * @return Manus属性配置
     */
    IManusProperties getManusProperties();

    /**
     * 清理子任务目录
     * 
     * @param rootPlanId 根计划ID
     * @param subTaskId 子任务ID
     * @throws IOException IO异常
     */
    void cleanupSubTaskDirectory(String rootPlanId, String subTaskId) throws IOException;

    /**
     * 清理根计划目录
     * 
     * @param rootPlanId 根计划ID
     * @throws IOException IO异常
     */
    void cleanupRootPlanDirectory(String rootPlanId) throws IOException;

    /**
     * 设置工作目录路径
     * 
     * @param workingDirectoryPath 工作目录路径字符串
     */
    void setWorkingDirectory(String workingDirectoryPath);

}
