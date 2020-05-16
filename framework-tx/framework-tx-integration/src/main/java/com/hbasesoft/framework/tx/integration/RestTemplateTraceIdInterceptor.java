package com.hbasesoft.framework.tx.integration;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.hbasesoft.framework.tx.core.TxManager;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Mar 4, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.client.producer.springcloud.interceptor <br>
 */
public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param httpRequest
     * @param bytes
     * @param clientHttpRequestExecution
     * @return
     * @throws IOException <br>
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest httpRequest, final byte[] bytes,
        final ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();

        headers.put(TraceIdFilter.TRACE_ID, Arrays.asList(TxManager.getTraceId()));

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
