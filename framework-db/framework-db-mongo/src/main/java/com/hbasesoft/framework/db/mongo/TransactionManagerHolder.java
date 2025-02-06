
package com.hbasesoft.framework.db.mongo;

import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.config.DaoTypeDef;
import com.hbasesoft.framework.db.mongo.config.MongodbSourceUtil;
import com.mongodb.client.MongoClient;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年5月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db <br>
 */
@NoArgsConstructor
public final class TransactionManagerHolder {

    /** transaction */
    private static Map<String, PlatformTransactionManager> transactionManagerHolder = new ConcurrentHashMap<>();

    /** session factory */
    private static Map<String, MongoDatabaseFactory> mongoTemplateHolder = new ConcurrentHashMap<>();

    /**
     * Description: 获取Spring MongoDB transaction manager <br>
     *
     * @return PlatformTransactionManager <br>
     */
    public static PlatformTransactionManager getTransactionManager() {
        String key = DynamicDataSourceManager.getInstance(DaoTypeDef.mongodb).getDataSourceCode();
        return transactionManagerHolder.computeIfAbsent(key, dbCode -> {
            MongoTransactionManager mongoTransactionManager = new MongoTransactionManager();
            mongoTransactionManager.setDatabaseFactory(getDatabaseFactory(dbCode));
            return mongoTransactionManager;
        });
    }

    /**
     * Description: 获取MongoTemplate <br>
     *
     * @param key 数据源标识符
     * @return MongoTemplate <br>
     */
    public static MongoDatabaseFactory getDatabaseFactory(final String key) {
        return mongoTemplateHolder.computeIfAbsent(key, dbCode -> {
            MongoClient mongoClient = MongodbSourceUtil.getDataSource(dbCode);
            String databaseName = MongodbSourceUtil.getDatabaseName(dbCode);
            SimpleMongoClientDatabaseFactory databaseFactory =
                    new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
            return databaseFactory;
        });
    }

    /**
     * Description: 获取默认的MongoTemplate <br>
     *
     * @return MongoTemplate <br>
     */
    public static MongoTemplate getMongoTemplate() {
        String dbCode = DynamicDataSourceManager.getInstance(DaoTypeDef.mongodb).getDataSourceCode();
        return new MongoTemplate(getDatabaseFactory(dbCode));
    }
}
