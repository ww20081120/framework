package com.fccfc.framework.cache.core.redis.clients.jedis;

public abstract class Builder<T> {
    public abstract T build(Object data);
}
