/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core.util;

import java.nio.ByteBuffer;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 1, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core.util <br>
 */
public final class ArgsSerializationUtil {

    /** Number */
    private static final int NUM_FF = 0xff;

    /** Number */
    private static final int NUM_FFFF = 0xffff;

    /** Number */
    private static final int NUM_65536 = 65536;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args
     * @return <br>
     */
    public static byte[] serializeArgs(final Object[] args) {
        if (CommonUtil.isNotEmpty(args)) {
            byte[][] argsData = new byte[args.length][];
            int bufferSize = args.length * 2 + 1;
            for (int i = 0, len = args.length; i < len; i++) {
                Object arg = args[i];
                if (arg != null) {
                    byte[] data = SerializationUtil.jdkSerial(arg);
                    Assert.isTrue(data.length < NUM_65536, ErrorCodeDef.ARGUMENTS_SIZE_TOO_LARGE);
                    argsData[i] = data;
                    bufferSize += data.length;
                }
            }
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.put((byte) args.length);

            for (int i = 0, len = args.length; i < len; i++) {
                byte[] data = argsData[i];
                if (data == null) {
                    buffer.putShort((short) 0);
                }
                else {
                    buffer.putShort((short) data.length);
                    buffer.put(data);
                }
            }
            return buffer.array();
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data
     * @return <br>
     */
    public static Object[] unserialArgs(final byte[] data) {
        if (data != null && data.length > 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int len = buffer.get() & NUM_FF;
            Object[] args = new Object[len];
            for (int i = 0; i < len; i++) {
                int objLen = buffer.getShort() & NUM_FFFF;
                if (objLen > 0) {
                    byte[] temp = new byte[objLen];
                    buffer.get(temp);
                    args[i] = SerializationUtil.jdkUnserial(temp);
                }
            }
            return args;
        }
        return null;
    }
}
