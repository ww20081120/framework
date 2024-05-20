package com.hbasesoft.framework.common.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
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

    /** 最大参数 */
    private static final int MAX_PARAMS = 16;

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
     * Description: 拼接url请求参数 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param params
     * @param encode
     * @return <br>
     */
    public static String paramsToString(final Map<String, String> params, final boolean encode) {
        if (MapUtils.isNotEmpty(params)) {
            List<String> paramList = new ArrayList<>();
            try {
                for (Entry<String, String> entry : params.entrySet()) {
                    if (entry.getValue() == null) {
                        paramList.add(entry.getValue() + "=");
                    }
                    else {
                        paramList.add(entry.getValue() + "="
                            + (encode ? URLUtil.encode(entry.getValue(), GlobalConstants.DEFAULT_CHARSET)
                                : entry.getValue()));
                    }
                }
            }
            catch (Exception e) {
                LoggerUtil.error(e);
                throw new UtilException(e);
            }
            return String.join("&", paramList);
        }
        return GlobalConstants.BLANK;
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
        RequestBody requestBody = RequestBody.create(body == null ? null : body.getBytes(), mediaType);
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
        RequestBody requestBody = RequestBody.create(body == null ? null : body.getBytes(), mediaType);
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
        RequestBody requestBody = RequestBody.create(body == null ? null : body.getBytes(), mediaType);
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
                .sslSocketFactory(getSSLSocketFactory(), TrustManagerUtil.getAcceptAllTrustManager())
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
