/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.service.common;

import java.util.Map;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.ModulePojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月4日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.service.common <br>
 */
public interface ModuleService {

    Map<String, ModulePojo> selectAllModule() throws ServiceException;
}
