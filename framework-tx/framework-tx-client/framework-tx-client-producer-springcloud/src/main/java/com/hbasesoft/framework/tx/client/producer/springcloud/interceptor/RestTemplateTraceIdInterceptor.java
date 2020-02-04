package com.hbasesoft.framework.tx.client.producer.springcloud.interceptor;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hbasesoft.framework.tx.core.TxManager;

public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        HttpHeaders headers = httpRequest.getHeaders();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {

            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            String traceId = request.getHeader(TraceIdFilter.TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = TxManager.getTraceId();
            }
            headers.put(TraceIdFilter.TRACE_ID, Arrays.asList(traceId));
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
