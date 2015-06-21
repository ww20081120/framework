/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.rpc.thrift.exchange;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.remoting.exchange.ExchangeHandler;
import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.alibaba.dubbo.remoting.exchange.Exchanger;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年5月19日 <br>
 * @see com.fccfc.framework.rpc.thrift.exchange <br>
 */
public class HeaderExchanger implements Exchanger {

    public static final String NAME = "header";

    /**
     * 默认构造函数
     */
    public HeaderExchanger() {
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.Exchanger#bind(com.alibaba.dubbo.common.URL,
     *      com.alibaba.dubbo.remoting.exchange.ExchangeHandler)
     */
    @Override
    public ExchangeServer bind(URL url, ExchangeHandler handler) throws RemotingException {
        return null;
    }

    /**
     * @see com.alibaba.dubbo.remoting.exchange.Exchanger#connect(com.alibaba.dubbo.common.URL,
     *      com.alibaba.dubbo.remoting.exchange.ExchangeHandler)
     */
    @Override
    public ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException {
        return null;
    }

}
