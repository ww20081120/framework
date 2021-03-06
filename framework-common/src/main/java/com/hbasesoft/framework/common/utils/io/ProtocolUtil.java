/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.io <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProtocolUtil {

    /** DEFAULT_PORT */
    public static final int DEFAULT_FTP_PORT = 21;

    /** DEFAULT_PORT */
    public static final int DEFAULT_SFTP_PORT = 22;

    /** DEFAULT_PORT */
    public static final int DEFAULT_SSH_PORT = 22;

    /** DEFAULT_PORT */
    public static final int DEFAULT_REDIS_PORT = 6379;

    /** DEFAULT_PORT */
    public static final int DEFAULT_ES_PORT = 9200;

    /** DEFAULT_PORT */
    public static final int DEFAULT_MYSQL_PORT = 3306;

    /** DEFAULT_PORT */
    public static final int DEFAULT_ORACLE_PORT = 1521;

    /** DEFAULT_PORT */
    public static final int DEFAULT_ZK_PORT = 2181;

    /** DEFAULT_PORT */
    public static final int DEFAULT_KAFKA_PORT = 9092;

    /** DEFAULT_SMTP_PORT */
    public static final int DEFAULT_SMTP_PORT = 25;

    /** DEFAULT_SMTP_PORT */
    public static final int DEFAULT_SMTPS_PORT = 465;

    /** "://" */
    private static final int LEN = "://".length();

    /**
     * logger
     */
    private static Logger logger = new Logger(ProtocolUtil.class);

    /** defaultPortMap */
    private static Map<String, Integer> defaultPortMap;

    static {
        defaultPortMap = new HashMap<String, Integer>();
        defaultPortMap.put("http", HttpUtil.DEFAULT_HTTP_PORT);
        defaultPortMap.put("https", HttpUtil.DEFAULT_HTTPS_PORT);
        defaultPortMap.put("smtp", DEFAULT_SMTP_PORT);
        defaultPortMap.put("smtps", DEFAULT_SMTPS_PORT);
        defaultPortMap.put("ftp", DEFAULT_FTP_PORT);
        defaultPortMap.put("sftp", DEFAULT_SFTP_PORT);
        defaultPortMap.put("ssh", DEFAULT_SSH_PORT);
        defaultPortMap.put("redis", DEFAULT_REDIS_PORT);
        defaultPortMap.put("es", DEFAULT_ES_PORT);
        defaultPortMap.put("mysql", DEFAULT_MYSQL_PORT);
        defaultPortMap.put("oracle", DEFAULT_ORACLE_PORT);
        defaultPortMap.put("zk", DEFAULT_ZK_PORT);
        defaultPortMap.put("kafka", DEFAULT_KAFKA_PORT);
    }

    /**
     * Description: 详细的地址 protocol://username:password@host:port/path?param1=value1&param2=value2....<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param protocolStr
     * @return <br>
     */
    public static Address[] parseAddress(final String protocolStr) {
        String[] strs = StringUtils.split(protocolStr, GlobalConstants.SPLITOR);
        List<Address> protocols = new ArrayList<Address>(strs.length);
        for (String str : strs) {
            Address protocol = new Address();
            int start = 0;
            int end = 0;
            if ((end = str.indexOf("://")) != -1) {
                protocol.setProtocol(str.substring(0, end).toLowerCase());
                start = protocol.getProtocol().length() + LEN;
            }
            else {
                protocol.setProtocol("http");
            }

            String url;
            if ((end = str.indexOf("?")) != -1) {
                url = str.substring(start, end);
                String paramStr = str.substring(end + 1);
                Map<String, String> map = new HashMap<String, String>();
                String[] paramStrs = StringUtils.split(paramStr, GlobalConstants.PARAM_SPLITOR);
                try {
                    for (String param : paramStrs) {
                        String[] keyValue = StringUtils.split(param, GlobalConstants.EQUAL_SPLITER);
                        if (keyValue.length == 2) {
                            map.put(keyValue[0],
                                new String(keyValue[1].getBytes("ISO-8859-1"), GlobalConstants.DEFAULT_CHARSET));
                        }
                    }
                    protocol.params = map;
                }
                catch (UnsupportedEncodingException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            else {
                url = str.substring(start);
            }

            if ((end = url.indexOf("@")) != -1) {
                String auth = url.substring(0, end);
                url = url.substring(end + 1);
                String[] up = StringUtils.split(auth, ":");
                if (up.length >= 1) {
                    protocol.setUsername(up[0]);
                }

                if (up.length >= 2) {
                    protocol.setPassword(up[1]);
                }
            }

            if ((end = url.indexOf(GlobalConstants.PATH_SPLITOR)) != -1) {
                protocol.setPath(url.substring(end + 1));
                url = url.substring(0, end);
            }

            String[] addresses = StringUtils.split(url, ":");
            if (addresses.length == 1) {
                protocol.setHost(addresses[0]);
                protocol.setPort(HttpUtil.DEFAULT_HTTP_PORT);
            }
            else if (addresses.length == 2) {
                protocol.setHost(addresses[0]);
                protocol.setPort(Integer.parseInt(addresses[1]));
            }

            protocols.add(protocol);
        }
        return protocols.toArray(new Address[0]);
    }

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2015年11月23日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.common.utils.io <br>
     */
    @Getter
    @Setter
    public static class Address {

        /** protocol */
        private String protocol;

        /** host */
        private String host;

        /** port */
        private Integer port;

        /** username */
        private String username;

        /** password */
        private String password;

        /** path */
        private String path;

        /** params */
        private Map<String, String> params;

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param key
         * @return <br>
         */
        public String getParameter(final String key) {
            return this.params != null ? this.params.get(key) : null;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        @Override
        public String toString() {
            return new StringBuilder().append(protocol).append("://").append(username).append(":").append(password)
                .append("@").append(host).append(":").append(port).append(GlobalConstants.PATH_SPLITOR).append(path)
                .append("?").append(params).toString();
        }
    }
}
