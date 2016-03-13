package com.hbasesoft.framework.cache.core.redis.clients.jedis.exceptions;

import com.hbasesoft.framework.cache.core.redis.clients.jedis.HostAndPort;

public class JedisMovedDataException extends JedisRedirectionException {
    private static final long serialVersionUID = 3878126572474819403L;

    public JedisMovedDataException(String message, HostAndPort targetNode,
	    int slot) {
	super(message, targetNode, slot);
    }

    public JedisMovedDataException(Throwable cause, HostAndPort targetNode,
	    int slot) {
	super(cause, targetNode, slot);
    }

    public JedisMovedDataException(String message, Throwable cause,
	    HostAndPort targetNode, int slot) {
	super(message, cause, targetNode, slot);
    }
}
