package com.hbasesoft.framework.common.utils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> 将文件或文件夹压缩为zip格式， 以及将zip压缩文件解压 <br>
 *
 * @author 孙兆欣<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Create in 2018/6/5
 * @see com.hbasesoft.vcc.sgp.plat.configuration.util <br>
 * @since V1.0<br>
 */
public class FileZipUtil {
    /**
     *
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     *
     */
    private static final int BUFFER_LEN = 1024;

    /**
     * @param srcPath 要压缩的源文件路径。如果要压缩一个文件，则为文件的全路径；如果 要压缩一个目录，则为该目录的顶层目录路径
     * @param zipPath 压缩文件保存的路径。 zipPath不能是srcPath路径下的子文件夹
     * @param zipFileName 压缩文件名
     */
    public static void preZip(final String srcPath, final String zipPath, final String zipFileName) throws IOException {
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            File srcFile = new File(srcPath);
            // 判断压缩文件的路径是否为源文件路径的子文件夹，防止无限递归的出现
            if (srcFile.isDirectory() && zipPath.indexOf(srcPath) != -1) {
                Assert.isFalse(true, ErrorCodeDef.ZIP_PATH_IS_IN_SRC_PATH);
            }
            File zipDir = new File(zipPath);
            // 判断压缩文件的路径是否存在，不存在创建
            if (!zipDir.exists() || !zipDir.isDirectory()) {
                zipDir.mkdirs();
            }
            StringBuffer sb = new StringBuffer();
            String zipFilePath = sb.append(zipPath).append(File.separator).append(zipFileName).toString();
            File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                // 检查文件是否允许删除，不允许则抛出SecurityException
                zipFile.delete();
            }
            cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
            zos = new ZipOutputStream(cos);
            // 如果只是压缩一个文件则需要截取该文件的父目录
            String srcRootDir = srcPath;
            if (srcFile.isFile()) {
                int index = srcPath.lastIndexOf(File.separator);
                if (index != -1) {
                    srcRootDir = srcPath.substring(0, index);
                }
            }
            // 调用压缩方法
            zip(srcRootDir, srcFile, zos);
            zos.flush();
        }
        finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /**
     * 递归压缩文件夹
     *
     * @param srcRootDir
     * @param file
     * @param zos
     */
    public static void zip(final String srcRootDir, final File file, final ZipOutputStream zos) throws IOException {

        if (file == null) {
            return;
        }
        // 如果是文件直接压缩
        if (file.isFile()) {
            int count;
            int bufferLen = BUFFER_LEN;
            byte[] byteData = new byte[bufferLen];
            // 获取文件相对于压缩文件夹根目录的子路径，作为zipEntry的name
            String subPath = file.getAbsolutePath();
            int index = subPath.indexOf(srcRootDir);
            if (index != -1) {
                subPath = subPath.substring(srcRootDir.length());
            }
            ZipEntry entry = new ZipEntry(subPath);
            zos.putNextEntry(entry);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            try {
                while ((count = bis.read(byteData, 0, bufferLen)) != -1) {
                    zos.write(byteData, 0, count);
                }
            }
            finally {
                if (bis != null) {
                    bis.close();
                }
                if (zos != null) {
                    zos.closeEntry();
                }
            }
        }
        else {
            // 递归压缩文件夹的内容
            File[] childrenFiles = file.listFiles();
            if (childrenFiles != null) {
                for (File child : childrenFiles) {
                    zip(srcRootDir, child, zos);
                }
            }
        }
    }

    /***
     * 解压缩zip包
     *
     * @param zipFilePath zip文件的全路径
     * @param unzipFilePath 解压后文件的保存路径
     * @param includeZipFileName 解压缩后的文件路径是否包含压缩文件的文件名 true 包含， false 不包含
     */
    @SuppressWarnings("unchecked")
    public static void unzip(final String zipFilePath, final String unzipFilePath, final boolean includeZipFileName)
        throws IOException {
        String newUnzipFilePath = unzipFilePath;
        StringBuilder sb = new StringBuilder();
        File zipFile = new File(zipFilePath);
        if (includeZipFileName) {
            String fileName = zipFile.getName();
            if (StringUtils.isNotEmpty(fileName)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            newUnzipFilePath = sb.append(unzipFilePath).append(File.separator).append(fileName).toString();
            sb.delete(0, sb.length());
        }
        // 创建解压缩文件的保存路径
        File unzipFileDir = new File(newUnzipFilePath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            unzipFileDir.mkdirs();
        }
        // 开始解压
        ZipEntry entry = null;
        String entryFilePath = null;
        String entryDirPath = null;
        File entryFile = null;
        File entryDir = null;
        int index = 0;
        int count = 0;
        int bufferSize = BUFFER_SIZE;
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                // 压缩包中一个文件解压后保存的文件全路径
                entryFilePath = sb.append(newUnzipFilePath).append(File.separator).append(entry.getName()).toString();
                sb.delete(0, sb.length());
                index = entryFilePath.lastIndexOf(File.separator);
                if (index != -1) {
                    entryDirPath = entryFilePath.substring(0, index);
                }
                else {
                    entryDirPath = "";
                }
                entryDir = new File(entryDirPath);
                // 如果文件夹路径不存在，创建文件夹
                if (!entryDir.exists() || !entryDir.isDirectory()) {
                    entryDir.mkdirs();
                }
                // 创建解压文件
                entryFile = new File(entryFilePath);
                if (entryFile.exists()) {
                    // 检查文件是否允许删除，不允许抛出SecurityException
                    // 删除已存在的文件
                    entryFile.delete();
                }
                // 写入文件
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                    bis = new BufferedInputStream(zip.getInputStream(entry));
                    while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                    bos.flush();
                }
                finally {
                    if (bos != null) {
                        bos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                }
            }
        }
    }
}
