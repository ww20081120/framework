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
 * @author yankai
 * @date 2012-8-27
 */
public class HeaderExchangeServer implements ExchangeServer {

    private static Logger logger = new Logger(HeaderExchangeServer.class);

    private final Server server;

    private volatile boolean closed = false;

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

    private boolean isRunning() {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (DefaultFuture.hasFuture(channel)) {
                return true;
            }
        }
        return false;
    }

    public void close() {
        server.close();
    }

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

    private void doClose() {
        if (closed) {
            return;
        }
        closed = true;
    }

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

    public void reset(URL url) {
        server.reset(url);
    }

    @Deprecated
    public void reset(com.alibaba.dubbo.common.Parameters parameters) {
        reset(getUrl().addParameters(parameters.getParameters()));
    }

    public void send(Object message) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message
                + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        server.send(message);
    }

    public void send(Object message, boolean sent) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message
                + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        server.send(message, sent);
    }
}
