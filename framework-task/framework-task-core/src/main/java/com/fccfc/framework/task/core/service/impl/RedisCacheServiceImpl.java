package com.fccfc.framework.task.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.redis.RedisCache;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;
import com.fccfc.framework.task.core.dao.RedisCacheDao;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {
	
	private RedisCache redisCache = null;
	
	@Resource
	private RedisCacheDao redisCacheDao;
	
	public RedisCacheServiceImpl() {
		redisCache = new RedisCache("127.0.0.1", 6379);
	}
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param nodeName
	 * @param key
	 * 
	 * @param object <br>
	 */
	@Override
	public void putDataToRedis(String nodeName, String key, Object object) {
		try {
			redisCache.putValue(nodeName, key, object);
		} catch (CacheException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param nodeName
	 * @param key
	 * @return <br>
	 */
	@Override
	public Object getDataFromRedis(String nodeName, String key) {
		Object obj = null;
		try {
			obj = redisCache.getValue(nodeName, key);
		} catch (CacheException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br> <br>
	 */
	@Override
	public void clear() {
		try {
			redisCache.clean();
		} catch (CacheException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @return <br>
	 */
	@Override
	public List<ChangeNotifRedisPojo> getChangeNotifRedis(int num) {
		List<ChangeNotifRedisPojo> pojos = null;
		try {
			pojos = redisCacheDao.getChangeNotifRedis(num);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return pojos;
	}
}
