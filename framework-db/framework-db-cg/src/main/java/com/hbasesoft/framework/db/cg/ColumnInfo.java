/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.cg;

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
public class ColumnInfo {

    /** 字段编码 */
    private String dbCode;

    /** 代码 */
    private String code;

    /** 名称 */
    private String name;

    /** 注释 */
    private String comment;

    /** 字段类型 */
    private String type;

    /** java类型 */
    private String javaType;

    /** 长度 */
    private int length;

    /** 精度 */
    private int precision;

    /** 是否必填 */
    private boolean required;

    /** 是否为主建 */
    private boolean primaryKey;

    /** 默认值 */
    private String defaultValue;

    /** 值列表 */
    private String valueList;

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
