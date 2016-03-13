package com.hbasesoft.framework.rpc.thrift.proxy;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import com.hbasesoft.framework.rpc.thrift.common.TBaseTools;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * ThriftProxyFactory
 * @author yankai
 * @date 2012-3-28
 */
public class ThriftProxyFactory extends AbstractProxyFactory {

    /**
     * logger
     */
    private static Logger logger = new Logger(ThriftProxyFactory.class);

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param invoker <br>
     * @param <T> <br>
     * @param interfaces <br>
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param proxy <br>
     * @param <T> <br>
     * @param type <br>
     * @param url <br>
     * @return <br>
     */
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {

        final TProcessor processor = getTProcessor(type, proxy);

        return new AbstractProxyInvoker<T>(proxy, type, url) {

            @Override
            protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
                throws Throwable {

                ChannelBuffer input = (ChannelBuffer) arguments[arguments.length - 1];
                ChannelBuffer output = ChannelBuffers.dynamicBuffer();
                TProtocol prot = TBaseTools.newProtocol(input, output);
                try {
                    processor.process(prot, prot);
                }
                catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                    input.resetReaderIndex();
                    TMessage tmessage = prot.readMessageBegin();
                    TBaseTools.createErrorTMessage(prot, tmessage.name, tmessage.seqid,
                        "Server-Side Error:" + t.toString());
                }
                finally {
                    prot.getTransport().flush();
                }
                return output;
            }
        };
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param serviceIface <br>
     * @param serviceImpl <br>
     * @return <br>
     */
    final TProcessor getTProcessor(Class<?> serviceIface, Object serviceImpl) {
        try {
            if (serviceImpl == null) {
                throw new IllegalStateException("serviceImpl is null, can not create TProcessor");
            }
            TProcessor processor = serviceClazz2Processor.get(serviceIface);
            if (processor == null) {
                String iface = serviceIface.getName();
                String processorServiceName = iface.substring(0, iface.lastIndexOf("$")) + "$Processor";
                Class<?> proServiceClazz = Class.forName(processorServiceName);
                processor = (TProcessor) proServiceClazz.getConstructor(serviceIface).newInstance(serviceImpl);
                serviceClazz2Processor.putIfAbsent(serviceIface, processor);
            }
            return processor;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * serviceClazz2Processor
     */
    static ConcurrentHashMap<Class<?>, TProcessor> serviceClazz2Processor = new ConcurrentHashMap<Class<?>, TProcessor>();
}
