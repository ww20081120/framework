/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
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

    /**
     * client
     */
    private final Client client;

    /**
     * channel
     */
    private final ExchangeChannel channel;

    /**
     * HeaderExchangeClient
     * @param client <br>
     */
    public HeaderExchangeClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("client == null");
        }
        this.client = client;
        this.channel = new HeaderExchangeClient(client);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws RemotingException <br>
     */
    @Override
    public void reconnect() throws RemotingException {
        client.reconnect();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public URL getUrl() {
        return channel.getUrl();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public ChannelHandler getChannelHandler() {
        return channel.getChannelHandler();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
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
    @Override
    public void send(Object message) throws RemotingException {
        channel.send(message);
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
    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        channel.send(message, sent);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    @Override
    public void close() {
        channel.close();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param timeout <br>
     */
    @Override
    public void close(int timeout) {
        channel.close(timeout);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean isClosed() {
        return channel.isClosed();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean isConnected() {
        return channel.isConnected();
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
    @Override
    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
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
    @Override
    public Object getAttribute(String key) {
        return channel.getAttribute(key);
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
    @Override
    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parameters <br>
     */
    @Override
    public void reset(Parameters parameters) {
        reset(getUrl().addParameters(parameters.getParameters()));
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     */
    @Override
    public void reset(URL url) {
        client.reset(url);
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
    @Override
    public ResponseFuture request(Object request) throws RemotingException {
        return channel.request(request);
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
    @Override
    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        return channel.request(request, timeout);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public ExchangeHandler getExchangeHandler() {
        return channel.getExchangeHandler();
    }

}
