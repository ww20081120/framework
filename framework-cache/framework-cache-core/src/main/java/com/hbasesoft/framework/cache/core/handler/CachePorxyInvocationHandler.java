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

    /** target */
    private Object target;

    /** cacheProxy */
    private CacheProxy cacheProxy;

    /** cacheMethodConfigMap */
    private Map<String, CacheMethodConfig> cacheMethodConfigMap;

    /** clazz */
    private Class<?> clazz;

    /** nodeNamePrefix */
    private String nodeNamePrefix;

    /**
     * CachePorxyInvocationHandler
     * 
     * @param t
     * @param cp
     * @param c
     */
    public CachePorxyInvocationHandler(final Object t, final CacheProxy cp, final Class<?> c) {
        this.target = t;
        this.cacheProxy = cp;
        this.clazz = c;
        this.cacheMethodConfigMap = new HashMap<>();

        CacheMethodConfig[] configs = cp.value();
        if (CommonUtil.isNotEmpty(configs)) {
            for (CacheMethodConfig config : configs) {
                this.cacheMethodConfigMap.put(config.value(), config);
            }
        }

        nodeNamePrefix = new StringBuilder().append("__CACHE_PROXY_").append(clazz.getName()).append('@')
            .append(t.hashCode()).toString();

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param proxy proxy
     * @param method method
     * @param args args
     * @return Object
     * @throws Throwable <br>
     */
    @SuppressWarnings("unlikely-arg-type")
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object result = null;
        CacheMethodConfig config = cacheMethodConfigMap.get(method.getName());
        if (config == null || Void.class.equals(method.getAnnotatedReturnType().getType().getTypeName())) {
            result = method.invoke(target, args);
        }
        else {
            if (config.cacheAble()) {
                result = CacheHelper.getCache().getNodeValue(nodeNamePrefix + method.getName(),
                    getCacheKey(method, args));
                if (result == null) {
                    result = getAndCache(method, args, cacheProxy.expireTime());
                }
            }
            else {
                result = method.invoke(target, args);
            }

            if (CommonUtil.isNotEmpty(config.removeMethods())) {
                for (String methodName : config.removeMethods()) {
                    CacheHelper.getCache().remove(nodeNamePrefix + methodName);
                    LoggerUtil.debug("Success evict proxy cache [class={0}, method={1}]", clazz, methodName);
                }
            }
        }
        return result;
    }

    private String getCacheKey(final Method method, final Object[] args) {
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

    private Object getAndCache(final Method method, final Object[] args, final int expireTime)
        throws InvocationTargetException, IllegalAccessException {
        Object result = method.invoke(target, args);
        if (result != null) {
            CacheHelper.getCache().putNodeValue(nodeNamePrefix + method.getName(), expireTime, getCacheKey(method, args),
                result);
            LoggerUtil.debug("Success proxy cache [class={0}, method={1}, expireTime={2}]", clazz, method.getName(),
                expireTime);
        }
        return result;
    }

}
