/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.cg;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年4月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.cg.util <br>
 */
@Data
public class TableInfo {

    /** 表名称 */
    private String tableCode;

    /** 表代码 */
    private String code;

    /** 表名称 */
    private String name;

    /** 表注释 */
    private String comment;

    /** 列表 */
    private List<ColumnInfo> columnInfos;

    /**
     * toString
     * 
     * @see java.lang.Object#toString()
     * @return <br>
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
