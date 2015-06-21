/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.rpc.thrift.exchange;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.exchange.ResponseFuture;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.rpc.thrift.common.TBaseTools;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年5月19日 <br>
 * @see com.fccfc.framework.rpc.thrift.exchange <br>
 */
public class HeaderExchangeChannel implements ExchangeChannel {

    public static final String SEQ_ID = "seqId";

    private static final String CHANNEL_KEY = HeaderExchangeChannel.class.getName() + ".CHANNEL";

    private static Logger logger = new Logger(HeaderExchangeChannel.class);

    private final Channel channel;

    private volatile boolean closed = false;

    /** seqId generator */
    protected static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    /**
     * 默认构造函数
     */
    public HeaderExchangeChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == null");
        }
        this.channel = channel;
    }

    public static HeaderExchangeChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        HeaderExchangeChannel ret = (HeaderExchangeChannel) ch.getAttribute(CHANNEL_KEY);
        if (ret == null) {
            ret = new HeaderExchangeChannel(ch);
            if (ch.isConnected()) {
                ch.setAttribute(CHANNEL_KEY, ret);
            }
        }
        return ret;
    }

    public static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isConnected()) {
            ch.removeAttribute(CHANNEL_KEY);
        }
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
        send(message, getUrl().getParameter(Constants.SENT_KEY, false));
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#send(java.lang.Object, boolean)
     */
    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message
                + ", cause: The channel " + this + " is closed!");
        }

        if (message instanceof RpcInvocation) {
            RpcInvocation inv = (RpcInvocation) message;
            int id = SEQUENCE.incrementAndGet();
            ChannelBuffer output = createRequestBuffer(id, inv);
            channel.send(output, sent);
        }
    }

    private ChannelBuffer createRequestBuffer(int id, RpcInvocation inv) throws RemotingException {
        ChannelBuffer output = ChannelBuffers.dynamicBuffer(512);
        TProtocol oprot = TBaseTools.newProtocol(null, output);

        String methodName = inv.getMethodName();
        String serviceName = inv.getAttachment(Constants.PATH_KEY);
        Class<?>[] parameterTypes = inv.getParameterTypes();
        Object[] arguments = inv.getArguments();

        try {
            oprot.writeMessageBegin(new TMessage(methodName, TMessageType.CALL, id));
            String argsServiceName = TBaseTools.getArgsClassName(serviceName, methodName, "_args");
            Class<?> clazz = TBaseTools.getTBaseClass(argsServiceName);
            TBase<?, ?> _args = TBaseTools.getTBaseObject(clazz, parameterTypes, arguments);
            _args.write(oprot);
        }
        catch (Exception e) {
            throw new RemotingException(channel, e);
        }
        return output;
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#close()
     */
    @Override
    public void close() {
        try {
            channel.close();
        }
        catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * @see com.alibaba.dubbo.remoting.Endpoint#isClosed()
     */
    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#request(java.lang.Object)
     */
    @Override
    public ResponseFuture request(Object request) throws RemotingException {
        return request(request, channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT));
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#request(java.lang.Object, int)
     */
    @Override
    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send request " + request
                + ", cause: The channel " + this + " is closed!");
        }

        if (request instanceof RpcInvocation) {
            RpcInvocation inv = (RpcInvocation) request;
            int id = SEQUENCE.incrementAndGet();
            ChannelBuffer output = createRequestBuffer(id, inv);
            DefaultFuture future = new DefaultFuture(id, channel, timeout, inv.getAttachment(Constants.PATH_KEY),
                inv.getMethodName());
            channel.send(output);
            return future;
        }
        return null;
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#getExchangeHandler()
     */
    @Override
    public ExchangeHandler getExchangeHandler() {
        return (ExchangeHandler) channel.getChannelHandler();
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.ExchangeChannel#close(int)
     */
    @Override
    public void close(int timeout) {
        if (closed) {
            return;
        }
        closed = true;
        if (timeout > 0) {
            long start = System.currentTimeMillis();
            while (DefaultFuture.hasFuture(HeaderExchangeChannel.this) && System.currentTimeMillis() - start < timeout) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        close();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HeaderExchangeChannel other = (HeaderExchangeChannel) obj;
        if (channel == null) {
            if (other.channel != null)
                return false;
        }
        else if (!channel.equals(other.channel))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return channel.toString();
    }

}
