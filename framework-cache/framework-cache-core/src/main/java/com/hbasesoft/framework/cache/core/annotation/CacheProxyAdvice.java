package com.hbasesoft.framework.cache.core.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.cache.core.handler.CachePorxyInvocationHandler;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 *
 * @author hejiawen<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017/2/10 <br>
 * @see com.hbasesoft.framework.cache.core.annotation <br>
 * @since V1.0<br>
 */
@Component
public class CacheProxyAdvice implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Method[] methods = bean.getClass().getMethods();

        for (Method method : methods) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("set") && method.getParameterTypes().length == 1
                && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                try {
                    CacheProxy cacheProxy = method.getAnnotation(CacheProxy.class);
                    if (cacheProxy != null) {
                        Object value = refer(cacheProxy, method.getParameterTypes()[0]);
                        if (value != null) {
                            method.invoke(bean, value);
                        }
                    }
                }
                catch (Throwable e) {
                    LoggerUtil.error("Failed to init proxy cache at method " + name + " in class "
                        + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
                }
            }
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                CacheProxy cacheProxy = field.getAnnotation(CacheProxy.class);

                if (cacheProxy != null) {

                    Object value = refer(cacheProxy, field.getType());
                    if (value != null) {
                        field.set(bean, value);
                    }
                }
            }
            catch (Throwable e) {
                LoggerUtil.error("Failed to init proxy cache at filed " + field.getName() + " in class "
                    + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @SuppressWarnings("unchecked")
    private <T> T refer(CacheProxy cacheProxy, Class<T> clazz) {

        if (Modifier.isAbstract(clazz.getModifiers())) {
            T target = null;
            if (CommonUtil.isNotEmpty(cacheProxy.name())) {
                target = applicationContext.getBean(cacheProxy.name(), clazz);
            }
            else {
                target = applicationContext.getBean(clazz);
            }

            Assert.notNull(target, ErrorCodeDef.PROXY_TARGET_NOT_FOUND, clazz);

            CachePorxyInvocationHandler invocationHandler = new CachePorxyInvocationHandler(target, cacheProxy, clazz);

            T proxyObj = (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.isInterface() ? new Class[] {
                clazz
            } : clazz.getInterfaces(), invocationHandler);

            LoggerUtil.info("Success cache proxy clazz[{0}].", clazz);
            return proxyObj;
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param applicationContext
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
