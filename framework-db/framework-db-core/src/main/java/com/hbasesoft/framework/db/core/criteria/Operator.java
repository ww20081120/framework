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
    /** equal to */
    EQ,

    /** like */
    LIKE,

    /** not like */
    NE,

    /** greater than or equal to */
    GE,

    /** greater than */
    GT,

    /** Less than or equal to */
    LE,

    /** Less than */
    LT,

    /** in */
    IN,

    /** not like */
    NOTLIKE,

    /** between and */
    BETWEEN,

    /** not in */
    NOTIN,

    /** is null */
    ISNULL,

    /** not null */
    NOTNULL;
}
