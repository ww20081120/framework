/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年3月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.security <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JWTUtil {

    /** 固定的头部 */
    private static final String HEADER = DataUtil.base64UrlEncode("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes())
        + ".";

    /** jwt固定为3段 */
    private static final int FIX_LENGTH = 3;

    /**
     * Description: 创建JWT Token<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param expireTime
     * @param payload
     * @param privateKey
     * @return <br>
     */
    public static String createToken(final long expireTime, final Map<String, Object> payload,
        final String privateKey) {
        payload.put("exp", expireTime);
        String jsonPayload = DataUtil.base64UrlEncode(JSONObject.toJSONString(payload).getBytes());
        String data = HEADER + jsonPayload;
        String sign = RSAUtil.sign(data, privateKey);
        return new StringBuilder().append(data).append('.').append(sign).toString();
    }

    /**
     * Description: 校验token<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param token
     * @param publicKey
     * @return <br>
     */
    public static boolean verify(final String token, final String publicKey) {
        if (token != null && token.indexOf(".") != -1) {
            int index = token.lastIndexOf(".");
            String sign = token.substring(index + 1);
            String data = token.substring(0, index);
            return RSAUtil.verify(data, publicKey, sign);
        }
        return false;
    }

    /**
     * Description: 解析token，且不用校验 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param token
     * @return <br>
     */
    public static Map<String, Object> parseToken(final String token) {
        if (token != null) {
            String[] data = StringUtils.split(token, ".");
            if (data.length == FIX_LENGTH && StringUtils.isNotEmpty(data[1])) {
                JSONObject payload = JSONObject.parseObject(new String(DataUtil.base64Decode(data[1])));
                Long exp = payload.getLong("exp");
                if (exp != null && System.currentTimeMillis() < exp) {
                    return payload;
                }
            }
        }
        return null;
    }

    /**
     * Description: 解析token并校验<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param token
     * @param publicKey
     * @return <br>
     */
    public static Map<String, Object> parseToken(final String token, final String publicKey) {
        if (token != null) {
            String[] data = StringUtils.split(token, ".");
            if (data.length == FIX_LENGTH) {
                String vd = token.substring(0, token.lastIndexOf("."));
                if (RSAUtil.verify(vd, publicKey, data[2])) {
                    JSONObject payload = JSONObject.parseObject(new String(DataUtil.base64Decode(data[1])));
                    Long exp = payload.getLong("exp");
                    if (exp != null && System.currentTimeMillis() < exp) {
                        return payload;
                    }
                }
            }
        }
        return null;
    }
}
