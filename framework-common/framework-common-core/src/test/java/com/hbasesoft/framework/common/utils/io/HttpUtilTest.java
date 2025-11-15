/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.security.URLUtil;

import org.junit.jupiter.api.Disabled;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class HttpUtilTest {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void doGet() {
        System.out.println(HttpUtil.doGet("https://www.towngasvcc.com"));
    }

    /**
     * Description: 测试带请求头的GET请求 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doGetWithHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        System.out.println(HttpUtil.doGet("https://www.towngasvcc.com", headers));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void doPost() {
        Map<String, String> param = new HashMap<>();
        param.put("test", "value");
        System.out.println(HttpUtil.doPost("http://www.baidu.com", param));
    }

    /**
     * Description: 测试带请求头的POST请求 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithHeaders() {
        Map<String, String> param = new HashMap<>();
        param.put("test", "value");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        System.out.println(HttpUtil.doPost("http://www.baidu.com", param, headers));
    }

    /**
     * Description: 测试POST请求带字符串体 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithStringBody() {
        String jsonBody = "{\"test\":\"value\",\"name\":\"test\"}";
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", jsonBody));
    }

    /**
     * Description: 测试POST请求带字符串体和请求头 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithStringBodyAndHeaders() {
        String jsonBody = "{\"test\":\"value\",\"name\":\"test\"}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("User-Agent", "Test Client");
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", jsonBody, headers));
    }

    /**
     * Description: 测试参数转字符串功能 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void testParamsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "value2");
        params.put("key3", "特殊字符&测试");
        
        String result = UrlBuilder.paramsToString(params, true);
        System.out.println("Encoded params: " + result);
        
        String result2 = UrlBuilder.paramsToString(params, false);
        System.out.println("Unencoded params: " + result2);
    }

    /**
     * Description: 测试字符串解析参数功能 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void testParamsParse() {
        String paramStr = "key1=value1&key2=value2&key3=特殊字符测试";
        Map<String, String> params = HttpUtil.paramsParse(paramStr, true);
        System.out.println("Parsed params: " + params);
        
        String paramStr2 = "key1=value1&key2&key3=with=equals&key4=";
        Map<String, String> params2 = HttpUtil.paramsParse(paramStr2, false);
        System.out.println("Parsed params2: " + params2);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    @Disabled("下载文件测试，需要网络连接")
    public void doGetDowloadFile() {
        HttpUtil.downloadFile("https://www.baidu.com/img/"
            + "PCfb_5bf082d29588c07f842ccde3f97243ea.png", "target/a2.jpg");
    }

    /**
     * Description: 测试POST请求带参数和字符集 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithCharset() {
        Map<String, String> param = new HashMap<>();
        param.put("username", "testuser");
        param.put("password", "testpass");
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", param, java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Description: 测试POST请求带参数、字符集和请求头 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithAllParams() {
        Map<String, String> param = new HashMap<>();
        param.put("username", "testuser");
        param.put("password", "testpass");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        headers.put("X-Custom-Header", "custom-value");
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", param, 
            java.nio.charset.StandardCharsets.UTF_8, headers));
    }

    /**
     * Description: 测试POST请求带字符串体和内容类型 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithStringAndContentType() {
        String body = "test=data&other=value";
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", body, "application/x-www-form-urlencoded"));
    }

    /**
     * Description: 测试POST请求带字符串体、内容类型和字符集 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithStringContentTypeAndCharset() {
        String body = "test=data&other=value";
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", body, "application/x-www-form-urlencoded",
            java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Description: 测试POST请求带字符串体、内容类型、字符集和请求头 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithStringAllParams() {
        String body = "{\"test\":\"data\",\"other\":\"value\"}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer test-token");
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", body, "application/json",
            java.nio.charset.StandardCharsets.UTF_8, headers));
    }

    /**
     * Description: 测试POST请求带字符串体、参数映射、内容类型、字符集和请求头 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void doPostWithAllParamsAdvanced() {
        String body = "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("field1", "value1");
        paramMap.put("field2", "value2");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer test-token");
        System.out.println(HttpUtil.doPost("http://httpbin.org/post", body, paramMap, "application/x-www-form-urlencoded",
            java.nio.charset.StandardCharsets.UTF_8, headers));
    }

    /**
     * Description: 测试URL协议检测功能 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void testProtocolDetection() {
        System.out.println("Is http://example.com HTTP? " + HttpUtil.isHttpProtocol("http://example.com"));
        System.out.println("Is https://example.com HTTP? " + HttpUtil.isHttpProtocol("https://example.com"));
        System.out.println("Is example.com HTTP? " + HttpUtil.isHttpProtocol("example.com"));
        System.out.println("Is http:// HTTP? " + HttpUtil.isHttpProtocol("http://"));
        
        System.out.println("Is http://example.com HTTPS? " + HttpUtil.isHttpsProtocol("http://example.com"));
        System.out.println("Is https://example.com HTTPS? " + HttpUtil.isHttpsProtocol("https://example.com"));
        System.out.println("Is example.com HTTPS? " + HttpUtil.isHttpsProtocol("example.com"));
        System.out.println("Is https:// HTTPS? " + HttpUtil.isHttpsProtocol("https://"));
    }

    /**
     * Description: 测试本地主机检测功能 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void testLocalhostDetection() {
        System.out.println("Is 127.0.0.1 localhost? " + HttpUtil.isLocalHost("127.0.0.1"));
        System.out.println("Is localhost localhost? " + HttpUtil.isLocalHost("localhost"));
        System.out.println("Is example.com localhost? " + HttpUtil.isLocalHost("example.com"));
        System.out.println("Is empty string localhost? " + HttpUtil.isLocalHost(""));
        System.out.println("Is null localhost? " + HttpUtil.isLocalHost(null));
    }

    /**
     * Description: 测试HTTPS协议或本地主机检测功能 <br>
     * 
     * @author 王伟<br>
     */
    @Test
    public void testHttpsOrLocalhostDetection() {
        System.out.println("Is https://example.com HTTPS or localhost? " + HttpUtil.isHttpsProtocolOrLocalHost("https://example.com"));
        System.out.println("Is http://localhost HTTPS or localhost? " + HttpUtil.isHttpsProtocolOrLocalHost("http://localhost"));
        System.out.println("Is 127.0.0.1 HTTPS or localhost? " + HttpUtil.isHttpsProtocolOrLocalHost("127.0.0.1"));
        System.out.println("Is http://example.com HTTPS or localhost? " + HttpUtil.isHttpsProtocolOrLocalHost("http://example.com"));
    }
}
