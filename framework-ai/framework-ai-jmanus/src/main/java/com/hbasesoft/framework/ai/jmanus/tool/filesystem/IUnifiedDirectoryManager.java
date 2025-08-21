/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.filesystem;

import java.io.IOException;
import java.nio.file.Path;

import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;

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

public interface IUnifiedDirectoryManager {

	/**
	 * Get working directory path
	 * @return Working directory path string
	 */
	String getWorkingDirectoryPath();

	/**
	 * Get working directory
	 * @return Working directory Path object
	 */
	Path getWorkingDirectory();

	/**
	 * Get root plan directory
	 * @param rootPlanId Root plan ID
	 * @return Root plan directory Path
	 */
	Path getRootPlanDirectory(String rootPlanId);

	/**
	 * Get subtask directory
	 * @param rootPlanId Root plan ID
	 * @param subTaskId Subtask ID
	 * @return Subtask directory Path
	 */
	Path getSubTaskDirectory(String rootPlanId, String subTaskId);

	/**
	 * Get specified directory
	 * @param targetPath Target path
	 * @return Specified directory Path
	 * @throws IOException IO exception
	 * @throws SecurityException Security exception
	 */
	Path getSpecifiedDirectory(String targetPath) throws IOException, SecurityException;

	/**
	 * Ensure directory exists
	 * @param directory Directory
	 * @throws IOException IO exception
	 */
	void ensureDirectoryExists(Path directory) throws IOException;

	/**
	 * Check if path is allowed
	 * @param targetPath Target path
	 * @return Whether allowed
	 */
	boolean isPathAllowed(Path targetPath);

	/**
	 * Get internal storage root directory
	 * @return Internal storage root directory Path
	 */
	Path getInnerStorageRoot();

	/**
	 * Get relative path from working directory
	 * @param absolutePath Absolute path
	 * @return Relative path string
	 */
	String getRelativePathFromWorkingDirectory(Path absolutePath);

	/**
	 * Get Manus properties
	 * @return Manus properties
	 */
	IManusProperties getManusProperties();

	/**
	 * Clean up subtask directory
	 * @param rootPlanId Root plan ID
	 * @param subTaskId Subtask ID
	 * @throws IOException IO exception
	 */
	void cleanupSubTaskDirectory(String rootPlanId, String subTaskId) throws IOException;

	/**
	 * Clean up root plan directory
	 * @param rootPlanId Root plan ID
	 * @throws IOException IO exception
	 */
	void cleanupRootPlanDirectory(String rootPlanId) throws IOException;

}
