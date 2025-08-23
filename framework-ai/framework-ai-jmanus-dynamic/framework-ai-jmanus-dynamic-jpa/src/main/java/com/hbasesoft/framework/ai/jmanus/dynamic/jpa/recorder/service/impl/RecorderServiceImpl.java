/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.recorder.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.recorder.dao.PlanExecutionRecordDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.recorder.po.PlanExecutionRecordPo4Jpa;
import com.hbasesoft.framework.ai.jmanus.recorder.RecorderService;
import com.hbasesoft.framework.ai.jmanus.recorder.model.vo.RecorderVo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.recorder.service.impl <br>
 */
@Service
public class RecorderServiceImpl implements RecorderService {

	@Autowired
	private PlanExecutionRecordDao planExecutionRecordDao;

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param planId <br>
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteById(String planId) {
		planExecutionRecordDao.deleteById(planId);
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param rootPlanId
	 * @return <br>
	 */
	@Transactional(readOnly = true)
	@Override
	public RecorderVo get(String rootPlanId) {
		PlanExecutionRecordPo4Jpa entity = planExecutionRecordDao.get(rootPlanId);
		if (entity == null) {
			return null;
		}
		RecorderVo recorderVo = new RecorderVo();
		BeanUtils.copyProperties(entity, recorderVo);
		return recorderVo;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param entity <br>
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void save(RecorderVo entity) {
		PlanExecutionRecordPo4Jpa entityJpa = new PlanExecutionRecordPo4Jpa();
		BeanUtils.copyProperties(entity, entityJpa);
		planExecutionRecordDao.save(entityJpa);
	}

}
