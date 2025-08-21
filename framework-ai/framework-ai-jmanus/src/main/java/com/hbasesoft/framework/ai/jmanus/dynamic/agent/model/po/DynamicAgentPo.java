package com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po;

import java.util.List;

import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.po.DynamicModelPo;
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
 * 动态代理配置持久化对象类
 * 该类继承自BaseEntity，代表数据库中的dynamic_agents表，用于存储动态代理的相关信息
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dynamic_agents")
public class DynamicAgentPo extends BaseEntity {

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
	private DynamicModelPo model;

	@Column(name = "namespace", nullable = true)
	private String namespace;

	@Column(name = "built_in", nullable = true)
	private Boolean builtIn = false;
}
