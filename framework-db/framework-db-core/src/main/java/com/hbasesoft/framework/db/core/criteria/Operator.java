/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.criteria;

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
enum Operator {
    /** 求平均值 */
    AVG,

    /** between and */
    BETWEEN,

    /** 计算 */
    COUNT,

    /** 相减 */
    DIFF,

    /** equal to */
    EQ,

    /** 字段 */
    FIELD,

    /** greater than or equal to */
    GE,

    /** greater than */
    GREATER_THAN,

    /** greater than or equal to */
    GREATER_THAN_OR_EQUAL_TO,

    /** greater than */
    GT,

    /** in */
    IN,

    /** is null */
    ISNULL,

    /** Less than or equal to */
    LE,

    /** Less than */
    LESS_THAN,

    /** Less than or equal to */
    LESS_THAN_OR_EQUAL_TO,

    /** like */
    LIKE,

    /** Less than */
    LT,

    /** 最大值 */
    MAX,

    /** 最小值 */
    MIN,

    /** not equal to */
    NE,

    /** not in */
    NOTIN,

    /** not like */
    NOTLIKE,

    /** not null */
    NOTNULL,

    /** 求和 */
    SUM,

    /** 求和 */
    SUMMING;
}
