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
import lombok.NoArgsConstructor;

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

    /**
     * logger
     */
    private static Logger logger = new Logger(ProtocolUtil.class);

    private static Map<String, Integer> defaultPortMap;

    static {
        defaultPortMap = new HashMap<String, Integer>();
        defaultPortMap.put("http", 80);
        defaultPortMap.put("https", 443);
        defaultPortMap.put("ftp", 21);
        defaultPortMap.put("sftp", 22);
        defaultPortMap.put("ssh", 22);
        defaultPortMap.put("redis", 6379);
        defaultPortMap.put("es", 9200);
        defaultPortMap.put("mysql", 3306);
        defaultPortMap.put("oracle", 1521);
        defaultPortMap.put("zk", 2181);
        defaultPortMap.put("kafka", 9092);
    }

    /**
     * Description: 详细的地址 protocol://username:password@host:port/path?param1=value1&param2=value2....<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param protocolStr
     * @return <br>
     */
    public static Address[] parseAddress(String protocolStr) {
        String[] strs = StringUtils.split(protocolStr, GlobalConstants.SPLITOR);
        List<Address> protocols = new ArrayList<Address>(strs.length);
        for (String str : strs) {
            Address protocol = new Address();
            int start = 0;
            int end = 0;
            if ((end = str.indexOf("://")) != -1) {
                protocol.setProtocol(str.substring(0, end).toLowerCase());
                start = protocol.getProtocol().length() + 3;
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
                protocol.setPort(80);
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
    public static class Address {
        private String protocol;

        private String host;

        private Integer port;

        private String username;

        private String password;

        private String path;

        private Map<String, String> params;

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String getParameter(String key) {
            return this.params != null ? this.params.get(key) : null;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(protocol).append("://").append(username).append(":").append(password)
                .append("@").append(host).append(":").append(port).append(GlobalConstants.PATH_SPLITOR).append(path)
                .append("?").append(params).toString();
        }
    }
}
