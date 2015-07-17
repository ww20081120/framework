/**
 * 
 */
package com.fccfc.framework.common.utils.io;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.common.utils.logger.Logger;

/**
 * HttpClientUtil
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
            logger.debug("send request:" + url);
            method.setRequestBody(getPostParamArr(paramMap));

            int statusCode = httpClient.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035,
                    "Http request failed, result code :${0}", statusCode);
            }
            body = method.getResponseBodyAsString();
            method.releaseConnection();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, message :${0}",
                e.getMessage());
        }

        return body;
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
            method.getParams()
                .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            int statusCode = httpClient.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035,
                    "Http request failed, result code :${0}", statusCode);
            }

            body = method.getResponseBodyAsString();
            method.releaseConnection();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.HTTP_REQUEST_ERROR_10035, "Http request failed, message :${0}",
                e.getMessage());
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
