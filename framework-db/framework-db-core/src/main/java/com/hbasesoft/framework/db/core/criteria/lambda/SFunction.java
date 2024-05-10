package com.hbasesoft.framework.db.core.criteria.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的 Function
 * 
 * @param <T> T
 * @param <R> R
 * @author miemie
 * @since 2018-05-12
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {

}
