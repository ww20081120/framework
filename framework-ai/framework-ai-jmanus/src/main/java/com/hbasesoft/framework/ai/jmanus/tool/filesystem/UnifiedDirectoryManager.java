/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.filesystem <br>
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
		this.workingDirectoryPath = getWorkingDirectory(manusProperties.getBaseDir());
	}

	/**
	 * Get the main working directory (baseDir/extensions)
	 * 
	 * @return Absolute path of the working directory
	 */
	public String getWorkingDirectoryPath() {
		return workingDirectoryPath;
	}

	/**
	 * Get the working directory path as a Path object
	 * 
	 * @return Path object of the working directory
	 */
	public Path getWorkingDirectory() {
		return Paths.get(workingDirectoryPath);
	}

	/**
	 * Get the root plan directory (baseDir/extensions/inner_storage/rootPlanId)
	 * This directory is accessible by the current task and all its subtasks
	 * 
	 * @param rootPlanId The root plan ID
	 * @return Path object of the root plan directory
	 */
	public Path getRootPlanDirectory(String rootPlanId) {
		if (rootPlanId == null || rootPlanId.trim().isEmpty()) {
			throw new IllegalArgumentException("rootPlanId cannot be null or empty");
		}
		return getWorkingDirectory().resolve(INNER_STORAGE_DIR).resolve(rootPlanId);
	}

	/**
	 * Get a subtask directory under the root plan directory
	 * (baseDir/extensions/inner_storage/rootPlanId/subTaskId)
	 * 
	 * @param rootPlanId The root plan ID
	 * @param subTaskId  The subtask ID
	 * @return Path object of the subtask directory
	 */
	public Path getSubTaskDirectory(String rootPlanId, String subTaskId) {
		if (subTaskId == null || subTaskId.trim().isEmpty()) {
			throw new IllegalArgumentException("subTaskId cannot be null or empty");
		}
		return getRootPlanDirectory(rootPlanId).resolve(subTaskId);
	}

	/**
	 * Get a specified directory with security validation. If ManusProperties
	 * configuration does not allow external access, only directories within the
	 * working directory are allowed.
	 * 
	 * @param targetPath The target directory path (absolute or relative)
	 * @return Path object of the validated directory
	 * @throws SecurityException if access is denied
	 * @throws IOException       if path validation fails
	 */
	public Path getSpecifiedDirectory(String targetPath) throws IOException, SecurityException {
		if (targetPath == null || targetPath.trim().isEmpty()) {
			throw new IllegalArgumentException("targetPath cannot be null or empty");
		}

		Path target = Paths.get(targetPath);

		// If it's a relative path, resolve it against working directory
		if (!target.isAbsolute()) {
			target = getWorkingDirectory().resolve(targetPath);
		}

		target = target.normalize();

		// Security check: validate if target is within allowed scope
		if (!isPathAllowed(target)) {
			throw new SecurityException("Access denied: Path is outside allowed working directory scope: " + target);
		}

		return target;
	}

	/**
	 * Ensure a directory exists, creating it if necessary
	 * 
	 * @param directory The directory path to ensure
	 * @throws IOException if directory creation fails
	 */
	public void ensureDirectoryExists(Path directory) throws IOException {
		if (!Files.exists(directory)) {
			Files.createDirectories(directory);
			LoggerUtil.debug("Created directory: {0}", directory);
		}
	}

	/**
	 * Validate if a path is within the allowed working directory scope. This method
	 * enforces security by ensuring all file operations stay within the designated
	 * working directory unless external access is explicitly allowed.
	 * 
	 * @param targetPath The path to validate
	 * @return true if path is allowed, false otherwise
	 */
	public boolean isPathAllowed(Path targetPath) {
		try {
			Path workingDir = getWorkingDirectory().toAbsolutePath().normalize();
			Path normalizedTarget = targetPath.toAbsolutePath().normalize();

			// Check if target is within working directory
			boolean isWithinWorkingDir = normalizedTarget.startsWith(workingDir);

			// If within working directory, always allow
			if (isWithinWorkingDir) {
				return true;
			}

			// If outside working directory, check configuration
			boolean allowExternal = manusProperties.getAllowExternalAccess();

			LoggerUtil.debug(
					"Path validation - Working Dir: {0}, Target: {1}, Within: {2}, External Allowed: {3}, Final: {4}",
					workingDir, normalizedTarget, isWithinWorkingDir, allowExternal, allowExternal);

			return allowExternal;
		} catch (Exception e) {
			LoggerUtil.error("Error validating path: {0}", targetPath, e);
			return false;
		}
	}

	/**
	 * Get working directory from base directory
	 * 
	 * @param baseDir Base directory (if empty, use system property user.dir)
	 * @return Absolute path of the working directory
	 */
	private String getWorkingDirectory(String baseDir) {
		if (baseDir == null || baseDir.isEmpty()) {
			baseDir = System.getProperty("user.dir");
		}
		return Paths.get(baseDir, EXTENSIONS_DIR).toString();
	}

	/**
	 * Get the inner storage root directory
	 * 
	 * @return Path object of the inner storage root directory
	 */
	public Path getInnerStorageRoot() {
		return getWorkingDirectory().resolve(INNER_STORAGE_DIR);
	}

	/**
	 * Create a relative path from working directory
	 * 
	 * @param absolutePath The absolute path
	 * @return Relative path from working directory, or the original path if not
	 *         within working directory
	 */
	public String getRelativePathFromWorkingDirectory(Path absolutePath) {
		try {
			Path workingDir = getWorkingDirectory().toAbsolutePath().normalize();
			Path normalized = absolutePath.toAbsolutePath().normalize();

			if (normalized.startsWith(workingDir)) {
				return workingDir.relativize(normalized).toString();
			} else {
				return absolutePath.toString();
			}
		} catch (Exception e) {
			LoggerUtil.error(e, "Error getting relative path for: {0}", absolutePath);
			return absolutePath.toString();
		}
	}

	/**
	 * Get ManusProperties for configuration access
	 * 
	 * @return ManusProperties instance
	 */
	public IManusProperties getManusProperties() {
		return manusProperties;
	}

	/**
	 * Clean up a subtask directory only
	 * 
	 * @param rootPlanId The root plan ID
	 * @param subTaskId  The subtask ID to clean up
	 * @throws IOException if directory deletion fails
	 */
	public void cleanupSubTaskDirectory(String rootPlanId, String subTaskId) throws IOException {
		Path subTaskDir = getSubTaskDirectory(rootPlanId, subTaskId);
		if (Files.exists(subTaskDir)) {
			deleteDirectoryRecursively(subTaskDir);
			LoggerUtil.info("Cleaned up subtask directory: {0}", subTaskDir);
		}
	}

	/**
	 * Clean up the entire root plan directory and all its subtasks
	 * 
	 * @param rootPlanId The root plan ID to clean up
	 * @throws IOException if directory deletion fails
	 */
	public void cleanupRootPlanDirectory(String rootPlanId) throws IOException {
		Path rootPlanDir = getRootPlanDirectory(rootPlanId);
		if (Files.exists(rootPlanDir)) {
			deleteDirectoryRecursively(rootPlanDir);
			LoggerUtil.info("Cleaned up root plan directory: {0}", rootPlanDir);
		}
	}

	/**
	 * Recursively delete a directory and all its contents
	 * 
	 * @param directory The directory to delete
	 * @throws IOException if deletion fails
	 */
	private void deleteDirectoryRecursively(Path directory) throws IOException {
		Files.walk(directory).sorted((path1, path2) -> path2.compareTo(path1)) // Delete files before
																				// directories
				.forEach(path -> {
					try {
						Files.delete(path);
					} catch (IOException e) {
						LoggerUtil.error(e, "Failed to delete: {0}", path);
					}
				});
	}

}
