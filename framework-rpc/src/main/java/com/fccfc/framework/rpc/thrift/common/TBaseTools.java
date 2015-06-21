package com.fccfc.framework.rpc.thrift.common;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;

import com.fccfc.framework.rpc.thrift.transport.ThriftTransport;

/**
 * TBase帮助类
 * @author yankai
 * @date 2012-3-31
 */
public class TBaseTools {
	
	static final ConcurrentMap<String, Class<?>> CACHED_CLASS = new ConcurrentHashMap<String, Class<?>>();
	static final ConcurrentMap<String, Constructor<?>> CACHED_CONSTRUCTOR = new ConcurrentHashMap<String, Constructor<?>>();
	
	public static int getTMessageId(ChannelBuffer buf) {
    	int length = buf.getInt(4);
    	int id = buf.getInt(8 + length);
    	return id;
	}
	
	public static String getStringAtAbsoluteIndex(ChannelBuffer buf, int index){
        byte[] arr = new byte[buf.getInt(index)];
        buf.getBytes(index + 4, arr);
        String str = null;
		try {
			str = new String(arr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static TProtocol newProtocol(ChannelBuffer input, ChannelBuffer output) {
		return new TBinaryProtocol(new ThriftTransport(input, output));
	}
	
	public static String getArgsClassName(String serviceName, String methodName, String tag) {
		return serviceName.substring(0, serviceName.lastIndexOf("$")) + "$" + methodName + tag;
	}
	
	public static Class<?> getTBaseClass(String argsServiceName) throws Exception {
		Class<?> clazz = CACHED_CLASS.get(argsServiceName);
		if (clazz == null) {
			CACHED_CLASS.putIfAbsent(argsServiceName, Class.forName(argsServiceName));
			clazz = CACHED_CLASS.get(argsServiceName);
		}
		return clazz;
	}
	
	public static Class<?> getTBaseClass(String serviceName, String methodName, String tag) 
			throws Exception {
		String argsServiceName = getArgsClassName(serviceName, methodName, tag);
		return getTBaseClass(argsServiceName);
	}
	
	public static TBase<?, ?> getTBaseObject(Class<?> clazz, Class<?>[] parameterTypes, Object[] initargs) 
			throws Exception {
		StringBuilder keyBuf = new StringBuilder(clazz.getName());
		if (parameterTypes != null) {
			 for (Class<?> t : parameterTypes) {
				 keyBuf.append(".").append(t.getName());
			 }
		} else {
			keyBuf.append(".").append(parameterTypes);
		}
		String key = keyBuf.toString();
		Constructor<?> constructor = CACHED_CONSTRUCTOR.get(key);
		if (constructor == null) {
			CACHED_CONSTRUCTOR.putIfAbsent(key, clazz.getConstructor(parameterTypes));
			constructor = CACHED_CONSTRUCTOR.get(key);
		}
		return (TBase<?, ?>) constructor.newInstance(initargs);
	}
	
	public static Object getResult(TBase<?, ?> _result) throws Exception {
		try {
			Field success = _result.getClass().getDeclaredField("success");//hard code
			success.setAccessible(true);
			Object object = success.get(_result);
			if (object != null) {
				return object;
			}
		} catch (NoSuchFieldException e) {//没有success字段说明方法是void
			return void.class;
		}
		Field[] fields = _result.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getModifiers() == Modifier.PUBLIC 
					&& TBase.class.isAssignableFrom(f.getType())
					&& Exception.class.isAssignableFrom(f.getType())) {
				f.setAccessible(true);
				if (f.get(_result) != null) {
					throw (Exception)f.get(_result);
				}
			}
		}
		throw new TApplicationException(TApplicationException.MISSING_RESULT, "unknown result");
	}
	
	public static void createErrorTMessage(TProtocol oprot, String methodName, int id, String errMsg)
			throws Exception {
		TMessage tmessage = new TMessage(methodName, TMessageType.EXCEPTION, id);
		oprot.writeMessageBegin(tmessage);
		oprot.writeMessageEnd();
		TApplicationException ex = new TApplicationException(TApplicationException.INTERNAL_ERROR, errMsg); 
		try {
			ex.write(oprot);
		} catch (TException e) {
			e.printStackTrace();
		}
	}
}
