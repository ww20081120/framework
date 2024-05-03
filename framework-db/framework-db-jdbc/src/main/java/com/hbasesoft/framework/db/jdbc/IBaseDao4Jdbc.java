/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jdbc;

import java.util.Collection;

import com.hbasesoft.framework.db.core.BaseDto;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @param <T> T
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate <br>
 */
public interface IBaseDao4Jdbc<T extends BaseDto> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param objcts
     * @param commitNumber
     * @throws DaoException <br>
     * @return int[]
     */
    int[] batchExecute(String sql, Collection<T> objcts, int commitNumber);
}
