package com.fccfc.framework.web.dao;

import java.util.List;

import com.fccfc.framework.config.core.bean.DirectoryPojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 * 
 * @author 刘柏洋 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月2日 <br>
 * @see com.fccfc.test.dao.api <br>
 */
@Dao
public interface DirectoryDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directoryCode <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(value = "SELECT * FROM DIRECTORY WHERE DIRECTORY_CODE = :directoryCode", bean = DirectoryPojo.class)
    List<DirectoryPojo> queryDirectory(@Param("directoryCode") String directoryCode) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parentDirectoryCode <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(value = "SELECT * FROM DIRECTORY WHERE PARENT_DIRECTORY_CODE = :parentDirectoryCode",
        bean = DirectoryPojo.class)
    List<DirectoryPojo> queryDirectoryByParentCode(@Param("parentDirectoryCode") String parentDirectoryCode)
        throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directory <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("INSERT INTO DIRECTORY(DIRECTORY_CODE,DIRECTORY_NAME,PARENT_DIRECTORY_CODE,REMARK)"
        + " VALUES(:directory.directoryCode,:directory.directoryName,:directory.parentDirectoryCode,:directory.remark)")
    int insertDirectory(@Param("directory") DirectoryPojo directory) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directoryCode <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("DELETE FROM DIRECTORY WHERE DIRECTORY_CODE = :directoryCode")
    int deleteDirectory(@Param("directoryCode") String directoryCode) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("UPDATE DIRECTORY SET DIRECTORY_NAME = :directory.directoryName , REMARK= :directory.remark WHERE DIRECTORY_CODE = :directory.directoryCode")
    int updateDirectory(@Param("directory") DirectoryPojo pojo) throws DaoException;

}
