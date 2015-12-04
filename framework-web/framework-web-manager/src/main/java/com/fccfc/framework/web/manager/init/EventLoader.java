package com.fccfc.framework.web.manager.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.web.manager.bean.common.EventPojo;
import com.fccfc.framework.web.manager.service.common.EventService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since V6.11<br>
 * @see com.fccfc.framework.web.manager.init <br>
 */
public class EventLoader implements Initialization {

    /** logger */
    private Logger logger = Logger.getLogger(EventLoader.class);

    /** eventService */
    @Resource
    private EventService eventService;

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("loading event cache start...");
        List<EventPojo> eventPojoList = eventService.selectList(-1, -1);
        if (eventPojoList != null && !eventPojoList.isEmpty()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (EventPojo eventPojo : eventPojoList) {
                map.put(String.valueOf(eventPojo.getEventId()), eventPojo);
            }
            CacheHelper.getCache().putNode(CacheConstant.EVENT, map);
        }
        logger.debug("loading event cache end...");

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @Override
    public void destroy() throws Exception {
        CacheHelper.getCache().removeNode(CacheConstant.EVENT);
    }

}
