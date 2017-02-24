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

import org.apache.commons.io.IOUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

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
    private static final int INIT_SIZE = 1024;

    private static final int MAX_SIZE = 100 * INIT_SIZE;

    private static Kryo kryo = new Kryo();

    private static Kryo getKryo(Class<?> clazz) {
        if (!clazz.getName().startsWith("java.")) {
            kryo.register(clazz);
        }
        return kryo;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     * @throws UtilException UtilException
     */
    public static <T> byte[] serial(T obj) throws UtilException {
        Output out = new Output(INIT_SIZE, MAX_SIZE);
        try {
            getKryo(obj.getClass()).writeObject(out, obj);
            return out.toBytes();
        }
        finally {
            if (out != null) {
                out.close();
            }
        }

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
            throw new UtilException(ErrorCodeDef.SERIALIZE_ERROR, e);
        }
        finally {
            IOUtils.closeQuietly(out);
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
    public static <T> T unserial(Class<T> clazz, byte[] data) throws UtilException {
        if (data != null && data.length > 0) {
            Input in = null;
            try {
                in = new Input(data);
                return getKryo(clazz).readObject(in, clazz);
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        return null;
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
                throw new UtilException(ErrorCodeDef.UNSERIALIZE_ERROR, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        throw new UtilException(ErrorCodeDef.UNSERIALIZE_ERROR);
                    }
                }
            }
        }
        return result;
    }
}
