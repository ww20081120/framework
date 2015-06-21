package com.fccfc.framework.rpc.thrift.exchange;

import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.ExecutionException;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.transport.ChannelHandlerDelegate;
import com.alibaba.dubbo.rpc.RpcResult;
import com.fccfc.framework.rpc.thrift.common.TBaseTools;

/**
 * @author yankai
 * @date 2012-8-31
 */
public class HeaderExchangeServerHandler implements ChannelHandlerDelegate {

    private final ExchangeHandler handler;

    public HeaderExchangeServerHandler(ExchangeHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        this.handler = handler;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.connected(exchangeChannel);
        }
        finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.disconnected(exchangeChannel);
        }
        finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void sent(Channel channel, Object message) throws RemotingException {
        Throwable exception = null;
        try {
            ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
            try {
                handler.sent(exchangeChannel, message);
            }
            finally {
                HeaderExchangeChannel.removeChannelIfDisconnected(channel);
            }
        }
        catch (Throwable t) {
            exception = t;
        }
        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            }
            else if (exception instanceof RemotingException) {
                throw (RemotingException) exception;
            }
            else {
                throw new RemotingException(channel.getLocalAddress(), channel.getRemoteAddress(),
                    exception.getMessage(), exception);
            }
        }
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        try {
            ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
            Object object = handler.reply(exchangeChannel, message);
            RpcResult result = (RpcResult) object;
            channel.send(result.getValue());
        }
        finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {
        if (exception instanceof ExecutionException) {
            ExecutionException e = (ExecutionException) exception;
            Object msg = e.getRequest();
            if (msg instanceof ChannelBuffer) {
                ChannelBuffer input = (ChannelBuffer) msg;
                ChannelBuffer output = ChannelBuffers.dynamicBuffer();
                TProtocol prot = TBaseTools.newProtocol(input, output);
                try {
                    TMessage tmessage = prot.readMessageBegin();
                    TBaseTools.createErrorTMessage(prot, tmessage.name, tmessage.seqid, StringUtils.toString(e));
                    channel.send(output);
                    return;
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }

}
