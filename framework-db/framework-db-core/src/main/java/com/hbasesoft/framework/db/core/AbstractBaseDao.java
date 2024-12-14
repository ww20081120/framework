/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import java.lang.reflect.Field;
import java.util.List;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.db.core.utils.PagerList;

import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
public abstract class AbstractBaseDao<T extends BaseEntity> implements BaseDao<T> {

    /** entity class */
    private Class<T> entityClazz;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClazz <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = (Class<T>) entityClazz;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void deleteBySpecification(final CriterialDeleteSpecification<T> specification) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaDelete<T> query = cb.createCriteriaDelete(getEntityClazz());
        Root root = query.from(getEntityClazz());
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "删除条件");
        specification.toPredicate(root, query, cb);
        deleteByCriteria(query);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public T getBySpecification(final CriterialQuerySpecification<T> specification) {
        return getBySpecification(specification, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> queryBySpecification(final CriterialQuerySpecification<T> specification) {
        return queryBySpecification(specification, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @param pi
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPagerBySpecification(final CriterialQuerySpecification<T> specification, final int pi,
        final int pageSize) {
        return queryPagerBySpecification(specification, pi, pageSize, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void updateBySpecification(final CriterialUpdateSpecification<T> specification) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaUpdate<T> query = cb.createCriteriaUpdate(getEntityClazz());
        Root root = query.from(getEntityClazz());
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "修改条件");
        specification.toPredicate(root, query, cb);
        updateByCriteria(query);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected Class<T> getEntityClazz() {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        return entityClazz;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @return <br>
     */
    protected Field findPrimaryKeyField(final Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true); // 解除私有访问限制
                return field;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            return findPrimaryKeyField(superClass);
        }
        return null;
    }
}
