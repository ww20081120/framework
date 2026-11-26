/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.tracing.core.TraceLog;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.EventListener;
import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelPropagator;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.micrometer.tracing.otel.bridge.Slf4JBaggageEventListener;
import io.micrometer.tracing.otel.bridge.Slf4JEventListener;
import io.micrometer.tracing.propagation.Propagator;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.logging.otlp.OtlpJsonLoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.ServiceAttributes;

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
@ConditionalOnBean(annotation = TraceLog.class)
@Configuration
public class OpenTelemetryConfig {

    /**
     * Description: 创建OpenTelemetry SDK实例 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return OpenTelemetrySdk实例
     */
    @Bean
    @ConditionalOnMissingBean(OpenTelemetrySdk.class)
    public OpenTelemetrySdk openTelemetrySdk() {
        Resource resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, PropertyHolder.getProjectName())));
        SpanExporter exporter = OtlpJsonLoggingSpanExporter.create();

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(exporter)).setResource(resource).build();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider)
            // 配置W3C Trace Context Propagator，使trace context可通过traceparent header跨服务传播
            // OpenTelemetrySdkBuilder默认使用ContextPropagators.noop()，不配置则跨服务traceId无法打通
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance())).build();
        try {
            GlobalOpenTelemetry.set(sdk);
        }
        catch (IllegalStateException e) {
            // GlobalOpenTelemetry已被其他组件(如MySQL Connector/J)初始化，忽略重复注册
        }
        return sdk;
    }

    /**
     * Description: 创建Micrometer Tracer桥接bean <br>
     * <p>
     * 配置完整的 OtelTracer，包含：
     * </p>
     * <ul>
     * <li>Slf4JEventListener：自动将 traceId/spanId 写入 MDC，与 MicrometerTracerAgent 使用相同的 key</li>
     * <li>Slf4JBaggageEventListener：将 baggage 字段写入 MDC（可选，通过 management.tracing.baggage.correlation.fields 配置）</li>
     * <li>OtelBaggageManager：支持 baggage 跨线程/跨服务传播</li>
     * </ul>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param openTelemetrySdk OpenTelemetry SDK实例
     * @return Micrometer Tracer
     */
    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    public Tracer micrometerTracer(final OpenTelemetrySdk openTelemetrySdk) {
        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk.getTracer(PropertyHolder.getProjectName());
        OtelCurrentTraceContext currentTraceContext = new OtelCurrentTraceContext();

        // EventListener：Slf4JEventListener 自动将 traceId/spanId 写入 MDC
        List<EventListener> eventListeners = new ArrayList<>();
        eventListeners.add(new Slf4JEventListener());

        // 可选：Baggage 字段写入 MDC（通过 management.tracing.baggage.correlation.fields 配置）
        String baggageCorrelationFields = PropertyHolder
            .getProperty("management.tracing.baggage.correlation.fields", "");
        if (baggageCorrelationFields != null && !baggageCorrelationFields.isEmpty()) {
            List<String> fields = Arrays.asList(baggageCorrelationFields.split(","));
            eventListeners.add(new Slf4JBaggageEventListener(fields));
        }

        // BaggageManager：支持 baggage 跨线程/跨服务传播
        OtelBaggageManager baggageManager = new OtelBaggageManager(currentTraceContext, new ArrayList<>(),
            new ArrayList<>());

        // 组合多个 EventListener 为单个 EventPublisher
        OtelTracer.EventPublisher publisher = event -> {
            for (EventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        };

        return new OtelTracer(otelTracer, currentTraceContext, publisher, baggageManager);
    }

    /**
     * Description: 创建Micrometer Propagator bean <br>
     * <p>
     * Propagator 是跨服务 trace context 传播的关键组件。没有它：
     * </p>
     * <ul>
     * <li>Spring Boot 的 PropagatingSenderTracingObservationHandler 不会创建（出站请求不传播 trace）</li>
     * <li>Spring Boot 的 PropagatingReceiverTracingObservationHandler 不会创建（入站请求不接收 trace）</li>
     * <li>RestTemplate/WebClient 调用下游服务时不会携带 traceparent header</li>
     * <li>Kafka 消息的 trace context 不会传播</li>
     * </ul>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param openTelemetrySdk OpenTelemetry SDK实例
     * @return Micrometer Propagator
     */
    @Bean
    @ConditionalOnMissingBean(Propagator.class)
    public Propagator propagator(final OpenTelemetrySdk openTelemetrySdk) {
        return new OtelPropagator(openTelemetrySdk.getPropagators(),
            openTelemetrySdk.getTracer(PropertyHolder.getProjectName()));
    }
}
