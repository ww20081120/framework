/**
 * 
 */
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.area.AreaPojo;

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

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<AreaPojo> queryAllAreaPojo() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param areaId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<AreaPojo> queryAreaAndParents(Integer areaId) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param areaId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<Integer> queryAreaAndParentIds(Integer areaId) throws ServiceException;
}
