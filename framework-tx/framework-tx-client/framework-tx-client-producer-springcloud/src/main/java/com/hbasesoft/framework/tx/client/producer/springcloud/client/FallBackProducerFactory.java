/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.producer.springcloud.client;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

import feign.hystrix.FallbackFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 1, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.client.producer.springcloud.client <br>
 */
@Component
public class FallBackProducerFactory implements FallbackFactory<FeginProducer> {

    /** logger */
    private static final Logger LOGGER = new Logger("tx");

    /** producer */
    private static final FeginProducer PRODUCER = new FeginProducer() {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param clientInfo <br>
         */
        @Override
        public boolean registClient(ClientInfo clientInfo) {
            LOGGER.warn("registClient|" + JSONObject.toJSONString(clientInfo));
            return false;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param id <br>
         */
        @Override
        public void removeClient(String id) {
            LOGGER.warn("removeClient|" + id);
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
        @Override
        public CheckInfo check(String id, String mark) {
            LOGGER.warn("registMsg|" + id + "|" + mark);
            return null;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param checkInfo <br>
         */
        @Override
        public void saveResult(CheckInfo checkInfo) {
            LOGGER.warn("saveResult|" + JSONObject.toJSONString(checkInfo));
        }

        @Override
        public boolean containClient(String id) {
            LOGGER.warn("containClient|" + id);
            return false;
        }

    };

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param cause
     * @return <br>
     */
    @Override
    public FeginProducer create(Throwable cause) {
        return FallBackProducerFactory.PRODUCER;
    }

}
