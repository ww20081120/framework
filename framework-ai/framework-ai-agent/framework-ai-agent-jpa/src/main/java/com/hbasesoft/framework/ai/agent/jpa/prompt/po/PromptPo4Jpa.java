/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.prompt.po;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
 * @see com.hbasesoft.framework.ai.agent.jpa.prompt.po <br>
 */

@Getter
@Setter
@Entity
@Table(name = "prompt", uniqueConstraints = { @UniqueConstraint(columnNames = { "namespace", "prompt_name" }) })
public class PromptPo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 4567444462827997251L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "prompt_name", nullable = false)
	private String promptName;

	@Column(name = "namespace", nullable = true)
	private String namespace;

	@Column(name = "message_type", nullable = false)
	private String messageType;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "built_in", nullable = false)
	private Boolean builtIn;

	@Column(name = "prompt_description", nullable = false, length = 1024)
	private String promptDescription;

	@Column(name = "prompt_content", columnDefinition = "TEXT", nullable = false)
	private String promptContent;
}
