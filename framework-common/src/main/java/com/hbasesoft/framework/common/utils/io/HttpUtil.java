package com.hbasesoft.framework.common.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.UtilException;

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

    private static ThreadLocal<OkHttpClient> httpClientHold = new ThreadLocal<>();

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
        Request request = new Request.Builder().url(url).build();
        return getStringRequest(request, charset);
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
        return doPost(url, paramMap, GlobalConstants.DEFAULT_CHARSET);
    }

    public static String doPost(String url, Map<String, String> paramMap, String charset) {
        Builder builder = new FormBody.Builder();
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> param : paramMap.entrySet()) {
                builder.add(param.getKey(), param.getValue());
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return getStringRequest(request, charset);
    }

    public static String getStringRequest(Request request, String charset) {

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
     * <p>
     * 执行Post请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(String url, String body, String contentType, String charset) {
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return getStringRequest(request, charset);
    }

    public static String doPost(String url, String body, String contentType) {
        return doPost(url, body, contentType, GlobalConstants.DEFAULT_CHARSET);
    }

    public static String doPost(String url, String body) {
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
     * <p>
     * 执行get请求
     * </p>
     * 
     * @param url
     * @param paramMap
     * @return
     */
    public static void doGetDowloadFile(String url, String absolutePath) {
        File f = new File(absolutePath);
        if (!f.exists()) {
            Request request = new Request.Builder().url(url).build();
            OkHttpClient okHttpClient = getOkHttpClient();
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                IOUtil.copyFileFromInputStream(absolutePath, response.body().byteStream());
            }
            catch (IOException e) {
                throw new UtilException(e);
            }
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
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
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

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = httpClientHold.get();
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(PropertyHolder.getLongProperty("ribbon.ConnectTimeout",5000L), TimeUnit.MILLISECONDS)
                    .readTimeout(PropertyHolder.getLongProperty("ribbon.ReadTimeout",30000L), TimeUnit.MILLISECONDS)
                    .build();
            httpClientHold.set(okHttpClient);
        }
        return okHttpClient;
    }

}
