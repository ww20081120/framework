/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.jpa <br>
 */
public class NativeJpaRepositoryFactoryBean<R extends JpaRepository<S, ID>, S, ID extends Serializable>
    extends JpaRepositoryFactoryBean<R, S, ID> {

    /**
     * @param repositoryInterface
     */
    public NativeJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityManager
     * @return <br>
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new NativeJpaRepositoryFactory(entityManager);
    }

}
