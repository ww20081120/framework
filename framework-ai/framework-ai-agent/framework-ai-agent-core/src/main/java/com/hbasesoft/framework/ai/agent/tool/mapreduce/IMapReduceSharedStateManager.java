/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.mapreduce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.mapreduce <br>
 */
public interface IMapReduceSharedStateManager {


	/**
	 * Get or create plan state
	 * @param planId Plan ID
	 * @return Plan state
	 */
	IMapReduceSharedStateManager.PlanState getOrCreatePlanState(String planId);

	/**
	 * Get plan state
	 * @param planId Plan ID
	 * @return Plan state
	 */
	IMapReduceSharedStateManager.PlanState getPlanState(String planId);

	/**
	 * Clean up plan state
	 * @param planId Plan ID
	 */
	void cleanupPlanState(String planId);

	/**
	 * Get next task ID
	 * @param planId Plan ID
	 * @return Next task ID
	 */
	String getNextTaskId(String planId);

	/**
	 * Add split result
	 * @param planId Plan ID
	 * @param taskDirectory Task directory
	 */
	void addSplitResult(String planId, String taskDirectory);

	/**
	 * Get split results
	 * @param planId Plan ID
	 * @return Split results list
	 */
	List<String> getSplitResults(String planId);

	/**
	 * Set split results
	 * @param planId Plan ID
	 * @param splitResults Split results list
	 */
	void setSplitResults(String planId, List<String> splitResults);

	/**
	 * Record Map task status
	 * @param planId Plan ID
	 * @param taskId Task ID
	 * @param taskStatus Task status
	 */
	void recordMapTaskStatus(String planId, String taskId, IMapReduceSharedStateManager.TaskStatus taskStatus);

	/**
	 * Get Map task status
	 * @param planId Plan ID
	 * @param taskId Task ID
	 * @return Task status
	 */
	IMapReduceSharedStateManager.TaskStatus getMapTaskStatus(String planId, String taskId);

	/**
	 * Get all Map task statuses
	 * @param planId Plan ID
	 * @return All task statuses
	 */
	Map<String, IMapReduceSharedStateManager.TaskStatus> getAllMapTaskStatuses(String planId);

	/**
	 * Set last operation result
	 * @param planId Plan ID
	 * @param result Operation result
	 */
	void setLastOperationResult(String planId, String result);

	/**
	 * Get last operation result
	 * @param planId Plan ID
	 * @return Last operation result
	 */
	String getLastOperationResult(String planId);

	/**
	 * Set last processed file
	 * @param planId Plan ID
	 * @param filePath File path
	 */
	void setLastProcessedFile(String planId, String filePath);

	/**
	 * Get last processed file
	 * @param planId Plan ID
	 * @return Last processed file path
	 */
	String getLastProcessedFile(String planId);

	/**
	 * Get current tool status string
	 * @param planId Plan ID
	 * @return Current tool status string
	 */
	String getCurrentToolStateString(String planId);

	/**
	 * Get all plan overview
	 * @return All plan overview string
	 */
	String getAllPlansOverview();

	/**
	 * Clean up all plan states
	 */
	void cleanupAllPlanStates();
	
	/**
	 * Plan state inner class containing all shared state information for a single plan
	 */
	public static class PlanState {

		// Map task status management
		private final Map<String, TaskStatus> mapTaskStatuses = new ConcurrentHashMap<>();

		// Task counter for generating task IDs
		private final AtomicInteger taskCounter = new AtomicInteger(1);

		// Split results list
		private final List<String> splitResults = Collections.synchronizedList(new ArrayList<>());

		// Last operation result
		private volatile String lastOperationResult = "";

		// Last processed file
		private volatile String lastProcessedFile = "";

		// Creation timestamp
		private final long createTime = System.currentTimeMillis();

		public Map<String, TaskStatus> getMapTaskStatuses() {
			return mapTaskStatuses;
		}

		public AtomicInteger getTaskCounter() {
			return taskCounter;
		}

		public List<String> getSplitResults() {
			return splitResults;
		}

		public String getLastOperationResult() {
			return lastOperationResult;
		}

		public void setLastOperationResult(String lastOperationResult) {
			this.lastOperationResult = lastOperationResult;
		}

		public String getLastProcessedFile() {
			return lastProcessedFile;
		}

		public void setLastProcessedFile(String lastProcessedFile) {
			this.lastProcessedFile = lastProcessedFile;
		}

		public long getCreateTime() {
			return createTime;
		}

	}

	/**
	 * Task status class
	 */
	public static class TaskStatus {

		public String taskId;

		public String inputFile;

		public String outputFilePath;

		public String status;

		public String timestamp;

		public TaskStatus() {
		}

		public TaskStatus(String taskId, String status) {
			this.taskId = taskId;
			this.status = status;
		}

	}

}
