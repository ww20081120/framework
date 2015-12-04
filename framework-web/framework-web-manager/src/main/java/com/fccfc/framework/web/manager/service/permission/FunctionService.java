package com.fccfc.framework.web.manager.service.permission;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;

/**
 * 
 * <Description> 功能点模块<br>
 * 
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月31日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.service.menu <br>
 */
public interface FunctionService {

	/**
	 * Description:查询所有的功能模块 <br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	List<FunctionPojo> queryFunction() throws ServiceException;
	
	/**
	 * 
	 * Description: 分页查询功能点<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param functionName
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	List<FunctionPojo> queryFunction(String directoryCode, String functionName, Integer pageIndex,
			Integer pageSize) throws ServiceException;

	int listTotal(String directoryCode, String functionName) throws ServiceException;
	/**
	 * 
	 * Description: 添加功能点信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param functionPojo
	 * @throws ServiceException
	 * <br>
	 */
	void addFunction(FunctionPojo functionPojo) throws ServiceException;

	/**
	 * 
	 * Description: 修改功能点信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param functionPojo
	 * @throws ServiceException
	 * <br>
	 */
	void modifyFunction(FunctionPojo functionPojo) throws ServiceException;

	/**
	 * 
	 * Description: 根据functionId查询FunctionPojo对象<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param functionId
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	FunctionPojo queryFunction(Long functionId) throws ServiceException;

	/**
	 * 
	 * Description: 根据id批量删除<br> 
	 *  
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param splitIdsByLong
	 * @throws ServiceException <br>
	 */
	void deleteFunctions(Long[] splitIdsByLong) throws ServiceException;

	boolean checkName(String functionId, String functionName);

	void importFunction(String directoryCode, String mediaId, String mediaName) throws ServiceException;
}
