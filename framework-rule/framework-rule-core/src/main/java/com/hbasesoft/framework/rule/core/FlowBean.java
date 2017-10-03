/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.utils.date.DateConstants;

/**
 * <Description> 业务流程bean<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core <br>
 */
public class FlowBean implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 9000900333240002476L;

    private String transId;

    public String getTransId() {
        return this.transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return JSONObject.toJSONStringWithDateFormat(this, DateConstants.DATETIME_FORMAT_19);
    }
}
