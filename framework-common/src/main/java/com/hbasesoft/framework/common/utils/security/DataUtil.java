/**
 * 
 */
package com.hbasesoft.framework.common.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.UtilException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.security <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataUtil {

    /** password */
    private static final String SITE_WIDE_SECRET = "hbasesoft.com";

    private static final String ALGORITHM = "PBEWithMD5AndDES";

    private static final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * 16位字符串数组
     */
    private static char hexDigits[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * 16位加密
     * 
     * @param msg 加密信息
     * @return 结果
     * @throws UtilException
     * @throws UtilException 异常
     */
    public static String md5For16(String msg) {
        String result = md5(msg);
        return result.substring(8, 24);
    }

    /**
     * 32位加密
     * 
     * @param msg 加密信息
     * @return 结果
     * @throws UtilException
     * @throws UtilException 异常
     */
    public static String md5(String msg) {
        byte[] msgBytes = msg.getBytes();
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(msgBytes);
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];

            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(str);
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilException(ErrorCodeDef.MD5_ERROR_10036, e);
        }

    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param content <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static byte[] base64Decode(String content) {
        try {
            return Base64.decodeBase64(content.getBytes(GlobalConstants.DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR_10037, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param content <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static String base64Encode(byte[] content) {
        try {
            return new String(Base64.encodeBase64(content), GlobalConstants.DEFAULT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR_10037, e);
        }
    }

    /**
     * Description: 不可以解密的加密算法 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param rawPassword
     * @return <br>
     */
    public static String encryptPassowrd(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Description: 匹配加密的密码<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param rawPassword
     * @param password
     * @return <br>
     */
    public static boolean matchPassword(CharSequence rawPassword, String password) {
        return encoder.matches(rawPassword, password);
    }

    /**
     * Description: 可以解密的加密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param algorithm
     * @param password
     * @return <br>
     */
    public static String encrypt(String password) {
        // 加密工具
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        // 加密配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(ALGORITHM);
        // 自己在用的时候更改此密码
        config.setPassword(SITE_WIDE_SECRET);
        // 应用配置
        encryptor.setConfig(config);
        // 加密
        return encryptor.encrypt(password);
    }

    /**
     * Description: 解密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param algorithm
     * @param password
     * @return <br>
     */
    public static String decrypt(String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        // 加密配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(ALGORITHM);
        // 自己在用的时候更改此密码
        config.setPassword(SITE_WIDE_SECRET);
        // 应用配置
        encryptor.setConfig(config);
        // 解密
        return encryptor.decrypt(password);
    }

}
