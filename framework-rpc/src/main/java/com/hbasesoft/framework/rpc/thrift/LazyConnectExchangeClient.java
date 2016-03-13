/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.rpc.thrift;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.Parameters;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.exchange.Exchangers;
import com.alibaba.dubbo.remoting.exchange.ResponseFuture;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * dubbo protocol support class.
 * 
 * @author chao.liuc
 */
@SuppressWarnings("deprecation")
final class LazyConnectExchangeClient implements ExchangeClient {

    /**
     * logger
     */
    private static Logger logger = new Logger(LazyConnectExchangeClient.class);

    /**
     * url
     */
    private final URL url;

    /**
     * requestHandler
     */
    private final ExchangeHandler requestHandler;

    /**
     * client
     */
    private volatile ExchangeClient client;

    /**
     * connectLock
     */
    private final Lock connectLock = new ReentrantLock();

    /**
     * lazy connect 如果没有初始化时的连接状态
     */
    private final boolean initialState;

    /**
     * requestWithWarning
     */
    protected final boolean requestWithWarning;

    /**
     * 当调用时warning，出现这个warning，表示程序可能存在bug.
     */
    static final String REQUEST_WITH_WARNING_KEY = "lazyclient_request_with_warning";

    /**
     * warningcount
     */
    private AtomicLong warningcount = new AtomicLong(0);

    /**
     * LazyConnectExchangeClient
     * @param url <br>
     * @param requestHandler <br>
     */
    public LazyConnectExchangeClient(URL url, ExchangeHandler requestHandler) {
        // lazy connect ,need set send.reconnect = true, to avoid channel bad status.
        this.url = url.addParameter(Constants.SEND_RECONNECT_KEY, Boolean.TRUE.toString());
        this.requestHandler = requestHandler;
        this.initialState = url.getParameter(Constants.LAZY_CONNECT_INITIAL_STATE_KEY,
            Constants.DEFAULT_LAZY_CONNECT_INITIAL_STATE);
        this.requestWithWarning = url.getParameter(REQUEST_WITH_WARNING_KEY, false);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws RemotingException <br>
     */
    private void initClient() throws RemotingException {
        if (client != null) {
            return;
        }
        logger.info("Lazy connect to " + url);
        connectLock.lock();
        try {
            if (client != null) {
                return;
            }
            this.client = Exchangers.connect(url, requestHandler);
        }
        finally {
            connectLock.unlock();
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws RemotingException <br>
     */
    public ResponseFuture request(Object request) throws RemotingException {
        warning(request);
        initClient();
        return client.request(request);
    }

    public URL getUrl() {
        return url;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public InetSocketAddress getRemoteAddress() {
        if (client == null) {
            return InetSocketAddress.createUnresolved(url.getHost(), url.getPort());
        }
        else {
            return client.getRemoteAddress();
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @param timeout <br>
     * @return <br>
     * @throws RemotingException <br>
     */
    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        warning(request);
        initClient();
        return client.request(request, timeout);
    }

    /**
     * 如果配置了调用warning，则每调用5000次warning一次.
     * 
     * @param request <br>
     */
    private void warning(Object request) {
        if (requestWithWarning) {
            if (warningcount.get() % 5000 == 0) {
                logger.warn(new IllegalStateException("safe guard client , should not be called ,must have a bug."));
            }
            warningcount.incrementAndGet();
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public ChannelHandler getChannelHandler() {
        checkClient();
        return client.getChannelHandler();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public boolean isConnected() {
        if (client == null) {
            return initialState;
        }
        else {
            return client.isConnected();
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public InetSocketAddress getLocalAddress() {
        if (client == null) {
            return InetSocketAddress.createUnresolved(NetUtils.getLocalHost(), 0);
        }
        else {
            return client.getLocalAddress();
        }
    }

    public ExchangeHandler getExchangeHandler() {
        return requestHandler;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @throws RemotingException <br>
     */
    public void send(Object message) throws RemotingException {
        initClient();
        client.send(message);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @param sent <br>
     * @throws RemotingException <br>
     */
    public void send(Object message, boolean sent) throws RemotingException {
        initClient();
        client.send(message, sent);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public boolean isClosed() {
        if (client != null) {
            return client.isClosed();
        }
        else {
            return true;
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param timeout <br>
     */
    public void close(int timeout) {
        if (client != null) {
            client.close(timeout);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     */
    public void reset(URL url) {
        checkClient();
        client.reset(url);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parameters <br>
     */
    @Deprecated
    public void reset(Parameters parameters) {
        reset(getUrl().addParameters(parameters.getParameters()));
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws RemotingException <br>
     */
    public void reconnect() throws RemotingException {
        checkClient();
        client.reconnect();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @return <br>
     */
    public Object getAttribute(String key) {
        if (client == null) {
            return null;
        }
        else {
            return client.getAttribute(key);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @param value <br>
     */
    public void setAttribute(String key, Object value) {
        checkClient();
        client.setAttribute(key, value);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     */
    public void removeAttribute(String key) {
        checkClient();
        client.removeAttribute(key);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @return <br>
     */
    public boolean hasAttribute(String key) {
        if (client == null) {
            return false;
        }
        else {
            return client.hasAttribute(key);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    private void checkClient() {
        if (client == null) {
            throw new IllegalStateException("LazyConnectExchangeClient state error. the client has not be init .url:"
                + url);
        }
    }
}