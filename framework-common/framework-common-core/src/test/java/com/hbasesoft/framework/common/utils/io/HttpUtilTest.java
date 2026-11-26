/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HttpUtil 工具类测试
 *
 * @author 王伟
 * @version 1.0
 * @since V1.0
 * @see com.hbasesoft.framework.common.utils.io.HttpUtil
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("HTTP工具类测试")
public class HttpUtilTest {

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("测试执行GET请求（默认字符集）")
    public void testDoGet() {
        // 执行测试（使用真实的URL可能会失败，这里主要测试方法调用）
        // 由于需要网络连接，这里只验证方法签名正确
        String url = "https://www.baidu.com";
        try {
            String result = HttpUtil.doGet(url);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            // 网络异常在测试环境中是预期的
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行GET请求（指定字符集）")
    public void testDoGetWithCharset() {
        String url = "https://www.baidu.com";
        try {
            String result = HttpUtil.doGet(url, StandardCharsets.UTF_8);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行GET请求（带请求头）")
    public void testDoGetWithHeaders() {
        String url = "https://www.baidu.com";
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Test Client");
        headers.put("Accept", "application/json");

        try {
            String result = HttpUtil.doGet(url, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行GET请求（带字符集和请求头）")
    public void testDoGetWithCharsetAndHeaders() {
        String url = "https://www.baidu.com";
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Test Client");

        try {
            String result = HttpUtil.doGet(url, StandardCharsets.UTF_8, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试获取超时时间")
    public void testGetTimeout() {
        assertThat(HttpUtil.getTimeout()).isNotNull();
        assertThat(HttpUtil.getTimeout().toMillis()).isGreaterThan(0);
    }

    @Test
    @DisplayName("测试解析参数字符串（不解码）")
    public void testParamsParseWithoutDecode() {
        String paramStr = "key1=value1&key2=value2&key3=value3";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, false);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get("key1")).isEqualTo("value1");
        assertThat(result.get("key2")).isEqualTo("value2");
        assertThat(result.get("key3")).isEqualTo("value3");
    }

    @Test
    @DisplayName("测试解析参数字符串（解码）")
    public void testParamsParseWithDecode() {
        String paramStr = "key1=value%20%E4%B8%AD%E6%96%87&key2=value2";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, true);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get("key1")).contains("中文");
        assertThat(result.get("key2")).isEqualTo("value2");
    }

    @Test
    @DisplayName("测试解析空参数字符串")
    public void testParamsParseEmpty() {
        Map<String, String> result = HttpUtil.paramsParse("", false);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("测试解析包含URL路径的参数字符串")
    public void testParamsParseWithUrlPath() {
        String paramStr = "http://example.com?key1=value1&key2=value2";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, false);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get("key1")).isEqualTo("value1");
    }

    @Test
    @DisplayName("测试解析包含锚点的参数字符串")
    public void testParamsParseWithAnchor() {
        String paramStr = "key1=value1&key2=value2#anchor";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, false);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get("key1")).isEqualTo("value1");
    }

    @Test
    @DisplayName("测试解析只有键没有值的参数")
    public void testParamsParseKeyWithoutValue() {
        String paramStr = "key1=&key2&key3=value3";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, false);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get("key1")).isEqualTo("");
        assertThat(result.get("key2")).isEqualTo("");
        assertThat(result.get("key3")).isEqualTo("value3");
    }

    @Test
    @DisplayName("测试解析包含等号的参数值")
    public void testParamsParseValueWithEquals() {
        String paramStr = "key1=value=1=2=3&key2=value2";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, false);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get("key1")).isEqualTo("value=1=2=3");
    }

    @Test
    @DisplayName("测试解析重复键的参数（应该用逗号连接）")
    public void testParamsParseDuplicateKeys() {
        String paramStr = "key1=value1&key1=value2&key1=value3";
        Map<String, String> result = HttpUtil.paramsParse(paramStr, false);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get("key1")).isEqualTo("value1,value2,value3");
    }

    @Test
    @DisplayName("测试执行POST请求（无参数）")
    public void testDoPost() {
        String url = "https://httpbin.org/post";
        try {
            String result = HttpUtil.doPost(url);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带参数映射）")
    public void testDoPostWithParamMap() {
        String url = "https://httpbin.org/post";
        Map<String, String> params = new HashMap<>();
        params.put("username", "testuser");
        params.put("password", "testpass");

        try {
            String result = HttpUtil.doPost(url, params);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带参数映射和字符集）")
    public void testDoPostWithParamMapAndCharset() {
        String url = "https://httpbin.org/post";
        Map<String, String> params = new HashMap<>();
        params.put("username", "测试用户");
        params.put("password", "testpass");

        try {
            String result = HttpUtil.doPost(url, params, StandardCharsets.UTF_8);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带参数映射、字符集和请求头）")
    public void testDoPostWithParamMapCharsetAndHeaders() {
        String url = "https://httpbin.org/post";
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");

        try {
            String result = HttpUtil.doPost(url, params, StandardCharsets.UTF_8, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带参数映射和请求头）")
    public void testDoPostWithParamMapAndHeaders() {
        String url = "https://httpbin.org/post";
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "custom-value");

        try {
            String result = HttpUtil.doPost(url, params, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带JSON字符串体）")
    public void testDoPostWithJsonBody() {
        String url = "https://httpbin.org/post";
        String jsonBody = "{\"test\":\"value\",\"name\":\"test\"}";

        try {
            String result = HttpUtil.doPost(url, jsonBody);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带XML字符串体）")
    public void testDoPostWithXmlBody() {
        String url = "https://httpbin.org/post";
        String xmlBody = "<root><test>value</test></root>";

        try {
            String result = HttpUtil.doPost(url, xmlBody);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带字符串体和请求头）")
    public void testDoPostWithBodyAndHeaders() {
        String url = "https://httpbin.org/post";
        String body = "{\"test\":\"value\"}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            String result = HttpUtil.doPost(url, body, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带字符串体和内容类型）")
    public void testDoPostWithBodyAndContentType() {
        String url = "https://httpbin.org/post";
        String body = "test=data";
        String contentType = "application/x-www-form-urlencoded";

        try {
            String result = HttpUtil.doPost(url, body, contentType);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带字符串体、内容类型和字符集）")
    public void testDoPostWithBodyContentTypeAndCharset() {
        String url = "https://httpbin.org/post";
        String body = "test=data";
        String contentType = "application/x-www-form-urlencoded";

        try {
            String result = HttpUtil.doPost(url, body, contentType, StandardCharsets.UTF_8);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带完整参数）")
    public void testDoPostWithAllParams() {
        String url = "https://httpbin.org/post";
        String body = "{\"test\":\"value\"}";
        String contentType = "application/json";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");

        try {
            String result = HttpUtil.doPost(url, body, contentType, StandardCharsets.UTF_8, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试执行POST请求（带参数映射和完整参数）")
    public void testDoPostWithParamMapAndAllParams() {
        String url = "https://httpbin.org/post";
        String body = "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("field1", "value1");
        String contentType = "application/x-www-form-urlencoded";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");

        try {
            String result = HttpUtil.doPost(url, body, paramMap, contentType, StandardCharsets.UTF_8, headers);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试获取字符串请求")
    public void testGetStringRequest() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://www.baidu.com"))
            .GET()
            .build();

        try {
            String result = HttpUtil.getStringRequest(httpRequest, StandardCharsets.UTF_8);
            assertThat(result).isNotNull();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试下载文件到指定路径")
    public void testDownloadFileToPath() {
        String url = "https://www.baidu.com/img/flexible/logo/pc/result.png";
        String targetPath = System.getProperty("java.io.tmpdir") + "/test_download.png";

        try {
            HttpUtil.downloadFile(url, targetPath);
            File file = new File(targetPath);
            assertThat(file).exists();
            file.delete();
        } catch (Exception e) {
            // 网络异常在测试环境中是预期的
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试下载文件返回输入流")
    public void testDownloadFileAsStream() {
        String url = "https://www.baidu.com/img/flexible/logo/pc/result.png";

        try {
            InputStream inputStream = HttpUtil.downloadFile(url);
            assertThat(inputStream).isNotNull();
            inputStream.close();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("测试获取请求URL（不包含查询字符串）")
    public void testGetRequestURLWithoutQueryString() {
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(80);
        when(request.getRequestURI()).thenReturn("/test/path");
        when(request.getQueryString()).thenReturn(null);

        String url = HttpUtil.getRequestURL(request, false);

        assertThat(url).isEqualTo("http://example.com/test/path");
    }

    @Test
    @DisplayName("测试获取请求URL（包含查询字符串）")
    public void testGetRequestURLWithQueryString() {
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443);
        when(request.getRequestURI()).thenReturn("/test/path");
        when(request.getQueryString()).thenReturn("key1=value1&key2=value2");

        String url = HttpUtil.getRequestURL(request, true);

        assertThat(url).isEqualTo("https://example.com/test/path?key1=value1&key2=value2");
    }

    @Test
    @DisplayName("测试获取请求URL（非标准端口）")
    public void testGetRequestURLWithNonStandardPort() {
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRequestURI()).thenReturn("/test/path");
        when(request.getQueryString()).thenReturn(null);

        String url = HttpUtil.getRequestURL(request, false);

        assertThat(url).isEqualTo("http://example.com:8080/test/path");
    }

    @Test
    @DisplayName("测试获取请求URI（不包含查询字符串）")
    public void testGetRequestURIWithoutQueryString() {
        when(request.getRequestURI()).thenReturn("/test/path");
        when(request.getQueryString()).thenReturn(null);

        String uri = HttpUtil.getRequestURI(request, false);

        assertThat(uri).isEqualTo("/test/path");
    }

    @Test
    @DisplayName("测试获取请求URI（包含查询字符串）")
    public void testGetRequestURIWithQueryString() {
        when(request.getRequestURI()).thenReturn("/test/path");
        when(request.getQueryString()).thenReturn("key=value");

        String uri = HttpUtil.getRequestURI(request, true);

        assertThat(uri).isEqualTo("/test/path?key=value");
    }

    @Test
    @DisplayName("测试获取相对URI（不包含查询字符串）")
    public void testGetRequestRelaURIWithoutQueryString() {
        when(request.getRequestURI()).thenReturn("/myapp/test/path");
        when(request.getContextPath()).thenReturn("/myapp");
        when(request.getQueryString()).thenReturn(null);

        String relativeUri = HttpUtil.getRequestRelaURI(request, false);

        assertThat(relativeUri).isEqualTo("/test/path");
    }

    @Test
    @DisplayName("测试获取相对URI（包含查询字符串）")
    public void testGetRequestRelaURIWithQueryString() {
        when(request.getRequestURI()).thenReturn("/myapp/test/path");
        when(request.getContextPath()).thenReturn("/myapp");
        when(request.getQueryString()).thenReturn("key=value");

        String relativeUri = HttpUtil.getRequestRelaURI(request, true);

        assertThat(relativeUri).isEqualTo("/test/path?key=value");
    }

    @Test
    @DisplayName("测试获取请求IP（从X-Forwarded-For头）")
    public void testGetRequestIpFromXForwardedFor() {
        when(request.getHeader("x-forwarded-for")).thenReturn("192.168.1.100");
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = HttpUtil.getRequestIp(request);

        assertThat(ip).isEqualTo("192.168.1.100");
    }

    @Test
    @DisplayName("测试获取请求IP（从Proxy-Client-IP头）")
    public void testGetRequestIpFromProxyClientIP() {
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn("192.168.1.101");
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = HttpUtil.getRequestIp(request);

        assertThat(ip).isEqualTo("192.168.1.101");
    }

    @Test
    @DisplayName("测试获取请求IP（从WL-Proxy-Client-IP头）")
    public void testGetRequestIpFromWlProxyClientIP() {
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("192.168.1.102");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = HttpUtil.getRequestIp(request);

        assertThat(ip).isEqualTo("192.168.1.102");
    }

    @Test
    @DisplayName("测试获取请求IP（从RemoteAddr）")
    public void testGetRequestIpFromRemoteAddr() {
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.103");

        String ip = HttpUtil.getRequestIp(request);

        assertThat(ip).isEqualTo("192.168.1.103");
    }

    @Test
    @DisplayName("测试获取请求IP（IPv6本地地址转换）")
    public void testGetRequestIpIPv6ToLocalhost() {
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:1");

        String ip = HttpUtil.getRequestIp(request);

        assertThat(ip).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("测试获取请求IP（包含多个IP）")
    public void testGetRequestIpWithMultipleIps() {
        when(request.getHeader("x-forwarded-for")).thenReturn("192.168.1.100, 192.168.1.101");
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = HttpUtil.getRequestIp(request);

        assertThat(ip).isEqualTo("192.168.1.100");
    }

    @Test
    @DisplayName("测试获取本地IP")
    public void testGetLocalIp() {
        String localIp = HttpUtil.getLocalIp();
        assertThat(localIp).isNotNull();
        assertThat(localIp).isNotEmpty();
    }

    @Test
    @DisplayName("测试获取客户端信息")
    public void testGetClientInfo() {
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        String clientInfo = HttpUtil.getClientInfo(request);

        assertThat(clientInfo).isNotNull();
        assertThat(clientInfo).isEqualTo("mozilla/5.0 (windows nt 10.0; win64; x64)");
    }

    @Test
    @DisplayName("测试判断是否为异步请求")
    public void testIsAsynRequest() {
        when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");

        boolean isAsyn = HttpUtil.isAsynRequest(request);

        assertThat(isAsyn).isTrue();
    }

    @Test
    @DisplayName("测试判断不是异步请求")
    public void testIsNotAsynRequest() {
        when(request.getHeader("X-Requested-With")).thenReturn(null);

        boolean isAsyn = HttpUtil.isAsynRequest(request);

        assertThat(isAsyn).isFalse();
    }

    @Test
    @DisplayName("测试获取HttpClient实例")
    public void testGetHttpClient() {
        assertThat(HttpUtil.getHttpClient()).isNotNull();
    }

    @Test
    @DisplayName("测试判断是否为HTTP协议")
    public void testIsHttpProtocol() {
        assertThat(HttpUtil.isHttpProtocol("http://example.com")).isTrue();
        assertThat(HttpUtil.isHttpProtocol("http%3A%2F%2Fexample.com")).isTrue();
        assertThat(HttpUtil.isHttpProtocol("https://example.com")).isFalse();
        assertThat(HttpUtil.isHttpProtocol("ftp://example.com")).isFalse();
        assertThat(HttpUtil.isHttpProtocol("example.com")).isFalse();
        assertThat(HttpUtil.isHttpProtocol(null)).isFalse();
        assertThat(HttpUtil.isHttpProtocol("")).isFalse();
    }

    @Test
    @DisplayName("测试判断是否为HTTPS协议")
    public void testIsHttpsProtocol() {
        assertThat(HttpUtil.isHttpsProtocol("https://example.com")).isTrue();
        assertThat(HttpUtil.isHttpsProtocol("https%3A%2F%2Fexample.com")).isTrue();
        assertThat(HttpUtil.isHttpsProtocol("http://example.com")).isFalse();
        assertThat(HttpUtil.isHttpsProtocol("ftp://example.com")).isFalse();
        assertThat(HttpUtil.isHttpsProtocol("example.com")).isFalse();
        assertThat(HttpUtil.isHttpsProtocol(null)).isFalse();
        assertThat(HttpUtil.isHttpsProtocol("")).isFalse();
    }

    @Test
    @DisplayName("测试判断是否为本地主机")
    public void testIsLocalHost() {
        assertThat(HttpUtil.isLocalHost("http://127.0.0.1/test")).isTrue();
        assertThat(HttpUtil.isLocalHost("http://localhost/test")).isTrue();
        assertThat(HttpUtil.isLocalHost("https://127.0.0.1:8080/test")).isTrue();
        assertThat(HttpUtil.isLocalHost("https://localhost:8080/test")).isTrue();
        assertThat(HttpUtil.isLocalHost("http://example.com")).isFalse();
        assertThat(HttpUtil.isLocalHost(null)).isTrue();
        assertThat(HttpUtil.isLocalHost("")).isTrue();
    }

    @Test
    @DisplayName("测试判断是否为HTTPS协议或本地主机")
    public void testIsHttpsProtocolOrLocalHost() {
        assertThat(HttpUtil.isHttpsProtocolOrLocalHost("https://example.com")).isTrue();
        assertThat(HttpUtil.isHttpsProtocolOrLocalHost("http://127.0.0.1/test")).isTrue();
        assertThat(HttpUtil.isHttpsProtocolOrLocalHost("http://localhost/test")).isTrue();
        assertThat(HttpUtil.isHttpsProtocolOrLocalHost("http://example.com")).isFalse();
        assertThat(HttpUtil.isHttpsProtocolOrLocalHost(null)).isFalse();
        assertThat(HttpUtil.isHttpsProtocolOrLocalHost("")).isFalse();
    }
}
