package com.hbasesoft.framework.tx.client.producer.springcloud.interceptor;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.hbasesoft.framework.tx.core.TxManager;

public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        HttpHeaders headers = httpRequest.getHeaders();

        headers.put(TraceIdFilter.TRACE_ID, Arrays.asList(TxManager.getTraceId()));

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
