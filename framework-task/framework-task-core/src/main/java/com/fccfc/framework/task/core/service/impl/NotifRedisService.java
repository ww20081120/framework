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
public interface NotifRedisService {

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param nodeName <br>
     * @param key <br>
     * @param object <br>
     * @throws ServiceException <br>
     */
    public void putDataToRedis(String nodeName, String key, Object object) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param nodeName <br>
     * @param key <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public Object getDataFromRedis(String nodeName, String key) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param nodeName <br>
     * @throws ServiceException <br>
     */
    public void removeByNodeName(String nodeName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param num <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public List<ChangeNotifRedisPojo> getChangeNotifRedis(int num) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param code <br>
     * @return <br>
     */
    public int getConfigNum(String code);

}
