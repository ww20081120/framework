package com.hbasesoft.framework.common.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CodingErrorAction;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.springframework.util.MultiValueMap;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * http
 * 
 * @author meiguiyang
 */
@SuppressWarnings("deprecation")
public class HttpUtil {
    private static final Logger logger = new Logger(HttpUtil.class);

    /**
     * 默认连接池中最大连接数
     */
    public static final int DEFAULT_CONNECTION_MAX_TOTAL = 200;

    /**
     * 从连接池中获取请求连接的超时时间
     */
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;

    /**
     * 默认连接超时时间
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 15000;

    /**
     * 默认socket读取数据超时时间,具体的长耗时请求中(如文件传送等)必须覆盖此设置
     */
    public static final int DEFAULT_SO_TIMEOUT = 15000;

    private static final PoolingHttpClientConnectionManager defaultConnectionManager;

    private static final ConnectionConfig defaultConnectionConfig;

    private static final RequestConfig defaultRequestConfig;

    private static final Registry<CookieSpecProvider> defaultCookieSpecRegistry;

    private static final CloseableHttpClient httpClient;

    static {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            }, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext)).build();
            // 消息约束
            /*
             * MessageConstraints messageConstraints = MessageConstraints.custom() .setMaxHeaderCount(200)
             * .setMaxLineLength(2000) .build();
             */
            // 默认连接配置
            defaultConnectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
            // 默认请求配置
            defaultRequestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setSocketTimeout(DEFAULT_SO_TIMEOUT).build();
            defaultConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            defaultConnectionManager.setMaxTotal(DEFAULT_CONNECTION_MAX_TOTAL);
            defaultConnectionManager.setDefaultMaxPerRoute(20);
            // 设置连接配置
            defaultConnectionManager.setDefaultConnectionConfig(defaultConnectionConfig);
            // 注册Cookie策略
            defaultCookieSpecRegistry = RegistryBuilder.<CookieSpecProvider> create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory()).build();
            httpClient = HttpClients.custom().setConnectionManager(defaultConnectionManager)
                .setDefaultCookieSpecRegistry(defaultCookieSpecRegistry).setDefaultRequestConfig(defaultRequestConfig)
                .setDefaultConnectionConfig(defaultConnectionConfig)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).build();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * 执行Get请求
     * </p>
     * 
     * @param url
     * @return - 默认返回text/html
     */
    public static String doGet(String url) {
        return doGet(url, GlobalConstants.DEFAULT_CHARSET);
    }

    public static String doGet(String url, String charset) {
        logger.debug(">>> doGet[url = {0}]", url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "text/html");
        return doHttpRequest(httpGet, null, new DefaultStringResponseHandler(charset));
    }

    public static String doGet(CloseableHttpClient httpClient, String url, String charset) {
        logger.debug(">>> doGet[url = {0}]", url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "text/html");
        return doHttpRequest(httpClient, httpGet, null, new DefaultStringResponseHandler(charset));
    }

    /**
     * <p>
     * 执行Post请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(String url, Map<String, String> paramMap) {
        logger.debug(">>> doPost[url = {0}, paramMap = {1}]", url, paramMap);
        HttpPost httpPost = createHttpPost(url, paramMap);
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>
     * 执行Post请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(CloseableHttpClient httpClient, String url, Map<String, String> paramMap) {
        logger.debug(">>> doPost[url = {0}, paramMap = {1}]", url, paramMap);
        HttpPost httpPost = createHttpPost(url, paramMap);
        return doHttpRequest(httpClient, httpPost, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>
     * 执行Post请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(String url, String body, String contentType) {
        logger.debug(">>> doPost[url = {0}, body = {1}]", url, body);
        HttpPost httpPost = createHttpPost(url, body);
        if (contentType == null) {
            httpPost.setHeader("Content-type", "text/plain");
        }
        else {
            httpPost.setHeader("Content-type", contentType);
        }
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>
     * 执行Post请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(CloseableHttpClient httpClient, String url, String body, String contentType) {
        logger.debug(">>> doPost[url = {0}, body = {1}]", url, body);
        HttpPost httpPost = createHttpPost(url, body);
        if (contentType == null) {
            httpPost.setHeader("Content-type", "text/plain");
        }
        else {
            httpPost.setHeader("Content-type", contentType);
        }
        return doHttpRequest(httpClient, httpPost, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>
     * 根据URL和参数创建HttpPost对象
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    protected static HttpPost createHttpPost(String url, Map<String, String> paramMap) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (paramMap != null && !paramMap.isEmpty()) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                if (paramMap instanceof MultiValueMap) {
                    @SuppressWarnings({
                        "rawtypes", "unchecked"
                    })
                    MultiValueMap<String, String> multiParamMap = (MultiValueMap) paramMap;
                    for (Map.Entry<String, List<String>> entry : multiParamMap.entrySet()) {
                        for (String value : entry.getValue()) {
                            params.add(new BasicNameValuePair(entry.getKey(), value));
                        }
                    }
                }
                else {
                    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                        params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, GlobalConstants.DEFAULT_CHARSET);
                httpPost.setEntity(formEntity);
            }
            return httpPost;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * 根据URL和参数创建HttpPost对象
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    protected static HttpPost createHttpPost(String url, String body) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (StringUtils.isNotEmpty(body)) {
                HttpEntity entity = new StringEntity(body, GlobalConstants.DEFAULT_CHARSET);
                httpPost.setEntity(entity);
            }
            return httpPost;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * 执行http请求
     * </p>
     * 
     * @param httpMethod - HTTP请求(HttpGet、HttpPost等等)
     * @param httpClientContextSetter - 可选参数,请求前的一些参数设置(如：cookie、鉴权认证等)
     * @param responseHandler - 必选参数,响应处理类(如针对httpstatu的各种值做一些策略处理等等)
     * @return
     */
    public static <T> T doHttpRequest(HttpRequestBase httpMethod, HttpClientContextSetter httpClientContextSetter,
        ResponseHandler<T> responseHandler) {
        return doHttpRequest(getHttpClient(), httpMethod, httpClientContextSetter, responseHandler);
    }

    public static <T> T doHttpRequest(CloseableHttpClient httpClient, HttpRequestBase httpMethod,
        HttpClientContextSetter httpClientContextSetter, ResponseHandler<T> responseHandler) {
        Args.notNull(responseHandler, "Parameter 'httpMethod' can not be null!");
        Args.notNull(responseHandler, "Parameter 'responseHandler' can not be null!");
        CloseableHttpResponse response = null;
        try {
            if (httpClientContextSetter != null) {
                HttpClientContext context = HttpClientContext.create();
                httpClientContextSetter.setHttpClientContext(context);
                response = getHttpClient().execute(httpMethod, context);
            }
            else {
                response = httpClient.execute(httpMethod);
            }
            return response == null ? null : responseHandler.handleResponse(response);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new HttpClientException(e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(response);
        }
    }

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * <p>
     * 对于特殊请求(比如请求涉及到cookie的处理,鉴权认证等),默认的一些配置已经满足不了了,
     * 这时就可以使用一个独立于全局的配置来执行请求,这个独立于全局,又不会干扰其他线程的请求执行的机制就是使用HttpClientContext,
     * 该设置类用于对已经提供的一个基于全局配置的副本,来设置一些配置(见HttpClientContext.setXxx)
     * </p>
     * 
     * @author pengpeng
     * @date 2014年7月20日 下午4:27:50
     * @version 1.0
     */
    public static interface HttpClientContextSetter {
        public void setHttpClientContext(HttpClientContext context);
    }

    /**
     * <p>
     * 默认keepAlive策略：如果响应中存在服务器端的keepAlive超时时间则返回该时间否则返回默认的
     * </p>
     * 
     * @author pengpeng
     * @date 2014年7月20日 下午3:26:39
     * @version 1.0
     */
    public static class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    }
                    catch (NumberFormatException ignore) {
                    }
                }
            }
            return 30 * 1000; // 默认30秒
        }
    }

    /**
     * <p>
     * 默认的处理返回值为String的ResponseHandler
     * </p>
     * 
     * @author pengpeng
     * @date 2014年7月21日 上午9:13:22
     * @version 1.0
     */
    public static class DefaultStringResponseHandler implements ResponseHandler<String> {

        private String charset;

        public DefaultStringResponseHandler() {
            charset = GlobalConstants.DEFAULT_CHARSET;
        }

        public DefaultStringResponseHandler(String charset) {
            this.charset = charset;
        }

        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity, charset);
            }
            return GlobalConstants.BLANK;
        }
    }

    /**
     * <p>
     * 执行get请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static void doGetDowloadFile(String absolutePath, String url) {
        logger.debug(">>> doGetDowloadFile[url = {0}]", url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "image/jpeg");
        doHttpRequest(httpGet, null, new DownFileResponseHandler(absolutePath));
    }

    /**
     * <p>
     * 附件下载处理的ResponseHandler
     * </p>
     * 
     * @author pengpeng
     * @date 2014年7月21日 上午9:13:22
     * @version 1.0
     */
    public static class DownFileResponseHandler implements ResponseHandler<String> {
        private String absolutePath;

        public DownFileResponseHandler(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                if (inputStream != null) {
                    Header[] headers = response.getHeaders("Content-Disposition");
                    String filename = headers[0].getValue().split("\"")[1];
                    String type = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
                    filename = "/" + System.currentTimeMillis() + "." + type;
                    try {
                        IOUtil.copyFileFromInputStream(absolutePath, inputStream, type);
                    }
                    catch (UtilException e) {
                        throw new IOException(e);
                    }
                }

            }
            return "SUCCESS";

        }
    }

    public String getType(String type) {
        String tempType = "," + type + ",";

        if (",xml,html,json,css,js,txt,".indexOf(tempType) != -1) {
            return "text";
        }
        else if (",jpg,png,gif,".indexOf(tempType) != -1) {
            return "image";
        }
        else if (",mp3,amr,".indexOf(tempType) != -1) {
            return "voice";
        }
        else {
            return "file";
        }
    }

    public static class HttpClientException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpClientException(String message) {
            super(message);
        }

        public HttpClientException(Throwable cause) {
            super(cause);
        }
    }

    public static String getRequestURL(HttpServletRequest request, boolean includeQueryString) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        String urlPath = request.getRequestURI();

        url.append(scheme); // http, https
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(request.getServerPort());
        }
        url.append(urlPath);
        if (includeQueryString && !StringUtils.isEmpty(request.getQueryString())) {
            url.append("?" + request.getQueryString());
        }
        return url.toString();
    }

    public static String getRequestURI(HttpServletRequest request, boolean includeQueryString) {
        String url = request.getRequestURI();
        if (includeQueryString && !StringUtils.isEmpty(request.getQueryString())) {
            url += "?" + request.getQueryString();
        }
        return url;
    }

    public static String getRequestRelaURI(HttpServletRequest request, boolean includeQueryString) {
        String url = request.getRequestURI();
        if (includeQueryString && !StringUtils.isEmpty(request.getQueryString())) {
            url += "?" + request.getQueryString();
        }
        return url.substring(request.getContextPath().length());
    }

    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (CommonUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (CommonUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (CommonUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public static String getClientInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent").toLowerCase();
    }

    /**
     * 判断来的请求是否是异步请求
     * 
     * @param request
     * @return
     */
    public static boolean isAsynRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

}
