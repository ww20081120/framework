/**
 * 
 */
package com.fccfc.framework.common.utils.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.common.utils.logger.Logger;

/**
 * HttpClientUtil
 * 
 * @author Administrator
 */
public final class HttpClientUtil {

    /**
     * logger
     */
    private static Logger logger = new Logger(HttpClientUtil.class);

    /**
     * http POST请求
     * 
     * @param url 链接
     * @param paramMap 参数
     * @return 结果
     * @throws UtilException <br>
     */
    public static String post(String url, Map<String, String> paramMap) throws UtilException {
        HttpClient httpClient = new HttpClient();
        String body = null;
        PostMethod method = new PostMethod(url);

        try {
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, GlobalConstants.DEFAULT_CHARSET);

            logger.debug("send request:" + url);
            method.setRequestBody(getPostParamArr(paramMap));

            int statusCode = httpClient.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, result code :${0}",
                    statusCode);
            }
            //body = method.getResponseBodyAsString();
            BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));  
            StringBuffer stringBuffer = new StringBuffer();  
            String str = "";  
            while ((str = reader.readLine()) != null) {
                stringBuffer.append(str);  
            }  
            body = stringBuffer.toString();  
            method.releaseConnection();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, message :${0}", e,
                e.getMessage());
        }

        return body;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     * @param json <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static String post(String url, JSONObject json) throws UtilException {
        return post(null, url, "application/json", json.toJSONString());
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param sslContext <br>
     * @param url <br>
     * @param contentType <br>
     * @param content <br>
     * @return <br>
     * @throws UtilException <br>
     */
    @SuppressWarnings({
        "deprecation"
    })
    public static String post(SSLContext sslContext, String url, String contentType, String content)
        throws UtilException {
        org.apache.http.client.HttpClient httpClient = null;
        if (sslContext != null) {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] {
                "TLSv1"
            }, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        }
        else {
            httpClient = HttpClients.custom().build();
        }

        HttpPost request = new HttpPost(url);
        try {
            StringEntity params = new StringEntity(content, GlobalConstants.DEFAULT_CHARSET);
            request.addHeader("content-type", contentType);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), GlobalConstants.DEFAULT_CHARSET);
            }
            else {
                throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, message :${0}",
                    statusCode);
            }
        }
        catch (IOException e) {
            throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, message :${0}", e,
                e.getMessage());
        }
    }

    /**
     * http GET请求
     * 
     * @param url 链接
     * @return 结果
     * @throws UtilException 异常
     */
    public static String get(String url) throws UtilException {
        HttpClient httpClient = new HttpClient();
        GetMethod method = new GetMethod(url);
        String body = null;

        try {
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, GlobalConstants.DEFAULT_CHARSET);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

            int statusCode = httpClient.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, result code :${0}", statusCode);
            }

            body = method.getResponseBodyAsString();
            method.releaseConnection();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, message :${0}",
                e.getMessage(), e);
        }

        return body;
    }

    /**
     * 封装post请求参数
     * 
     * @param paramMap 参数map
     * @return 结果
     */
    private static NameValuePair[] getPostParamArr(Map<String, String> paramMap) {
        if (CommonUtil.isEmpty(paramMap)) {
            return null;
        }

        Set<String> keySet = paramMap.keySet();
        NameValuePair[] paramArr = new NameValuePair[keySet.size()];
        String key = null;

        Iterator<String> it = keySet.iterator();
        int index = 0;
        while (it.hasNext()) {
            key = it.next();
            paramArr[index++] = new NameValuePair(key, paramMap.get(key));
        }

        return paramArr;
    }
}
