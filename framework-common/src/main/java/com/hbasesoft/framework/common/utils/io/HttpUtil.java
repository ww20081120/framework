package com.hbasesoft.framework.common.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <Description>Http 工具类 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.io <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtil {

    /** DEFAULT_HTTP_PORT */
    public static final int DEFAULT_HTTP_PORT = 80;

    /** DEFAULT_HTTPS_PORT */
    public static final int DEFAULT_HTTPS_PORT = 443;

    /** CONNECT_TIMEOUT */
    public static final Long CONNECT_TIMEOUT = 5000L;

    /** READ_TIMEOUT */
    public static final Long READDING_TIMEOUT = 30000L;

    /** httpClientHold */
    private static ThreadLocal<OkHttpClient> httpClientHold = new ThreadLocal<>();

    /**
     * <p>
     * 执行Get请求
     * </p>
     * 
     * @param url
     * @return - 默认返回text/html
     */
    public static String doGet(final String url) {
        return doGet(url, GlobalConstants.DEFAULT_CHARSET);
    }

    /**
     * Description: doGet<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param charset
     * @return <br>
     */
    public static String doGet(final String url, final String charset) {
        Request request = new Request.Builder().url(url).build();
        return getStringRequest(request, charset);
    }

    /**
     * Description: doGet<br>
     *
     * @author 王伟<br>
     * @param url
     * @param charset
     * @param authorization
     * @return String
     */
    public static String doGet(final String url, final String charset, final String authorization) {
        Request request = new Request.Builder().url(url).addHeader("Authorization", authorization).build();
        return getStringRequest(request, charset);
    }

    /**
     * Description: 执行Post请求<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param paramMap
     * @return <br>
     */
    public static String doPost(final String url, final Map<String, String> paramMap) {
        return doPost(url, paramMap, GlobalConstants.DEFAULT_CHARSET);
    }

    /**
     * Description: doPost<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param paramMap
     * @param charset
     * @return <br>
     */
    public static String doPost(final String url, final Map<String, String> paramMap, final String charset) {
        Builder builder = new FormBody.Builder();
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> param : paramMap.entrySet()) {
                builder.add(param.getKey(), param.getValue());
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return getStringRequest(request, charset);
    }

    /**
     * Description: getStringRequest<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @param charset
     * @return <br>
     */
    public static String getStringRequest(final Request request, final String charset) {

        Call call = getOkHttpClient().newCall(request);
        try {
            Response response = call.execute();
            return IOUtil.readString(new InputStreamReader(response.body().byteStream(), charset));
        }
        catch (IOException e) {
            throw new UtilException(e);
        }
    }

    /**
     * Description: 执行Post请求<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param body
     * @param contentType
     * @param charset
     * @return <br>
     */
    public static String doPost(final String url, final String body, final String contentType, final String charset) {
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return getStringRequest(request, charset);
    }

    /**
     * Description: doPost<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param body
     * @param contentType
     * @return <br>
     */
    public static String doPost(final String url, final String body, final String contentType) {
        return doPost(url, body, contentType, GlobalConstants.DEFAULT_CHARSET);
    }

    /**
     * Description: doPost<br>
     *
     * @param url
     * @param body
     * @param contentType
     * @param authorization
     * @param charset
     * @author 王伟<br>
     * @return String
     */
    public static String doPost(final String url, final String body, final String contentType,
        final String authorization, final String charset) {
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).addHeader("Authorization", authorization).post(requestBody)
            .build();
        return getStringRequest(request, charset);
    }

    /**
     * @Method doPost
     * @param url
     * @param body
     * @param contentType
     * @param paramMap
     * @param charset
     * @return java.lang.String
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:24
     */
    public static String doPost(final String url, final String body, final String contentType,
        final Map<String, String> paramMap, final String charset) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> param : paramMap.entrySet()) {
                builder.addHeader(param.getKey(), param.getValue());
            }
        }
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = builder.post(requestBody).build();
        return getStringRequest(request, charset);
    }

    /**
     * Description:doPost <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param body
     * @return <br>
     */
    public static String doPost(final String url, final String body) {
        String contentType = "text/plain";
        if (body.startsWith("{") && body.endsWith("}")) {
            contentType = "application/json";
        }
        else if (body.startsWith("<") && body.endsWith(">")) {
            contentType = "text/xml";
        }
        return doPost(url, body, contentType);
    }

    /**
     * Description: 执行get请求<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param absolutePath <br>
     */
    public static void downloadFile(final String url, final String absolutePath) {
        IOUtil.copyFileFromInputStream(absolutePath, downloadFile(url));
    }

    /**
     * Description: 执行get请求<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @return InputStream <br>
     */
    public static InputStream downloadFile(final String url) {
        Request request = new Request.Builder().url(url).build();
        OkHttpClient okHttpClient = getOkHttpClient();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return response.body().byteStream();
        }
        catch (IOException e) {
            throw new UtilException(e);
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @param includeQueryString
     * @return <br>
     */
    public static String getRequestURL(final HttpServletRequest request, final boolean includeQueryString) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        String urlPath = request.getRequestURI();

        url.append(scheme); // http, https
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && port != DEFAULT_HTTP_PORT)
            || (scheme.equals("https") && port != DEFAULT_HTTPS_PORT)) {
            url.append(':');
            url.append(request.getServerPort());
        }
        url.append(urlPath);
        if (includeQueryString && !StringUtils.isEmpty(request.getQueryString())) {
            url.append("?" + request.getQueryString());
        }
        return url.toString();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @param includeQueryString
     * @return <br>
     */
    public static String getRequestURI(final HttpServletRequest request, final boolean includeQueryString) {
        String url = request.getRequestURI();
        if (includeQueryString && !StringUtils.isEmpty(request.getQueryString())) {
            url += "?" + request.getQueryString();
        }
        return url;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @param includeQueryString
     * @return <br>
     */
    public static String getRequestRelaURI(final HttpServletRequest request, final boolean includeQueryString) {
        String url = request.getRequestURI();
        if (includeQueryString && !StringUtils.isEmpty(request.getQueryString())) {
            url += "?" + request.getQueryString();
        }
        return url.substring(request.getContextPath().length());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @return <br>
     */
    public static String getRequestIp(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotEmpty(ip)) {
            int index = ip.indexOf(GlobalConstants.SPLITOR);
            if (index != -1) {
                ip = ip.substring(0, index);
            }
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @return <br>
     */
    public static String getClientInfo(final HttpServletRequest request) {
        return request.getHeader("User-Agent").toLowerCase();
    }

    /**
     * Description: 判断来的请求是否是异步请求<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @return <br>
     */
    public static boolean isAsynRequest(final HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * @Method getOkHttpClient
     * @param
     * @return okhttp3.OkHttpClient
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:25
     */
    public static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = httpClientHold.get();
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(PropertyHolder.getLongProperty("ribbon.ConnectTimeout", CONNECT_TIMEOUT),
                    TimeUnit.MILLISECONDS)
                .readTimeout(PropertyHolder.getLongProperty("ribbon.ReadTimeout", READDING_TIMEOUT),
                    TimeUnit.MILLISECONDS)
                .sslSocketFactory(getSSLSocketFactory(), TrustManagerUtils.getAcceptAllTrustManager())
                .hostnameVerifier(getHostnameVerifier()).build();
            httpClientHold.set(okHttpClient);
        }
        return okHttpClient;
    }

    /**
     * @Method getSSLSocketFactory
     * @param
     * @return javax.net.ssl.SSLSocketFactory
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:27
     */
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Method getTrustManager
     * @param
     * @return javax.net.ssl.TrustManager[]
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:25
     */
    public static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
        return trustAllCerts;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return (s, sslSession) -> true;
    }
}
