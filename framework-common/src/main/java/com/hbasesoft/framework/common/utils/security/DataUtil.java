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

    /** NUM */
    private static final int NUM_0X0F0 = 0x0f0;

    /** NUM */
    private static final int NUM_0X0F = 0x0f;

    /** NUM */
    private static final int NUM_0XF = 0x0f;

    /** NUM */
    private static final int NUM_4 = 4;

    /** NUM */
    private static final int NUM_8 = 8;

    /** NUM */
    private static final int NUM_24 = 24;

    /** password */
    private static final String SITE_WIDE_SECRET = "hbasesoft.com";

    /** ALGORITHM */
    private static final String ALGORITHM = "PBEWithMD5AndDES";

    /** ENCODER */
    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * 16位字符串数组
     */
    private static char hexDigits[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /** HEX_DIGIT */
    private static final String HEX_DIGIT = "0123456789ABCDEF";

    /**
     * 16位加密
     * 
     * @param msg 加密信息
     * @return 结果
     * @throws UtilException
     * @throws UtilException 异常
     */
    public static String md5For16(final String msg) {
        String result = md5(msg);
        return result.substring(NUM_8, NUM_24);
    }

    /**
     * 32位加密
     * 
     * @param msg 加密信息
     * @return 结果
     * @throws UtilException
     * @throws UtilException 异常
     */
    public static String md5(final String msg) {
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
                str[k++] = hexDigits[byte0 >>> NUM_4 & NUM_0XF];
                str[k++] = hexDigits[byte0 & NUM_0XF];
            }

            return new String(str);
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilException(ErrorCodeDef.MD5_ERROR, e);
        }

    }

    /**
     * Description: sha256 加密 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msg
     * @return <br>
     */
    public static String sha256(final String msg) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(msg.getBytes(GlobalConstants.DEFAULT_CHARSET));
            return byte2HexStr(messageDigest.digest());
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.SHA256_ERROR, e);
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
    public static byte[] base64Decode(final String content) {
        try {
            return Base64.decodeBase64(content.getBytes(GlobalConstants.DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR, e);
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
    public static String base64Encode(final byte[] content) {
        try {
            return new String(Base64.encodeBase64(content), GlobalConstants.DEFAULT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR, e);
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
    public static String base64UrlEncode(final byte[] content) {
        try {
            return Base64.encodeBase64URLSafeString(content);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR, e);
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
    public static String encryptPassowrd(final String rawPassword) {
        return ENCODER.encode(rawPassword);
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
    public static boolean matchPassword(final CharSequence rawPassword, final String password) {
        return ENCODER.matches(rawPassword, password);
    }

    /**
     * Description: 可以解密的加密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param password
     * @return <br>
     */
    public static String encrypt(final String password) {
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
     * Description: 解密 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param password
     * @return <br>
     */
    public static String decrypt(final String password) {
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

    /**
     * Description: 将字节数组转化为16进制字符串 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bs
     * @return <br>
     */
    public static String byte2HexStr(final byte[] bs) {
        char[] chars = HEX_DIGIT.toCharArray();
        StringBuilder sb = new StringBuilder();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & NUM_0X0F0) >> NUM_4;
            sb.append(chars[bit]);
            bit = bs[i] & NUM_0X0F;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * Description: 把16进制字符串转换成字节数组<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hex
     * @return <br>
     */
    public static byte[] hexStr2Byte(final String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << NUM_4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(final char c) {
        return (byte) HEX_DIGIT.indexOf(c);
    }

}
