/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @SpringBootApplication
 * @ComponentScan(basePackages = "com.hbasesoft") <br>
 *                             @ImportResource("classpath*:META-INF/spring/*.xml") <br>
 * @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public final class Bootstrap {

    /** listenerList */
    private static List<StartupListener> listenerList = null;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public static void before() {
        // 检测并设置http\https\ftp\socks的代理
        setHttpProxy();
        setHttpsProxy();
        setFtpProxy();
        setSocksProxy();

        ServiceLoader<StartupListener> loader = ServiceLoader.load(StartupListener.class);
        if (loader != null) {
            listenerList = new ArrayList<StartupListener>();
            for (StartupListener listener : loader) {
                if (listener.enable()) {
                    listenerList.add(listener);
                }
            }

            Collections.sort(listenerList, new Comparator<StartupListener>() {
                @Override
                public int compare(final StartupListener o1, final StartupListener o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
        }

        if (CollectionUtils.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.init();
                LoggerUtil.info("   {0} 初始化", listener.getClass().getName());
            }
        }

        LoggerUtil.info("====================>准备加载Spring配置文件<====================");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    public static void after(final ApplicationContext context) {
        LoggerUtil.info("====================>Spring配置文件加载完毕<====================");

        if (CollectionUtils.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.complete(context);
                LoggerUtil.info("   {0} 初始化成功。", listener.getClass().getName());
            }
        }

        LoggerUtil.info("**********************************************************");

        StringBuilder sb = new StringBuilder().append("\n***************************************").append('\n')
            .append("      ").append(ManagementFactory.getRuntimeMXBean().getName()).append('\n').append("      ")
            .append(PropertyHolder.getProjectName()).append('-').append(PropertyHolder.getVersion()).append(" 模块启动成功！")
            .append('\n');

        String protocol = PropertyHolder.getBooleanProperty("server.ssl.enabled", false) ? "https" : "http";

        String port = context.getEnvironment().getProperty("server.port", "8080");
        for (String ip : listAllIpAddresses()) {
            sb.append("      ").append(protocol).append("://").append(ip).append(":").append(port).append('\n');
        }
        sb.append("***************************************");

        LoggerUtil.info(sb.toString());
        LoggerUtil.info("====================>系统正常启动<====================");

    }

    private static List<String> listAllIpAddresses() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof java.net.Inet4Address && !inetAddress.isLoopbackAddress()) {
                        ipList.add(inetAddress.getHostAddress());
                    }
                }
            }
        }
        catch (SocketException e) {
            LoggerUtil.error(e);
            ipList.add("127.0.0.1");
        }
        return ipList;
    }

    private static void setHttpProxy() {
        // 检查并设置http代理
        // if (StringUtils.isEmpty(System.getProperty("http.proxyHost"))) {
        String httpProxy = PropertyHolder.getProperty("http.proxy");
        if (StringUtils.isEmpty(httpProxy)) {
            httpProxy = System.getenv("http_proxy");
        }

        if (StringUtils.isNotEmpty(httpProxy)) {
            Address[] protocols = ProtocolUtil.parseAddress(httpProxy);
            if (ArrayUtils.isNotEmpty(protocols)) {
                Address addr = protocols[0];
                System.setProperty("http.proxyHost", addr.getHost());
                System.setProperty("http.proxyPort", addr.getPort() + GlobalConstants.BLANK);
                String nonProxyHosts = PropertyHolder.getProperty("http.nonProxyHosts");
                if (StringUtils.isNotEmpty(nonProxyHosts)) {
                    System.setProperty("http.nonProxyHosts", nonProxyHosts);
                }
                LoggerUtil.info("set http proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
            }
        }
        // }
    }

    private static void setHttpsProxy() {
        // 检查并设置http代理
        // if (StringUtils.isEmpty(System.getProperty("https.proxyHost"))) {
        String httpsProxy = PropertyHolder.getProperty("https.proxy");
        if (StringUtils.isEmpty(httpsProxy)) {
            httpsProxy = System.getenv("https_proxy");
        }

        if (StringUtils.isNotEmpty(httpsProxy)) {
            Address[] protocols = ProtocolUtil.parseAddress(httpsProxy);
            if (ArrayUtils.isNotEmpty(protocols)) {
                Address addr = protocols[0];
                System.setProperty("https.proxyHost", addr.getHost());
                System.setProperty("https.proxyPort", addr.getPort() + GlobalConstants.BLANK);
                String nonProxyHosts = PropertyHolder.getProperty("https.nonProxyHosts");
                if (StringUtils.isNotEmpty(nonProxyHosts)) {
                    System.setProperty("https.nonProxyHosts", nonProxyHosts);
                }
                LoggerUtil.info("set https proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
            }
        }
        // }
    }

    private static void setFtpProxy() {
        // 检查并设置http代理
        // if (StringUtils.isEmpty(System.getProperty("ftp.proxyHost"))) {
        String ftpProxy = PropertyHolder.getProperty("ftp.proxy");
        if (StringUtils.isEmpty(ftpProxy)) {
            ftpProxy = System.getenv("ftp_proxy");
        }

        if (StringUtils.isNotEmpty(ftpProxy)) {
            Address[] protocols = ProtocolUtil.parseAddress(ftpProxy);
            if (ArrayUtils.isNotEmpty(protocols)) {
                Address addr = protocols[0];
                System.setProperty("ftp.proxyHost", addr.getHost());
                System.setProperty("ftp.proxyPort", addr.getPort() + GlobalConstants.BLANK);
                String nonProxyHosts = PropertyHolder.getProperty("ftp.nonProxyHosts");
                if (StringUtils.isNotEmpty(nonProxyHosts)) {
                    System.setProperty("ftp.nonProxyHosts", nonProxyHosts);
                }
                LoggerUtil.info("set ftp proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
            }
        }
        // }
    }

    private static void setSocksProxy() {
        // 检查并设置http代理
        // if (StringUtils.isEmpty(System.getProperty("socksProxyHost"))) {
        String socksProxy = PropertyHolder.getProperty("socksProxy");
        if (StringUtils.isEmpty(socksProxy)) {
            socksProxy = System.getenv("socks_proxy");
        }

        if (StringUtils.isNotEmpty(socksProxy)) {
            Address[] protocols = ProtocolUtil.parseAddress(socksProxy);
            if (ArrayUtils.isNotEmpty(protocols)) {
                Address addr = protocols[0];
                System.setProperty("socksProxyHost", addr.getHost());
                System.setProperty("socksProxyPort", addr.getPort() + GlobalConstants.BLANK);

                LoggerUtil.info("set socks proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
            }
        }
        // }
    }
}
