/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年8月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.core <br>
 */
public class RemoteShell {

    /** */
    private static final int NUM5 = 5;

    /** */
    private static final int NUM10 = 10;

    /** */
    private static final int NUM20 = 20;

    /** */
    private static final int NUM600 = 600;

    /** */
    private static final int NUM1000 = 1000;

    /** */
    private static final int NUM60000 = 60000;

    /** */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
        PropertyHolder.getIntProperty("server.executor.coreSize", NUM5), // 设置核心线程数量
        PropertyHolder.getIntProperty("server.executor.maxPoolSize", NUM20), // 线程池维护线程的最大数量
        PropertyHolder.getIntProperty("server.executor.keepAliveSeconds", NUM600), TimeUnit.SECONDS, // 允许的空闲时间
        new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("server.executor.queueCapacity", NUM10))); // 缓存队列

    /** */
    public static void run() throws IOException {
        int port = PropertyHolder.getIntProperty("server.port", NUM60000);
        
        try (ServerSocket severSocket = new ServerSocket(port)) {
            System.out.println("==========>服务端启动成功，端口为" + port + "<===============");

            while (!Thread.interrupted()) {
                try {
                    Socket socket = severSocket.accept();
                    if (socket != null && socket.isConnected()) {
                        executor.execute(() -> {
                            try {
                                new Shell(socket.getInputStream(), new PrintStream(socket.getOutputStream()), true)
                                    .run(new String[0]);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(NUM1000);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
