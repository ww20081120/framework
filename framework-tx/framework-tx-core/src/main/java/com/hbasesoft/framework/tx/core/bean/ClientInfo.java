/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core.bean;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 10, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core <br>
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ClientInfo implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -1186576125746340794L;

    private final String id;

    private final String mark;

    private Map<String, String> context;

    private Object[] args;

    private int maxRetryTimes;

    private String retryConfigs;
}
