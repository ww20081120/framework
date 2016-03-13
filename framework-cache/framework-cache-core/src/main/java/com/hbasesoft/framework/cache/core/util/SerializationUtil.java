/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.util <br>
 */
public final class SerializationUtil {

    /**
     * INIT_SIZE
     */
    private static final int INIT_SIZE = 4096;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     * @throws UtilException UtilException
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serial(T obj) throws UtilException {
        byte[] result = null;
        if (obj != null && !(obj instanceof Void)) {

            if (obj instanceof Collection || obj instanceof Map) {
                result = jdkSerial(obj);
            }
            else {
                Schema<T> schema = RuntimeSchema.getSchema((Class<T>) obj.getClass());
                LinkedBuffer buffer = LinkedBuffer.allocate(INIT_SIZE);
                try {
                    result = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
                }
                catch (Exception e) {
                    throw new UtilException(ErrorCodeDef.SERIALIZE_ERROR, e);
                }
            }
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     */
    public static byte[] jdkSerial(Object obj) throws UtilException {
        byte[] bytes = null;
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(obj);
            out.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        catch (IOException e) {
            throw new UtilException(ErrorCodeDef.SERIALIZE_ERROR, "序列化错误", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    throw new UtilException(ErrorCodeDef.SERIALIZE_ERROR, "序列化错误", e);
                }
            }
        }
        return bytes;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param data <br>
     * @return <br>
     * @throws UtilException UtilException
     */
    @SuppressWarnings("unchecked")
    public static <T> T unserial(Class<T> clazz, byte[] data) throws UtilException {
        T result = null;
        if (data != null && data.length > 0) {
            if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
                result = (T) jdkUnserial(data);
            }
            else {
                Schema<T> schema = RuntimeSchema.getSchema(clazz);
                try {
                    result = clazz.newInstance();
                    ProtostuffIOUtil.mergeFrom(data, result, schema);
                }
                catch (Exception e) {
                    throw new UtilException(ErrorCodeDef.UNSERIALIZE_ERROR, e);
                }
            }
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param data <br>
     * @return <br>
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object jdkUnserial(byte[] data) throws UtilException {
        Object result = null;
        if (data != null && data.length > 0) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(data));
                result = in.readObject();
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.UNSERIALIZE_ERROR, "反序列化异常", e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        throw new UtilException(ErrorCodeDef.UNSERIALIZE_ERROR, "反序列化异常");
                    }
                }
            }
        }
        return result;
    }

}
