/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.rpc.thrift.exchange;

import java.net.InetSocketAddress;

import com.alibaba.dubbo.common.Parameters;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.Client;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.exchange.ResponseFuture;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年5月19日 <br>
 * @see com.fccfc.framework.rpc.thrift.exchange <br>
 */
@SuppressWarnings("deprecation")
public class HeaderExchangeClient implements ExchangeClient {

    private final Client client;

    private final ExchangeChannel channel;

    /**
     * 默认构造函数
     */
    public HeaderExchangeClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("client == null");
        }
        this.client = client;
        this.channel = new HeaderExchangeClient(client);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Client#reconnect()
     */
    @Override
    public void reconnect() throws RemotingException {
        client.reconnect();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#getUrl()
     */
    @Override
    public URL getUrl() {
        return channel.getUrl();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#getChannelHandler()
     */
    @Override
    public ChannelHandler getChannelHandler() {
        return channel.getChannelHandler();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#getLocalAddress()
     */
    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#send(java.lang.Object)
     */
    @Override
    public void send(Object message) throws RemotingException {
        channel.send(message);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#send(java.lang.Object, boolean)
     */
    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        channel.send(message, sent);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#close()
     */
    @Override
    public void close() {
        channel.close();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#close(int)
     */
    @Override
    public void close(int timeout) {
        channel.close(timeout);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#isClosed()
     */
    @Override
    public boolean isClosed() {
        return channel.isClosed();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Channel#getRemoteAddress()
     */
    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Channel#isConnected()
     */
    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    /**
     * @see com.alibaba.dubbo.remoting.Channel#hasAttribute(java.lang.String)
     */
    @Override
    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Channel#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Channel#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Channel#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    /**
     * @see com.alibaba.dubbo.remoting.Client#reset(com.alibaba.dubbo.common.Parameters)
     */
    @Override
    public void reset(Parameters parameters) {
        reset(getUrl().addParameters(parameters.getParameters()));
    }

    /**
     * @see com.alibaba.dubbo.common.Resetable#reset(com.alibaba.dubbo.common.URL)
     */
    @Override
    public void reset(URL url) {
        client.reset(url);
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#request(java.lang.Object)
     */
    @Override
    public ResponseFuture request(Object request) throws RemotingException {
        return channel.request(request);
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#request(java.lang.Object, int)
     */
    @Override
    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        return channel.request(request, timeout);
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#getExchangeHandler()
     */
    @Override
    public ExchangeHandler getExchangeHandler() {
        return channel.getExchangeHandler();
    }

}
