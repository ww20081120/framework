/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年5月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempSelection {

    /**
     * 匿名
     */
    private String alias;

    /**
     * 字段名
     */
    private String field;

    /**
     * 字段名2
     */
    private String field2;

    /**
     * 操作符
     */
    private Operator operator;
}
