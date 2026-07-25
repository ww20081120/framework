/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.otlp;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.hbasesoft.framework.common.utils.PropertyHolder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.LoggerProvider;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;

/**
 * <Description> OpenObserve Logback Appender - 将logback日志通过OTLP协议异步发送到OpenObserve <br>
 *
 * <p>性能说明：</p>
 * <ul>
 *   <li>append()方法仅将日志事件入队到OTel SDK内部的BatchLogRecordProcessor，不执行网络IO</li>
 *   <li>实际的网络发送由OTel SDK后台线程批量异步完成</li>
 *   <li>建议在logback.xml中配合AsyncAppender使用，进一步隔离logback的格式化开销</li>
 * </ul>
 *
 * <p>使用方式（直接使用）：</p>
 * <pre>
 * &lt;appender name="openobserveAppender"
 *     class="com.hbasesoft.framework.tracing.otlp.OpenObserveLogbackAppender"&gt;
 *     &lt;filter class="ch.qos.logback.classic.filter.ThresholdFilter"&gt;
 *         &lt;level&gt;INFO&lt;/level&gt;
 *     &lt;/filter&gt;
 * &lt;/appender&gt;
 * </pre>
 *
 * <p>使用方式（配合AsyncAppender，推荐生产环境）：</p>
 * <pre>
 * &lt;appender name="openobserveAppender"
 *     class="com.hbasesoft.framework.tracing.otlp.OpenObserveLogbackAppender"&gt;
 *     &lt;filter class="ch.qos.logback.classic.filter.ThresholdFilter"&gt;
 *         &lt;level&gt;INFO&lt;/level&gt;
 *     &lt;/filter&gt;
 * &lt;/appender&gt;
 * &lt;appender name="asyncOpenobserveAppender"
 *     class="ch.qos.logback.classic.AsyncAppender"&gt;
 *     &lt;queueSize&gt;1024&lt;/queueSize&gt;
 *     &lt;discardingThreshold&gt;0&lt;/discardingThreshold&gt;
 *     &lt;appender-ref ref="openobserveAppender"/&gt;
 * &lt;/appender&gt;
 * </pre>
 *
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2025年2月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.otlp <br>
 */
public class OpenObserveLogbackAppender extends AppenderBase<ILoggingEvent> {

    /** OTel OpenTelemetry实例（延迟注入） */
    private static final AtomicReference<OpenTelemetry> OPEN_TELEMETRY_REF = new AtomicReference<>();

    /** 单例引用，供OtlpTracingConfig注入OpenTelemetry实例 */
    private static final AtomicReference<OpenObserveLogbackAppender> INSTANCE = new AtomicReference<>();

    /** OTel Logger（延迟初始化） */
    private final AtomicReference<Logger> otelLoggerRef = new AtomicReference<>();

    /** 应用名称 */
    private final AtomicReference<String> appNameRef = new AtomicReference<>();

    /** 是否已打印初始化警告 */
    private final AtomicBoolean initWarnLogged = new AtomicBoolean();

    /** 是否已成功初始化Logger */
    private final AtomicBoolean loggerInitialized = new AtomicBoolean();

    /**
     * Description: 构造函数，注册单例 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     */
    public OpenObserveLogbackAppender() {
        INSTANCE.compareAndSet(null, this);
    }

    /**
     * Description: 由OtlpTracingConfig在SDK初始化完成后调用，注入OpenTelemetry实例 <br>
     * 解决logback初始化早于Spring Boot的时序问题
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param openTelemetry OpenTelemetry SDK实例
     */
    public static void setOpenTelemetry(final OpenTelemetry openTelemetry) {
        OPEN_TELEMETRY_REF.set(openTelemetry);
        OpenObserveLogbackAppender appender = INSTANCE.get();
        if (appender != null) {
            appender.initLogger(openTelemetry);
        }
    }

    /**
     * Description: 初始化Logger <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param openTelemetry OpenTelemetry实例
     */
    private void initLogger(final OpenTelemetry openTelemetry) {
        if (openTelemetry == null || loggerInitialized.get()) {
            return;
        }
        try {
            LoggerProvider loggerProvider = openTelemetry.getLogsBridge();
            this.otelLoggerRef.set(loggerProvider.get(OpenObserveLogbackAppender.class.getName()));
            this.loggerInitialized.set(true);
        }
        catch (Exception e) {
            addWarn("Failed to initialize OTel Logger: " + e.getMessage());
        }
    }

    /**
     * Description: 初始化 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void start() {
        this.appNameRef.set(PropertyHolder.getProjectName());
        // 如果SDK已经先于logback初始化完成，立即初始化Logger
        OpenTelemetry otel = OPEN_TELEMETRY_REF.get();
        if (otel != null) {
            initLogger(otel);
        }
        super.start();
    }

    /**
     * Description: 发送日志事件到OpenObserve <br>
     * <p>
     * 此方法仅将日志入队到OTel SDK内部的BatchLogRecordProcessor，
     * 实际网络IO由SDK后台线程异步批量完成，不阻塞业务线程。
     * </p>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param event logback日志事件
     */
    @Override
    protected void append(final ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        Logger logger = otelLoggerRef.get();

        // 双重检查：Logger可能尚未初始化，尝试延迟初始化
        if (logger == null) {
            OpenTelemetry otel = OPEN_TELEMETRY_REF.get();
            if (otel != null) {
                initLogger(otel);
            }
            logger = otelLoggerRef.get();
        }

        if (logger == null) {
            // SDK尚未注入，静默跳过（仅打印一次警告）
            if (initWarnLogged.compareAndSet(false, true)) {
                addWarn("OpenTelemetry Logs SDK not ready, log events will be dropped until it initializes");
            }
            return;
        }

        try {
            AttributesBuilder attrBuilder = Attributes.builder()
                .put("app", appNameRef.get())
                .put("logger", event.getLoggerName())
                .put("thread", event.getThreadName())
                .put("level", event.getLevel().toString());

            // 添加MDC中所有自定义属性
            if (event.getMDCPropertyMap() != null) {
                event.getMDCPropertyMap().forEach((key, value) -> {
                    if (value != null && !"traceId".equals(key) && !"spanId".equals(key)) {
                        attrBuilder.put("mdc." + key, value);
                    }
                });
            }

            Severity severity = toSeverity(event.getLevel());

            io.opentelemetry.api.logs.LogRecordBuilder recordBuilder = logger.logRecordBuilder()
                .setTimestamp(event.getTimeStamp(), TimeUnit.MILLISECONDS).setSeverity(severity)
                .setSeverityText(event.getLevel().toString()).setBody(event.getFormattedMessage())
                .setAllAttributes(attrBuilder.build());

            // 关联trace context：从ILoggingEvent的MDC快照中提取traceId/spanId
            // 注意：不能使用MDC.get()，因为AsyncAppender在工作线程中调用append()，
            // 此时MDC是工作线程的ThreadLocal，不含业务线程的traceId
            // 必须使用event.getMDCPropertyMap()，它在事件创建时就保存了MDC快照
            String traceId = null;
            String spanId = null;
            if (event.getMDCPropertyMap() != null) {
                traceId = event.getMDCPropertyMap().get("traceId");
                spanId = event.getMDCPropertyMap().get("spanId");
            }
            if (traceId != null && !traceId.isEmpty() && spanId != null && !spanId.isEmpty()) {
                try {
                    SpanContext spanContext = SpanContext.create(traceId, spanId, TraceFlags.getSampled(),
                        TraceState.getDefault());
                    recordBuilder.setContext(Context.current().with(Span.wrap(spanContext)));
                }
                catch (Exception e) {
                    addWarn("Failed to set trace context from MDC: " + e.getMessage());
                }
            }

            // emit()仅入队到BatchLogRecordProcessor，不执行网络IO
            recordBuilder.emit();
        }
        catch (Throwable e) {
            addWarn("Failed to emit log to OpenObserve: " + e.getMessage());
        }
    }

    /**
     * Description: 将logback日志级别映射为OTel Severity <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param level logback日志级别
     * @return OTel Severity
     */
    private static Severity toSeverity(final Level level) {
        switch (level.toInt()) {
            case Level.TRACE_INT:
                return Severity.TRACE;
            case Level.DEBUG_INT:
                return Severity.DEBUG;
            case Level.INFO_INT:
                return Severity.INFO;
            case Level.WARN_INT:
                return Severity.WARN;
            case Level.ERROR_INT:
                return Severity.ERROR;
            default:
                return Severity.UNDEFINED_SEVERITY_NUMBER;
        }
    }
}
