package com.hbasesoft.framework.web.system.service;

import java.util.List;

import com.hbasesoft.framework.web.system.bean.AttrPojo;
import com.hbasesoft.framework.web.system.bean.AttrValuePojo;
import com.hbasesoft.framework.common.ServiceException;

public interface AttrService {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<AttrPojo> queryAttrPager(Integer pageIndex, Integer pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws ServiceException <br>
     */
    List<AttrPojo> queryChildAttr(Integer attrId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<AttrPojo> queryAttr() throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws ServiceException <br>
     */
    AttrPojo queryAttr(Integer attrId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<AttrValuePojo> queryAttrValuePager(Integer attrId, Integer pageIndex, Integer pageSize)
        throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws ServiceException <br>
     */
    List<AttrValuePojo> queryAttrValue(Integer attrId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrValueId
     * @return
     * @throws ServiceException <br>
     */
    AttrValuePojo getAttrValue(Integer attrValueId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<AttrValuePojo> queryAttrValue() throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importAttr(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addAttr(AttrPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyAttr(AttrPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrIds
     * @throws ServiceException <br>
     */
    void deleteAttr(Integer[] attrIds) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importAttrValue(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addAttrValue(AttrValuePojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyAttrValue(AttrValuePojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrIds
     * @throws ServiceException <br>
     */
    void deleteAttrValue(Integer[] attrValueIds) throws ServiceException;
}
