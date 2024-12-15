/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年5月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
public abstract class AbstractWrapper<T> {

    /**
     * 临时过滤条件-复杂的or的时候用的
     */
    private List<List<TempPredicate>> orTempPredicates = new ArrayList<>();

    /**
     * 临时过滤条件
     */
    private List<TempPredicate> tempPredicates = new ArrayList<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<List<TempPredicate>> getOrTempPredicates() {
        return this.orTempPredicates;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<TempPredicate> getTempPredicates() {
        return this.tempPredicates;
    }

}
