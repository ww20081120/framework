/**
 * 
 */
package com.fccfc.framework.config.core.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.ModulePojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月16日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.core.config <br>
 */
public interface ConfigurationService {
    List<ModulePojo> selectAllModule() throws ServiceException;
}
