/**
 * 
 */
package com.fccfc.framework.web.dao;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.bean.resource.AttachmentsPojo;

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

    @Sql("SELECT * FROM ATTACHMENTS WHERE ATTACHMENTS_ID = :id")
    AttachmentsPojo selectAttachments(@Param("id") int attachmentId) throws DaoException;

    int updateAttachments(@Param("id") int attachmentId, @Param("thumbPath") String thumbPath) throws DaoException;
}
