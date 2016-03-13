/**
 * 
 */
package com.hbasesoft.framework.web.core.dao;

import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.dao.common <br>
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

    /**
     * queryAttachmentsByFilePath
     * Description: <br> 
     *  
     * @author XXX<br>
     * @taskId <br>
     * @param filePath filePath
     * @return AttachmentsPojo
     * @throws DaoException <br>
     */
    AttachmentsPojo queryAttachmentsByFilePath(@Param("filePath") String filePath) throws DaoException;

}
