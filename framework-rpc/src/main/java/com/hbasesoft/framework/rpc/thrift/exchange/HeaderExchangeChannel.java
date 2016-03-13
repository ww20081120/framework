/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rpc.thrift.exchange;

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
import com.hbasesoft.framework.rpc.thrift.common.TBaseTools;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年5月19日 <br>
 * @see com.hbasesoft.framework.rpc.thrift.exchange <br>
 */
public class HeaderExchangeChannel implements ExchangeChannel {

    /**
     * SEQ_ID
     */
    public static final String SEQ_ID = "seqId";

    /**
     * CHANNEL_KEY
     */
    private static final String CHANNEL_KEY = HeaderExchangeChannel.class.getName() + ".CHANNEL";

    /**
     * logger
     */
    private static Logger logger = new Logger(HeaderExchangeChannel.class);

    /**
     * channel
     */
    private final Channel channel;

    /**
     * closed
     */
    private volatile boolean closed = false;

    /** seqId generator */
    protected static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    /**
     * 默认构造函数
     * @param channel <br>
     */
    public HeaderExchangeChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == null");
        }
        this.channel = channel;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param ch <br>
     * @return <br>
     */
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

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param ch <br>
     */
    public static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isConnected()) {
            ch.removeAttribute(CHANNEL_KEY);
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
        send(message, getUrl().getParameter(Constants.SENT_KEY, false));
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

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param id <br>
     * @param inv <br>
     * @return <br>
     * @throws RemotingException <br>
     */
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
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
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
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean isClosed() {
        return closed;
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
        return request(request, channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT));
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
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public ExchangeHandler getExchangeHandler() {
        return (ExchangeHandler) channel.getChannelHandler();
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HeaderExchangeChannel other = (HeaderExchangeChannel) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        }
        else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return channel.toString();
    }

}
