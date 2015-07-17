package com.fccfc.framework.log.mongodb.repositoryimpl;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.fccfc.framework.log.mongodb.model.TransLog;
import com.fccfc.framework.log.mongodb.repository.AbstractRepository;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月4日 <br>
 * @see com.fccfc.framework.log.mongodb.repositoryimpl <br>
 */
public class LogRepository implements AbstractRepository {
    /**mongoTemplate*/
    private MongoTemplate mongoTemplate;

    @Override
    public void insert(TransLog log) {
        getMongoTemplate().insert(log);

    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param id <br>
     * @return <br>
     */
    @Override
    public TransLog findOne(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<TransLog> findAll() {
        // TODO Auto-generated method stub
        return getMongoTemplate().find(new Query(), TransLog.class);
    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param regex <br>
     * @return <br>
     */
    @Override
    public List<TransLog> findByRegex(String regex) {
        // TODO Auto-generated method stub
        return null;
    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void removeOne(String id) {
        // TODO Auto-generated method stub

    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * <br>
     */
    @Override
    public void removeAll() {
        // TODO Auto-generated method stub

    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void findAndModify(String id) {
        // TODO Auto-generated method stub

    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @param mongoTemplate <br>
     */
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @return <br>
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
