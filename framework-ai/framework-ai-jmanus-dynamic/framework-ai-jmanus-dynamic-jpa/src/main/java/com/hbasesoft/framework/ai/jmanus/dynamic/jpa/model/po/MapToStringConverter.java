/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.model.po;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.model.model.po <br>
 */
//@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, String>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();


	@Override
	public String convertToDatabaseColumn(Map<String, String> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error converting map to string", e);
		}
	}

	@Override
	public Map<String, String> convertToEntityAttribute(String dbData) {
		// Add null or empty string check
		if (dbData == null || dbData.isEmpty()) {
			// Return empty Map or null, depending on business logic
			return new HashMap<>();
		}
		try {
			return objectMapper.readValue(dbData, new TypeReference<>() {
			});
		} catch (Exception e) {
			throw new IllegalArgumentException("Error converting string to map", e);
		}
	}

}