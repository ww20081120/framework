/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool.workflow;

import java.util.concurrent.CompletableFuture;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool.workflow <br>
 */
public interface ISummaryWorkflow {

	/**
	 * Execute summary workflow
	 * 
	 * @param parentPlanId              parent plan ID
	 * @param fileName                  file name
	 * @param content                   content
	 * @param queryKey                  query key
	 * @param thinkActRecordId          think-act record ID
	 * @param outputFormatSpecification A file used to describe in what format the
	 *                                  data should be stored (default is an excel
	 *                                  table), the table header of this file is the
	 *                                  specification description
	 * @return asynchronous summary result
	 */
	CompletableFuture<String> executeSummaryWorkflow(String parentPlanId, String fileName, String content,
			String queryKey, Long thinkActRecordId, String outputFormatSpecification);
}
