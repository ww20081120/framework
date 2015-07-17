package com.fccfc.framework.web.init;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.utils.bean.JsonUtil;
import com.fccfc.framework.web.bean.event.EventPojo;
import com.fccfc.framework.web.service.EventService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since V6.11<br>
 * @see com.fccfc.framework.web.cache <br>
 */
public class EventCache implements InitializingBean {

    /** logger */
    private Logger logger = Logger.getLogger(EventCache.class);

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
        List<EventPojo> eventPojoList = eventService.selectList();
        if (eventPojoList != null && !eventPojoList.isEmpty()) {
            for (EventPojo eventPojo : eventPojoList) {
                String data = JsonUtil.writeObj2JSON(eventPojo);
                CacheHelper.getStringCache().putValue(CacheConstant.EVENT, eventPojo.getEventId(), data);

            }
        }
        logger.debug("loading event cache end...");

    }

}
