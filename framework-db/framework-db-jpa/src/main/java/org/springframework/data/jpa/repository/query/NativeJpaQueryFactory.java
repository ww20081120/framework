/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package org.springframework.data.jpa.repository.query;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.db.jpa.util.FindSqlUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月24日 <br>
 * @since V1.0<br>
 * @see org.springframework.data.jpa.repository.query <br>
 */
enum NativeJpaQueryFactory {

    INSTANCE;

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private static final Logger LOG = LoggerFactory.getLogger(JpaQueryFactory.class);

    /**
     * Creates a {@link RepositoryQuery} from the given {@link QueryMethod} that is potentially annotated with
     * {@link Query}.
     * 
     * @param method must not be {@literal null}.
     * @param em must not be {@literal null}.
     * @param evaluationContextProvider
     * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
     */
    AbstractJpaQuery fromQueryAnnotation(JpaQueryMethod method, EntityManager em,
        EvaluationContextProvider evaluationContextProvider) {

        LOG.debug("Looking up query for method {}", method.getName());

        String queryString = method.getAnnotatedQuery();

        if (method.isNativeQuery() && StringUtils.isEmpty(queryString)) {
            try {
                Field m = JpaQueryMethod.class.getDeclaredField("method");
                m.setAccessible(true);
                queryString = FindSqlUtil.checkSqlPath((Method) m.get(method));
            }
            catch (Exception e) {
                throw new InitializationException(e);
            }
        }

        return fromMethodWithQueryString(method, em, queryString, evaluationContextProvider);
    }

    /**
     * Creates a {@link RepositoryQuery} from the given {@link String} query.
     * 
     * @param method must not be {@literal null}.
     * @param em must not be {@literal null}.
     * @param queryString must not be {@literal null} or empty.
     * @param evaluationContextProvider
     * @return
     */
    AbstractJpaQuery fromMethodWithQueryString(JpaQueryMethod method, EntityManager em, String queryString,
        EvaluationContextProvider evaluationContextProvider) {

        if (queryString == null) {
            return null;
        }

        return method.isNativeQuery() ? new NativeJpaQuery(method, em, queryString, evaluationContextProvider, PARSER)
            : new SimpleJpaQuery(method, em, queryString, evaluationContextProvider, PARSER);
    }

    /**
     * Creates a {@link StoredProcedureJpaQuery} from the given {@link JpaQueryMethod} query.
     * 
     * @param method must not be {@literal null}.
     * @param em must not be {@literal null}.
     * @return
     */
    public StoredProcedureJpaQuery fromProcedureAnnotation(JpaQueryMethod method, EntityManager em) {

        if (!method.isProcedureQuery()) {
            return null;
        }

        return new StoredProcedureJpaQuery(method, em);
    }
}