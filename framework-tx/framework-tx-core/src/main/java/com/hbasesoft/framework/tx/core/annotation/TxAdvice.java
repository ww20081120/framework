/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core.annotation;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.TxManager;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.annotation <br>
 */
@Aspect
@Configuration
public class TxAdvice {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Pointcut("execution(public * com.hbasesoft..*(..))")
    public void tx() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param thisJoinPoint
     * @return Object
     * @throws Throwable <br>
     */
    @Around("tx()")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Signature sig = thisJoinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) sig;
            Object target = thisJoinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            Tx tx = AnnotationUtils.findAnnotation(currentMethod, Tx.class);
            if (tx != null && !TxManager.isRetry()) {

                ClientInfo clientInfo = new ClientInfo(TxManager.getTraceId(),
                    StringUtils.isNotEmpty(tx.name()) ? tx.name() : TxManager.getMarker(currentMethod));
                clientInfo.setArgs(ArgsSerializationUtil.serializeArgs(thisJoinPoint.getArgs()));
                clientInfo.setMaxRetryTimes(tx.maxRetryTimes());
                clientInfo.setRetryConfigs(tx.retryConfigs());

                return TxInvokerProxy.registInvoke(clientInfo, () -> {
                    try {
                        return thisJoinPoint.proceed();
                    }
                    catch (Throwable e) {
                        throw e instanceof FrameworkException ? (FrameworkException) e : new FrameworkException(e);
                    }
                });
            }
        }

        return thisJoinPoint.proceed();
    }

}
