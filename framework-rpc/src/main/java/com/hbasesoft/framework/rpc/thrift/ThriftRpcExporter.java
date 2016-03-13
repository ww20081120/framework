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
package com.hbasesoft.framework.rpc.thrift;

import java.util.Map;

import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.protocol.AbstractExporter;

/**
 * InjvmExporter
 * @param <T> <br>
 * @author william.liangf
 */
public class ThriftRpcExporter<T> extends AbstractExporter<T> {

    /**
     * key
     */
    private final String key;

    /**
     * exporterMap
     */
    private final Map<String, Exporter<?>> exporterMap;

    /**
     * ThriftRpcExporter
     * @param invoker <br>
     * @param key <br>
     * @param exporterMap <br>
     */
    public ThriftRpcExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    @Override
    public void unexport() {
        super.unexport();
        exporterMap.remove(key);
    }
}