/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;
import com.hbasesoft.framework.common.utils.logger.Logger;

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
public class Bootstrap {

    /**
     * logger
     */
    private static Logger logger = new Logger(Bootstrap.class);

    private static List<StartupListener> listenerList = null;

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
                listenerList.add(listener);
            }

            Collections.sort(listenerList, new Comparator<StartupListener>() {
                @Override
                public int compare(StartupListener o1, StartupListener o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
        }

        logger.info("*********************初始化StartupListener*************************");
        if (CollectionUtils.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.init();
                logger.info("   {0} 初始化", listener.getClass().getName());
            }
        }

        logger.info("====================>准备加载Spring配置文件<====================");
    }

    public static void after(ApplicationContext context) {
        logger.info("====================>Spring配置文件加载完毕<====================");

        if (CollectionUtils.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.complete(context);
                logger.info("   {0} 初始化成功。", listener.getClass().getName());
            }
        }

        logger.info("**********************************************************");

        System.out.println(new StringBuilder().append("\n***************************************").append('\n')
            .append("         ").append(ManagementFactory.getRuntimeMXBean().getName()).append('\n').append("         ")
            .append(PropertyHolder.getProjectName()).append("模块启动成功！").append('\n')
            .append("***************************************"));
        logger.info("====================>系统正常启动<====================");

    }

    private static void setHttpProxy() {
        // 检查并设置http代理
        if (StringUtils.isEmpty(System.getProperty("http.proxyHost"))) {
            String httpProxy = PropertyHolder.getProperty("http.proxy");
            if (StringUtils.isEmpty(httpProxy)) {
                httpProxy = System.getenv("http_proxy");
            }

            if (StringUtils.isNotEmpty(httpProxy)) {
                Address[] protocols = ProtocolUtil.parseAddress(httpProxy);
                if (CommonUtil.isNotEmpty(protocols)) {
                    Address addr = protocols[0];
                    System.setProperty("http.proxyHost", addr.getHost());
                    System.setProperty("http.proxyPort", addr.getPort() + GlobalConstants.BLANK);
                    String nonProxyHosts = PropertyHolder.getProperty("http.nonProxyHosts");
                    if (StringUtils.isNotEmpty(nonProxyHosts)) {
                        System.setProperty("http.nonProxyHosts", nonProxyHosts);
                    }
                    logger.info("set http proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
                }
            }
        }
    }

    private static void setHttpsProxy() {
        // 检查并设置http代理
        if (StringUtils.isEmpty(System.getProperty("https.proxyHost"))) {
            String httpsProxy = PropertyHolder.getProperty("https.proxy");
            if (StringUtils.isEmpty(httpsProxy)) {
                httpsProxy = System.getenv("https_proxy");
            }

            if (StringUtils.isNotEmpty(httpsProxy)) {
                Address[] protocols = ProtocolUtil.parseAddress(httpsProxy);
                if (CommonUtil.isNotEmpty(protocols)) {
                    Address addr = protocols[0];
                    System.setProperty("https.proxyHost", addr.getHost());
                    System.setProperty("https.proxyPort", addr.getPort() + GlobalConstants.BLANK);
                    String nonProxyHosts = PropertyHolder.getProperty("https.nonProxyHosts");
                    if (StringUtils.isNotEmpty(nonProxyHosts)) {
                        System.setProperty("https.nonProxyHosts", nonProxyHosts);
                    }
                    logger.info("set https proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
                }
            }
        }
    }

    private static void setFtpProxy() {
        // 检查并设置http代理
        if (StringUtils.isEmpty(System.getProperty("ftp.proxyHost"))) {
            String ftpProxy = PropertyHolder.getProperty("ftp.proxy");
            if (StringUtils.isEmpty(ftpProxy)) {
                ftpProxy = System.getenv("ftp_proxy");
            }

            if (StringUtils.isNotEmpty(ftpProxy)) {
                Address[] protocols = ProtocolUtil.parseAddress(ftpProxy);
                if (CommonUtil.isNotEmpty(protocols)) {
                    Address addr = protocols[0];
                    System.setProperty("ftp.proxyHost", addr.getHost());
                    System.setProperty("ftp.proxyPort", addr.getPort() + GlobalConstants.BLANK);
                    String nonProxyHosts = PropertyHolder.getProperty("ftp.nonProxyHosts");
                    if (StringUtils.isNotEmpty(nonProxyHosts)) {
                        System.setProperty("ftp.nonProxyHosts", nonProxyHosts);
                    }
                    logger.info("set ftp proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
                }
            }
        }
    }

    private static void setSocksProxy() {
        // 检查并设置http代理
        if (StringUtils.isEmpty(System.getProperty("socksProxyHost"))) {
            String socksProxy = PropertyHolder.getProperty("socksProxy");
            if (StringUtils.isEmpty(socksProxy)) {
                socksProxy = System.getenv("socks_proxy");
            }

            if (StringUtils.isNotEmpty(socksProxy)) {
                Address[] protocols = ProtocolUtil.parseAddress(socksProxy);
                if (CommonUtil.isNotEmpty(protocols)) {
                    Address addr = protocols[0];
                    System.setProperty("socksProxyHost", addr.getHost());
                    System.setProperty("socksProxyPort", addr.getPort() + GlobalConstants.BLANK);

                    logger.info("set socks proxy[{0}:{1}] success", addr.getHost(), addr.getPort());
                }
            }
        }
    }
}
