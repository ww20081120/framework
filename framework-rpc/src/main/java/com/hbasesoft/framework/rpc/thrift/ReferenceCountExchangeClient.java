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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.Parameters;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.exchange.ResponseFuture;

/**
 * dubbo protocol support class.
 * 
 * @author chao.liuc
 */
@SuppressWarnings("deprecation")
final class ReferenceCountExchangeClient implements ExchangeClient {

    /**
     * client
     */
    private ExchangeClient client;

    /**
     * url
     */
    private final URL url;

    // private final ExchangeHandler handler;

    /**
     * refenceCount
     */
    private final AtomicInteger refenceCount = new AtomicInteger(0);

    /**
     * ghostClientMap
     */
    private final ConcurrentMap<String, LazyConnectExchangeClient> ghostClientMap;

    /**
     * ReferenceCountExchangeClient
     * @param client <br>
     * @param ghostClientMap <br>
     */
    public ReferenceCountExchangeClient(ExchangeClient client,
        ConcurrentMap<String, LazyConnectExchangeClient> ghostClientMap) {
        this.client = client;
        refenceCount.incrementAndGet();
        this.url = client.getUrl();
        if (ghostClientMap == null) {
            throw new IllegalStateException("ghostClientMap can not be null, url: " + url);
        }
        this.ghostClientMap = ghostClientMap;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     */
    public void reset(URL url) {
        client.reset(url);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws RemotingException <br>
     */
    public ResponseFuture request(Object request) throws RemotingException {
        return client.request(request);
    }

    public URL getUrl() {
        return client.getUrl();
    }

    public InetSocketAddress getRemoteAddress() {
        return client.getRemoteAddress();
    }

    public ChannelHandler getChannelHandler() {
        return client.getChannelHandler();
    }

    /**
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
        return client.request(request, timeout);
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws RemotingException <br>
     */
    public void reconnect() throws RemotingException {
        client.reconnect();
    }

    public InetSocketAddress getLocalAddress() {
        return client.getLocalAddress();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @return <br>
     */
    public boolean hasAttribute(String key) {
        return client.hasAttribute(key);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parameters <br>
     */
    public void reset(Parameters parameters) {
        client.reset(parameters);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @throws RemotingException <br>
     */
    public void send(Object message) throws RemotingException {
        client.send(message);
    }

    public ExchangeHandler getExchangeHandler() {
        return client.getExchangeHandler();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @return <br>
     */
    public Object getAttribute(String key) {
        return client.getAttribute(key);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @param sent <br>
     * @throws RemotingException <br>
     */
    public void send(Object message, boolean sent) throws RemotingException {
        client.send(message, sent);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @param value <br>
     */
    public void setAttribute(String key, Object value) {
        client.setAttribute(key, value);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     */
    public void removeAttribute(String key) {
        client.removeAttribute(key);
    }

    /**
     * close方法将不再幂等,调用需要注意.
     */
    public void close() {
        close(0);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param timeout <br>
     */
    public void close(int timeout) {
        if (refenceCount.decrementAndGet() <= 0) {
            if (timeout == 0) {
                client.close();
            }
            else {
                client.close(timeout);
            }
            client = replaceWithLazyClient();
        }
    }

    /**
     * 幽灵client,
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private LazyConnectExchangeClient replaceWithLazyClient() {
        // 这个操作只为了防止程序bug错误关闭client做的防御措施，初始client必须为false状态
        URL lazyUrl = url.addParameter(Constants.LAZY_CONNECT_INITIAL_STATE_KEY, Boolean.FALSE)
            .addParameter(Constants.RECONNECT_KEY, Boolean.FALSE)
            .addParameter(Constants.SEND_RECONNECT_KEY, Boolean.TRUE.toString())
            .addParameter("warning", Boolean.TRUE.toString())
            .addParameter(LazyConnectExchangeClient.REQUEST_WITH_WARNING_KEY, true)
            .addParameter("_client_memo", "referencecounthandler.replacewithlazyclient");

        String key = url.getAddress();
        // 最差情况下只有一个幽灵连接
        LazyConnectExchangeClient gclient = ghostClientMap.get(key);
        if (gclient == null || gclient.isClosed()) {
            gclient = new LazyConnectExchangeClient(lazyUrl, client.getExchangeHandler());
            ghostClientMap.put(key, gclient);
        }
        return gclient;
    }

    public boolean isClosed() {
        return client.isClosed();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    public void incrementAndGetCount() {
        refenceCount.incrementAndGet();
    }
}