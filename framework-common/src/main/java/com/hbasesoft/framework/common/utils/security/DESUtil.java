package com.hbasesoft.framework.common.utils.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

/**
 * <Description> <br>
 *
 * @author fb<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Create in 10:45 2018/3/27
 * @see com.hbasesoft.cloud.common.util <br>
 * @since V1.0<br>
 */
public class DESUtil {

    /** */
    private static final String CRYPTION_SECRET_KEY = "hbasesof";

    /** */
    private static final String DES_ALGORITHM = "DES";

    /**
     * Description: DES加密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param plainData
     * @return <br>
     */
    public static String encryption(final String plainData) {
        return encryption(plainData, CRYPTION_SECRET_KEY);
    }

    /**
     * DES加密
     *
     * @param plainData 原始字符串
     * @param secretKey 加密密钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encryption(final String plainData, final String secretKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(secretKey));
            // 为了防止解密时报javax.crypto.IllegalBlockSizeException: Input length must
            // be multiple of 8 when decrypting with padded cipher异常，
            // 不能把加密后的字节数组直接转换成字符串
            byte[] buf = cipher.doFinal(plainData.getBytes());
            return DataUtil.base64Encode(buf);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.ENCRYPTION_ERROR, e);
        }
    }

    /**
     * DES解密
     * 
     * @param secretData 密码字符串
     * @return 原始字符串
     * @throws Exception
     */
    public static String decryption(final String secretData) {
        return decryption(secretData, CRYPTION_SECRET_KEY);
    }

    /**
     * DES解密
     * 
     * @param secretData 密码字符串
     * @param secretKey 解密密钥
     * @return 原始字符串
     * @throws Exception
     */
    public static String decryption(final String secretData, final String secretKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(secretKey));
            byte[] buf = cipher.doFinal(DataUtil.base64Decode(secretData));
            return new String(buf);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.DECRYPTION_ERROR, e);
        }
    }

    /**
     * 获得秘密密钥
     *
     * @param secretKey
     * @return <br>
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    private static SecretKey generateKey(final String secretKey)
        throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
        DESKeySpec keySpec = new DESKeySpec(secretKey.getBytes());
        keyFactory.generateSecret(keySpec);
        return keyFactory.generateSecret(keySpec);
    }

}
