package com.hbasesoft.framework.common.utils.io;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

import lombok.Setter;

/**
 * <p>
 * 构造URL
 * </p>
 *
 * @author yangkai.shen (https://xkcoding.com)
 * @since 1.9.0
 */
@Setter
public final class UrlBuilder {

    /** params */
    private final Map<String, String> params = new LinkedHashMap<>(7);

    /** baseUrl */
    private String baseUrl;

    private UrlBuilder() {

    }

    /**
     * @param baseUrl 基础路径
     * @return the new {@code UrlBuilder}
     */
    public static UrlBuilder fromBaseUrl(final String baseUrl) {
        UrlBuilder builder = new UrlBuilder();
        builder.setBaseUrl(baseUrl);
        return builder;
    }

    /**
     * 只读的参数Map
     *
     * @return unmodifiable Map
     * @since 1.15.0
     */
    public Map<String, Object> getReadOnlyParams() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * 添加参数
     *
     * @param key 参数名称
     * @param value 参数值
     * @return this UrlBuilder
     */
    public UrlBuilder queryParam(final String key, final Object value) {
        Assert.notEmpty(key, ErrorCodeDef.PARAM_NOT_NULL, "参数名");
        String valueAsString = (value != null ? value.toString() : null);
        this.params.put(key, valueAsString);
        return this;
    }

    /**
     * 构造url
     *
     * @return url
     */
    public String build() {
        return this.build(false);
    }

    /**
     * 构造url
     *
     * @param encode 转码
     * @return url
     */
    public String build(final boolean encode) {
        if (MapUtils.isEmpty(this.params)) {
            return this.baseUrl;
        }
        String url = appendIfNotContain(this.baseUrl, "?", "&");
        String paramString = HttpUtil.paramsToString(this.params, encode);
        return url + paramString;
    }

    /**
     * 如果给定字符串{@code str}中不包含{@code appendStr}，则在{@code str}后追加{@code appendStr}；
     * 如果已包含{@code appendStr}，则在{@code str}后追加{@code otherwise}
     *
     * @param str 给定的字符串
     * @param appendStr 需要追加的内容
     * @param otherwise 当{@code appendStr}不满足时追加到{@code str}后的内容
     * @return 追加后的字符串
     */
    private static String appendIfNotContain(final String str, final String appendStr, final String otherwise) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(appendStr)) {
            return str;
        }
        if (str.contains(appendStr)) {
            return str.concat(otherwise);
        }
        return str.concat(appendStr);
    }
}
