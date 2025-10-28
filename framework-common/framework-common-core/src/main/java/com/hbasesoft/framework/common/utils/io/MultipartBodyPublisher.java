/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * <Description> HTTP多部分请求体发布器，用于构建multipart/form-data格式的请求体 <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年10月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.io <br>
 */
public class MultipartBodyPublisher {
    /** 缓冲区大小 */
    private static final int BUFFER_SIZE = 8192;

    /** 部件规范列表 */
    private List<PartsSpecification> partsSpecificationList = new ArrayList<>();

    /** 边界字符串 */
    private String boundary = UUID.randomUUID().toString(); // 生成唯一边界

    /**
     * 构建HTTP请求体发布器
     *
     * @return HttpRequest.BodyPublisher HTTP请求体发布器
     */
    public HttpRequest.BodyPublisher build() {
        if (partsSpecificationList.isEmpty()) {
            throw new IllegalStateException("必须至少有一个部件才能构建 multipart 消息。");
        }
        addFinalBoundaryPart(); // 添加结束边界
        // 返回一个按需生成字节数据的 BodyPublisher
        return HttpRequest.BodyPublishers.ofByteArrays(PartsIterator::new);
    }

    /**
     * 获取边界字符串
     *
     * @return 边界字符串
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * 添加文本参数部件
     *
     * @param name 参数名称
     * @param value 参数值
     * @return 当前实例，支持链式调用
     */
    public MultipartBodyPublisher addTextPart(final String name, final String value) {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.TYPE.STRING;
        newPart.name = name;
        newPart.value = value;
        partsSpecificationList.add(newPart);
        return this;
    }

    /**
     * 添加文件参数部件
     *
     * @param name 参数名称
     * @param filePath 文件路径
     * @param contentType 内容类型，可以为null
     * @return 当前实例，支持链式调用
     * @throws IOException 文件读取异常
     */
    public MultipartBodyPublisher addFilePart(final String name, final Path filePath,
        final String contentType) throws IOException {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.TYPE.FILE;
        newPart.name = name;
        newPart.path = filePath;
        newPart.contentType = (contentType != null) ? contentType : Files.probeContentType(filePath);
        if (newPart.contentType == null) {
            newPart.contentType = "application/octet-stream"; // 默认类型
        }
        partsSpecificationList.add(newPart);
        return this;
    }

    private void addFinalBoundaryPart() {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.TYPE.FINAL_BOUNDARY;
        newPart.value = "--" + boundary + "--";
        partsSpecificationList.add(newPart);
    }

    /**
     * 部件规格定义
     */
    static class PartsSpecification {
        /** 部件类型枚举 */
        enum TYPE {
            /** 字符串类型 */
            STRING,
            /** 文件类型 */
            FILE,
            /** 最终边界 */
            FINAL_BOUNDARY
        }

        /** 部件类型 */
        private TYPE type;

        /** 部件名称 */
        private String name;

        /** 部件值 */
        private String value;

        /** 文件路径 */
        private Path path;

        /** 内容类型 */
        private String contentType;
    }

    /**
     * 迭代器，用于按需生成每个部分的字节数组
     */
    class PartsIterator implements Iterator<byte[]> {
        /** 部件规范迭代器 */
        private Iterator<PartsSpecification> iter;

        /** 当前文件输入流 */
        private InputStream currentFileInput;

        /** 是否完成 */
        private boolean done;

        /** 下一个字节数组 */
        private byte[] next;

        PartsIterator() {
            iter = partsSpecificationList.iterator();
        }

        @Override
        public boolean hasNext() {
            if (done) {
                return false;
            }
            if (next != null) {
                return true;
            }
            try {
                next = computeNext();
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            if (next == null) {
                done = true;
                return false;
            }
            return true;
        }

        @Override
        public byte[] next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            byte[] res = next;
            next = null;
            return res;
        }

        /**
         * 计算下一个字节数组
         *
         * @return 下一个字节数组，如果没有更多数据则返回null
         * @throws IOException 文件读取异常
         */
        private byte[] computeNext() throws IOException {
            if (currentFileInput == null) {
                if (!iter.hasNext()) {
                    return null;
                }
                PartsSpecification nextPart = iter.next();
                // 处理文本部件
                if (PartsSpecification.TYPE.STRING.equals(nextPart.type)) {
                    String partHeader = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\""
                        + nextPart.name + "\"\r\n" + "Content-Type: text/plain; charset=UTF-8\r\n\r\n";
                    String partData = nextPart.value + "\r\n";
                    return (partHeader + partData).getBytes(StandardCharsets.UTF_8);
                }
                // 处理最终边界
                if (PartsSpecification.TYPE.FINAL_BOUNDARY.equals(nextPart.type)) {
                    return nextPart.value.getBytes(StandardCharsets.UTF_8);
                }
                // 处理文件部件
                if (PartsSpecification.TYPE.FILE.equals(nextPart.type)) {
                    String filename = nextPart.path.getFileName().toString();
                    String partHeader = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\""
                        + nextPart.name + "\"; filename=\"" + filename + "\"\r\n" + "Content-Type: "
                        + nextPart.contentType + "\r\n\r\n";
                    currentFileInput = Files.newInputStream(nextPart.path);
                    return partHeader.getBytes(StandardCharsets.UTF_8);
                }
            }
            else {
                // 读取文件内容
                byte[] buf = new byte[BUFFER_SIZE];
                int r = currentFileInput.read(buf);
                if (r > 0) {
                    byte[] actualBytes = new byte[r];
                    System.arraycopy(buf, 0, actualBytes, 0, r);
                    return actualBytes;
                }
                else {
                    currentFileInput.close();
                    currentFileInput = null;
                    return "\r\n".getBytes(StandardCharsets.UTF_8); // 文件读取结束，换行
                }
            }
            return null;
        }
    }
}
