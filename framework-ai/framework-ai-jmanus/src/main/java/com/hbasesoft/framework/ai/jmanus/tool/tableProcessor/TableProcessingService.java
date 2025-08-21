/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.tableProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ConverterUtils;
import com.hbasesoft.framework.ai.jmanus.tool.filesystem.UnifiedDirectoryManager;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.tableProcessor <br>
 */
@Service
public class TableProcessingService implements ITableProcessingService {

	// Supported file extensions for table processing
	private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();

	static {
		SUPPORTED_EXTENSIONS.add(".xlsx");
		SUPPORTED_EXTENSIONS.add(".xls");
		SUPPORTED_EXTENSIONS.add(".csv");
	}

	private final UnifiedDirectoryManager unifiedDirectoryManager;

	// Store file states for each plan
	private final Map<String, Map<String, String>> planFileStates = new ConcurrentHashMap<>();

	// Store current file paths for each plan
	private final Map<String, String> currentFilePaths = new ConcurrentHashMap<>();

	public TableProcessingService(UnifiedDirectoryManager unifiedDirectoryManager) {
		this.unifiedDirectoryManager = unifiedDirectoryManager;
	}

	/**
	 * Header reading listener - specifically for reading header data Based on
	 * EasyExcel official documentation best practices
	 */
	private static class HeaderDataListener implements ReadListener<Map<Integer, String>> {

		private final List<String> headers = new ArrayList<>();

		/**
		 * This will return headers row by row
		 */
		@Override
		public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
			LoggerUtil.debug("Parsed a header data:{0}", headMap);
			// Use ConverterUtils to convert to Map<Integer,String>
			Map<Integer, String> stringHeadMap = ConverterUtils.convertToStringMap(headMap, context);

			// Sort headers by column index and extract values
			List<String> headRow = stringHeadMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
					.map(Map.Entry::getValue).collect(Collectors.toList());

			if (!headRow.isEmpty()) {
				headers.clear();
				headers.addAll(headRow);
				LoggerUtil.debug("Extracted headers: {0}", headers);
			}
		}

		/**
		 * Process data rows - for header reading, we mainly focus on invokeHead method
		 */
		@Override
		public void invoke(Map<Integer, String> data, AnalysisContext context) {
			// For header reading, this method is not the main focus
			// But we can use the first row of data as a backup header (if invokeHead is
			// not called)
			if (headers.isEmpty() && data != null && !data.isEmpty()) {
				List<String> firstRowAsHeaders = data.entrySet().stream().sorted(Map.Entry.comparingByKey())
						.map(Map.Entry::getValue).collect(Collectors.toList());
				headers.addAll(firstRowAsHeaders);
				LoggerUtil.debug("Using first row data as headers: {0}", headers);
			}
		}

		/**
		 * Called after all data parsing is completed
		 */
		@Override
		public void doAfterAllAnalysed(AnalysisContext context) {
			LoggerUtil.debug("Header data parsing completed, got {0} headers in total", headers.size());
		}

		public List<String> getHeaders() {
			return new ArrayList<>(headers);
		}

	}

	/**
	 * Check if the file type is supported
	 * 
	 * @param filePath file path
	 * @return true if supported, false otherwise
	 */
	public boolean isSupportedFileType(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			return false;
		}

		String lowerPath = filePath.toLowerCase();
		return SUPPORTED_EXTENSIONS.stream().anyMatch(lowerPath::endsWith);
	}

	/**
	 * Validate and get absolute file path
	 * 
	 * @param planId   plan ID
	 * @param filePath file path (relative or absolute)
	 * @return absolute Path object
	 * @throws IOException if path validation fails
	 */
	public Path validateFilePath(String planId, String filePath) throws IOException {
		if (filePath == null || filePath.isEmpty()) {
			throw new IOException("File path cannot be null or empty");
		}

		// Get plan directory
		Path planDir = unifiedDirectoryManager.getRootPlanDirectory(planId);

		// Handle relative vs absolute paths
		Path path;
		if (Paths.get(filePath).isAbsolute()) {
			path = Paths.get(filePath);
		} else {
			path = planDir.resolve(filePath);
		}

		// Only update current file path if the file actually exists or we're creating
		// it
		// This prevents overwriting the current path during read operations
		if (Files.exists(path) || !currentFilePaths.containsKey(planId)) {
			currentFilePaths.put(planId, filePath);
		}

		return path;
	}

	/**
	 * Get absolute path for a given relative path
	 * 
	 * @param planId   plan ID
	 * @param filePath file path
	 * @return absolute Path
	 * @throws IOException if path resolution fails
	 */
	public Path getAbsolutePath(String planId, String filePath) throws IOException {
		Path planDir = unifiedDirectoryManager.getRootPlanDirectory(planId);
		return planDir.resolve(filePath);
	}

	/**
	 * Update file state for a plan
	 * 
	 * @param planId   plan ID
	 * @param filePath file path
	 * @param state    state message
	 */
	public void updateFileState(String planId, String filePath, String state) {
		if (planId == null || filePath == null) {
			return;
		}

		planFileStates.computeIfAbsent(planId, k -> new ConcurrentHashMap<>());
		planFileStates.get(planId).put(filePath, state);
		LoggerUtil.debug("Updated file state for planId={0}, filePath={1}, state={2}", planId, filePath, state);
	}

	/**
	 * Get last operation result for a plan
	 * 
	 * @param planId plan ID
	 * @return last operation result
	 */
	public String getLastOperationResult(String planId) {
		if (planId == null) {
			return "";
		}

		Map<String, String> fileStates = planFileStates.get(planId);
		if (fileStates == null || fileStates.isEmpty()) {
			return "";
		}

		// Get the current file path and return its state
		String currentFile = currentFilePaths.get(planId);
		if (currentFile != null && fileStates.containsKey(currentFile)) {
			return fileStates.get(currentFile);
		}

		// If no current file, return the first available state
		return fileStates.values().iterator().next();
	}

	/**
	 * Get current file path for a plan
	 * 
	 * @param planId plan ID
	 * @return current file path
	 */
	public String getCurrentFilePath(String planId) {
		return currentFilePaths.getOrDefault(planId, "");
	}

	/**
	 * Create a new table with headers
	 * 
	 * @param planId    plan ID
	 * @param filePath  relative file path (absolute path will cause an error)
	 * @param sheetName sheet name
	 * @param headers   list of headers (ID column will be added as the first
	 *                  column)
	 * @throws IOException if file operation fails
	 */
	public void createTable(String planId, String filePath, String sheetName, List<String> headers) throws IOException {
		if (Paths.get(filePath).isAbsolute()) {
			throw new IOException("Absolute path is not allowed: " + filePath);
		}

		// Check if headers contain "ID" (case-insensitive)
		if (headers != null) {
			for (String header : headers) {
				if ("ID".equalsIgnoreCase(header)) {
					throw new IOException(
							"ID is a reserved column name and cannot be used as a header. Please use a different column name.");
				}
			}
		}

		Path absolutePath = validateFilePath(planId, filePath);

		// Ensure parent directory exists
		Files.createDirectories(absolutePath.getParent());

		// Create headers with ID as the first column
		List<String> finalHeaders = new ArrayList<>();
		finalHeaders.add("ID");
		if (headers != null) {
			finalHeaders.addAll(headers);
		}

		// Create table with headers - using simple data writing method
		// Write headers as the first row of data
		List<List<String>> tableData = new ArrayList<>();
		tableData.add(finalHeaders); // First row is headers

		// Write headers to file
		String actualSheetName = (sheetName != null && !sheetName.isEmpty()) ? sheetName : "Sheet1";
		// Use model-free writing method to directly write data (including header row)
		LoggerUtil.debug("Writing table data to file: {0}, sheetName: {1}, tableData: {2}", absolutePath,
				actualSheetName, tableData);
		EasyExcel.write(absolutePath.toFile()).sheet(actualSheetName).doWrite(tableData);
		LoggerUtil.debug("Successfully created table file: {0}", absolutePath);

		updateFileState(planId, filePath, "Success: Table created with headers");
		LoggerUtil.info("Created table with headers for planId={0}, filePath={1}", planId, filePath);
	}

	/**
	 * Get table structure (headers) using EasyExcel's official best practice
	 * Rewritten according to EasyExcel official documentation header reading best
	 * practices
	 * 
	 * @param planId   plan ID
	 * @param filePath file path (relative or absolute)
	 * @return list of headers
	 * @throws IOException if file operation fails
	 */
	public List<String> getTableStructure(String planId, String filePath) throws IOException {
		Path absolutePath = validateFilePath(planId, filePath);

		if (!Files.exists(absolutePath)) {
			throw new IOException("File does not exist: " + absolutePath);
		}

		// Add file basic information debugging
		try {
			long fileSize = Files.size(absolutePath);
			boolean isReadable = Files.isReadable(absolutePath);
			LoggerUtil.debug("File info - path: {0}, size: {1} bytes, readable: {2}", absolutePath, fileSize,
					isReadable);
		} catch (Exception e) {
			LoggerUtil.warn("Failed to get file info: {0}", e.getMessage());
		}

		LoggerUtil.debug("Reading table structure from: {0}", absolutePath);

		// Use official recommended listener method to read headers
		HeaderDataListener headerListener = new HeaderDataListener();

		try {
			// Use EasyExcel's official method to read header data
			EasyExcel.read(absolutePath.toFile(), headerListener).sheet().doRead();

			List<String> headers = headerListener.getHeaders();
			LoggerUtil.debug("Successfully extracted headers using listener: {0}", headers);

			if (headers.isEmpty()) {
				LoggerUtil.warn("No headers found in file: {0}", absolutePath);
				// If listener method fails, try backup method
				return fallbackReadHeaders(absolutePath);
			}

			// Remove ID column from returned headers if it exists and is the first column
			if (!headers.isEmpty() && "ID".equals(headers.get(0))) {
				return new ArrayList<>(headers.subList(1, headers.size()));
			}

			return headers;
		} catch (Exception e) {
			LoggerUtil.warn("Failed to read headers using listener, trying fallback method: {0}", e.getMessage());
			List<String> headers = fallbackReadHeaders(absolutePath);

			// Remove ID column from returned headers if it exists and is the first column
			if (!headers.isEmpty() && "ID".equals(headers.get(0))) {
				return new ArrayList<>(headers.subList(1, headers.size()));
			}

			return headers;
		}
	}

	/**
	 * Backup header reading method - used when listener method fails
	 */
	private List<String> fallbackReadHeaders(Path absolutePath) throws IOException {
		LoggerUtil.debug("Using fallback method to read headers from: {0}", absolutePath);

		try {
			// Try synchronous reading method
			// Read from row 0, do not skip headers
			List<Map<Integer, String>> rawData = EasyExcel.read(absolutePath.toFile()).sheet().headRowNumber(0)
					.doReadSync();

			LoggerUtil.debug("Fallback raw data size: {0}", rawData.size());

			if (!rawData.isEmpty()) {
				Map<Integer, String> headerRow = rawData.get(0);
				List<String> headers = headerRow.entrySet().stream().sorted(Map.Entry.comparingByKey())
						.map(Map.Entry::getValue).collect(Collectors.toList());

				LoggerUtil.debug("Fallback extracted headers: {0}", headers);
				return headers;
			}
		} catch (Exception e) {
			LoggerUtil.error("Fallback method also failed: {0}", e.getMessage());
		}

		return new ArrayList<>();
	}

	/**
	 * Write data to table, ensuring data matches header size
	 * 
	 * @param planId   plan ID
	 * @param filePath file path (relative or absolute)
	 * @param data     data to write (must match header size)
	 * @throws IOException if file operation fails or data size mismatch
	 */
	public void writeDataToTable(String planId, String filePath, List<String> data) throws IOException {
		Path absolutePath = validateFilePath(planId, filePath);

		if (!Files.exists(absolutePath)) {
			throw new IOException("File does not exist: " + absolutePath);
		}

		// Get table structure to validate data size
		List<String> headers = getTableStructure(planId, filePath);

		// Check if table has auto-generated ID column
		boolean hasIdColumn = !headers.isEmpty() && "ID".equals(headers.get(0));
		int expectedDataSize = hasIdColumn ? headers.size() - 1 : headers.size();

		// Validate data size (excluding auto-generated ID column if present)
		if (data.size() != expectedDataSize) {
			throw new IOException(String.format(
					"Data size mismatch. Expected: %d columns (excluding ID), Actual: %d columns. Headers: %s",
					expectedDataSize, data.size(), headers));
		}

		// Read existing data
		List<List<String>> existingData = readAllData(planId, filePath);

		// Process data based on whether ID column exists
		List<String> dataToWrite;
		if (hasIdColumn) {
			// Check if the first element in data is a valid ID for update
			if (!data.isEmpty() && isNumeric(data.get(0))) {
				String idToUpdate = data.get(0);
				List<String> updatedData = new ArrayList<>();
				updatedData.add(idToUpdate); // Add ID as first column
				updatedData.addAll(data.subList(1, data.size())); // Add remaining data

				// Try to find and update existing row with this ID
				boolean updated = false;
				for (int i = 0; i < existingData.size(); i++) {
					List<String> row = existingData.get(i);
					if (row.size() > 0 && idToUpdate.equals(row.get(0))) {
						existingData.set(i, updatedData);
						updated = true;
						break;
					}
				}

				// If not found, add as new row with specified ID
				if (!updated) {
					existingData.add(updatedData);
				}

				dataToWrite = null; // We've already handled adding the data
			} else {
				// Auto-generate ID as the first column
				String nextId = String.valueOf(existingData.size() > 0 ? existingData.size() - 1 : 0);
				dataToWrite = new ArrayList<>();
				dataToWrite.add(nextId);
				dataToWrite.addAll(data);
			}
		} else {
			// No ID column, use data as is
			dataToWrite = new ArrayList<>(data);
		}

		// Add new data if not already handled
		if (dataToWrite != null) {
			existingData.add(dataToWrite);
		}

		// Write all data back
		String sheetName = getSheetName(absolutePath);
		// Use model-free writing method, according to official documentation best
		// practices
		LoggerUtil.debug("Writing data to file: {0}, sheetName: {1}, dataSize: {2}", absolutePath, sheetName,
				existingData.size());
		if (!existingData.isEmpty()) {
			LoggerUtil.debug("First row (headers): {0}", existingData.get(0));
			if (existingData.size() > 1) {
				LoggerUtil.debug("Second row (first data): {0}", existingData.get(1));
			}
		}
		EasyExcel.write(absolutePath.toFile()).sheet(sheetName).doWrite(existingData);
		LoggerUtil.debug("Successfully wrote data to file: {0}", absolutePath);
		updateFileState(planId, filePath, "Success: Data written to table");
		LoggerUtil.info("Written data to table for planId={0}, filePath={1}", planId, filePath);
	}

	/**
	 * Write multiple rows of data to table, ensuring data matches header size
	 * 
	 * @param planId   plan ID
	 * @param filePath file path (relative or absolute)
	 * @param data     list of data rows to write (each row must match header size)
	 * @throws IOException if file operation fails or data size mismatch
	 */
	public void writeMultipleRowsToTable(String planId, String filePath, List<List<String>> data) throws IOException {
		LoggerUtil.info("Writing multiple rows to table for planId={0}, filePath={1}", planId, filePath);
		Path absolutePath = validateFilePath(planId, filePath);

		// Check file type
		if (!isSupportedFileType(filePath)) {
			updateFileState(planId, filePath, "Error: Unsupported file type");
			throw new IOException(
					"Unsupported file type. Only Excel (.xlsx, .xls) and CSV (.csv) files are supported.");
		}

		// Get headers to validate data size
		List<String> headers = getTableStructure(planId, filePath);
		if (headers.isEmpty()) {
			updateFileState(planId, filePath, "Error: Empty table or failed to read headers");
			throw new IOException("Empty table or failed to read header information");
		}

		// Check if table has auto-generated ID column
		boolean hasIdColumn = !headers.isEmpty() && "ID".equals(headers.get(0));
		int expectedDataSize = hasIdColumn ? headers.size() - 1 : headers.size();

		// Validate all data rows
		for (int i = 0; i < data.size(); i++) {
			List<String> row = data.get(i);
			if (row.size() != expectedDataSize) {
				String errorMsg = String.format(
						"Data column count mismatch. Expected: %d columns (excluding ID column), Actual: %d columns. Headers: %s, Row %d data: %s",
						expectedDataSize, row.size(), headers, i + 1, row);
				updateFileState(planId, filePath, "Error: Data size mismatch");
				throw new IOException(errorMsg);
			}
		}

		// Read existing data
		List<List<String>> existingData = readAllData(planId, filePath);

		// Process data rows
		if (hasIdColumn) {
			// Handle rows with potential ID updates
			for (List<String> row : data) {
				if (!row.isEmpty() && isNumeric(row.get(0))) {
					// Row contains an ID, try to update
					String idToUpdate = row.get(0);
					List<String> updatedData = new ArrayList<>();
					updatedData.add(idToUpdate); // Add ID as first column
					updatedData.addAll(row.subList(1, row.size())); // Add remaining data

					// Try to find and update existing row with this ID
					boolean updated = false;
					for (int i = 0; i < existingData.size(); i++) {
						List<String> existingRow = existingData.get(i);
						if (existingRow.size() > 0 && idToUpdate.equals(existingRow.get(0))) {
							existingData.set(i, updatedData);
							updated = true;
							break;
						}
					}

					// If not found, add as new row
					if (!updated) {
						existingData.add(updatedData);
					}
				} else {
					// No ID specified, generate one
					String nextId = String.valueOf(existingData.size() > 0 ? existingData.size() - 1 : 0);
					List<String> dataToWrite = new ArrayList<>();
					dataToWrite.add(nextId);
					dataToWrite.addAll(row);
					existingData.add(dataToWrite);
				}
			}
		} else {
			// No ID column, add rows as is
			existingData.addAll(data);
		}

		// Write all data back
		String sheetName = getSheetName(absolutePath);
		LoggerUtil.debug("Writing multiple rows to file: {0}, sheetName: {1}, dataSize: {2}", absolutePath, sheetName,
				existingData.size());
		if (!existingData.isEmpty()) {
			LoggerUtil.debug("First row (headers): {0}", existingData.get(0));
			if (existingData.size() > 1) {
				LoggerUtil.debug("Second row (first data): {0}", existingData.get(1));
			}
		}
		EasyExcel.write(absolutePath.toFile()).sheet(sheetName).doWrite(existingData);
		LoggerUtil.debug("Successfully wrote multiple rows to file: {0}", absolutePath);
		updateFileState(planId, filePath, "Success: Multiple rows written to table");
		LoggerUtil.info("Written multiple rows to table for planId={0}, filePath={1}", planId, filePath);
	}

	/**
	 * Search for rows matching keywords
	 * 
	 * @param planId   plan ID
	 * @param filePath file path (relative or absolute)
	 * @param keywords list of keywords to search for
	 * @return list of matching rows
	 * @throws IOException if file operation fails
	 */
	public List<List<String>> searchRows(String planId, String filePath, List<String> keywords) throws IOException {
		Path absolutePath = validateFilePath(planId, filePath);

		if (!Files.exists(absolutePath)) {
			throw new IOException("File does not exist: " + absolutePath);
		}

		List<List<String>> allData = readAllData(planId, filePath);
		if (allData.isEmpty()) {
			return new ArrayList<>();
		}

		// Skip header row and search in data rows
		List<List<String>> dataRows = allData.subList(1, allData.size());

		return dataRows.stream().filter(
				row -> keywords.stream().anyMatch(keyword -> row.stream().anyMatch(cell -> cell.contains(keyword))))
				.collect(Collectors.toList());
	}

	/**
	 * Delete rows by list of row indices
	 * 
	 * @param planId     plan ID
	 * @param filePath   file path (relative or absolute)
	 * @param rowIndices list of row indices to delete (0-based, excluding header)
	 * @throws IOException if file operation fails
	 */
	public void deleteRowsByList(String planId, String filePath, List<Integer> rowIndices) throws IOException {
		Path absolutePath = validateFilePath(planId, filePath);

		if (!Files.exists(absolutePath)) {
			throw new IOException("File does not exist: " + absolutePath);
		}

		List<List<String>> allData = readAllData(planId, filePath);
		if (allData.isEmpty()) {
			return;
		}

		// Validate indices
		int dataRowCount = allData.size() - 1; // Exclude header row
		for (Integer index : rowIndices) {
			if (index < 0 || index >= dataRowCount) {
				throw new IOException(
						"Row index out of bounds: " + index + " (valid range: 0-" + (dataRowCount - 1) + ")");
			}
		}

		// Sort indices in descending order to avoid index shifting issues
		List<Integer> sortedIndices = rowIndices.stream().distinct() // Remove duplicates
				.sorted((a, b) -> b.compareTo(a)).collect(Collectors.toList());

		// Remove rows (adjusting for header row)
		for (Integer index : sortedIndices) {
			// Add 1 to index to account for header row
			int actualIndex = index + 1;
			if (actualIndex < allData.size()) {
				allData.remove(actualIndex);
			}
		}

		// Write data back
		String sheetName = getSheetName(absolutePath);
		// Use model-free writing method, according to official documentation best
		// practices
		EasyExcel.write(absolutePath.toFile()).sheet(sheetName).doWrite(allData);

		updateFileState(planId, filePath, "Success: Rows deleted");
		LoggerUtil.info("Deleted rows for planId={0}, filePath={1}, indices={2}", planId, filePath, rowIndices);
	}

	/**
	 * Read all data from table
	 * 
	 * @param planId   plan ID
	 * @param filePath file path (relative or absolute)
	 * @return list of all data rows
	 * @throws IOException if file operation fails
	 */
	private List<List<String>> readAllData(String planId, String filePath) throws IOException {
		Path absolutePath = validateFilePath(planId, filePath);

		LoggerUtil.debug("Reading all data from: {0}", absolutePath);

		// Try different reading methods
		List<Map<Integer, String>> rawData = null;

		// Method 1: Do not specify headRowNumber, read all data including headers
		try {
			rawData = EasyExcel.read(absolutePath.toFile()).sheet().headRowNumber(0) // Read from row 0, do not skip
																						// headers
					.doReadSync();
			LoggerUtil.debug("Raw data size with headRowNumber=0: {0}", rawData.size());
		} catch (Exception e) {
			LoggerUtil.warn("Failed to read with headRowNumber=0: {0}", e.getMessage());
		}

		// Method 2: If above fails, try default method
		if (rawData == null || rawData.isEmpty()) {
			try {
				rawData = EasyExcel.read(absolutePath.toFile()).sheet().doReadSync();
				LoggerUtil.debug("Raw data size without headRowNumber: {0}", rawData.size());
			} catch (Exception e) {
				LoggerUtil.warn("Failed to read without headRowNumber: {0}", e.getMessage());
			}
		}

		// Method 3: If still fails, try specifying headRowNumber=1
		if (rawData == null || rawData.isEmpty()) {
			try {
				rawData = EasyExcel.read(absolutePath.toFile()).sheet().headRowNumber(1).doReadSync();
				LoggerUtil.debug("Raw data size with headRowNumber=1: {0}", rawData.size());
			} catch (Exception e) {
				LoggerUtil.warn("Failed to read with headRowNumber=1: {0}", e.getMessage());
			}
		}

		if (rawData == null) {
			rawData = new ArrayList<>();
		}

		LoggerUtil.debug("Raw data size in readAllData: {0}", rawData.size());

		List<List<String>> result = rawData.stream().map(row -> row.entrySet().stream()
				.sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList()))
				.collect(Collectors.toList());

		LoggerUtil.debug("Converted data size: {0}", result.size());

		// If the first column is ID, remove it from all rows
		if (!result.isEmpty() && !result.get(0).isEmpty() && "ID".equals(result.get(0).get(0))) {
			return result.stream().map(row -> row.subList(1, row.size())) // Remove first column (ID)
					.collect(Collectors.toList());
		}

		return result;
	}

	/**
	 * Get sheet name from file
	 * 
	 * @param filePath file path
	 * @return sheet name
	 * @throws IOException if file operation fails
	 */
	private String getSheetName(Path filePath) throws IOException {
		List<ReadSheet> sheets = EasyExcel.read(filePath.toFile()).build().excelExecutor().sheetList();
		return sheets.isEmpty() ? "Sheet1" : sheets.get(0).getSheetName();
	}

	/**
	 * Clean up plan directory resources
	 * 
	 * @param planId plan ID
	 */
	public void cleanupPlanDirectory(String planId) {
		if (planId == null) {
			return;
		}

		// Clean up file states
		planFileStates.remove(planId);
		currentFilePaths.remove(planId);

		LoggerUtil.info("Cleaned up table processing resources for plan: {0}", planId);
	}

	/**
	 * Check if a string is numeric
	 * 
	 * @param str the string to check
	 * @return true if the string is numeric, false otherwise
	 */
	private boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
