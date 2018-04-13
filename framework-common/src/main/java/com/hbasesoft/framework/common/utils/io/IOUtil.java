/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月5日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOUtil {

    /**
     * Description: 复制文件<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param src
     * @param dist <br>
     */
    public static void copyFile(File src, File dist) {
        OutputStream out = null;
        InputStream in = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(dist));
            in = new BufferedInputStream(new FileInputStream(src));
            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR_10029, e);
        }
        finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Description: 复制文件进流<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param filePath
     * @param in <br>
     */
    public static void copyFileFromInputStream(String filePath, InputStream in) {
        copyFileFromInputStream(filePath, in, null);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param filePath <br>
     * @param in <br>
     * @param charset <br>
     * @throws UtilException <br>
     */
    public static void copyFileFromInputStream(String filePath, InputStream in, String charset) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath));
            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR_10029, e);
        }
        finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param in <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static String readString(InputStream in) {
        try {
            return IOUtils.toString(in);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.READ_PARAM_ERROR_10027, e);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param in <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static String readString(Reader in) {
        try {
            return IOUtils.toString(in);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.READ_PARAM_ERROR_10027, e);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Description: readFile<br>
     * 
     * @author 王伟 <br>
     * @param filePath <br>
     * @return <br>
     * @throws IOException <br>
     */
    public static String readFile(String filePath) throws IOException {
        return readFile(new File(filePath));
    }

    /**
     * Description: readFile<br>
     * 
     * @author 王伟 <br>
     * @param filePath <br>
     * @return <br>
     * @throws IOException <br>
     */
    public static <T> List<T> readFile(String filePath, LineTransfer<T> transfer) throws IOException {
        return readFile(new File(filePath), transfer);
    }

    /**
     * Description: readFile<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param file
     * @return
     * @throws IOException <br>
     */
    public static String readFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            return readString(new BufferedReader(new FileReader(file)));
        }
        return null;
    }

    public static <T> List<T> readFile(File file, LineTransfer<T> transfer) {
        List<T> list = new ArrayList<T>();
        if (file.exists() && file.isFile()) {
            BufferedReader in = null;
            String line = null;
            try {
                in = new BufferedReader(new FileReader(file));
                while ((line = in.readLine()) != null) {
                    T t = transfer.transfer(line);
                    if (t != null) {
                        list.add(t);
                    }
                }
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR_10029, e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }
        }
        return list;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param content <br>
     * @param file <br>
     * @throws UtilException <br>
     */
    public static void writeFile(byte[] content, File file) {
        if (file != null) {
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
                out.write(content);
                out.flush();
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR_10029, e);
            }
            finally {
                IOUtils.closeQuietly(out);
            }
        }
    }

    /**
     * Description: writeFile<br>
     * 
     * @author 王伟 <br>
     * @param contents <br>
     * @param file <br>
     * @throws UtilException <br>
     */
    public static void writeFile(String contents, File file) {
        if (file != null) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(file));
                out.write(contents);
                out.flush();
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR_10029, e);
            }
            finally {
                IOUtils.closeQuietly(out);
            }
        }
    }
}
