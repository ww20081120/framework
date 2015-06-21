/**
 * 
 */
package com.fccfc.framework.web.dao;

import com.fccfc.framework.api.bean.resource.AttachmentsPojo;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.core.db.annotation.DAO;
import com.fccfc.framework.core.db.annotation.Param;
import com.fccfc.framework.core.db.annotation.Sql;
import com.fccfc.framework.core.db.support.hibernate.IGenericBaseDao;

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
@DAO
public interface AttachmentsDao extends IGenericBaseDao{

    @Sql("SELECT * FROM ATTACHMENTS WHERE ATTACHMENTS_ID = :id")
    AttachmentsPojo selectAttachments(@Param("id") int attachmentId) throws DaoException;

    int updateAttachments(@Param("id") int attachmentId, @Param("thumbPath") String thumbPath) throws DaoException;
}
