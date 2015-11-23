package com.fccfc.framework.task.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;
import com.fccfc.framework.task.core.dao.RedisCacheDao;
import com.fccfc.framework.task.core.service.NotifyCacheService;

/**
 * <Description> <br>
 * 
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年8月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.service.impl <br>
 */
@Service
public class NotifyCacheServiceImpl implements NotifyCacheService {

    /**
     * redisCacheDao
     */
    @Resource
    private RedisCacheDao redisCacheDao;

    /**
     * NotifRedisServiceImpl
     */
    public NotifyCacheServiceImpl() {
    }

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
    @Override
    public void putDataToRedis(String nodeName, String key, Object object) throws ServiceException {
        try {
            CacheHelper.getCache().putValue(nodeName, key, object);
        }
        catch (CacheException e) {
            throw new ServiceException(ErrorCodeDef.PUT_VALUE_ERROR_20022, "向redis中保存数据失败", e);
        }
    }

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
    @Override
    public Object getDataFromRedis(String nodeName, String key) throws ServiceException {
        Object obj = null;
        try {
            obj = CacheHelper.getCache().getValue(nodeName, key);
        }
        catch (CacheException e) {
            throw new ServiceException(ErrorCodeDef.GET_VALUE_ERROR_20023, "获取redis中数据失败", e);
        }
        return obj;
    }

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param nodeName <br>
     * @throws ServiceException <br>
     */
    @Override
    public void removeByNodeName(String nodeName) throws ServiceException {
        try {
            CacheHelper.getCache().removeNode(nodeName);
        }
        catch (CacheException e) {
            throw new ServiceException(ErrorCodeDef.REMOVE_VALUE_ERROR_20024, "删除redis中数据失败", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param num <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<ChangeNotifRedisPojo> getChangeNotifRedis(int num) throws ServiceException {
        List<ChangeNotifRedisPojo> pojos = null;
        try {
            pojos = redisCacheDao.getChangeNotifRedis(num);
        }
        catch (DaoException e) {
            throw new ServiceException(ErrorCodeDef.GET_VALUE_ERROR_20023, "获取redis中数据失败", e);
        }
        return pojos;
    }

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param code <br>
     * @return <br>
     */
    @Override
    public int getConfigNum(String code) {
        return Configuration.getInt(code);
    }
}
