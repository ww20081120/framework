/**
 * 
 */
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.area.AreaPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年1月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service <br>
 */
public interface AreaService {

    List<AreaPojo> queryAllAreaPojo() throws ServiceException;

    List<AreaPojo> queryAreaAndParents(Integer areaId) throws ServiceException;

    List<Integer> queryAreaAndParentIds(Integer areaId) throws ServiceException;
}
