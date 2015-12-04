package com.fccfc.framework.web.manager.dao.common.attr;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.common.AttrPojo;
import com.fccfc.framework.web.manager.bean.common.AttrValuePojo;

@Dao
public interface AttrDao extends IGenericBaseDao{

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AttrPojo.class)
    List<AttrPojo> queryAttr(@Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AttrPojo.class)
    List<AttrPojo> queryAttr() throws DaoException;
    
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AttrPojo.class)
    List<AttrPojo> queryChildAttr(@Param("attrId") Integer attrId) throws DaoException;
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AttrValuePojo.class)
    List<AttrValuePojo> queryAttrValue(@Param("attrId") Integer attrId, @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AttrValuePojo.class)
    List<AttrValuePojo> queryAttrValue(@Param("attrId") Integer attrId) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AttrValuePojo.class)
    List<AttrValuePojo> queryAttrValue() throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @return
     * @throws DaoException <br>
     */
    int batchInsertAttr(@Param("pojoList") List<AttrPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @return
     * @throws DaoException <br>
     */
    int batchInsertAttrValue(@Param("pojoList") List<AttrValuePojo> pojoList) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrIds
     * @throws DaoException <br>
     */
    void deleteAttr(@Param("attrIds") Integer[] attrIds) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrValueIds
     * @throws DaoException <br>
     */
    void deleteAttrValue(@Param("attrValueIds") Integer[] attrValueIds) throws DaoException;
}
