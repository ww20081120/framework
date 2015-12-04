package com.fccfc.framework.web.manager.dao.permission.menu;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;

/**
 * <Description> 功能模块 FunctionDao<br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月21日 <br>
 * @see com.fccfc.framework.web.manager.dao.permission.menu <br>
 * @since V1.0<br>
 */
@Dao
public interface FunctionDao extends IGenericBaseDao {

    /**
     * Description: 批量删除功能模块<br>
     *
     * @param functionIds
     * @return
     * @throws DaoException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Sql(bean = FunctionPojo.class)
    int deleteByFunctionId(@Param("functionIds") Long[] functionIds) throws DaoException;

    /**
     * Description: 按照directoryCode查询<br>
     *
     * @param directoryCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Sql(bean = FunctionPojo.class)
    List<FunctionPojo> selectList(@Param("directoryCode") String directoryCode, @Param("functionName") String functionName,
                                  @Param(Param.PAGE_INDEX) Integer pageIndex,
                                  @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    /**
     * Description: 根据funcitonId修改functionPojo中的属性<br>
     *
     * @param functionPojo
     * @return
     * @throws DaoException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Sql(value = "UPDATE FUNCTION SET FUNCTION_NAME = :functionPojo.functionName, REMARK = :functionPojo.remark WHERE FUNCTION_ID = :functionPojo.functionId")
    int update(@Param("functionPojo") FunctionPojo functionPojo) throws DaoException;

    int count(@Param("directoryCode") String directoryCode, @Param("functionName") String functionName) throws DaoException;

    int batchInsert(@Param("pojoList") List<FunctionPojo> pojoList) throws DaoException;
}
