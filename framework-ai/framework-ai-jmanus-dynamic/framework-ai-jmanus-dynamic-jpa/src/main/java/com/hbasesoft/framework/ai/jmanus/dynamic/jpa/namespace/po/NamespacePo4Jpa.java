/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.namespace.po;

import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.vo.NamespaceConfig;
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
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.namespace.po <br>
 */
@Getter
@Setter
@Entity
@Table(name = "namespace")
public class NamespacePo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 8837398615700453899L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "description", nullable = true, length = 1024)
	private String description;

	@Column(name = "host")
	private String host;

	public NamespaceConfig mapToNamespaceConfig() {
		NamespaceConfig config = new NamespaceConfig();
		config.setId(this.getId());
		config.setName(this.getName());
		config.setCode(this.getCode());
		config.setHost(this.getHost());
		config.setDescription(this.getDescription());
		config.setHost(this.getHost());
		return config;
	}
}
