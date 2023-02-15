/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.tx.core.TxConsumer;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

/**
 * <Description> 维护的工具<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate May 18, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server <br>
 */
@RestController("/txOperation")
public class TxOperationController {

    /**
     * txConsumer
     */
    private static TxConsumer txConsumer;

    /** txStorage */
    @Resource
    private TxStorage txStorage;

    /**
     * Description: 增加client<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo client信息
     * @return <br>
     */
    @PutMapping("/clientInfo")
    public ClientInfo addClient(@RequestBody final ClientInfo clientInfo) {
        txStorage.saveClientInfo(clientInfo);
        return clientInfo;
    }

    /**
     * Description: 取消<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @PutMapping("/clientInfo/{id}")
    public void delClient(@PathVariable("id") final String id) {
        txStorage.delete(id);
    }

    /**
     * Description: 增加checkinfo<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo check信息
     * @return <br>
     */
    @PutMapping("/checkInfo")
    public CheckInfo addCheckInfo(@RequestBody final CheckInfo checkInfo) {
        txStorage.saveCheckInfo(checkInfo);
        return checkInfo;
    }

    /**
     * Description: 取消CheckInfo<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     * @param mark
     */
    @PutMapping("/checkInfo/{id}/{mark}")
    public void delCheckInfo(@PathVariable("id") final String id, @PathVariable("mark") final String mark) {
        txStorage.deleteCheckInfo(id, mark);
    }

    /**
     * Description: 重试 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id 重新的id
     * @return <br>
     */
    @GetMapping("/retry/{id}")
    public ClientInfo retry(@PathVariable("id") final String id) {
        ClientInfo clientInfo = txStorage.getClientInfo(id);
        if (clientInfo != null) {
            if (!getConsumer().retry(clientInfo)) {
                txStorage.updateClientRetryTimes(id);
            }
        }
        return clientInfo;
    }

    /**
     * Description: 更新参数 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id 标识
     * @param index 参数位置
     * @param type 参数的类型
     * @param data 参数的数据
     * @return <br>
     */
    @PatchMapping("/clientInfo/{id}/{index}/{type}")
    public ClientInfo updateClientArgs(@PathVariable("id") final String id, @PathVariable("index") final Integer index,
        @PathVariable("type") final String type, @RequestBody final String data) {

        ClientInfo clientInfo = txStorage.getClientInfo(id);
        if (clientInfo != null) {
            byte[] args = ArgsSerializationUtil.updateArg(clientInfo.getArgs(), index, type, data);
            clientInfo.setArgs(args);
            txStorage.updateClientinfo(clientInfo);
        }
        return clientInfo;
    }

    /**
     * Description: 更新checkinfo的结果<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id 标识
     * @param mark mark标志
     * @param type 参数的类型
     * @param data 参数的数据
     * @return <br>
     */
    @PatchMapping("/checkInfo/{id}/{mark}/{type}")
    public CheckInfo updateCheckInfo(@PathVariable("id") final String id, @PathVariable("mark") final String mark,
        @PathVariable("type") final String type, @RequestBody final String data) {
        CheckInfo checkInfo = txStorage.getCheckInfo(id, mark);
        if (checkInfo != null) {
            checkInfo.setResult(SerializationUtil.jdkSerial(ArgsSerializationUtil.parseObj(type, data)));
            txStorage.updateCheckInfo(checkInfo);
        }
        return checkInfo;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private static TxConsumer getConsumer() {
        if (txConsumer == null) {
            ServiceLoader<TxConsumer> serviceLoader = ServiceLoader.load(TxConsumer.class);
            Iterator<TxConsumer> iterator = serviceLoader.iterator();
            if (iterator.hasNext()) {
                txConsumer = iterator.next();
            }
        }
        return txConsumer;
    }
}
