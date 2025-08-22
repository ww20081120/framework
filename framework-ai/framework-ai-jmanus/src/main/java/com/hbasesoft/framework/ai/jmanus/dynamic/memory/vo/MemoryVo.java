/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.memory.vo;

import java.util.Date;
import java.util.List;

import org.springframework.ai.chat.messages.Message;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.memory.vo <br>
 */
@Data
public class MemoryVo {

	private Long id;

	private String memoryId;

	private String memoryName;

	private Date createTime;

	private List<Message> messages;

	public MemoryVo() {
		this.createTime = new Date();
	}

	public MemoryVo(String memoryId, String memoryName) {
		this.memoryId = memoryId;
		this.memoryName = memoryName;
		this.createTime = new Date();
	}
}
