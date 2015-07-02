package com.fccfc.framework.rpc.thrift;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.Transporter;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.alibaba.dubbo.remoting.exchange.Exchangers;
import com.alibaba.dubbo.remoting.exchange.support.ExchangeHandlerAdapter;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.protocol.AbstractProtocol;
import com.fccfc.framework.rpc.thrift.common.TBaseTools;
import com.fccfc.framework.rpc.thrift.exchange.HeaderExchanger;
import com.fccfc.framework.rpc.thrift.netty.NettyTransporter;
//import com.alibaba.dubbo.rpc.protocol.dubbo.DubboExporter;
//import com.sodao.dubbo.thrift.codec.ThriftExchangeCodec;

/**
 * ThriftRpcProtocol
 * @author yankai
 * @date 2012-8-31
 */
public class ThriftRpcProtocol extends AbstractProtocol implements Protocol {

    /**
     * NAME
     */
    public static final String NAME = "thrift";

    /**
     * DEFAULT_PORT
     */
    public static final int DEFAULT_PORT = 28088;

    /**
     * lock
     */
    public final ReentrantLock lock = new ReentrantLock();

    /**
     * serverMap
     */
    private final Map<String, ExchangeServer> serverMap = new ConcurrentHashMap<String, ExchangeServer>();

    /**
     * referenceClientMap
     */
    private final Map<String, ReferenceCountExchangeClient> referenceClientMap = new ConcurrentHashMap<String, ReferenceCountExchangeClient>();

    /**
     * ghostClientMap
     */
    private final ConcurrentMap<String, LazyConnectExchangeClient> ghostClientMap = new ConcurrentHashMap<String, LazyConnectExchangeClient>();

    /**
     * requestHandler
     */
    private ExchangeHandler requestHandler = new ExchangeHandlerAdapter() {

        @Override
        public Object reply(ExchangeChannel channel, Object msg) throws RemotingException {

            if (msg instanceof ChannelBuffer) {
                ChannelBuffer buf = (ChannelBuffer) msg;
                String serviceName = channel.getUrl().getParameter(Constants.INTERFACE_KEY);
                String serviceKey = serviceKey(channel.getLocalAddress().getPort(), serviceName, null, null);
                ThriftRpcExporter<?> exporter = (ThriftRpcExporter<?>) exporterMap.get(serviceKey);
                if (exporter == null) {
                    throw new RemotingException(channel, "Not found exported service: " + serviceKey + " in "
                        + exporterMap.keySet() + ", may be version or group mismatch " + ", channel: consumer: "
                        + channel.getRemoteAddress() + " --> provider: " + channel.getLocalAddress() + ", message:"
                        + msg);
                }

                RpcContext.getContext().setRemoteAddress(channel.getRemoteAddress());
                String method = TBaseTools.getStringAtAbsoluteIndex(buf, 4);
                Invocation inv = new RpcInvocation(method, null, new Object[] {
                    buf
                });
                return exporter.getInvoker().invoke(inv);

            }

            throw new RemotingException(channel, "Unsupported request: " + (msg.getClass().getName() + ": " + msg)
                + ", channel: consumer: " + channel.getRemoteAddress() + " --> provider: " + channel.getLocalAddress());
        }

        @Override
        public void received(Channel channel, Object message) throws RemotingException {
            if (message instanceof Invocation) {
                reply((ExchangeChannel) channel, message);
            }
            else {
                super.received(channel, message);
            }
        }

    };

    /**
     * INSTANCE
     */
    private static ThriftRpcProtocol INSTANCE;

    /**
     * ThriftRpcProtocol
     */
    public ThriftRpcProtocol() {
        INSTANCE = this;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public static ThriftRpcProtocol getThriftRpcProtocol() {
        if (INSTANCE == null) {
            ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(ThriftRpcProtocol.NAME); // load
        }
        return INSTANCE;
    }

    public Collection<ExchangeServer> getServers() {
        return Collections.unmodifiableCollection(serverMap.values());
    }

    public Collection<Exporter<?>> getExporters() {
        return Collections.unmodifiableCollection(exporterMap.values());
    }

    Map<String, Exporter<?>> getExporterMap() {
        return exporterMap;
    }

    public Collection<Invoker<?>> getInvokers() {
        return Collections.unmodifiableCollection(invokers);
    }

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param <T> <br>
     * @param invoker <br>
     * @return <br>
     * @throws RpcException <br>
     */
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        URL url = invoker.getUrl();
        // export service.
        String key = serviceKey(url);
        ThriftRpcExporter<T> exporter = new ThriftRpcExporter<T>(invoker, key, exporterMap);
        exporterMap.put(key, exporter);

        openServer(url);

        return exporter;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     */
    private void openServer(URL url) {
        // find server.
        String key = url.getAddress();
        // client 也可以暴露一个只有server可以调用的服务。
        boolean isServer = url.getParameter(Constants.IS_SERVER_KEY, true);
        if (isServer) {
            ExchangeServer server = serverMap.get(key);
            if (server == null) {
                serverMap.put(key, getServer(url));
            }
            else {
                // server支持reset,配合override功能使用
                // server.reset(url);
                // 直接抛异常，目前thrift协议只支持一个端口上暴露一个服务，做检查
                throw new RpcException("Fail to open server(url: " + url
                    + "), thrift2 protocol only supported one service binded to one port!");
            }
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     * @return <br>
     */
    private ExchangeServer getServer(URL url) {
        // 指定自己的exchanger
        url = url.addParameterIfAbsent(Constants.EXCHANGER_KEY, HeaderExchanger.NAME);
        // server type setting
        url = url.addParameterIfAbsent(Constants.SERVER_KEY, NettyTransporter.NAME);
        // check server type
        String str = url.getParameter(Constants.SERVER_KEY, NettyTransporter.NAME);
        if (str != null && str.length() > 0 && !ExtensionLoader.getExtensionLoader(Transporter.class).hasExtension(str)) {
            throw new RpcException("Unsupported server type: " + str + ", url: " + url);
        }

        ExchangeServer server;
        try {
            server = Exchangers.bind(url, requestHandler);
        }
        catch (RemotingException e) {
            throw new RpcException("Fail to start server(url: " + url + ") " + e.getMessage(), e);
        }
        str = url.getParameter(Constants.CLIENT_KEY);
        if (str != null && str.length() > 0) {
            Set<String> supportedTypes = ExtensionLoader.getExtensionLoader(Transporter.class).getSupportedExtensions();
            if (!supportedTypes.contains(str)) {
                throw new RpcException("Unsupported client type: " + str);
            }
        }
        return server;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param serviceType <br>
     * @param url <br>
     * @param <T> <br>
     * @return <br>
     * @throws RpcException <br>
     */
    public <T> Invoker<T> refer(Class<T> serviceType, URL url) throws RpcException {
        // create rpc invoker.
        ThriftRpcInvoker<T> invoker = new ThriftRpcInvoker<T>(serviceType, url, getClients(url), invokers);
        invokers.add(invoker);
        return invoker;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     * @return <br>
     */
    private ExchangeClient[] getClients(URL url) {
        // 是否共享连接
        boolean service_share_connect = false;
        int connections = url.getParameter(Constants.CONNECTIONS_KEY, 0);
        // 如果connections不配置，则共享连接，否则每服务每连接
        if (connections == 0) {
            service_share_connect = true;
            connections = 1;
        }

        ExchangeClient[] clients = new ExchangeClient[connections];
        for (int i = 0; i < clients.length; i++) {
            if (service_share_connect) {
                clients[i] = getSharedClient(url);
            }
            else {
                clients[i] = initClient(url);
            }
        }
        return clients;
    }

    /**
     * 
     * Description:获取共享连接 <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     * @return <br>
     */
    private ExchangeClient getSharedClient(URL url) {
        String key = url.getAddress();
        ReferenceCountExchangeClient client = referenceClientMap.get(key);
        if (client != null) {
            if (!client.isClosed()) {
                client.incrementAndGetCount();
                return client;
            }
            else {
                // logger.warn(new IllegalStateException("client is closed,but stay in clientmap .client :"+ client));
                referenceClientMap.remove(key);
            }
        }
        ExchangeClient exchagneclient = initClient(url);

        client = new ReferenceCountExchangeClient(exchagneclient, ghostClientMap);
        referenceClientMap.put(key, client);
        ghostClientMap.remove(key);
        return client;
    }

    /**
     * 
     * Description:创建新连接. <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param url <br>
     * @return <br>
     */
    private ExchangeClient initClient(URL url) {
        // 指定自己的exchanger
        url = url.addParameterIfAbsent(Constants.EXCHANGER_KEY, HeaderExchanger.NAME);
        // client type setting.
        url = url.addParameterIfAbsent(Constants.CLIENT_KEY,
            url.getParameter(Constants.SERVER_KEY, NettyTransporter.NAME));
        // check client type
        String str = url.getParameter(Constants.CLIENT_KEY,
            url.getParameter(Constants.SERVER_KEY, NettyTransporter.NAME));
        // BIO存在严重性能问题，暂时不允许使用
        if (str != null && str.length() > 0 && !ExtensionLoader.getExtensionLoader(Transporter.class).hasExtension(str)) {
            throw new RpcException("Unsupported client type: " + str + "," + " supported client type is "
                + StringUtils.join(ExtensionLoader.getExtensionLoader(Transporter.class).getSupportedExtensions(), " "));
        }

        ExchangeClient client;
        try {
            // 设置连接应该是lazy的
            if (url.getParameter(Constants.LAZY_CONNECT_KEY, false)) {
                client = new LazyConnectExchangeClient(url, requestHandler);
            }
            else {
                client = Exchangers.connect(url, requestHandler);
            }
        }
        catch (RemotingException e) {
            throw new RpcException("Fail to create remoting client for service(" + url + "): " + e.getMessage(), e);
        }
        return client;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    public void destroy() {
        super.destroy();
        for (String key : new ArrayList<String>(serverMap.keySet())) {
            ExchangeServer server = serverMap.remove(key);
            if (server != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Close dubbo server: " + server.getLocalAddress());
                    }
                    server.close(getServerShutdownTimeout());
                }
                catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }

        for (String key : new ArrayList<String>(referenceClientMap.keySet())) {
            ExchangeClient client = referenceClientMap.remove(key);
            if (client != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Close dubbo connect: " + client.getLocalAddress() + "-->"
                            + client.getRemoteAddress());
                    }
                    client.close();
                }
                catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }

        for (String key : new ArrayList<String>(ghostClientMap.keySet())) {
            ExchangeClient client = ghostClientMap.remove(key);
            if (client != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Close dubbo connect: " + client.getLocalAddress() + "-->"
                            + client.getRemoteAddress());
                    }
                    client.close();
                }
                catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
    }
}