/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.bootstrap;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fccfc.framework.bootstrap.utils.cmd.Option;
import com.fccfc.framework.bootstrap.utils.cmd.OptionParser;
import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.Configuration;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月23日 <br>
 * @see com.fccfc.framework.bootstrap <br>
 */
public class Startup {

    /**
     * xmlPath
     */
    @Option(name = "-x", value = "classpath:/META-INF/spring/*.xml")
    private String xmlPath;

    /**
     * host
     */
    @Option(name = "-h", value = "127.0.0.1")
    private String host;

    /**
     * port
     */
    @Option(name = "-p", value = "7777")
    private String port;

    /**
     * logger
     */
    private static Logger logger = new Logger(Startup.class);

    /**
     * context
     */
    private static ApplicationContext context;

    /**
     * paramMap
     */
    private static Map<String, String> paramMap = null;

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param args <br>
     * @throws IOException <br>
     */
    public static void main(String[] args) throws IOException {

        paramMap = OptionParser.parse(Startup.class, args);

        logger.info("====================>准备加载Spring配置文件<====================");
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(
            StringUtils.split(paramMap.get("xmlPath")));
        context = ac;
        ac.start();
        logger.info("====================>Spring配置文件加载完毕<====================");
        System.out.println(new StringBuilder().append("\n***************************************").append('\n')
            .append("*         ").append(ManagementFactory.getRuntimeMXBean().getName()).append("        *")
            .append('\n').append("*            ").append(Configuration.get(CacheConstant.MODULE_CODE)).append("模块启动成功！")
            .append("                                   *").append('\n').append("*    ").append("关闭地址为:UDP://")
            .append(paramMap.get("host")).append(':').append(paramMap.get("port")).append("      *").append('\n')
            .append("***************************************"));

        startShutdownServer();

        ac.close();
        logger.info("====================>系统正常停止运行<====================");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     *         <br>
     */
    private static void startShutdownServer() {
        DatagramSocket server = null;
        try {
            server = new DatagramSocket(
                new InetSocketAddress(paramMap.get("host"), Integer.valueOf(paramMap.get("port"))));
            boolean flag = true;

            byte[] buffer = new byte[128];

            while (flag) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                server.receive(packet);

                String data = new String(packet.getData(), 0, packet.getLength(), GlobalConstants.DEFAULT_CHARSET);
                if (StringUtils.equals(Configuration.getString(CacheConstant.MODULE_CODE), data)) {
                    flag = false;
                    System.out.println("接收到关闭服务端请求");
                }

            }
        }
        catch (Exception e) {
            logger.error("服务器意外关闭", e);
        }
        finally {
            if (server != null) {
                server.close();
            }
        }
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
