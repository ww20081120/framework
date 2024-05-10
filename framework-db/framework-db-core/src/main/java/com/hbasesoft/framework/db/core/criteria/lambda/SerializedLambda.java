package com.hbasesoft.framework.db.core.criteria.lambda;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import com.hbasesoft.framework.common.utils.bean.ClassUtil;
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;

/**
 * 这个类是从 {@link java.lang.invoke.SerializedLambda} 里面 copy 过来的， 字段信息完全一样
 * <p>
 * 负责将一个支持序列的 Function 序列化为 SerializedLambda
 * </p>
 *
 * @author HCL
 * @since 2018/05/10
 */
@SuppressWarnings("unused")
public class SerializedLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;

    /** capturingClass */
    private Class<?> capturingClass;

    /** functionalInterfaceClass */
    private String functionalInterfaceClass;

    /** functionalInterfaceMethodName */
    private String functionalInterfaceMethodName;

    /** functionalInterfaceMethodSignature */
    private String functionalInterfaceMethodSignature;

    /** implClass */
    private String implClass;

    /** implMethodName */
    private String implMethodName;

    /** implMethodSignature */
    private String implMethodSignature;

    /** implMethodKind */
    private int implMethodKind;

    /** instantiatedMethodType */
    private String instantiatedMethodType;

    /** capturedArgs */
    private Object[] capturedArgs;

    /**
     * 通过反序列化转换 lambda 表达式，该方法只能序列化 lambda 表达式，不能序列化接口实现或者正常非 lambda 写法的对象
     *
     * @param lambda lambda对象
     * @return 返回解析后的 SerializedLambda
     */
    public static SerializedLambda resolve(final SFunction<?, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new RuntimeException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try (ObjectInputStream objIn = new ObjectInputStream(
            new ByteArrayInputStream(SerializationUtil.jdkSerial(lambda))) {
            @Override
            protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass)
                throws IOException, ClassNotFoundException {
                Class<?> clazz;
                try {
                    clazz = ClassUtil.toClassConfident(objectStreamClass.getName());
                }
                catch (Exception ex) {
                    clazz = super.resolveClass(objectStreamClass);
                }
                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            return (SerializedLambda) objIn.readObject();
        }
        catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("This is impossible to happen", e);
        }
    }

    /**
     * 获取接口 class
     *
     * @return 返回 class 名称
     */
    public String getFunctionalInterfaceClassName() {
        return normalizedName(functionalInterfaceClass);
    }

    /**
     * 获取实现的 class
     *
     * @return 实现类
     */
    public Class<?> getImplClass() {
        return ClassUtil.toClassConfident(getImplClassName());
    }

    /**
     * 获取 class 的名称
     *
     * @return 类名
     */
    public String getImplClassName() {
        return normalizedName(implClass);
    }

    /**
     * 获取实现者的方法名称
     *
     * @return 方法名称
     */
    public String getImplMethodName() {
        return implMethodName;
    }

    /**
     * 正常化类名称，将类名称中的 / 替换为 .
     *
     * @param name 名称
     * @return 正常的类名
     */
    private String normalizedName(final String name) {
        return name.replace('/', '.');
    }

    /**
     * @return 获取实例化方法的类型
     */
    public Class<?> getInstantiatedType() {
        String instantiatedTypeName = normalizedName(
            instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(';')));
        return ClassUtil.toClassConfident(instantiatedTypeName);
    }

    /**
     * @return 字符串形式
     */
    @Override
    public String toString() {
        String interfaceName = getFunctionalInterfaceClassName();
        String implName = getImplClassName();
        return String.format("%s -> %s::%s", interfaceName.substring(interfaceName.lastIndexOf('.') + 1),
            implName.substring(implName.lastIndexOf('.') + 1), implMethodName);
    }

}
