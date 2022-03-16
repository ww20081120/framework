/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.ArrayUtils;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Mar 2, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.io <br>
 */
public class JarFileSpliterator implements Spliterator<String> {
    private final String prefix;

    private Enumeration<URL> dirs;

    private byte subState = -1;

    private URL current;

    private Enumeration<JarEntry> entries;

    private Stack<File> files;

    private int filePrefixLength;

    public JarFileSpliterator(String prefix) throws IOException {
        this.prefix = prefix;
        dirs = this.getClass().getClassLoader().getResources(prefix);
    }

    private String getNextURL() throws IOException {
        // 循环便利第一层
        if (current == null) {
            if (!dirs.hasMoreElements()) {
                return null;
            }
            current = dirs.nextElement();
        }

        // 有没有开始循环第一层的url了
        if (subState == -1) {
            initFind();
        }

        String url = null;

        // 查看文件夹的数据
        if (subState == 0) {
            url = findInFiles();
        }

        // 查看jar包内的数据
        else if (subState == 1) {
            url = findInJar();
        }

        if (url == null) {
            restroy();
            if (dirs.hasMoreElements()) {
                url = getNextURL();
            }
        }
        return url;
    }

    private void restroy() {
        subState = -1;
        current = null;
        entries = null;
        files = null;
        filePrefixLength = 0;
    }

    private String findInJar() throws IOException {
        if (entries == null) {
            // 获取jar
            JarFile jar = ((JarURLConnection) current.openConnection()).getJarFile();
            // 从此jar包 得到一个枚举类
            entries = jar.entries();
        }

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!entry.isDirectory() && name.startsWith(prefix)) {
                return name;
            }
        }
        return null;
    }

    private String findInFiles() throws IOException {
        if (files == null) {
            files = new Stack<File>();
            String filePath = URLDecoder.decode(current.getFile(), "UTF-8");
            File f = new File(filePath);
            filePrefixLength = f.getAbsolutePath().length() - prefix.length();
            files.push(f);
        }

        while (!files.empty()) {
            File f = files.pop();
            if (f.exists()) {
                if (f.isDirectory()) {
                    File[] fs = f.listFiles();
                    if (ArrayUtils.isNotEmpty(fs)) {
                        for (File tf : fs) {
                            files.push(tf);
                        }
                    }
                }
                else {
                    return f.getAbsolutePath().substring(filePrefixLength);
                }
            }
        }
        return null;
    }

    private void initFind() {
        // 得到协议的名称
        String protocol = current.getProtocol();
        if ("file".equals(protocol)) {
            subState = 0;
        }
        else if ("jar".equals(protocol)) {
            subState = 1;
        }
        else {
            current = null;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param action
     * @return <br>
     */
    @Override
    public boolean tryAdvance(Consumer<? super String> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        try {
            String url = getNextURL();
            if (url != null) {
                action.accept(url);
                return true;
            }
        }
        catch (Exception e) {
            throw new UtilException(e);
        }
        return false;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Spliterator<String> trySplit() {
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public long estimateSize() {
        return dirs != null && dirs.hasMoreElements() ? -1 : 0;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int characteristics() {
        return Spliterator.DISTINCT | Spliterator.NONNULL;
    }
}
