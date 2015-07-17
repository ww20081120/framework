/**
 * 
 */
package com.fccfc.framework.cache.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.fccfc.framework.cache.core <br>
 */
public abstract class AbstractCache implements ICache {

    /**
     * INIT_SIZE
     */
    private static final int INIT_SIZE = 4096;

    /**
     * 序列话类
     */
    private final Kryo kryo = new Kryo();

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     */
    protected byte[] serial(Object obj) {
        byte[] result = null;
        if (obj != null && !(obj instanceof Void)) {
            Output output = null;
            try {
                output = new Output(1, INIT_SIZE);
                kryo.writeClassAndObject(output, obj);
                result = output.toBytes();
            }
            finally {
                if (output != null) {
                    output.close();
                }
            }
        }
        return result;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param data <br>
     * @return <br>
     */
    protected Object unserial(byte[] data) {
        Object result = null;
        if (data != null && data.length > 0) {
            Input input = null;
            try {
                input = new Input(data);
                result = kryo.readClassAndObject(input);
            }
            finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return result;
    }

}
