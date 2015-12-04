package com.fccfc.framework.web.manager.service.common;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.DirectoryPojo;

/**
 * 
 * <Description> 目录<br> 
 *  
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月4日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.service.common <br>
 */
public interface DirectoryService {

	/**
	 * 
	 * Description: 查询目录<br> 
	 *  
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws ServiceException <br>
	 */
	List<com.fccfc.framework.web.manager.bean.system.DirectoryPojo> selectDirectory(String parentDirectoryCode) throws ServiceException;

	/**
	 * 
	 * Description: 添加目录<br> 
	 *  
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param directoryPojo
	 * @throws ServiceException <br>
	 */
    void addDirectory(DirectoryPojo directoryPojo) throws ServiceException;

    /**
     * 
     * Description: 删除目录<br> 
     *  
     * @author 胡攀<br>
     * @taskId <br>
     * @param directoryCode
     * @throws ServiceException <br>
     */
    void removeDirectory(String directoryCode) throws ServiceException;

    /**
     * 
     * Description: 按目录代码查询<br> 
     *  
     * @author 胡攀<br>
     * @taskId <br>
     * @param directoryCode
     * @return
     * @throws ServiceException <br>
     */
    DirectoryPojo queryDirectoryByCode(String directoryCode) throws ServiceException;

    /**
     * 
     * Description: 修改目录<br> 
     *  
     * @author 胡攀<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyDirectory(DirectoryPojo pojo) throws ServiceException;

    boolean checkCode(String code);

    boolean checkName(String code, String name);
}
