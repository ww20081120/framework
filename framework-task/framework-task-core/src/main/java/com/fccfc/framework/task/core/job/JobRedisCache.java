package com.fccfc.framework.task.core.job;

import java.util.List;

import javax.annotation.Resource;

import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;
import com.fccfc.framework.task.core.service.impl.NotifRedisService;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月14日 <br>
 * @since V1.0<br>
 * @see service.task.api <br>
 */
public class JobRedisCache {

    /**
     * redisCacheService
     */
    @Resource
    private NotifRedisService redisCacheService;

    /**
     * NODE_NAME
     */
    private static final String NODE_NAME = "nodeName";

    /**
     * KEY_PREFIX
     */
    private static final String KEY_PREFIX = "key";

    /**
     * CACHE_CONFIG_NUM
     */
    private static final String CACHE_CONFIG_NUM = "CACHE_NUM.CACHE_NUM";

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    public void jobPutDataToRedis() throws Exception {
        // 从配置项中获取要存入缓存中的记录数量,参数格式CONFIG_ITEM_CODE.CONFIG_ITEM_PARAM_CODE
        int num = redisCacheService.getConfigNum(CACHE_CONFIG_NUM);
        // 根据nodeName来清除Redis缓存中的数据
        redisCacheService.removeByNodeName(NODE_NAME);
        // 数数据库表中获取指定数量的记录数
        List<ChangeNotifRedisPojo> list = redisCacheService.getChangeNotifRedis(num);
        // 将从数据库中获取的数据逐个保存到Redis缓存中
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                redisCacheService.putDataToRedis(NODE_NAME, KEY_PREFIX + i, list.get(i));
                System.out.println("save to redis" + i);
            }
        }
    }
}
