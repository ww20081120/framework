/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.otlp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.utils.PropertyHolder;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.logging.otlp.OtlpJsonLoggingSpanExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporterBuilder;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporterBuilder;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.ServiceAttributes;

/**
 * <Description> OTLP链路追踪配置 - 将trace和logback日志异步发送到远程OTLP后端(如OpenObserve) <br>
 * <p>
 * 通过 otlp.enabled=true 激活，所有导出均使用异步批量处理，不阻塞业务线程：
 * </p>
 * <ul>
 * <li>远程Trace导出：BatchSpanProcessor + OtlpHttpSpanExporter/OtlpGrpcSpanExporter</li>
 * <li>远程日志导出：BatchLogRecordProcessor + OtlpHttpLogRecordExporter/OtlpGrpcLogRecordExporter</li>
 * <li>可选本地trans.log：当 otlp.local-trace-enabled=true 时启用，仅用于调试</li>
 * </ul>
 * <p>
 * 配置示例（application.yml）：
 * </p>
 *
 * <pre>
 * otlp:
 *   enabled: true
 *   local-trace-enabled: false        # 是否保留本地trans.log（默认false）
 *   protocol: http                    # http 或 grpc
 *   trace-endpoint: http://localhost:5080/api/default/v1/traces
 *   log-endpoint: http://localhost:5080/api/default/v1/logs
 *   authorization: Basic d3cyMDA4...  # Basic认证token
 *   organization: default             # OpenObserve组织
 *   stream-name: default              # OpenObserve流名称
 * </pre>
 *
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2025年2月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.otlp <br>
 */
@ConditionalOnProperty(prefix = "otlp", name = "enabled", havingValue = "true")
@Configuration
public class OtlpTracingConfig {

    /** 批量导出间隔（毫秒） */
    private static final int SCHEDULE_DELAY_MS = 5000;

    /** 批量导出最大条数 */
    private static final int MAX_EXPORT_BATCH_SIZE = 512;

    /** 导出超时（毫秒） */
    private static final int EXPORT_TIMEOUT_MS = 30000;

    /** URL协议前缀长度（"://"占3个字符） */
    private static final int SCHEME_SUFFIX_LEN = 3;

    /**
     * Description: 创建OpenTelemetry SDK实例 <br>
     * 同时创建远程OTLP Trace导出和远程OTLP Logs导出。
     * 当 otlp.local-trace-enabled=true 时，额外保留本地 trans.log 输出。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param protocol 协议类型 http/grpc
     * @param traceEndpoint trace端点地址
     * @param logEndpoint logs端点地址
     * @param authorization 认证信息（Basic token）
     * @param organization OpenObserve组织名称
     * @param streamName OpenObserve流名称
     * @param localTraceEnabled 是否保留本地trans.log输出（默认false）
     * @return OpenTelemetrySdk实例
     */
    @Bean
    public OpenTelemetrySdk openTelemetrySdk(
        @Value("${otlp.protocol:http}") final String protocol,
        @Value("${otlp.trace-endpoint:http://localhost:5080/api/default/v1/traces}") final String traceEndpoint,
        @Value("${otlp.log-endpoint:http://localhost:5080/api/default/v1/logs}") final String logEndpoint,
        @Value("${otlp.authorization:}") final String authorization,
        @Value("${otlp.organization:default}") final String organization,
        @Value("${otlp.stream-name:default}") final String streamName,
        @Value("${otlp.local-trace-enabled:false}") final boolean localTraceEnabled) {

        Resource resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, PropertyHolder.getProjectName())));

        // 1. 远程OTLP Trace导出
        SpanExporter otlpExporter = createSpanExporter(protocol, traceEndpoint, authorization, organization,
            streamName);

        // 构建TracerProvider，可选添加本地文件导出
        var tracerBuilder = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(otlpExporter)
                .setScheduleDelay(SCHEDULE_DELAY_MS, TimeUnit.MILLISECONDS)
                .setMaxExportBatchSize(MAX_EXPORT_BATCH_SIZE)
                .setExporterTimeout(EXPORT_TIMEOUT_MS, TimeUnit.MILLISECONDS).build())
            .setResource(resource);

        // 可选：保留本地 trans.log 输出（仅用于调试）
        if (localTraceEnabled) {
            tracerBuilder.addSpanProcessor(BatchSpanProcessor.builder(OtlpJsonLoggingSpanExporter.create())
                .setScheduleDelay(SCHEDULE_DELAY_MS, TimeUnit.MILLISECONDS)
                .setMaxExportBatchSize(MAX_EXPORT_BATCH_SIZE)
                .setExporterTimeout(EXPORT_TIMEOUT_MS, TimeUnit.MILLISECONDS).build());
        }

        SdkTracerProvider sdkTracerProvider = tracerBuilder.build();

        // 2. Logs SDK
        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
            .addLogRecordProcessor(BatchLogRecordProcessor
                .builder(createLogExporter(protocol, logEndpoint, authorization, organization, streamName))
                .setScheduleDelay(SCHEDULE_DELAY_MS, TimeUnit.MILLISECONDS)
                .setMaxExportBatchSize(MAX_EXPORT_BATCH_SIZE)
                .setExporterTimeout(EXPORT_TIMEOUT_MS, TimeUnit.MILLISECONDS).build())
            .setResource(resource).build();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider)
            .setLoggerProvider(loggerProvider).build();

        try {
            GlobalOpenTelemetry.set(sdk);
        }
        catch (IllegalStateException e) {
            // GlobalOpenTelemetry已被其他组件(如MySQL Connector/J)初始化，忽略重复注册
        }

        // 主动注入SDK到OpenObserveLogbackAppender，解决logback初始化时序问题
        OpenObserveLogbackAppender.setOpenTelemetry(sdk);

        return sdk;
    }

    /**
     * Description: 创建Micrometer Tracer桥接bean <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param openTelemetrySdk OpenTelemetry SDK实例
     * @return Micrometer Tracer
     */
    @Bean
    public Tracer micrometerTracer(final OpenTelemetrySdk openTelemetrySdk) {
        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk.getTracer(PropertyHolder.getProjectName());
        OtelCurrentTraceContext currentTraceContext = new OtelCurrentTraceContext();
        return new OtelTracer(otelTracer, currentTraceContext, event -> {
        });
    }

    /**
     * Description: 创建Span Exporter <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param protocol 协议类型
     * @param endpoint 端点地址
     * @param authorization 认证信息
     * @param organization 组织名称
     * @param streamName 流名称
     * @return SpanExporter实例
     */
    private SpanExporter createSpanExporter(final String protocol, final String endpoint, final String authorization,
        final String organization, final String streamName) {
        Map<String, String> headers = buildHeaders(protocol, authorization, organization, streamName);

        if ("grpc".equalsIgnoreCase(protocol)) {
            OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder()
                .setEndpoint(normalizeGrpcEndpoint(endpoint))
                // OpenObserve的gRPC要求设置grpc-encoding头，启用gzip压缩以满足该要求
                .setCompression("gzip");
            headers.forEach(builder::addHeader);
            return builder.build();
        }
        else {
            OtlpHttpSpanExporterBuilder builder = OtlpHttpSpanExporter.builder().setEndpoint(endpoint);
            headers.forEach(builder::addHeader);
            return builder.build();
        }
    }

    /**
     * Description: 创建LogRecord Exporter <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param protocol 协议类型
     * @param endpoint 端点地址
     * @param authorization 认证信息
     * @param organization 组织名称
     * @param streamName 流名称
     * @return LogRecordExporter实例
     */
    private io.opentelemetry.sdk.logs.export.LogRecordExporter createLogExporter(final String protocol,
        final String endpoint, final String authorization, final String organization, final String streamName) {
        Map<String, String> headers = buildHeaders(protocol, authorization, organization, streamName);

        if ("grpc".equalsIgnoreCase(protocol)) {
            OtlpGrpcLogRecordExporterBuilder builder = OtlpGrpcLogRecordExporter.builder()
                .setEndpoint(normalizeGrpcEndpoint(endpoint))
                // OpenObserve的gRPC要求设置grpc-encoding头，启用gzip压缩以满足该要求
                .setCompression("gzip");
            headers.forEach(builder::addHeader);
            return builder.build();
        }
        else {
            var builder = io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
                .builder().setEndpoint(endpoint);
            headers.forEach(builder::addHeader);
            return builder.build();
        }
    }

    /**
     * Description: 规范化gRPC端点地址 <br>
     * <p>
     * gRPC端点仅需 host:port，不需要路径后缀（如/v1/traces）。
     * 移除路径部分，并保证带协议前缀（OTel SDK要求）。
     * </p>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param endpoint 原始端点
     * @return 规范化后的gRPC端点，如 http://localhost:5081
     */
    private String normalizeGrpcEndpoint(final String endpoint) {
        String url = endpoint;
        // 移除路径部分，只保留 scheme://host:port
        int schemeEnd = url.indexOf("://") + SCHEME_SUFFIX_LEN;
        int pathIdx = url.indexOf("/", schemeEnd);
        if (pathIdx > 0) {
            url = url.substring(0, pathIdx);
        }
        // 保证带协议前缀
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * Description: 构建OpenObserve所需的HTTP Headers <br>
     * <p>
     * 注意：gRPC协议的HTTP/2 header name必须是全小写（gRPC规范要求），
     * 因此grpc模式下Authorization会被转为authorization。
     * </p>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param protocol 协议类型
     * @param authorization 认证信息
     * @param organization 组织名称
     * @param streamName 流名称
     * @return Headers Map
     */
    private Map<String, String> buildHeaders(final String protocol, final String authorization,
        final String organization, final String streamName) {
        boolean isGrpc = "grpc".equalsIgnoreCase(protocol);
        Map<String, String> headers = new HashMap<>();
        if (authorization != null && !authorization.isEmpty()) {
            // gRPC/HTTP2规范要求header name全小写
            headers.put(isGrpc ? "authorization" : "Authorization", authorization);
        }
        if (organization != null && !organization.isEmpty()) {
            headers.put("organization", organization);
        }
        if (streamName != null && !streamName.isEmpty()) {
            headers.put("stream-name", streamName);
        }
        return headers;
    }
}
