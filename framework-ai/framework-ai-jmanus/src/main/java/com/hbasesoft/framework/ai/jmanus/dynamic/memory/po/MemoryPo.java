/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.memory.po;

import java.util.Date;
import java.util.List;

import org.springframework.ai.chat.messages.Message;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.memory.po <br>
 */
@Entity
@Table(name = "dynamic_memories")
public class MemoryPo extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = -1961395484601490770L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "memory_id", nullable = false)
	private String memoryId;

	@Column(name = "memory_name", nullable = false)
	private String memoryName;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Transient
	private List<Message> messages;


	public MemoryPo() {
		this.createTime = new Date();
	}

	public MemoryPo(String memoryId, String memoryName) {
		this.memoryId = memoryId;
		this.memoryName = memoryName;
		this.createTime = new Date();
	}
}
