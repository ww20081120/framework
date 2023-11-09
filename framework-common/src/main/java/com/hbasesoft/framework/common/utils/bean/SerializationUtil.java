/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.security.DataUtil;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationUtil {

    /**
     * INIT_SIZE
     */
    private static final int INIT_SIZE = 1024;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param obj
     * @param <T> T
     * @return T
     * @throws UtilException <br>
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serial(final T obj) throws UtilException {
        if (obj != null && !(obj instanceof Void)) {
            try {
                if (obj instanceof Map) {
                    return jdkSerial(obj);
                }
                else {
                    Schema<T> schema = RuntimeSchema.getSchema((Class<T>) obj.getClass());
                    LinkedBuffer buffer = LinkedBuffer.allocate(INIT_SIZE);
                    return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
                }
            }
            catch (Exception e) {
                throw new UtilException(e, ErrorCodeDef.SERIALIZE_ERROR, obj);
            }

        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     */
    public static byte[] jdkSerial(final Object obj) throws UtilException {
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
            throw new UtilException(e, ErrorCodeDef.JDK_SERIALIZE_ERROR, obj);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
        return bytes;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param data
     * @param <T> T
     * @return T
     * @throws UtilException <br>
     */
    @SuppressWarnings("unchecked")
    public static <T> T unserial(final Class<T> clazz, final byte[] data) throws UtilException {
        T result = null;
        if (data != null && data.length > 0) {
            try {
                if (Map.class.isAssignableFrom(clazz)) {
                    result = (T) jdkUnserial(data);
                }
                else {
                    Schema<T> schema = RuntimeSchema.getSchema(clazz);
                    result = clazz.newInstance();
                    ProtostuffIOUtil.mergeFrom(data, result, schema);
                }
            }
            catch (Exception e) {
                throw new UtilException(e, ErrorCodeDef.UNSERIALIZE_ERROR, DataUtil.byte2HexStr(data));
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
    public static Object jdkUnserial(final byte[] data) throws UtilException {
        Object result = null;
        if (data != null && data.length > 0) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(data));
                result = in.readObject();
            }
            catch (Exception e) {
                throw new UtilException(e, ErrorCodeDef.JDK_UNSERIALIZE_ERROR, DataUtil.byte2HexStr(data));
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        throw new UtilException(ErrorCodeDef.JDK_UNSERIALIZE_ERROR);
                    }
                }
            }
        }
        return result;
    }
}
