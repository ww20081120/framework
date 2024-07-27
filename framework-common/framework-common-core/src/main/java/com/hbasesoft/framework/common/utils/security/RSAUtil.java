/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.UtilException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <Description> RSA 加解密工具 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年3月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.security <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RSAUtil {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 1024;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 1024;

    /**
     * Description: 获取密钥对 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return 密钥对
     */
    public static KeyPair getKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(MAX_ENCRYPT_BLOCK);
            return generator.generateKeyPair();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.DECRYPTION_ERROR, e);
        }
    }

    /**
     * Description: 获取密钥对<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return 密钥对 <br>
     */
    public static StrKeyPair createKeyPair() {
        KeyPair key = getKeyPair();
        StrKeyPair kp = new StrKeyPair();
        kp.publicKey = DataUtil.base64Encode(key.getPublic().getEncoded());
        kp.privateKey = DataUtil.base64Encode(key.getPrivate().getEncoded());
        return kp;
    }

    /**
     * Description: 获取私钥<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param privateKey 私钥字符串
     * @return <br>
     */
    public static PrivateKey getPrivateKey(final String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = DataUtil.base64Decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            return keyFactory.generatePrivate(keySpec);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.DECRYPTION_ERROR, e);
        }
    }

    /**
     * Description: 获取公钥<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param publicKey 公钥字符串
     * @return <br>
     */
    public static PublicKey getPublicKey(final String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = DataUtil.base64Decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            return keyFactory.generatePublic(keySpec);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.DECRYPTION_ERROR, e);
        }
    }

    /**
     * Description: RSA加密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return <br>
     */
    public static String encrypt(final String data, final String publicKey) {
        return encrypt(data, getPublicKey(publicKey));
    }

    /**
     * Description: RSA加密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return <br>
     */
    public static String encrypt(final String data, final PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int inputLen = data.getBytes().length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
                }
                else {
                    cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
            // 加密后的字符串
            return DataUtil.base64Encode(encryptedData);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.ENCRYPTION_ERROR, e);
        }
    }

    /**
     * Description: RSA解密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return <br>
     */
    public static String decrypt(final String data, final String privateKey) {
        return decrypt(data, getPrivateKey(privateKey));
    }

    /**
     * Description: RSA解密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return 解密后的数据
     */
    public static String decrypt(final String data, final PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] dataBytes = DataUtil.base64Decode(data);
            int inputLen = dataBytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
                }
                else {
                    cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            // 解密后的内容
            return new String(decryptedData, GlobalConstants.DEFAULT_CHARSET);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.DECRYPTION_ERROR, e);
        }
    }

    /**
     * Description: 签名<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return <br>
     */
    public static String sign(final String data, final String privateKey) {
        return sign(data, getPrivateKey(privateKey));
    }

    /**
     * Description: 签名<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return <br>
     */
    public static String sign(final String data, final PrivateKey privateKey) {
        try {
            byte[] keyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(key);
            signature.update(data.getBytes());
            return DataUtil.base64Encode(signature.sign());
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.ENCRYPTION_ERROR, e);
        }
    }

    /**
     * Description: 验签<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param srcData 原始字符串
     * @param publicKey 公钥
     * @param sign 签名
     * @return 是否验签通过<br>
     */
    public static boolean verify(final String srcData, final String publicKey, final String sign) {
        return verify(srcData, getPublicKey(publicKey), sign);
    }

    /**
     * Description: 验签<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param srcData 原始字符串
     * @param publicKey 公钥
     * @param sign 签名
     * @return 是否验签通过<br>
     */
    public static boolean verify(final String srcData, final PublicKey publicKey, final String sign) {
        try {
            byte[] keyBytes = publicKey.getEncoded();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(key);
            signature.update(srcData.getBytes());
            return signature.verify(DataUtil.base64Decode(sign));
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.DECRYPTION_ERROR, e);
        }
    }

    /**
     * <Description> 密钥对 <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2023年3月17日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.common.utils.security <br>
     */
    @Getter
    public static class StrKeyPair {

        /** 公钥 */
        private String publicKey;

        /** 私钥 */
        private String privateKey;
    }
}
