package com.fccfc.framework.task.core.service.impl;

import java.util.List;

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
	 */
	public void putDataToRedis(String nodeName, String key, Object object);
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param nodeName
	 * @param key
	 * @return <br>
	 */
	public Object getDataFromRedis(String nodeName, String key);
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br> <br>
	 */
	public void clear();
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param num	取出的数据量
	 * @return <br>
	 */
	public List<ChangeNotifRedisPojo> getChangeNotifRedis(int num);
}
