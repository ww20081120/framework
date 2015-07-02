/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fccfc.framework.rpc.thrift;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.AtomicPositiveInteger;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.remoting.exchange.ResponseFuture;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.rpc.protocol.AbstractInvoker;
import com.alibaba.dubbo.rpc.protocol.dubbo.FutureAdapter;

/**
 * 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @param <T> <br>
 * @CreateDate 2015年7月2日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.rpc.thrift <br>
 */
public class ThriftRpcInvoker<T> extends AbstractInvoker<T> {

    /**
     * clients
     */
    private final ExchangeClient[]      clients;

    /**
     * index
     */
    private final AtomicPositiveInteger index = new AtomicPositiveInteger();

    /**
     * version
     */
    private final String                version;
    
    /**
     * destroyLock
     */
    private final ReentrantLock     destroyLock = new ReentrantLock();
    
    /**
     * invokers
     */
    private final Set<Invoker<?>> invokers;
    
    /**
     * ThriftRpcInvoker
     * @param serviceType <br>
     * @param url <br>
     * @param clients <br>
     * @param invokers <br>
     */
    public ThriftRpcInvoker(Class<T> serviceType, URL url, ExchangeClient[] clients, Set<Invoker<?>> invokers) {
        super(serviceType, url, new String[] {Constants.INTERFACE_KEY, Constants.GROUP_KEY, Constants.TOKEN_KEY, Constants.TIMEOUT_KEY});
        this.clients = clients;
        this.version = url.getParameter(Constants.VERSION_KEY, "2.0.0");
        this.invokers = invokers; 
    }
    
    /**
     * ThriftRpcInvoker
     * @param serviceType <br>
     * @param url <br>
     * @param clients <br>
     */
    public ThriftRpcInvoker(Class<T> serviceType, URL url, ExchangeClient[] clients) {
        this(serviceType, url, clients, null);
    }
    
    @Override
    protected Result doInvoke(final Invocation invocation) throws Throwable {
        RpcInvocation inv = new RpcInvocation(invocation);
        final String methodName = invocation.getMethodName();;
        inv.setAttachment(Constants.PATH_KEY, getUrl().getPath());
        inv.setAttachment(Constants.VERSION_KEY, version);
        
        ExchangeClient currentClient;
        if (clients.length == 1) {
            currentClient = clients[0];
        }
        else {
            currentClient = clients[index.getAndIncrement() % clients.length];
        }
        try {
            // 不可靠异步
            boolean isAsync = getUrl().getMethodParameter(methodName, Constants.ASYNC_KEY, false);
            int timeout = getUrl().getMethodParameter(methodName, Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
            if (isAsync) { 
                boolean isReturn = getUrl().getMethodParameter(methodName, Constants.RETURN_KEY, true);
                if (isReturn) {
                    ResponseFuture future = currentClient.request(inv, timeout);
                    RpcContext.getContext().setFuture(new FutureAdapter<Object>(future));
                }
                else {
                    boolean isSent = getUrl().getMethodParameter(methodName, Constants.SENT_KEY, false);
                    currentClient.send(inv, isSent);
                    RpcContext.getContext().setFuture(null);
                }
                return new RpcResult();
            }
            RpcContext.getContext().setFuture(null);
            return (Result) currentClient.request(inv, timeout).get();
        }
        catch (TimeoutException e) {
            throw new RpcException(RpcException.TIMEOUT_EXCEPTION, 
                "Failed to invoke remote invocation " + invocation + " to " + getUrl() + ", cause: " + e.getMessage(), e);
        }
        catch (RemotingException e) {
            throw new RpcException(RpcException.NETWORK_EXCEPTION, 
                "Failed to invoke remote invocation " + invocation + " to " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        for (ExchangeClient client : clients) {
            if (client.isConnected() && !client.hasAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY)) {
                //cannot write == not Available ?
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
    public void destroy() {
        //防止client被关闭多次.在connect per jvm的情况下，client.close方法会调用计数器-1，当计数器小于等于0的情况下，才真正关闭
        if (super.isDestroyed()) {
            return;
        } 
        else {
            //dubbo check ,避免多次关闭
            destroyLock.lock();
            try {
                if (super.isDestroyed()) {
                    return;
                }
                super.destroy();
                if (invokers != null) {
                    invokers.remove(this);
                }
                for (ExchangeClient client : clients) {
                    try {
                        client.close();
                    }
                    catch (Throwable t) {
                        logger.warn(t.getMessage(), t);
                    }
                }
                
            }
            finally {
                destroyLock.unlock();
            }
        }
    }
}