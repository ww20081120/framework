/**
 * 
 */
package com.fccfc.framework.web.dao;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.dao <br>
 */
@Dao
public interface AttachmentsDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attachmentId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    AttachmentsPojo selectAttachments(@Param("id") int attachmentId) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attachmentId <br>
     * @param thumbPath <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int updateAttachments(@Param("id") int attachmentId, @Param("thumbPath") String thumbPath) throws DaoException;
}
