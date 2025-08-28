/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.innerStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.innerStorage <br>
 */

@Service
public class SmartContentSavingService implements ISmartContentSavingService {

	private final IManusProperties manusProperties;

	private final UnifiedDirectoryManager directoryManager;

	public SmartContentSavingService(IManusProperties manusProperties, UnifiedDirectoryManager directoryManager) {
		this.manusProperties = manusProperties;
		this.directoryManager = directoryManager;
	}

	public IManusProperties getManusProperties() {
		return manusProperties;
	}

	/**
	 * Intelligently process content, automatically store and return summary if
	 * content is too long
	 * 
	 * @param planId        Plan ID
	 * @param content       Content
	 * @param callingMethod Calling method name
	 * @return Processing result containing filename and summary
	 */
	public SmartProcessResult processContent(String planId, String content, String callingMethod) {
		if (planId == null || content == null) {
			return new SmartProcessResult(null, content);
		}

		// Check if infinite context is enabled
		boolean infiniteContextEnabled = isInfiniteContextEnabled();

		if (!infiniteContextEnabled) {
			// When infinite context is disabled, return content directly without any
			// processing
			LoggerUtil.info(
					"Infinite context disabled for plan {0}, returning content directly without smart processing",
					planId);
			return new SmartProcessResult(null, content);
		}

		// Use configured threshold from ManusProperties when infinite context is
		// enabled
		int threshold = manusProperties.getInfiniteContextTaskContextSize();

		LoggerUtil.info(
				"Processing content for plan {0}: content length = {1}, threshold = {2}, infinite context enabled",
				planId, content.length(), threshold);

		// If content is within threshold, return directly
		if (content.length() <= threshold) {
			LoggerUtil.info("Content length {0} is within threshold {1}, returning original content", content.length(),
					threshold);
			return new SmartProcessResult(null, content);
		}

		LoggerUtil.info("Content length {0} exceeds threshold {1}, triggering auto storage", content.length(),
				threshold);

		try {
			// Generate storage filename
			String storageFileName = generateStorageFileName(planId);

			// Ensure plan directory exists - store directly in planId directory, not
			// using agent subdirectory
			Path planDir = directoryManager.getRootPlanDirectory(planId);
			directoryManager.ensureDirectoryExists(planDir);

			// Save detailed content to InnerStorage - store directly in plan directory
			Path storagePath = planDir.resolve(storageFileName);
			saveDetailedContentToStorage(storagePath, content, planId);

			// Generate simplified summary
			String summary = generateSmartSummary(content, storageFileName, callingMethod);

			LoggerUtil.info("Content exceeds threshold ({0} bytes), saved to storage file: {1}", threshold,
					storageFileName);

			return new SmartProcessResult(storageFileName, summary);

		} catch (IOException e) {
			LoggerUtil.error("Failed to save content to storage for plan {0}", planId, e);
			// If save fails, return truncated content
			return new SmartProcessResult(null,
					content.substring(0, threshold) + "\n\n... (Content too long, truncated)");
		}
	}

	/**
	 * Generate storage filename - format: planId_timestamp_random4digits.md
	 */
	private String generateStorageFileName(String planId) {
		String timestamp = java.time.LocalDateTime.now()
				.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		// Generate 4-digit random number
		int randomNum = (int) (Math.random() * 9000) + 1000; // 1000-9999
		return String.format("%s_%s_%04d.md", planId, timestamp, randomNum);
	}

	/**
	 * Save detailed content to storage
	 */
	private void saveDetailedContentToStorage(Path storagePath, String content, String planId) throws IOException {
		StringBuilder detailedContent = new StringBuilder();
		detailedContent.append(content);

		Files.writeString(storagePath, detailedContent.toString(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Generate intelligent summary
	 */
	private String generateSmartSummary(String content, String storageFileName, String callingMethod) {
		// Build calling method information
		String methodInfo = (callingMethod != null && !callingMethod.trim().isEmpty())
				? "Successfully called " + callingMethod + " function,\n\n"
				: "";

		return String.format(
				"""
						%sBut the function returned content is too long, so it was automatically stored in a file

						## You can freely use the following two operations to meet user expectations (no need to follow order, but according to user expectations)

						### Operation 1: Use inner_storage_content_tool to get specific content
						```json
						{
						  "action": "extract_relevant_content",
						  "file_name": "%s",
						  "query_key": "Keywords or questions you want to query, be specific and don't miss any requirements from user requests"
						}
						```

						### Operation 2: Use file_merge_tool to aggregate (or copy) files to specified folder
						```json
						{
						  "action": "merge_file",
						  "file_name": "%s",
						  "target_folder": "merged_data"
						}
						```

						Please choose appropriate tools and parameters for subsequent operations based on specific requirements.

						""",
				methodInfo, storageFileName, storageFileName);
	}

	/**
	 * Check if infinite context is enabled
	 * 
	 * @return true if infinite context is enabled, false otherwise
	 */
	private boolean isInfiniteContextEnabled() {
		if (manusProperties != null) {
			Boolean enabled = manusProperties.getInfiniteContextEnabled();
			return enabled != null ? enabled : false;
		}
		return false;
	}

}
