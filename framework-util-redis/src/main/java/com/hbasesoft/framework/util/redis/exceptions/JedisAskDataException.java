package com.hbasesoft.framework.util.redis.exceptions;

import com.hbasesoft.framework.util.redis.HostAndPort;

public class JedisAskDataException extends JedisRedirectionException {
    private static final long serialVersionUID = 3878126572474819403L;

    public JedisAskDataException(Throwable cause, HostAndPort targetHost, int slot) {
        super(cause, targetHost, slot);
    }

    public JedisAskDataException(String message, Throwable cause, HostAndPort targetHost, int slot) {
        super(message, cause, targetHost, slot);
    }

    public JedisAskDataException(String message, HostAndPort targetHost, int slot) {
        super(message, targetHost, slot);
    }

}
