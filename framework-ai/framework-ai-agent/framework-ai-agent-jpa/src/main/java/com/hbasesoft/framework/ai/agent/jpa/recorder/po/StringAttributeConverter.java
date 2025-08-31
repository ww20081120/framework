/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.recorder.po;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.util.json.JsonParser;

import com.alibaba.fastjson2.JSON;
import com.hbasesoft.framework.ai.agent.recorder.JManusSpringEnvironmentHolder;
import com.hbasesoft.framework.ai.agent.recorder.model.PlanExecutionRecord;
import com.hbasesoft.framework.ai.agent.recorder.model.SerializeType;

import jakarta.persistence.AttributeConverter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.recorder.model.po <br>
 */
public class StringAttributeConverter implements AttributeConverter<PlanExecutionRecord, String> {

	private final static String SERIALIZE_TYPE_KEY = "agent.serialize";

	@Override
	public String convertToDatabaseColumn(PlanExecutionRecord attribute) {
		if (attribute == null) {
			return null;
		}
		if (SerializeType.FASTJSON
				.equalsIgnoreCase(JManusSpringEnvironmentHolder.getEnvironment().getProperty(SERIALIZE_TYPE_KEY))) {
			return JSON.toJSONString(attribute);
		}
		return JsonParser.toJson(attribute);
	}

	@Override
	public PlanExecutionRecord convertToEntityAttribute(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		if (SerializeType.FASTJSON
				.equalsIgnoreCase(JManusSpringEnvironmentHolder.getEnvironment().getProperty(SERIALIZE_TYPE_KEY))) {
			return JSON.parseObject(json, PlanExecutionRecord.class);
		}
		return JsonParser.fromJson(json, PlanExecutionRecord.class);
	}

}
