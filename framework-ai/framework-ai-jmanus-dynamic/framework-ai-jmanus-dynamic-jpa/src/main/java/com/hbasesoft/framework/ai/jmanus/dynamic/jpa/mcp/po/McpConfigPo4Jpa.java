/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.mcp.po;

import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.enums.McpConfigStatus;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.enums.McpConfigType;
import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.mcp.po <br>
 */
@Getter
@Setter
@Entity
@Table(name = "mcp_config")
public class McpConfigPo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = -1081277357665532464L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "mcp_server_name", nullable = false, unique = true)
	private String mcpServerName;

	@Column(name = "connection_type", nullable = false)
	private String connectionType;

	@Column(name = "connection_config", nullable = false, length = 4000)
	private String connectionConfig;

	@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'ENABLE'")
	private String status = McpConfigStatus.ENABLE.name(); // Default to enabled status

}
