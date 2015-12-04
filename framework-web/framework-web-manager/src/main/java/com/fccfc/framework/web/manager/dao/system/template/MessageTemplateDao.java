package com.fccfc.framework.web.manager.dao.system.template;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.message.core.bean.MessageTemplatePojo;

/**
 * 
 * <Description> <br> 
 *  
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月18日 <br>
 * @since bps<br>
 * @see com.fccfc.framework.web.manager.dao.system.template <br>
 */
@Dao
public interface MessageTemplateDao extends IGenericBaseDao {

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = MessageTemplatePojo.class)
    List<MessageTemplatePojo> getAllMessageTemplate(@Param(Param.PAGE_INDEX) Integer pageIndex,
        @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int insertMessageTemplate(@Param("pojo") MessageTemplatePojo pojo) throws DaoException;
    
    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws DaoException <br>
     */
    void importMessageTemplate(@Param("pojo") MessageTemplatePojo pojo) throws DaoException;

    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws DaoException <br>
     */
    void modifyMessageTemplate(@Param("pojo") MessageTemplatePojo pojo) throws DaoException;

    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param messageTemplateId <br>
     * @throws DaoException <br>
     */
    void deleteMessageTemplate(@Param("id") Integer messageTemplateId) throws DaoException;

    /**
     * Description: 根据id查询MessageTemplate表<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param messageTemplateId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = MessageTemplatePojo.class)
    MessageTemplatePojo queryMessageTemplateById(@Param("messageTemplateId") Integer messageTemplateId) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = MessageTemplatePojo.class)
    List<MessageTemplatePojo> queryAllMt() throws DaoException;
}
