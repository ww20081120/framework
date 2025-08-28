/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool.innerStorage;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool.innerStorage <br>
 */
public interface ISmartContentSavingService {


	/**
	 * Get Manus properties
	 * @return Manus properties
	 */
	IManusProperties getManusProperties();

	/**
	 * Process content, automatically store if content is too long
	 * @param planId Plan ID
	 * @param content Content
	 * @param callingMethod Calling method name
	 * @return Processing result containing filename and summary
	 */
	SmartProcessResult processContent(String planId, String content, String callingMethod);
}
