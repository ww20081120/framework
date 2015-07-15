package com.fccfc.framework.task.core.service.impl;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;

/**
 * <Description> <br> 
 *  
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月14日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.service.impl <br>
 */
public interface RedisCacheService {
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param nodeName
	 * @param key
	 * @param object <br>
	 * @throws ServiceException 
	 */
	public void putDataToRedis(String nodeName, String key, Object object) throws ServiceException;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param nodeName
	 * @param key
	 * @return
	 * @throws ServiceException <br>
	 */
	public Object getDataFromRedis(String nodeName, String key) throws ServiceException;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param nodeName
	 * @throws ServiceException <br>
	 */
	public void removeByNodeName(String nodeName) throws ServiceException;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param num
	 * @return
	 * @throws ServiceException <br>
	 */
	public List<ChangeNotifRedisPojo> getChangeNotifRedis(int num) throws ServiceException;
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param code
	 * @return
	 * @throws ServiceException <br>
	 */
	public int getConfigNum(String code);

}
