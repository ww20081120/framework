package com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    /**
     * 主键ID，采用自增策略生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 代理名称，不可为空，且在数据库中唯一
     */
    @Column(name = "agent_name", nullable = false, unique = true)
    private String agentName;

    /**
     * 代理描述，不可为空，长度限制为1000字符
     */
    @Column(name = "agent_description", nullable = false, length = 1000)
    private String agentDescription;

    /**
     * 下一步提示信息，不可为空，长度限制为4000字符
     */
    @Column(name = "next_step_prompt", nullable = false, length = 4000)
    private String nextStepPrompt;

    /**
     * 可用工具键列表，采用EAGER策略获取，意味着在获取DynamicAgentPo对象时会立即加载该列表
     * 该列表存储在dynamic_agent_available_tools表中，与dynamic_agents表通过agent_id关联
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dynamic_agent_available_tools", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "available_tool_keys")
    private List<String> availableToolKeys;

    /**
     * 类名，不可为空，表示代理对应的类名
     */
    @Column(name = "class_name", nullable = false)
    private String className;

}

