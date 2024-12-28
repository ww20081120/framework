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
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年6月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
public abstract class AbstractQueryWrapper<T> extends AbstractWrapper<T> {

    /**
     * 分组条件
     */
    private List<String> groupBy = new ArrayList<>();

    /**
     * 排序
     */
    private List<OrderBy> orderByList = new ArrayList<>();

    /**
     * 查询类型
     */
    private List<TempSelection> selectionList = new ArrayList<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<String> getGroupList() {
        return this.groupBy;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<OrderBy> getOrderByList() {
        return this.orderByList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<TempSelection> getSelectionList() {
        return this.selectionList;
    }
}
