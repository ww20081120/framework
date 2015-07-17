package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.DirectoryPojo;

/**
 * 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service <br>
 */
public interface DirectoryService {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directoryCode <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<DirectoryPojo> selectDirectoryByCode(String directoryCode) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parentDirectoryCode <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<DirectoryPojo> queryDirectoryByParentCode(String parentDirectoryCode) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directoryCode <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    int deleteDirectory(String directoryCode) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    int modifyDirectory(DirectoryPojo pojo) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    int addDirectory(DirectoryPojo pojo) throws ServiceException;
}
