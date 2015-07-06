/**
 * 
 */
package com.fccfc.framework.common.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.UtilException;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月14日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.common.utils.security <br>
 */

public final class DataUtil {

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
    public static String md5For16(String msg) throws UtilException {
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
    public static String md5(String msg) throws UtilException {
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
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param content <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static byte[] base64Decode(String content) throws UtilException {
        try {
            return Base64.decodeBase64(content.getBytes(GlobalConstants.DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR_10037, e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param content <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static String base64Encode(byte[] content) throws UtilException {
        try {
            return new String(Base64.encodeBase64(content), GlobalConstants.DEFAULT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(ErrorCodeDef.BASE64_ERROR_10037, e);
        }
    }
}
