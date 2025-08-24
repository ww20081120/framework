/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.recorder.po;

import java.util.Date;

import com.hbasesoft.framework.ai.jmanus.recorder.model.PlanExecutionRecord;
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
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.recorder.po <br>
 */
@Getter
@Setter
@Entity
@Table(name = "plan_execution_record")
public class PlanExecutionRecordPo4Jpa extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 3363369708135846871L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "plan_id", nullable = false, unique = true)
	private String planId;

	@Column(name = "gmt_create", nullable = false)
	private Date gmtCreate;

	@Column(name = "gmt_modified", nullable = false)
	private Date gmtModified;

	// @Convert(converter = StringAttributeConverter.class)
	@Column(name = "plan_execution_record")
	private String planExecutionRecord;

	public void setPlanExecutionRecord(PlanExecutionRecord planExecutionRecord) {
		this.planExecutionRecord = new StringAttributeConverter().convertToDatabaseColumn(planExecutionRecord);
	}
}
