/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.file;

import org.springframework.context.annotation.Configuration;

/**
 * <Description> <br>
 * 
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年6月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.file <br>
 */
@Configuration
public class Slf4JSpanExporterProvider {

//    @Bean
//    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
//        observationRegistry.observationConfig().observationHandler(new SimpleLoggingHandler());
//
//        return new ObservedAspect(observationRegistry);
//    }
//
//    public class SimpleLoggingHandler implements ObservationHandler<Observation.Context> {
//        @Override
//        public void onStart(Observation.Context context) {
//            LoggerUtil.info("Starting context {0} ", context);
//        }
//
//        @Override
//        public void onStop(Observation.Context context) {
//            TracingContext txt = context.computeIfAbsent(TracingContext.class, clazz -> new TracingContext());
//            Span span = txt.getSpan();
//            LoggerUtil.info("Stopping span {0} ", JSONObject.toJSONString(span));
//        }
//
//        @Override
//        public boolean supportsContext(Observation.Context context) {
//            return true;
//        }
//    }
}
