/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core.vo;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * <Description> 基础传输基类<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年10月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.roach.api <br>
 */
public class AbstractVo implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -9032120721388401776L;

    /**
     * toString
     * 
     * @see java.lang.Object#toString()
     * @return <br>
     */
    @Override
    public String toString() {
        return JSONObject.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }

}
