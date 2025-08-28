/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.config.po;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hbasesoft.framework.ai.agent.config.model.enums.ConfigInputType;
import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
 * @see com.hbasesoft.framework.ai.agent.jpa.config.po <br>
 */
@Getter
@Setter
@Entity
@Table(name = "system_config")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigPo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 6396250254439013884L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Configuration group
	 */
	@Column(name = "config_group", nullable = false)
	private String configGroup;

	/**
	 * Configuration sub-group
	 */
	@Column(name = "config_sub_group", nullable = false)
	private String configSubGroup;

	/**
	 * Configuration key
	 */
	@Column(name = "config_key", nullable = false)
	private String configKey;

	/**
	 * Configuration item full path
	 */
	@Column(name = "config_path", nullable = false, unique = true)
	private String configPath;

	/**
	 * Configuration value
	 */
	@Column(name = "config_value", columnDefinition = "TEXT")
	private String configValue;

	/**
	 * Default value
	 */
	@Column(name = "default_value", columnDefinition = "TEXT")
	private String defaultValue;

	/**
	 * Configuration description
	 */
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	/**
	 * Input type
	 */
	@Column(name = "input_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ConfigInputType inputType;

	/**
	 * Options JSON string for storing SELECT type option data
	 */
	@Column(name = "options_json", columnDefinition = "TEXT")
	private String optionsJson;

	/**
	 * Last update time
	 */
	@Column(name = "update_time", nullable = false)
	private LocalDateTime updateTime;

	/**
	 * Create time
	 */
	@Column(name = "create_time", nullable = false)
	private LocalDateTime createTime;

	@PrePersist
	protected void onCreate() {
		createTime = LocalDateTime.now();
		updateTime = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updateTime = LocalDateTime.now();
	}
}
