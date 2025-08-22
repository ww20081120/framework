/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.namespace.service;

import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.vo.NamespaceConfig;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.namespace <br>
 */
public interface NamespaceManagerService {

	NamespaceConfig getNamespaceById(String id);

	NamespaceConfig createNamespace(NamespaceConfig namespaceConfig);

	NamespaceConfig updateNamespace(NamespaceConfig namespaceConfig);

	void deleteNamespace(String id);
}
