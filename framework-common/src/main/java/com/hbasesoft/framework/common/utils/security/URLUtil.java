package com.hbasesoft.framework.common.utils.security;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * URL 常用方法处理类
 * 
 * @author meiguiyang
 * @version [版本号, Apr 11, 2013]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class URLUtil {

    /**
     * UTF-8解码
     * 
     * @param str 字符串
     * @return 解码地址
     * @see [类、类#方法、类#成员]
     */
    public static String decode(final String str) {
        return decode(str, GlobalConstants.DEFAULT_CHARSET);
    }

    /**
     * 解密
     * 
     * @param str 加密的字符串
     * @param enc 编码
     * @return 解密的字符串
     * @see [类、类#方法、类#成员]
     */
    public static String decode(final String str, final Charset enc) {
        if (StringUtils.isNotEmpty(str)) {
            return URLDecoder.decode(str, enc);
        }
        return GlobalConstants.BLANK;
    }

    /**
     * utf-8加密
     * 
     * @param str 字符串
     * @return 加密的字符串
     * @see [类、类#方法、类#成员]
     */
    public static String encode(final String str) {
        return encode(str, GlobalConstants.DEFAULT_CHARSET);
    }

    /**
     * 加密
     * 
     * @param str 字符串
     * @param enc 编码
     * @return 加密的字符串
     * @see [类、类#方法、类#成员]
     */
    public static String encode(final String str, final Charset enc) {
        if (StringUtils.isNotEmpty(str)) {
            return URLEncoder.encode(str, enc);
        }
        return GlobalConstants.BLANK;
    }
}
