package com.hbasesoft.framework.web.system.interceptor;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.web.core.bean.UrlResource;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.web.system.bean.OperateLogPojo;
import com.hbasesoft.framework.web.system.service.EventService;
import com.hbasesoft.framework.web.system.service.OperateLogService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since V6.11<br>
 * @see com.hbasesoft.framework.web.interceptor <br>
 */
public class OperateLogIntercepter extends HandlerInterceptorAdapter {
    /** logger */
    Logger logger = Logger.getLogger(OperateLogIntercepter.class);

    /** operateLogService */
    @Resource
    private OperateLogService operateLogService;

    @Resource
    private EventService eventService;

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     * @param handler <br>
     * @param ex <br>
     * @throws Exception <br>
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        logger.debug("record operate log start...");
        // 获取当前请求路径
        // 获取当前路径对应的缓存中路径资源
        UrlResource urlResource = null;// WebUtil.urlMatch(request);
        String eventType = ConfigHelper.getString("EVENT_TYPE");
        if (urlResource != null && CommonUtil.isNotEmpty(urlResource.getEvents())) {
            Date now = new Date(DateUtil.getCurrentTime());
            String moduleCode = ConfigHelper.getString(CacheConstant.MODULE_CODE);
            // 事件可能有多个，是以","分隔的，多个则插多条记录
            OperateLogPojo operateLogPojo = null;
            for (String eventCode : urlResource.getEvents()) {
                // 查询缓存，判断当前事件是否需要记录操作日志
                EventPojo event = eventService.getEventByCode(eventCode);
                // 不是和配置项中的一至，不需要记录操作日志
                if (null != event && StringUtils.equals(eventType, event.getEventType())) {
                    operateLogPojo = new OperateLogPojo();
                    operateLogPojo.setEventId(event.getEventId());
                    operateLogPojo.setIp(WebUtil.getRemoteIP());
                    operateLogPojo.setCreateTime(now);
                    operateLogPojo.setParamsValue(getParams(request, event));

                    operateLogPojo.setModuleCode(moduleCode);
                    operateLogPojo.setOperatorId(WebUtil.getCurrentOperatorId());
                    operateLogService.save(operateLogPojo);
                }
            }

        }
        logger.debug("record operate log end...");
    }

    /***
     * Description: <br>
     * 从缓存中取出当前事件粗腰的参数字符串，解析，从request中获取参数
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param request <br>
     * @param event <br>
     * @return String <br>
     * @throws Exception <br>
     */
    public String getParams(HttpServletRequest request, EventPojo event) throws Exception {
        String paramsStr = event.getParamsName();
        JSONObject paramsJson = null;
        if (CommonUtil.isNotEmpty(paramsStr)) {
            paramsJson = new JSONObject();
            String[] paramsArr = StringUtils.split(paramsStr, GlobalConstants.SPLITOR);
            for (String param : paramsArr) {
                paramsJson.put(param, request.getParameter(param));
            }
        }

        return paramsJson == null ? null : paramsJson.toString();
    }
}
