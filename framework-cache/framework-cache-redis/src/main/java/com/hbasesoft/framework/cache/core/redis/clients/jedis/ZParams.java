package com.hbasesoft.framework.cache.core.redis.clients.jedis;

import static com.hbasesoft.framework.cache.core.redis.clients.jedis.Protocol.Keyword.AGGREGATE;
import static com.hbasesoft.framework.cache.core.redis.clients.jedis.Protocol.Keyword.WEIGHTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hbasesoft.framework.cache.core.redis.clients.util.SafeEncoder;

public class ZParams {
    public enum Aggregate {
	SUM, MIN, MAX;

	public final byte[] raw;

	Aggregate() {
	    raw = SafeEncoder.encode(name());
	}
    }

    private List<byte[]> params = new ArrayList<byte[]>();

    public ZParams weights(final int... weights) {
	params.add(WEIGHTS.raw);
	for (final int weight : weights) {
	    params.add(Protocol.toByteArray(weight));
	}

	return this;
    }

    public Collection<byte[]> getParams() {
	return Collections.unmodifiableCollection(params);
    }

    public ZParams aggregate(final Aggregate aggregate) {
	params.add(AGGREGATE.raw);
	params.add(aggregate.raw);
	return this;
    }
}
