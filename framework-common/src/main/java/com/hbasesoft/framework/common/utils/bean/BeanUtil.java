/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.beans.BeanUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public final class BeanUtil {

    /**
     * SUB_PACKAGE_SCREEN_SUFFIX
     */
    private static final String SUB_PACKAGE_SCREEN_SUFFIX = ".*";

    /**
     * SUB_PACKAGE_SCREEN_SUFFIX_RE
     */
    private static final String SUB_PACKAGE_SCREEN_SUFFIX_RE = ".\\*"; // 替换使用

    /**
     * logger
     */
    private static Logger logger = new Logger(BeanUtil.class);

    /**
     * 判断方法是否是抽象方法
     * 
     * @param method <br>
     * @return <br>
     */
    public static boolean isAbstract(Method method) {
        int mod = method.getModifiers();
        return Modifier.isAbstract(mod);
    }

    /**
     * 获取方法参数名称，按给定的参数类型匹配方法
     * 
     * @param clazz <br>
     * @param method <br>
     * @param paramTypes <br>
     * @return <br>
     * @throws NotFoundException 如果类或者方法不存在 <br>
     * @throws UtilException 如果最终编译的class文件不包含局部变量表信息 <br>
     */
    public static String[] getMethodParamNames(Class<?> clazz, String method, Class<?>... paramTypes)
        throws NotFoundException, UtilException {

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(clazz.getName());
        String[] paramTypeNames = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypeNames[i] = paramTypes[i].getName();
        }
        CtMethod cm = cc.getDeclaredMethod(method, pool.get(paramTypeNames));
        return getMethodParamNames(cm);
    }

    /**
     * 获取方法参数名称，匹配同名的某一个方法
     * 
     * @param method <br>
     * @return <br>
     * @throws NotFoundException 如果类或者方法不存在 <br>
     * @throws UtilException <br>
     */
    public static String[] getMethodParamNames(Method method) throws NotFoundException, UtilException {
        return getMethodParamNames(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }

    /**
     * 获取方法参数名称
     * 
     * @param cm <br>
     * @return <br>
     * @throws NotFoundException <br>
     * @throws UtilException 如果最终编译的class文件不包含局部变量表信息 <br>
     */
    protected static String[] getMethodParamNames(CtMethod cm) throws NotFoundException, UtilException {
        CtClass cc = cm.getDeclaringClass();
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        if (codeAttribute == null) {
            throw new UtilException(ErrorCodeDef.CAN_NOT_FIND_VER_NAME_10003, cc.getName());
        }
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (null == attr) {
            throw new UtilException(ErrorCodeDef.CAN_NOT_FIND_VER_NAME_10003, cc.getName());
        }
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }

    /**
     * 获取对象的方法签名
     * 
     * @param method <br>
     * @return <br>
     */
    public static String getMethodSignature(Method method) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(method.getName());
        sbuf.append('(');
        Class<?>[] paramTypes = method.getParameterTypes();
        if (CommonUtil.isNotEmpty(paramTypes)) {
            for (Class<?> clazz : paramTypes) {
                sbuf.append(clazz.getName()).append(',');
            }
        }
        sbuf.append(')');
        return sbuf.toString();
    }

    /**
     * 从包package中获取所有的Class
     * 
     * @param pack <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static Set<Class<?>> getClasses(String pack) throws UtilException {
        // 是否循环迭代
        boolean recursive = false;
        String[] packArr = {};

        if (pack.lastIndexOf(SUB_PACKAGE_SCREEN_SUFFIX) != -1) {
            packArr = pack.split(SUB_PACKAGE_SCREEN_SUFFIX_RE);
            if (packArr.length > 1) {
                // 需要匹配中间的任意层包
                pack = packArr[0];
                for (int i = 0; i < packArr.length; i++) {
                    packArr[i] = packArr[i].replace(SUB_PACKAGE_SCREEN_SUFFIX.substring(1), "");
                }
            }
            else {
                pack = pack.replace(SUB_PACKAGE_SCREEN_SUFFIX, "");
            }
            recursive = true;
        }

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    logger.debug("-------------- scan type file ----------------");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");

                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, packArr, filePath, recursive, classes);
                }
                else if ("jar".equals(protocol)) {
                    findAndAddClassesInPackageByJarFile(packageName, packArr, url, packageDirName, recursive, classes);
                }
            }
        }
        catch (IOException e) {
            throw new UtilException(ErrorCodeDef.READ_FILE_ERROR_10028);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class <br>
     * 
     * @author cmh <br>
     * @version 2014-2-16 下午6:59:03 <br>
     * @param packageName <br>
     * @param packArr <br>
     * @param packagePath <br>
     * @param recursive <br>
     * @param classes <br>
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String[] packArr, String packagePath,
        final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), packArr, file.getAbsolutePath(),
                    recursive, classes);
            }
            else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' +
                    // className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    String classUrl = packageName + '.' + className;
                    // 判断是否是以点开头
                    if (classUrl.startsWith(".")) {
                        classUrl = classUrl.replaceFirst(".", "");
                    }

                    boolean flag = true;
                    if (packArr.length > 1) {
                        for (int i = 1; i < packArr.length; i++) {
                            if (classUrl.indexOf(packArr[i]) <= -1) {
                                flag = flag & false;
                            }
                            else {
                                flag = flag & true;
                            }
                        }
                    }

                    if (flag) {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(classUrl));
                    }
                }
                catch (ClassNotFoundException e) {
                    logger.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
                }
            }
        }
    }

    /**
     * 以Jar文件的形式来获取包下的所有Class <br>
     * 
     * @author cmh <br>
     * @version 2014-2-16 下午7:51:05 <br>
     * @param packageName <br>
     * @param url <br>
     * @param packArr <br>
     * @param packageDirName <br>
     * @param recursive <br>
     * @param classes <br>
     */
    private static void findAndAddClassesInPackageByJarFile(String packageName, String[] packArr, URL url,
        String packageDirName, final boolean recursive, Set<Class<?>> classes) {
        // 如果是jar包文件
        // 定义一个JarFile
        logger.debug("------------------------ scan type jar ----------------------");
        JarFile jar;
        try {
            // 获取jar
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    // 获取后面的字符串
                    name = name.substring(1);
                }
                // 如果前半部分和定义的包名相同
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // 如果以"/"结尾 是一个包
                    if (idx != -1) {
                        // 获取包名 把"/"替换成"."
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    // 如果可以迭代下去 并且是一个包
                    if ((idx != -1) || recursive) {
                        // 如果是一个.class文件 而且不是目录
                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            // 去掉后面的".class" 获取真正的类名
                            String className = name.substring(packageName.length() + 1, name.length() - 6);
                            try {
                                // 添加到classes

                                boolean flag = true;
                                if (packArr.length > 1) {
                                    for (int i = 1; i < packArr.length; i++) {
                                        if (packageName.indexOf(packArr[i]) <= -1) {
                                            flag = flag & false;
                                        }
                                        else {
                                            flag = flag & true;
                                        }
                                    }
                                }

                                if (flag) {
                                    classes.add(Class.forName(packageName + '.' + className));
                                }
                            }
                            catch (ClassNotFoundException e) {
                                logger.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error("在扫描用户定义视图时从jar包获取文件出错", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param s <br>
     * @return <br>
     */
    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) {
                        sb.append(GlobalConstants.UNDERLINE);
                    }
                }
                upperCase = true;
            }
            else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param s <br>
     * @return <br>
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == GlobalConstants.UNDERLINE) {
                upperCase = true;
            }
            else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            }
            else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param s <br>
     * @return <br>
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param clazz <br>
     * @return <br>
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return BeanUtils.isSimpleValueType(clazz);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param clazz <br>
     * @return <br>
     */
    public static boolean isSimpleProperty(Class<?> clazz) {
        return BeanUtils.isSimpleProperty(clazz);
    }
}
