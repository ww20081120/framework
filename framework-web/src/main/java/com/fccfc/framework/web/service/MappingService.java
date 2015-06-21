/**
 * 
 */
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.api.ServiceException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service <br>
 */
public interface MappingService {

    /**
     * 获取url资源
     * 
     * @return
     * @throws ServiceException
     */
    List<String> selectAllUrlResource() throws ServiceException;

    String getMethodUrl(String clazz, String method) throws ServiceException;
}
