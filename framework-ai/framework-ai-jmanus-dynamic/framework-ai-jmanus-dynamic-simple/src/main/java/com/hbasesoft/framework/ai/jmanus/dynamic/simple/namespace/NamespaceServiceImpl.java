/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.namespace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.service.NamespaceService;
import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.vo.NamespaceConfig;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.namespace <br>
 */
@Service
public class NamespaceServiceImpl implements NamespaceService {

	@Value("${namespace.value:default}")
	private String namespace;
	
	/**
	 * Description: <br> 
	 *  
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	@Override
	public List<NamespaceConfig> getAllNamespaces() {
		List<NamespaceConfig> namespaces = new ArrayList<>();
		NamespaceConfig config = new NamespaceConfig();
		config.setId(1L);
		config.setName("Default Namespace");
		config.setCode(namespace);
		config.setDescription("Default namespace configuration");
		config.setHost("localhost");
		namespaces.add(config);
		return namespaces;
	}

}
