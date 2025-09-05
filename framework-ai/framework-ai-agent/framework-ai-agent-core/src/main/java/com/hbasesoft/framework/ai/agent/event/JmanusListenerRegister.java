/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.event <br>
 */
@Component
public class JmanusListenerRegister implements BeanPostProcessor {

    @Autowired
    private JmanusEventPublisher jmanusEventPublisher;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof JmanusListener) {
            ResolvableType resolvableType = ResolvableType.forClass(bean.getClass()).as(JmanusListener.class);
            ResolvableType eventType = resolvableType.getGeneric(0);
            Class<?> eventClass = eventType.resolve();
            Class<? extends JmanusEvent> jmanusEventClass;
            try {
                jmanusEventClass = (Class<? extends JmanusEvent>) eventClass;
            }
            catch (Exception e) {
                throw new IllegalArgumentException("The listener can only listen to JmanusEvent type");
            }
            jmanusEventPublisher.registerListener(jmanusEventClass, (JmanusListener) bean);
        }
        return bean;
    }

}
