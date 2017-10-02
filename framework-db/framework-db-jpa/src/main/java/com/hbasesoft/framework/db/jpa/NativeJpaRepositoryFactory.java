/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jpa;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.NativeJpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.db.jpa <br>
 */
public class NativeJpaRepositoryFactory extends JpaRepositoryFactory {

    private final EntityManager nativeEntityManager;

    private final QueryExtractor nativeExtractor;

    /**
     * @param entityManager
     */
    public NativeJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.nativeEntityManager = entityManager;
        this.nativeExtractor = PersistenceProvider.fromEntityManager(entityManager);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param evaluationContextProvider
     * @return <br>
     */
    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(Key key,
        EvaluationContextProvider evaluationContextProvider) {
        return Optional.of(
            NativeJpaQueryLookupStrategy.create(nativeEntityManager, key, nativeExtractor, evaluationContextProvider));
    }

}
