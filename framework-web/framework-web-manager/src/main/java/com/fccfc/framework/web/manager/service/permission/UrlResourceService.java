package com.fccfc.framework.web.manager.service.permission;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;

/**
 * 
 * <Description> Url配置资源<br>
 * 
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月29日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.service.menu <br>
 */
public interface UrlResourceService {

	/**
	 * 
	 * Description: 查询所有的Url资源<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return <br>
	 */
	List<UrlResourcePojo> queryUrlResource() throws ServiceException;

	/**
	 * 
	 * Description: 分页查询所有的Url资源<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	List<UrlResourcePojo> queryUrlResource(Long functionId, Integer pageIndex,
			Integer pageSize) throws ServiceException;

	/**
	 * 
	 * Description: 添加Url资源<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param urlResourcePojo
	 * <br>
	 * @throws ServiceException
	 */
	void addUrlResource(UrlResourcePojo urlResourcePojo)
			throws ServiceException;

	/**
	 * 
	 * Description: 批量删除URL资源信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param integers
	 * @throws ServiceException
	 * <br>
	 */
	void deleteUrls(Long[] integers) throws ServiceException;

	/**
	 * 
	 * Description: 根据resourceId查询UrlResourcePojo对象<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param resourceId
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	UrlResourcePojo queryUrl(Long resourceId) throws ServiceException;

	/**
	 * 
	 * Description: 修改URL资源对象<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param urlResourcePojo
	 * @throws ServiceException
	 * <br>
	 */
	void modifyUrl(UrlResourcePojo urlResourcePojo) throws ServiceException;

	boolean checkName(String resourceId, String resourceName);
}
