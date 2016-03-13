/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.service.common;

import java.util.Map;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.bean.ModulePojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.service.common <br>
 */
public interface ModuleService {

    Map<String, ModulePojo> selectAllModule() throws ServiceException;
}
