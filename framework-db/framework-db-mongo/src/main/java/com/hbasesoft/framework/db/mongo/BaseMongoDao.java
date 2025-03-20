/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongo;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.criteria.AbstractQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.utils.PagerList;


/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @param <T> T
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate <br>
 */
public interface BaseMongoDao<T extends BaseEntity> extends BaseDao<T> {

     /**
      *
      * @param wrapper
      */
     void updateBySpecification(LambdaUpdateWrapper<?> wrapper);

     /**
      *
      * @param wrapper
      */
     void updateBySpecification(UpdateWrapper<T> wrapper);

     /**
      *
      * @param wrapper
      * @param pi
      * @param ps
      * @param clazz
      * @return <M>
      * @param <M>
      */
     <M> PagerList<M> queryPagerBySpecification(AbstractQueryWrapper<T> wrapper, int pi,
                                                int ps, Class<M> clazz);
}
