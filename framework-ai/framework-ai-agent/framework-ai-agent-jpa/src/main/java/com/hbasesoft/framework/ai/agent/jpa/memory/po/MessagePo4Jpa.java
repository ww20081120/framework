/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.memory.po;

import java.util.Date;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.memory.po <br>
 */
@Getter
@Setter
@Entity
@Table(name = "ai_chat_memory")
public class MessagePo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = -1961395484601490770L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "conversation_id", nullable = false)
	private String conversationId;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "content")
	private String content;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

}
