/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.system.service;

import java.util.List;

import com.hbasesoft.framework.web.system.bean.AnnouncementPojo;
import com.hbasesoft.framework.common.ServiceException;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月26日 <br>
 * @since bps<br>
 * @see com.hbasesoft.framework.web.manager.service.system <br>
 */
public interface AnnouncementService {

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<AnnouncementPojo> listAnnouncement(int pageIndex, int pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<AnnouncementPojo> qryAnnouncement(AnnouncementPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    void add(AnnouncementPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @param isStateChange
     * @throws ServiceException <br>
     */
    void modify(AnnouncementPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @throws ServiceException <br>
     */
    void delete(String[] ids) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param mediaId <br>
     * @param mediaName <br>
     * @throws ServiceException <br>
     */
    void importAnnouncementData(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    void audit(String[] ids, AnnouncementPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @param state <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<AnnouncementPojo> auditCheck(String[] ids, String state) throws ServiceException;
}
