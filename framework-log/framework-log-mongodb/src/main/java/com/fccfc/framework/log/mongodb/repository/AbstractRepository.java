package com.fccfc.framework.log.mongodb.repository;

import java.util.List;

import com.fccfc.framework.log.mongodb.model.TransLog;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月4日 <br>
 * @see com.fccfc.framework.log.mongodb.repository <br>
 */
public interface AbstractRepository {

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param log <br>
     */
    public void insert(TransLog log);

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param id <br>
     * @return <br>
     */
    public TransLog findOne(String id);

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @return <br>
     */
    public List<TransLog> findAll();

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param regex <br>
     * @return <br>
     */
    public List<TransLog> findByRegex(String regex);

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param id <br>
     */
    public void removeOne(String id);

    /**
     * <b>function:</b>删除所有
     * 
     * @author cuiran
     * @createDate 2012-12-12 16:25:40
     */
    public void removeAll();

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param id <br>
     */
    public void findAndModify(String id);
}
