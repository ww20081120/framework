/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.rocketmq;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventLinsener;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.ability.oauth.server.event <br>
 */
@Component
public class RocketmqEventLinsener implements EventLinsener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    @Override
    public void onMessage(final String channel, final byte[] data) {
        System.out.println(channel);
        try {
            System.out.println(new String(data, "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        // EventLinsener.super.onMessage(channel, data);
    }

    // @Override
    // public void onSubscribe(String channel, int subscribeChannels) {
    // // TODO Auto-generated method stub
    // EventLinsener.super.onSubscribe(channel, subscribeChannels);
    // }

    /**
     * @Title: events @author 大刘杰 @Description: TODO @param @return @return @throws
     */
    @Override
    public String[] events() {
        return new String[] {
            "topic1"
        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean subscriber() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @Title: onEmmit @author 大刘杰 @Description: TODO @param @param event @param @param data @return @throws
     */
    @Override
    public void onEmmit(final String event, final EventData data) {
        // TODO Auto-generated method stub

    }

}
