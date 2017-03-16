package com.hbasesoft.framework.cache.core.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.annotation.CacheMethodConfig;
import com.hbasesoft.framework.cache.core.annotation.CacheProxy;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 *
 * @author hejiawen<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017/2/10 <br>
 * @see com.hbasesoft.framework.cache.core.handler <br>
 * @since V1.0<br>
 */
public class CachePorxyInvocationHandler implements InvocationHandler {

    private Object target;

    private CacheProxy cacheProxy;

    private Map<String, CacheMethodConfig> cacheMethodConfigMap;

    private Class<?> clazz;

    private String nodeNamePrefix;

    public CachePorxyInvocationHandler(Object target, CacheProxy cacheProxy, Class<?> clazz) {
        this.target = target;
        this.cacheProxy = cacheProxy;
        this.clazz = clazz;
        this.cacheMethodConfigMap = new HashMap<>();

        CacheMethodConfig[] configs = cacheProxy.value();
        if (CommonUtil.isNotEmpty(configs)) {
            for (CacheMethodConfig config : configs) {
                this.cacheMethodConfigMap.put(config.value(), config);
            }
        }

        nodeNamePrefix = new StringBuilder().append("__CACHE_PROXY_").append(clazz.getName()).append('@')
            .append(target.hashCode()).toString();

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        CacheMethodConfig config = cacheMethodConfigMap.get(method.getName());
        if (config == null || !config.cacheAble()
            || Void.class.equals(method.getAnnotatedReturnType().getType().getTypeName())) {
            result = method.invoke(target, args);
        }
        else {
            if (config.cacheAble()) {
                result = CacheHelper.getCache().get(nodeNamePrefix + method.getName(), getCacheKey(method, args));
                if (result == null) {
                    result = getAndCache(method, args, cacheProxy.expireTime());
                }
            }

            if (CommonUtil.isNotEmpty(config.removeMethods())) {
                for (String methodName : config.removeMethods()) {
                    CacheHelper.getCache().evict(nodeNamePrefix + methodName);
                    LoggerUtil.info("Success evict proxy cache [class={0}, method={1}]", clazz, methodName);
                }
            }
        }
        return result;
    }

    private String getCacheKey(Method method, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());
        if (CommonUtil.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(GlobalConstants.UNDERLINE);
                }
                sb.append(args[i] == null ? GlobalConstants.BLANK : args[i]);
            }
        }
        return sb.toString();
    }

    private Object getAndCache(Method method, Object[] args, int expireTime)
        throws InvocationTargetException, IllegalAccessException {
        Object result = method.invoke(target, args);
        if (result != null) {
            CacheHelper.getCache().put(nodeNamePrefix + method.getName(), expireTime, getCacheKey(method, args),
                result);
            LoggerUtil.info("Success proxy cache [class={0}, method={1}, expireTime={2}]", clazz, method.getName(),
                expireTime);
        }
        return result;
    }

}
