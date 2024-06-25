/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.wrapper;

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
    /** between and */
    BETWEEN,

    /** equal to */
    EQ,

    /** greater than or equal to */
    GE,

    /** greater than */
    GT,

    /** in */
    IN,

    /** is null */
    ISNULL,

    /** Less than or equal to */
    LE,

    /** like */
    LIKE,

    /** Less than */
    LT,

    /** not equal to */
    NE,

    /** not in */
    NOTIN,

    /** not like */
    NOTLIKE,

    /** not null */
    NOTNULL;
}
