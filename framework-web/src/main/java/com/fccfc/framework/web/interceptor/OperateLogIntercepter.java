package com.fccfc.framework.web.interceptor;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.date.DateUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.web.WebUtil;
import com.fccfc.framework.web.bean.operator.OperateLogPojo;
import com.fccfc.framework.web.service.OperateLogService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since V6.11<br>
 * @see com.fccfc.framework.web.interceptor <br>
 */
public class OperateLogIntercepter extends HandlerInterceptorAdapter {
    /** logger */
    Logger logger = Logger.getLogger(OperateLogIntercepter.class);

    /** operateLogService */
    @Resource
    private OperateLogService operateLogService;

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
        String contextPath = request.getContextPath();
        // 获取当前请求路径
        String path = request.getRequestURI();
        String realPath = path.substring(contextPath.length());
        // 获取当前路径对应的缓存中路径资源
        String urlData = CacheHelper.getStringCache().getValue(CacheConstant.URL, realPath);
        OperateLogPojo operateLogPojo = null;
        if (CommonUtil.isNotEmpty(urlData)) {
            JSONObject jsonData = JSONObject.parseObject(urlData);
            String[] eventIdArr = jsonData.getString("eventId").split(",");
            Date now = new Date(DateUtil.getCurrentTime());

            String moduleCode = Configuration.getString(CacheConstant.LOCAL_MODULE_CODE);
            // 事件可能有多个，是以","分隔的，多个则插多条记录
            for (String eventId : eventIdArr) {
                // 查询缓存，判断当前事件是否需要记录操作日志
                JSONObject eventJson = (JSONObject) JSONObject.parse(CacheHelper.getStringCache().getValue(
                    CacheConstant.EVENT, eventId));
                // 不是和配置项中的一至，不需要记录操作日志
                if (!Configuration.getString("EVENT_TYPE").equals(eventJson.getString("eventType"))) {
                    continue;
                }
                operateLogPojo = new OperateLogPojo();
                operateLogPojo.setEventId(eventId);
                operateLogPojo.setIp(WebUtil.getRemoteIP(request));
                operateLogPojo.setCreateTime(now);
                operateLogPojo.setParamsValue(getParams(request, eventJson));

                operateLogPojo.setModuleCode(moduleCode);
                operateLogPojo.setOperatorId(WebUtil.getCurrentOperatorId());
                operateLogService.save(operateLogPojo);
            }

        }
        logger.debug("record operate log end...");
    }

    /***
     * 
     * Description: <br> 
     *  从缓存中取出当前事件粗腰的参数字符串，解析，从request中获取参数
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param request <br>
     * @param eventJson <br>
     * @return String <br>
     * @throws Exception <br>
     */
    public String getParams(HttpServletRequest request, JSONObject eventJson) throws Exception {
        String paramsStr = eventJson.getString("paramsName");
        JSONObject paramsJson = null;
        if (CommonUtil.isNotEmpty(paramsStr)) {
            paramsJson = new JSONObject();
            String[] paramsArr = paramsStr.split(",");
            for (int i = 0; i < paramsArr.length; i++) {
                paramsJson.put(paramsArr[i], request.getParameter(paramsArr[i]));
            }
        }

        return paramsJson == null ? null : paramsJson.toString();
    }
}
