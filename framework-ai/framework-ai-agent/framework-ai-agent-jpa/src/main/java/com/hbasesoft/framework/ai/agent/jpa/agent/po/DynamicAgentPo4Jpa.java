/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.agent.po;

import java.util.List;

import com.hbasesoft.framework.ai.agent.jpa.model.po.DynamicModelPo4Jpa;
import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.agent.po <br>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dynamic_agents")
public class DynamicAgentPo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 8526003709620073848L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agent_name", nullable = false, unique = true)
	private String agentName;

	@Column(name = "agent_description", nullable = false, length = 1000)
	private String agentDescription;

	@Column(name = "system_prompt", nullable = true, length = 40000)
	@Deprecated
	private String systemPrompt = "";

	@Column(name = "next_step_prompt", nullable = false, length = 40000)
	private String nextStepPrompt;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "dynamic_agent_tools", joinColumns = @JoinColumn(name = "agent_id"))
	@Column(name = "tool_key")
	private List<String> availableToolKeys;

	@Column(name = "class_name", nullable = false)
	private String className;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "model_id")
	private DynamicModelPo4Jpa model;

	@Column(name = "namespace", nullable = true)
	private String namespace;

	@Column(name = "built_in", nullable = true)
	private Boolean builtIn = false;
}
