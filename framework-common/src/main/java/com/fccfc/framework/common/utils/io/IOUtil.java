/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.common.utils.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.utils.UtilException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月5日 <br>
 * @see com.fccfc.framework.core.utils <br>
 */
public final class IOUtil {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param filePath <br>
     * @param in <br>
     * @throws UtilException <br>
     */
    public static void copyFileFromInputStream(String filePath, InputStream in) throws UtilException {
        copyFileFromInputStream(filePath, in, null);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param filePath <br>
     * @param in <br>
     * @param charset <br>
     * @throws UtilException <br>
     */
    public static void copyFileFromInputStream(String filePath, InputStream in, String charset) throws UtilException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath));

            int len = 0;
            byte[] temp = new byte[1024];
            while ((len = in.read(temp)) != -1) {
                out.write(temp, 0, len);
            }
            out.flush();
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
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param in <br>
     * @return <br>
     * @throws UtilException <br>
     */
    public static String readString(InputStream in) throws UtilException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        String line = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.READ_PARAM_ERROR_10027, e);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }

        return sb.toString();
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
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            BufferedReader in = null;
            String line = null;
            try {
                in = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
            catch (Exception e) {
                throw new IOException(e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }

        }
        return null;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param content <br>
     * @param file <br>
     * @throws UtilException <br>
     */
    public static void writeFile(byte[] content, File file) throws UtilException {
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
    public static void writeFile(String contents, File file) throws UtilException {
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
