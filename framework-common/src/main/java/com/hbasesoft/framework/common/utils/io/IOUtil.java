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
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
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
     * template filePath
     */
    private static String tempFileDir = PropertyHolder.getProperty("server.fileupload.filePath",
        System.getProperty("user.home")) + "/uploadFiles/temp";

    /**
     * Description: 复制文件<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param src
     * @param dist <br>
     */
    public static void copyFile(final File src, final File dist) {
        OutputStream out = null;
        InputStream in = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(dist));
            in = new BufferedInputStream(new FileInputStream(src));
            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR, e);
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
    public static void copyFileFromInputStream(final String filePath, final InputStream in) {
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
    public static void copyFileFromInputStream(final String filePath, final InputStream in, final String charset) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath));
            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR, e);
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
    public static String readString(final InputStream in) {
        try {
            return IOUtils.toString(in, GlobalConstants.DEFAULT_CHARSET);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.READ_PARAM_ERROR, e);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Description: 读取包下面的数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fileName
     * @param clz
     * @return <br>
     */
    public static String readPackageString(final String fileName, final Class<?> clz) {
        String path = StringUtils
            .replace(clz.getName().substring(0, clz.getName().length() - clz.getSimpleName().length()), ".", "/");
        return readString(clz.getResourceAsStream(path + fileName));
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
    public static String readString(final Reader in) {
        try {
            return IOUtils.toString(in);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.READ_PARAM_ERROR, e);
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
    public static String readFile(final String filePath) throws IOException {
        return readFile(new File(filePath));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param filePath
     * @param transfer
     * @param <T> T
     * @return List
     * @throws IOException <br>
     */
    public static <T> List<T> readFile(final String filePath, final Function<String, ? extends T> transfer)
        throws IOException {
        return readFile(new File(filePath), transfer);
    }

    /**
     * Description: readFile<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param file
     * @return 文件内容
     * @throws IOException <br>
     */
    public static String readFile(final File file) throws IOException {
        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return readString(reader);
            }
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param file
     * @param transfer
     * @param <T> T
     * @return <br>
     */
    public static <T> List<T> readFile(final File file, final Function<String, ? extends T> transfer) {
        List<T> list = new ArrayList<T>();
        if (file.exists() && file.isFile()) {
            BufferedReader in = null;
            String line = null;
            try {
                in = new BufferedReader(new FileReader(file));
                while ((line = in.readLine()) != null) {
                    T t = transfer.apply(line);
                    if (t != null) {
                        list.add(t);
                    }
                }
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR, e);
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
     * @author 王伟<br>
     * @taskId <br>
     * @param file
     * @param batchProcessor <br>
     */
    public static void batchProcessFile(final File file, final BatchProcessor<String> batchProcessor) {
        batchProcessFile(file, s -> s, batchProcessor);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param file
     * @param transfer
     * @param <T> T
     * @param batchProcessor <br>
     */
    public static <T> void batchProcessFile(final File file, final Function<String, ? extends T> transfer,
        final BatchProcessor<T> batchProcessor) {
        batchProcessFile(file, transfer, batchProcessor, GlobalConstants.DEFAULT_LINES);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param file
     * @param transfer
     * @param batchProcessor
     * @param pageSize <br>
     * @param <T> T
     */
    public static <T> void batchProcessFile(final File file, final Function<String, ? extends T> transfer,
        final BatchProcessor<T> batchProcessor, final int pageSize) {
        if (file.exists() && file.isFile()) {
            BufferedReader in = null;
            String line = null;
            try {
                in = new BufferedReader(new FileReader(file));
                List<T> list = new ArrayList<>();
                int i = 0;
                while ((line = in.readLine()) != null) {
                    T t = transfer.apply(line);
                    if (t != null) {
                        list.add(t);
                    }

                    if (list.size() >= pageSize) {
                        boolean result = batchProcessor.process(list, ++i, pageSize);
                        list.clear();
                        if (!result) {
                            break;
                        }
                    }
                }
                if (list.size() != 0) {
                    batchProcessor.process(list, ++i, pageSize);
                    list.clear();
                }
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR, e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }
        }
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
    public static void writeFile(final byte[] content, final File file) {
        if (file != null) {
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
                out.write(content);
                out.flush();
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR, e);
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
    public static void writeFile(final String contents, final File file) {
        if (file != null) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(file));
                out.write(contents);
                out.flush();
            }
            catch (Exception e) {
                throw new UtilException(ErrorCodeDef.WRITE_FILE_ERROR, e);
            }
            finally {
                IOUtils.closeQuietly(out);
            }
        }
    }

    /**
     * Description: 创建临时文件 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static File createTempFile() {
        File dir = new File(tempFileDir);
        if (!dir.exists() || dir.isFile()) {
            if (!dir.mkdirs()) {
                throw new UtilException(ErrorCodeDef.CREATE_TEMP_FILE_ERROR, dir.getAbsolutePath());
            }
        }
        return new File(dir, CommonUtil.getTransactionID());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param tempFileDir <br>
     */
    public static void setTempFileDir(final String tempFileDir) {
        IOUtil.tempFileDir = tempFileDir;
    }

}
