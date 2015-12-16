/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.db.core.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.bootstrap.utils <br>
 */
public final class ConfigEncryptUtils {

    /** password */
    private static final String PASSWORD = "hbasesoft.com";

    public static void main(String[] args) {
        System.out.println(encrypt("PBEWithMD5AndDES", "root"));
        System.out.println(decrypt("PBEWithMD5AndDES", "6Hn9iiqLB7LHC4D41533xA=="));
    }

    /**
     * Description: 加密<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param algorithm
     * @param password
     * @return <br>
     */
    public static String encrypt(String algorithm, String password) {
        // 加密工具
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        // 加密配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        // 自己在用的时候更改此密码
        config.setPassword(PASSWORD);
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
    public static String decrypt(String algorithm, String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        // 加密配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(algorithm);
        // 自己在用的时候更改此密码
        config.setPassword(PASSWORD);
        // 应用配置
        encryptor.setConfig(config);
        // 解密
        return encryptor.decrypt(password);
    }

}
