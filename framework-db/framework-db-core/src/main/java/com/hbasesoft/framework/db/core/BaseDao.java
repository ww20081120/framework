/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.utils.PagerList;

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
public interface BaseDao<T extends IBaseEntity> extends ISqlExcutor {

	/**
	 * <Description> <br>
	 * 
	 * @param <T> 参数类型
	 * @author ww200<br>
	 * @version 1.0<br>
	 * @taskId <br>
	 * @CreateDate 2024年5月6日 <br>
	 * @since V1.0<br>
	 * @see com.hbasesoft.framework.db.hibernate <br>
	 */
	@FunctionalInterface
	interface DeleteSpecification<T> {

		/**
		 * Description: <br>
		 * 
		 * @author 王伟<br>
		 * @taskId <br>
		 * @param wrapper
		 * @return <br>
		 */
		DeleteWrapper<T> toSpecification(DeleteWrapper<T> wrapper);
	}

	/**
	 * <Description> <br>
	 * 
	 * @param <T> 参数类型
	 * @author ww200<br>
	 * @version 1.0<br>
	 * @taskId <br>
	 * @CreateDate 2024年5月6日 <br>
	 * @since V1.0<br>
	 * @see com.hbasesoft.framework.db.hibernate <br>
	 */
	@FunctionalInterface
	interface LambdaDeleteSpecification<T> {

		/**
		 * Description: <br>
		 * 
		 * @author 王伟<br>
		 * @taskId <br>
		 * @param wrapper
		 * @return <br>
		 */
		LambdaDeleteWrapper<T> toSpecification(LambdaDeleteWrapper<T> wrapper);
	}

	/**
	 * <Description> <br>
	 * 
	 * @param <T> 参数类型
	 * @param <M> 返参类型
	 * @author ww200<br>
	 * @version 1.0<br>
	 * @taskId <br>
	 * @CreateDate 2024年5月6日 <br>
	 * @since V1.0<br>
	 * @see com.hbasesoft.framework.db.hibernate <br>
	 */
	@FunctionalInterface
	interface LambdaQuerySpecification<T, M> {

		/**
		 * Description: <br>
		 * 
		 * @author 王伟<br>
		 * @taskId <br>
		 * @param wrapper
		 * @return <br>
		 */
		LambdaQueryWrapper<T, M> toSpecification(LambdaQueryWrapper<T, M> wrapper);
	}

	/**
	 * <Description> <br>
	 * 
	 * @param <T> 参数类型
	 * @author ww200<br>
	 * @version 1.0<br>
	 * @taskId <br>
	 * @CreateDate 2024年5月6日 <br>
	 * @since V1.0<br>
	 * @see com.hbasesoft.framework.db.hibernate <br>
	 */
	@FunctionalInterface
	interface LambdaUpdateSpecification<T> {

		/**
		 * Description: <br>
		 * 
		 * @author 王伟<br>
		 * @taskId <br>
		 * @param wrapper
		 * @return <br>
		 */
		LambdaUpdateWrapper<T> toSpecification(LambdaUpdateWrapper<T> wrapper);
	}

	/**
	 * <Description> <br>
	 * 
	 * @param <T> 参数类型
	 * @author ww200<br>
	 * @version 1.0<br>
	 * @taskId <br>
	 * @CreateDate 2024年5月6日 <br>
	 * @since V1.0<br>
	 * @see com.hbasesoft.framework.db.hibernate <br>
	 */
	@FunctionalInterface
	interface QuerySpecification<T> {

		/**
		 * Description: <br>
		 * 
		 * @author 王伟<br>
		 * @taskId <br>
		 * @param wrapper
		 * @return <br>
		 */
		QueryWrapper<T> toSpecification(QueryWrapper<T> wrapper);
	}

	/**
	 * <Description> <br>
	 * 
	 * @param <T> 参数类型
	 * @author ww200<br>
	 * @version 1.0<br>
	 * @taskId <br>
	 * @CreateDate 2024年5月6日 <br>
	 * @since V1.0<br>
	 * @see com.hbasesoft.framework.db.hibernate <br>
	 */
	@FunctionalInterface
	interface UpdateSpecification<T> {

		/**
		 * Description: <br>
		 * 
		 * @author 王伟<br>
		 * @taskId <br>
		 * @param wrapper
		 * @return <br>
		 */
		UpdateWrapper<T> toSpecification(UpdateWrapper<T> wrapper);
	}

	/**
	 * Description: 根据条件删除<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification <br>
	 */
	void delete(DeleteSpecification<T> specification);

	/**
	 * Description: 删除数据<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param entity <br>
	 */
	void delete(T entity);

	/**
	 * Description: 批量删除<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param entities <br>
	 */
	void deleteBatch(Collection<T> entities);

	/**
	 * Description: 根据id来删除<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param id <br>
	 */
	void deleteById(Serializable id);

	/**
	 * Description: 根据id批量删除<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param ids <br>
	 */
	void deleteByIds(Collection<? extends Serializable> ids);

	/**
	 * Description: 根据条件删除<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification <br>
	 */
	void deleteByLambda(LambdaDeleteSpecification<T> specification);

	/**
	 * Description: 根据条件删除<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper <br>
	 */
	void deleteByLambda(LambdaDeleteWrapper<T> wrapper);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @return <br>
	 */
	T get(QuerySpecification<T> specification);

	/**
	 * Description: 根据条件查询<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param <M>
	 * @param specification
	 * @param clazz
	 * @return <br>
	 */
	<M> M get(QuerySpecification<T> specification, Class<M> clazz);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @return <br>
	 */
	T get(QueryWrapper<T> wrapper);

	/**
	 * Description: 根据条件查询<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param <M>
	 * @param wrapper
	 * @param clazz
	 * @return <br>
	 */
	<M> M get(QueryWrapper<T> wrapper, Class<M> clazz);

	/**
	 * Description: 根据id来获取数据<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param id
	 * @return <br>
	 */
	T get(Serializable id);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @param clazz
	 * @param <M>
	 * @return <br>
	 */
	<M> M getByLambda(LambdaQuerySpecification<T, M> specification, Class<M> clazz);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @return <br>
	 */
	T getByLambda(LambdaQuerySpecification<T, T> specification);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @param clazz
	 * @param <M>
	 * @return <br>
	 */
	<M> M getByLambda(LambdaQueryWrapper<T, M> wrapper, Class<M> clazz);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @return <br>
	 */
	T getByLambda(LambdaQueryWrapper<T, T> wrapper);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @return <br>
	 */
	List<T> query(QuerySpecification<T> specification);

	/**
	 * Description:根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param <M>
	 * @param specification
	 * @param clazz
	 * @return <br>
	 */
	<M> List<M> query(QuerySpecification<T> specification, Class<M> clazz);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @return <br>
	 */
	List<T> query(QueryWrapper<T> wrapper);

	/**
	 * Description:根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param <M>
	 * @param wrapper
	 * @param clazz
	 * @return <br>
	 */
	<M> List<M> query(QueryWrapper<T> wrapper, Class<M> clazz);

	/**
	 * Description: 查询所有数据<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	List<T> queryAll();

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @param clazz
	 * @param <M>
	 * @return <br>
	 */
	<M> List<M> queryByLambda(LambdaQuerySpecification<T, M> specification, Class<M> clazz);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @return <br>
	 */
	List<T> queryByLambda(LambdaQuerySpecification<T, T> specification);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @param clazz
	 * @param <M>
	 * @return <br>
	 */
	<M> List<M> queryByLambda(LambdaQueryWrapper<T, M> wrapper, Class<M> clazz);

	/**
	 * Description: 根据条件查询 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @return <br>
	 */
	List<T> queryByLambda(LambdaQueryWrapper<T, T> wrapper);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @param pageIndex
	 * @param pageSize
	 * @return <br>
	 */
	PagerList<T> queryPager(QuerySpecification<T> specification, int pageIndex, int pageSize);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @param pageIndex
	 * @param pageSize
	 * @param <M>           M
	 * @param clazz
	 * @return <br>
	 */
	<M> PagerList<M> queryPager(QuerySpecification<T> specification, int pageIndex, int pageSize, Class<M> clazz);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @param pageIndex
	 * @param pageSize
	 * @return <br>
	 */
	PagerList<T> queryPager(QueryWrapper<T> wrapper, int pageIndex, int pageSize);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @param pageIndex
	 * @param pageSize
	 * @param <M>       M
	 * @param clazz
	 * @return <br>
	 */
	<M> PagerList<M> queryPager(QueryWrapper<T> wrapper, int pageIndex, int pageSize, Class<M> clazz);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @param pageIndex
	 * @param pageSize
	 * @param <M>           M
	 * @param clazz
	 * @return <br>
	 */
	<M> PagerList<M> queryPagerByLambda(LambdaQuerySpecification<T, M> specification, int pageIndex, int pageSize,
			Class<M> clazz);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification
	 * @param pageIndex
	 * @param pageSize
	 * @return <br>
	 */
	PagerList<T> queryPagerByLambda(LambdaQuerySpecification<T, T> specification, int pageIndex, int pageSize);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @param pageIndex
	 * @param pageSize
	 * @param <M>       M
	 * @param clazz
	 * @return <br>
	 */
	<M> PagerList<M> queryPagerByLambda(LambdaQueryWrapper<T, M> wrapper, int pageIndex, int pageSize, Class<M> clazz);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper
	 * @param pageIndex
	 * @param pageSize
	 * @return <br>
	 */
	PagerList<T> queryPagerByLambda(LambdaQueryWrapper<T, T> wrapper, int pageIndex, int pageSize);

	/**
	 * Description: 保存数据<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param entity
	 */
	void save(T entity);

	/**
	 * Description: 批量保存<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param entitys <br>
	 */
	void saveBatch(List<T> entitys);

	/**
	 * Description: 更新数据<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param pojo <br>
	 */
	void update(T pojo);

	/**
	 * Description: 根据条件来做更新<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification <br>
	 */
	void update(UpdateSpecification<T> specification);

	/**
	 * Description: 根据条件来做更新<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper <br>
	 */
	void update(UpdateWrapper<T> wrapper);

	/**
	 * Description: 批量更新<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param entitys <br>
	 */
	void updateBatch(List<T> entitys);

	/**
	 * Description: 根据条件来做更新<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param specification <br>
	 */
	void updateByLambda(LambdaUpdateSpecification<T> specification);

	/**
	 * Description: 根据条件来做更新<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param wrapper <br>
	 */
	void updateByLambda(LambdaUpdateWrapper<T> wrapper);

}
