package com.hbasesoft.framework.common.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.security.URLUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    public static final Long READING_TIMEOUT = 30000L;

    /** 最大参数 */
    private static final int MAX_PARAMS = 16;

    /** HTTP_CLIENT */
    private static final HttpClient HTTP_CLIENT = createHttpClient();

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
    public static String doGet(final String url, final Charset charset) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout()).GET().build();
        return getStringRequest(request, charset);
    }

    public static Duration getTimeout() {
        return Duration.ofMillis(PropertyHolder.getLongProperty("ribbon.ReadTimeout", READING_TIMEOUT));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param headers
     * @return <br>
     */
    public static String doGet(final String url, final Map<String, String> headers) {
        return doGet(url, GlobalConstants.DEFAULT_CHARSET, headers);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param charset
     * @param headers
     * @return <br>
     */
    public static String doGet(final String url, final Charset charset, final Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout()).GET();

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.header(header.getKey(), header.getValue());
            }
        }

        HttpRequest request = requestBuilder.build();
        return getStringRequest(request, charset);
    }

    /**
     * 字符串转map，字符串格式为 {@code xxx=xxx&xxx=xxx}
     *
     * @param strTemp 待转换的字符串
     * @param decode 是否解码
     * @return map
     */
    public static Map<String, String> paramsParse(final String strTemp, final boolean decode) {
        String str = preProcess(strTemp);

        Map<String, String> params = new HashMap<>(MAX_PARAMS);
        if (StringUtils.isEmpty(str)) {
            return params;
        }

        if (!str.contains("&")) {
            params.put(decode(str, decode), GlobalConstants.BLANK);
            return params;
        }

        final int len = str.length();
        String name = null;
        // 未处理字符开始位置
        int pos = 0;
        // 未处理字符结束位置
        int i;
        // 当前字符
        char c;
        for (i = 0; i < len; i++) {
            c = str.charAt(i);
            // 键值对的分界点
            if (c == '=') {
                if (null == name) {
                    // name可以是""
                    name = str.substring(pos, i);
                }
                pos = i + 1;
            }
            // 参数对的分界点
            else if (c == '&') {
                if (null == name && pos != i) {
                    // 对于像&a&这类无参数值的字符串，我们将name为a的值设为""
                    addParam(params, str.substring(pos, i), GlobalConstants.BLANK, decode);
                }
                else if (name != null) {
                    addParam(params, name, str.substring(pos, i), decode);
                    name = null;
                }
                pos = i + 1;
            }
        }

        // 处理结尾
        if (pos != i) {
            if (name == null) {
                addParam(params, str.substring(pos, i), GlobalConstants.BLANK, decode);
            }
            else {
                addParam(params, name, str.substring(pos, i), decode);
            }
        }
        else if (name != null) {
            addParam(params, name, GlobalConstants.BLANK, decode);
        }

        return params;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @return <br>
     */
    public static String doPost(final String url) {
        return doPost(url, new HashMap<>());
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
    public static String doPost(final String url, final Map<String, String> paramMap, final Charset charset) {
        MultipartBodyPublisher bodyPublisher = new MultipartBodyPublisher();
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> entry : paramMap.entrySet()) {
                bodyPublisher.addTextPart(entry.getKey(), entry.getValue());
            }
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout())
            .header("Content-Type", "multipart/form-data").POST(bodyPublisher.build());

        HttpRequest request = requestBuilder.build();
        return getStringRequest(request, charset);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param paramMap
     * @param charset
     * @param headers
     * @return <br>
     */
    public static String doPost(final String url, final Map<String, String> paramMap, final Charset charset,
        final Map<String, String> headers) {
        MultipartBodyPublisher bodyPublisher = new MultipartBodyPublisher();
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> entry : paramMap.entrySet()) {
                bodyPublisher.addTextPart(entry.getKey(), entry.getValue());
            }
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout())
            .header("Content-Type", "multipart/form-data").POST(bodyPublisher.build());

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.header(header.getKey(), header.getValue());
            }
        }

        HttpRequest request = requestBuilder.build();
        return getStringRequest(request, charset);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param paramMap
     * @param headers
     * @return <br>
     */
    public static String doPost(final String url, final Map<String, String> paramMap,
        final Map<String, String> headers) {
        return doPost(url, paramMap, GlobalConstants.DEFAULT_CHARSET, headers);
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
        if (StringUtils.isEmpty(body)) {
            return doPost(url);
        }

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
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param body
     * @param headers
     * @return <br>
     */
    public static String doPost(final String url, final String body, final Map<String, String> headers) {
        if (StringUtils.isEmpty(body)) {
            return doPost(url);
        }

        String contentType = "text/plain";
        if (body.startsWith("{") && body.endsWith("}")) {
            contentType = "application/json";
        }
        else if (body.startsWith("<") && body.endsWith(">")) {
            contentType = "text/xml";
        }
        return doPost(url, body, contentType, GlobalConstants.DEFAULT_CHARSET, headers);
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
    public static String doPost(final String url, final String body, final String contentType, final Charset charset) {
        return doPost(url, body, contentType, charset, null);
    }

    /**
     * Description: 执行Post请求 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param body
     * @param contentType
     * @param charset
     * @param headers
     * @return <br>
     */
    public static String doPost(final String url, final String body, final String contentType, final Charset charset,
        final Map<String, String> headers) {
        return doPost(url, body, null, contentType, charset, headers);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @param body
     * @param paramMap
     * @param contentType
     * @param charset
     * @param headers
     * @return <br>
     */
    public static String doPost(final String url, final String body, final Map<String, String> paramMap,
        final String contentType, final Charset charset, final Map<String, String> headers) {
        HttpRequest.Builder requestBuilder;

        if (paramMap != null && !paramMap.isEmpty()) {
            // 如果有参数映射，则构建表单数据
            MultipartBodyPublisher bodyPublisher = new MultipartBodyPublisher();
            if (MapUtils.isNotEmpty(paramMap)) {
                for (Entry<String, String> entry : paramMap.entrySet()) {
                    bodyPublisher.addTextPart(entry.getKey(), entry.getValue());
                }
            }
            requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout())
                .header("Content-Type", "application/x-www-form-urlencoded").POST(bodyPublisher.build());
        }
        else {
            // 否则使用提供的body
            requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout())
                .header("Content-Type", contentType).POST(BodyPublishers.ofString(body == null ? "" : body));
        }

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.header(header.getKey(), header.getValue());
            }
        }

        HttpRequest request = requestBuilder.build();
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
    public static String getStringRequest(final HttpRequest request, final Charset charset) {
        HttpClient client = HTTP_CLIENT;
        try {
            // 在请求级别设置超时时间
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString(charset));
            return response.body();
        }
        catch (IOException e) {
            throw new UtilException(e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 重置中断状态
            throw new UtilException(e);
        }
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(getTimeout()).GET().build();
        HttpClient client = HTTP_CLIENT;
        try {
            HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
            return response.body();
        }
        catch (IOException e) {
            throw new UtilException(e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 重置中断状态
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
        return ip.equals("0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    /**
     * 获取IP
     *
     * @return ip
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            LoggerUtil.warn(e);
            return null;
        }
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
     * @Method getHttpClient
     * @param
     * @return java.net.http.HttpClient
     * @Author 王伟
     * @Description 获取HttpClient实例
     * @Date 2025/9/24
     */
    public static HttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    /**
     * @Method createHttpClient
     * @param
     * @return java.net.http.HttpClient
     * @Author 王伟
     * @Description 创建HttpClient实例
     * @Date 2025/9/24
     */
    private static HttpClient createHttpClient() {
        try {
            return HttpClient.newBuilder()
                .connectTimeout(
                    Duration.ofMillis(PropertyHolder.getLongProperty("ribbon.ConnectTimeout", CONNECT_TIMEOUT)))
                .sslContext(createSSLContext()).sslParameters(createSSLParameters()).build();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create HttpClient", e);
        }
    }

    /**
     * @Method createSSLContext
     * @param
     * @return javax.net.ssl.SSLContext
     * @Author 王伟
     * @Description 创建SSLContext实例
     * @Date 2025/9/24
     */
    private static SSLContext createSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create SSLContext", e);
        }
    }

    /**
     * @Method createSSLParameters
     * @param
     * @return javax.net.ssl.SSLParameters
     * @Author 王伟
     * @Description 创建SSLParameters实例
     * @Date 2025/9/24
     */
    private static javax.net.ssl.SSLParameters createSSLParameters() {
        javax.net.ssl.SSLParameters sslParams = new javax.net.ssl.SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(null); // 禁用主机名验证
        return sslParams;
    }

    /**
     * @Method getTrustManager
     * @param
     * @return javax.net.ssl.TrustManager[]
     * @Author 王伟
     * @Description 获取信任所有证书的TrustManager
     * @Date 2025/9/24
     */
    private static TrustManager[] getTrustManager() {
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

    /**
     * 是否为http协议
     *
     * @param url 待验证的url
     * @return true: http协议, false: 非http协议
     */
    public static boolean isHttpProtocol(final String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("http%3A%2F%2F");
    }

    /**
     * 是否为https协议
     *
     * @param url 待验证的url
     * @return true: https协议, false: 非https协议
     */
    public static boolean isHttpsProtocol(final String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("https://") || url.startsWith("https%3A%2F%2F");
    }

    /**
     * 是否为本地主机（域名）
     *
     * @param url 待验证的url
     * @return true: 本地主机（域名）, false: 非本地主机（域名）
     */
    public static boolean isLocalHost(final String url) {
        return StringUtils.isEmpty(url) || url.contains("127.0.0.1") || url.contains("localhost");
    }

    /**
     * 是否为https协议或本地主机（域名）
     *
     * @param url 待验证的url
     * @return true: https协议或本地主机 false: 非https协议或本机主机
     */
    public static boolean isHttpsProtocolOrLocalHost(final String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        return isHttpsProtocol(url) || isLocalHost(url);
    }

    private static void addParam(final Map<String, String> params, final String tempKey, final String tempValue,
        final boolean decode) {
        String key = decode(tempKey, decode);
        String value = decode(tempValue, decode);
        if (params.containsKey(key)) {
            params.put(key, params.get(key) + "," + value);
        }
        else {
            params.put(key, value);
        }
    }

    private static String decode(final String str, final boolean decode) {
        return decode ? URLUtil.decode(str, GlobalConstants.DEFAULT_CHARSET) : str;
    }

    private static String preProcess(final String tempStr) {
        if (StringUtils.isEmpty(tempStr)) {
            return tempStr;
        }
        String str = tempStr;
        // 去除 URL 路径信息
        int beginPos = str.indexOf("?");
        if (beginPos > -1) {
            str = str.substring(beginPos + 1);
        }

        // 去除 # 后面的内容
        int endPos = str.indexOf("#");
        if (endPos > -1) {
            str = str.substring(0, endPos);
        }
        return str;
    }

}
