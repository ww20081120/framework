/**
 * 
 */
package com.fccfc.framework.web.manager.service.common;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.common.AreaPojo;

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
     * Description: 查询所有区域<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<AreaPojo> listArea() throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaId
     * @return
     * @throws ServiceException <br>
     */
    AreaPojo qryAreaDetailById(int areaId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void insertArea(AreaPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaId
     * @param areaCode
     * @return <br>
     */
    boolean checkCode(String areaId, String areaCode);

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaId
     * @param areaName
     * @return <br>
     */
    boolean checkName(String areaId, String areaName);

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param id
     * @throws ServiceException <br>
     */
    void remove(int id) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyArea(AreaPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @throws ServiceException <br>
     */
    void exportAreaData(HttpServletResponse response) throws Exception;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author XXX<br>
     * @taskId <br>
     * @param filePath
     * @throws ServiceException <br>
     */
    void importAreaData(String filePath) throws ServiceException;

}
