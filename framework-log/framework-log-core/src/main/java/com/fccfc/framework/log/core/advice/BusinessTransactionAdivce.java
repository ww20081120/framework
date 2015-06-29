/**
 * 
 */
package com.fccfc.framework.log.core.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.log.core.TransLoggerService;
import com.fccfc.framework.log.core.TransManager;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月6日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.log.advice <br>
 */

public class BusinessTransactionAdivce {

    private static Logger logger = new Logger(BusinessTransactionAdivce.class);

    /**
     * 栈最大深度
     */
    private int maxDeepLen;

    /** 最大执行时间 */
    private long maxExcuteTime;

    /**
     * 已经开启对服务
     */
    private String opendService;

    private Map<String, TransLoggerService> serviceMap;

    private List<TransLoggerService> transLoggerServices;

    /**
     * 方法执行前拦截
     * 
     * @param point 参数
     * @throws FrameworkException
     */
    public void before(JoinPoint point) throws FrameworkException {
        // 开始执行时间
        long beginTime = System.currentTimeMillis();

        // 执行方法
        String method = getMethodSignature(point);

        // 输入参数
        Object[] args = point.getArgs();

        TransManager manager = TransManager.getInstance();
        manager.setTransLoggerServices(getTransLoggerServices());

        // 深度检测
        if (manager.getStackSize() > maxDeepLen) {
            throw new FrameworkException(ErrorCodeDef.STACK_OVERFLOW_ERROR_10030, "业务过于复杂，请简化业务");
        }

        // 父id
        String parentStackId = manager.peek();

        // id
        String stackId = UUID.randomUUID().toString();
        manager.push(stackId, beginTime);

        // 执行记录
        List<TransLoggerService> serviceList = getTransLoggerServices();
        for (TransLoggerService service : serviceList) {
            service.before(stackId, parentStackId, beginTime, method, args);
        }
    }

    /**
     * 方法正常执行结束拦截
     * 
     * @param point 参数
     * @param returnValue 方法返回值
     * @throws ServiceException
     */
    public void afterReturning(JoinPoint point, Object returnValue) {
        // 执行完成时间
        long endTime = System.currentTimeMillis();

        TransManager manager = TransManager.getInstance();
        String stackId = manager.pop();
        if (CommonUtil.isEmpty(stackId)) {
            return;
        }

        long beginTime = manager.getBeginTime(stackId);
        long consumeTime = endTime - beginTime;

        if (consumeTime > maxExcuteTime) {
            manager.setTimeout(true);
        }

        // 执行记录
        List<TransLoggerService> serviceList = getTransLoggerServices();
        for (TransLoggerService service : serviceList) {
            service.afterReturn(stackId, endTime, consumeTime, returnValue);
        }

        if (manager.getStackSize() <= 0) {
            for (TransLoggerService service : serviceList) {
                service.end(stackId, beginTime, endTime, consumeTime, returnValue, null);
            }

            for (TransLoggerService service : serviceList) {
                service.clean();
            }

            manager.clean();
        }
    }

    /**
     * 方法异常执行结束拦截
     * 
     * @param point 参数
     * @param ex 异常信息
     * @throws ServiceException
     */
    public void afterThrowing(JoinPoint point, Exception ex) {
        // 执行完成时间
        long endTime = System.currentTimeMillis();

        TransManager manager = TransManager.getInstance();
        String stackId = manager.pop();
        if (CommonUtil.isEmpty(stackId)) {
            return;
        }

        long beginTime = manager.getBeginTime(stackId);
        long consumeTime = endTime - beginTime;

        manager.setError(true);

        // 执行记录
        List<TransLoggerService> serviceList = getTransLoggerServices();
        for (TransLoggerService service : serviceList) {
            service.afterThrow(stackId, endTime, consumeTime, ex);
        }

        if (manager.getStackSize() <= 0) {
            for (TransLoggerService service : serviceList) {
                service.end(stackId, beginTime, endTime, consumeTime, null, ex);
            }

            for (TransLoggerService service : serviceList) {
                service.clean();
            }
            
            manager.clean();
        }

    }

    /**
     * 获取 方法描述
     * 
     * @param point
     * @return
     */
    private String getMethodSignature(JoinPoint point) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(point.getTarget().getClass().getName()).append('<').append(point.getSignature().getName())
            .append('>');
        sbuf.append('(');

        Object[] args = point.getArgs();
        if (CommonUtil.isNotEmpty(args)) {
            for (Object obj : args) {
                sbuf.append(obj == null ? "NULL" : obj.getClass().getName()).append(',');
            }
        }
        sbuf.append(')');
        return sbuf.toString();
    }

    private List<TransLoggerService> getTransLoggerServices() {
        if (transLoggerServices == null) {
            transLoggerServices = new ArrayList<TransLoggerService>();
            if (CommonUtil.isNotEmpty(opendService) && CommonUtil.isNotEmpty(serviceMap)) {
                String[] serviceIds = StringUtils.split(opendService, GlobalConstants.SPLITOR);
                for (String id : serviceIds) {
                    TransLoggerService service = serviceMap.get(id);
                    if (service == null) {
                        logger.warn(new ServiceException(ErrorCodeDef.UNSPORT_LOGGER_TYPE), "不支持{0}日志类型", id);
                    }
                    else {
                        transLoggerServices.add(service);
                    }
                }
            }
        }
        return transLoggerServices;
    }

    public void setMaxDeepLen(int maxDeepLen) {
        this.maxDeepLen = maxDeepLen;
    }

    public void setOpendService(String opendService) {
        this.opendService = opendService;
    }

    public void setServiceMap(Map<String, TransLoggerService> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public void setMaxExcuteTime(long maxExcuteTime) {
        this.maxExcuteTime = maxExcuteTime * 1000;
    }
}
