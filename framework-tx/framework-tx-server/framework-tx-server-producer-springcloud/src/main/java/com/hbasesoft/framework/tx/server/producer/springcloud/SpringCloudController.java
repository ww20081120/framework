/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.producer.springcloud;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.tx.core.TxProducer;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 1, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server.producer.springcloud <br>
 */
@RestController
@RequestMapping("/framework/tx")
public class SpringCloudController {

    @Resource
    private TxProducer txProducer;

    @GetMapping("/{id}")
    public boolean containClient(@PathVariable("id") String id) {
        return txProducer.containClient(id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @PostMapping
    public void registClient(@RequestBody ClientInfo clientInfo) {
        txProducer.registClient(clientInfo);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @DeleteMapping("/{id}")
    public void removeClient(@PathVariable("id") String id) {
        txProducer.removeClient(id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    @GetMapping("/{id}/{mark}")
    public CheckInfo registMsg(@PathVariable("id") String id, @PathVariable("mark") String mark) {
        return txProducer.registMsg(id, mark);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    @PutMapping
    public void saveResult(@RequestBody CheckInfo checkInfo) {
        txProducer.saveResult(checkInfo);
    }

}
