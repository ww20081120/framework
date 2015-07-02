package com.fccfc.framework.rpc.thrift.exchange;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.Server;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.fccfc.framework.common.utils.logger.Logger;

/**
 * HeaderExchangeServer
 * @author yankai
 * @date 2012-8-27
 */
public class HeaderExchangeServer implements ExchangeServer {

    /**
     * logger
     */
    private static Logger logger = new Logger(HeaderExchangeServer.class);

    /**
     * server
     */
    private final Server server;

    /**
     * closed
     */
    private volatile boolean closed = false;

    /**
     * HeaderExchangeServer
     * @param server <br>
     */
    public HeaderExchangeServer(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("server == null");
        }
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public boolean isClosed() {
        return server.isClosed();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private boolean isRunning() {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (DefaultFuture.hasFuture(channel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    public void close() {
        server.close();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param timeout <br>
     */
    public void close(final int timeout) {
        if (timeout > 0) {
            final long max = (long) timeout;
            final long start = System.currentTimeMillis();
            while (HeaderExchangeServer.this.isRunning() && System.currentTimeMillis() - start < max) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }

        doClose();
        server.close(timeout);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    private void doClose() {
        if (closed) {
            return;
        }
        closed = true;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public Collection<ExchangeChannel> getExchangeChannels() {
        Collection<ExchangeChannel> exchangeChannels = new ArrayList<ExchangeChannel>();
        Collection<Channel> channels = server.getChannels();
        if (channels != null && channels.size() > 0) {
            for (Channel channel : channels) {
                exchangeChannels.add(HeaderExchangeChannel.getOrAddChannel(channel));
            }
        }
        return exchangeChannels;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param remoteAddress <br>
     * @return <br>
     */
    public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress) {
        Channel channel = server.getChannel(remoteAddress);
        return HeaderExchangeChannel.getOrAddChannel(channel);
    }

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    public Collection<Channel> getChannels() {
        return (Collection) getExchangeChannels();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param remoteAddress <br>
     * @return <br>
     */
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return getExchangeChannel(remoteAddress);
    }

    public boolean isBound() {
        return server.isBound();
    }

    public InetSocketAddress getLocalAddress() {
        return server.getLocalAddress();
    }

    public URL getUrl() {
        return server.getUrl();
    }

    public ChannelHandler getChannelHandler() {
        return server.getChannelHandler();
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
        server.reset(url);
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
    public void reset(com.alibaba.dubbo.common.Parameters parameters) {
        reset(getUrl().addParameters(parameters.getParameters()));
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
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message
                + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        server.send(message);
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
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message
                + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        server.send(message, sent);
    }
}
