/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.rpc.thrift.exchange;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.LogUtil;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.remoting.exchange.ResponseFuture;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
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
public class DefaultFuture implements ResponseFuture {

    private static final Map<Integer, Channel> CHANNELS = new ConcurrentHashMap<Integer, Channel>();

    private static final Map<Integer, DefaultFuture> FUTURES = new ConcurrentHashMap<Integer, DefaultFuture>();

    // add new TMessageType instead of Response.CLIENT_TIMEOUT, Response.SERVER_TIMEOUT
    private static final byte T_CLIENT_TIMEOUT = 127;

    private static final byte T_SERVER_TIMEOUT = 126;

    private static Logger logger = new Logger(DefaultFuture.class);

    private final int id;

    private final Channel channel;

    private final int timeout;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private final long start = System.currentTimeMillis();

    private final String serviceName;

    private final String methodName;

    private volatile long sent;

    private volatile ChannelBuffer response;

    /**
     * 默认构造函数
     */
    public DefaultFuture(int id, Channel channel, int timeout, String serviceName, String methodName) {
        this.channel = channel;
        this.id = id;
        this.timeout = timeout > 0 ? timeout : channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY,
            Constants.DEFAULT_TIMEOUT);
        this.serviceName = serviceName;
        this.methodName = methodName;
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }

    public Object get() throws RemotingException {
        return get(timeout);
    }

    public Object get(int timeout) throws RemotingException {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                lock.unlock();
            }
            if (!isDone()) {
                throw new TimeoutException(sent > 0, channel, getTimeoutMessage(false));
            }
        }
        return returnFromResponse();
    }

    public void cancel() {
        FUTURES.remove(id);
        CHANNELS.remove(id);
    }

    public boolean isDone() {
        return response != null;
    }

    private Object returnFromResponse() throws RemotingException {
        ChannelBuffer res = response;
        try {
            TProtocol iprot = TBaseTools.newProtocol(res, null);
            TMessage msg = iprot.readMessageBegin();
            if (msg.type == TMessageType.EXCEPTION) {
                TApplicationException x = TApplicationException.read(iprot);
                iprot.readMessageEnd();
                throw x;
            }
            if (msg.type == T_CLIENT_TIMEOUT || msg.type == T_SERVER_TIMEOUT) {
                throw new TimeoutException(msg.type == T_SERVER_TIMEOUT, channel, getTimeoutMessage(true));
            }
            if (msg.seqid != id) {
                throw new TApplicationException(TApplicationException.BAD_SEQUENCE_ID, methodName
                    + " failed: out of sequence response");
            }
            Class<?> clazz = TBaseTools.getTBaseClass(serviceName, methodName, "_result");
            TBase<?, ?> _result = TBaseTools.getTBaseObject(clazz, null, null);
            _result.read(iprot);
            Object value = TBaseTools.getResult(_result);
            RpcResult result = new RpcResult(value);
            return result;
        }
        catch (Exception e) {
            throw new RpcException(e);
        }
    }

    public static void sent(Channel channel, Object message) {
        ChannelBuffer buf = (ChannelBuffer) message;
        int id = TBaseTools.getTMessageId(buf);
        DefaultFuture future = FUTURES.get(id);
        if (future != null) {
            future.doSent();
        }
    }

    private void doSent() {
        sent = System.currentTimeMillis();
    }

    public static void received(Channel channel, ChannelBuffer response) {
        int id = TBaseTools.getTMessageId(response);
        try {
            DefaultFuture future = FUTURES.remove(id);
            if (future != null) {
                future.doReceived(response);
            }
            else {
                logger.warn("The timeout response finally returned at "
                    + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                    + ", response "
                    + response
                    + (channel == null ? "" : ", channel: " + channel.getLocalAddress() + " -> "
                        + channel.getRemoteAddress()));
            }
        }
        finally {
            CHANNELS.remove(id);
        }
    }

    private void doReceived(ChannelBuffer res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                done.signal();
            }
        }
        finally {
            lock.unlock();
        }
    }

    private String getTimeoutMessage(boolean scan) {
        long nowTimestamp = System.currentTimeMillis();
        return (sent > 0 ? "Waiting server-side response timeout" : "Sending request timeout in client-side")
            + (scan ? " by scan timer" : "")
            + ". start time: "
            + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(start)))
            + ", end time: "
            + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
            + ","
            + (sent > 0 ? " client elapsed: " + (sent - start) + " ms, server elapsed: " + (nowTimestamp - sent)
                : " elapsed: " + (nowTimestamp - start)) + " ms, timeout: " + timeout + " ms, channel: "
            + channel.getLocalAddress() + " -> " + channel.getRemoteAddress();
    }

    @Override
    public void setCallback(ResponseCallback callback) {
        // not supported callback
    }

    private int getId() {
        return id;
    }

    private Channel getChannel() {
        return channel;
    }

    private boolean isSent() {
        return sent > 0;
    }

    private int getTimeout() {
        return timeout;
    }

    private long getStartTimestamp() {
        return start;
    }

    public static boolean hasFuture(Channel channel) {
        return CHANNELS.containsValue(channel);
    }

    public static DefaultFuture getFuture(int id) {
        return FUTURES.get(id);
    }

    public String getMethod() {
        return methodName;
    }

    private static class RemotingInvocationTimeoutScan implements Runnable {

        public void run() {
            while (true) {
                try {
                    Thread.sleep(30);

                    for (DefaultFuture future : FUTURES.values()) {
                        if (future == null || future.isDone()) {
                            continue;
                        }
                        if (System.currentTimeMillis() - future.getStartTimestamp() > future.getTimeout()) {
                            // 这里只需要装TMessage就够了
                            ChannelBuffer timeoutResponse = ChannelBuffers.dynamicBuffer(64);
                            TProtocol prot = TBaseTools.newProtocol(null, timeoutResponse);
                            byte type = future.isSent() ? T_SERVER_TIMEOUT : T_CLIENT_TIMEOUT;
                            TMessage tmessage = new TMessage(future.getMethod(), type, future.getId());
                            prot.writeMessageBegin(tmessage);
                            DefaultFuture.received(future.getChannel(), timeoutResponse);
                        }
                    }
                }
                catch (Throwable e) {
                    logger.error("Exception when scan the timeout invocation of remoting.", e);
                }
            }
        }
    }

    static {
        Thread th = new Thread(new RemotingInvocationTimeoutScan(), "DubboResponseTimeoutScanTimer");
        th.setDaemon(true);
        th.start();
    }
}
