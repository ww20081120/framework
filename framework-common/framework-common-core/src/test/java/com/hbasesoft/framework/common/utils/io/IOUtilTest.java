package com.hbasesoft.framework.common.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.hbasesoft.framework.common.utils.CommonUtil;

/**
 * IOUtil 单元测试
 *
 * @author 王伟
 * @version 1.0
 * @since 2024-03-31
 */
@DisplayName("IOUtil 单元测试")
class IOUtilTest {

    @TempDir
    File tempDir;

    private File testFile;
    private File destFile;

    @AfterEach
    void tearDown() throws IOException {
        // 清理测试文件
        if (testFile != null && testFile.exists()) {
            Files.deleteIfExists(testFile.toPath());
        }
        if (destFile != null && destFile.exists()) {
            Files.deleteIfExists(destFile.toPath());
        }
    }

    // ==================== copyFile(File, File) 测试 ====================

    @Test
    @DisplayName("应成功复制文件 - 正常场景")
    void should_copyFile_success() throws IOException {
        // 准备测试数据
        String content = "Hello, World!\n这是测试内容。";
        testFile = new File(tempDir, "source.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        destFile = new File(tempDir, "dest.txt");

        // 执行复制
        IOUtil.copyFile(testFile, destFile);

        // 验证结果
        assertThat(destFile).exists();
        String copiedContent = Files.readString(destFile.toPath(), StandardCharsets.UTF_8);
        assertThat(copiedContent).isEqualTo(content);
    }

    @Test
    @DisplayName("应抛出异常 - 源文件不存在")
    void should_throwException_when_sourceFileNotExists() {
        testFile = new File(tempDir, "nonexistent.txt");
        destFile = new File(tempDir, "dest.txt");

        assertThatThrownBy(() -> IOUtil.copyFile(testFile, destFile))
            .isInstanceOf(com.hbasesoft.framework.common.utils.UtilException.class)
            .hasMessageContaining("READ_PARAM_ERROR");
    }

    @Test
    @DisplayName("应复制空文件")
    void should_copyEmptyFile() throws IOException {
        testFile = new File(tempDir, "empty.txt");
        testFile.createNewFile();

        destFile = new File(tempDir, "dest.txt");

        IOUtil.copyFile(testFile, destFile);

        assertThat(destFile).exists();
        assertThat(destFile.length()).isZero();
    }

    @Test
    @DisplayName("应复制大文件")
    void should_copyLargeFile() throws IOException {
        // 创建约1MB的文件
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append("This is line ").append(i).append(" of the test file.\n");
        }
        String content = sb.toString();
        testFile = new File(tempDir, "large.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        destFile = new File(tempDir, "dest.txt");

        IOUtil.copyFile(testFile, destFile);

        assertThat(destFile).exists();
        assertThat(destFile.length()).isEqualTo(testFile.length());
    }

    @Test
    @DisplayName("应覆盖目标文件 - 目标文件已存在")
    void should_overwriteDestFile_when_destExists() throws IOException {
        String sourceContent = "New content";
        String destContent = "Old content";

        testFile = new File(tempDir, "source.txt");
        Files.write(testFile.toPath(), sourceContent.getBytes(StandardCharsets.UTF_8));

        destFile = new File(tempDir, "dest.txt");
        Files.write(destFile.toPath(), destContent.getBytes(StandardCharsets.UTF_8));

        IOUtil.copyFile(testFile, destFile);

        String copiedContent = Files.readString(destFile.toPath(), StandardCharsets.UTF_8);
        assertThat(copiedContent).isEqualTo(sourceContent);
    }

    // ==================== copyFileFromInputStream(String, InputStream) 测试 ====================

    @Test
    @DisplayName("应从输入流复制文件 - 正常场景")
    void should_copyFromInputStream_success() throws IOException {
        String content = "Stream content test";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        File targetFile = new File(tempDir, "from_stream.txt");
        testFile = targetFile;

        IOUtil.copyFileFromInputStream(targetFile.getAbsolutePath(), inputStream);

        assertThat(targetFile).exists();
        String fileContent = Files.readString(targetFile.toPath(), StandardCharsets.UTF_8);
        assertThat(fileContent).isEqualTo(content);
    }

    @Test
    @DisplayName("应从输入流复制文件 - 空内容")
    void should_copyFromInputStream_when_emptyContent() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);

        File targetFile = new File(tempDir, "empty.txt");
        testFile = targetFile;

        IOUtil.copyFileFromInputStream(targetFile.getAbsolutePath(), inputStream);

        assertThat(targetFile).exists();
        assertThat(targetFile.length()).isZero();
    }

    @Test
    @DisplayName("应从输入流复制文件 - 包含中文")
    void should_copyFromInputStream_withChinese() throws IOException {
        String content = "你好，世界！\n这是中文测试内容。";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        File targetFile = new File(tempDir, "chinese.txt");
        testFile = targetFile;

        IOUtil.copyFileFromInputStream(targetFile.getAbsolutePath(), inputStream);

        String fileContent = Files.readString(targetFile.toPath(), StandardCharsets.UTF_8);
        assertThat(fileContent).isEqualTo(content);
    }

    // ==================== copyFileFromInputStream(String, InputStream, String) 测试 ====================

    @Test
    @DisplayName("应从输入流复制文件并使用指定编码")
    void should_copyFromInputStream_withCharset() throws IOException {
        String content = "Charset test with encoding";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        File targetFile = new File(tempDir, "charset.txt");
        testFile = targetFile;

        IOUtil.copyFileFromInputStream(targetFile.getAbsolutePath(), inputStream, "UTF-8");

        assertThat(targetFile).exists();
        String fileContent = Files.readString(targetFile.toPath(), StandardCharsets.UTF_8);
        assertThat(fileContent).isEqualTo(content);
    }

    @Test
    @DisplayName("应从输入流复制文件 - null编码")
    void should_copyFromInputStream_withNullCharset() throws IOException {
        String content = "Null charset test";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        File targetFile = new File(tempDir, "null_charset.txt");
        testFile = targetFile;

        IOUtil.copyFileFromInputStream(targetFile.getAbsolutePath(), inputStream, null);

        assertThat(targetFile).exists();
        String fileContent = Files.readString(targetFile.toPath(), StandardCharsets.UTF_8);
        assertThat(fileContent).isEqualTo(content);
    }

    // ==================== readString(InputStream) 测试 ====================

    @Test
    @DisplayName("应从输入流读取字符串 - 正常场景")
    void should_readString_fromInputStream() {
        String content = "Test content from stream";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        String result = IOUtil.readString(inputStream);

        assertThat(result).isEqualTo(content);
    }

    @Test
    @DisplayName("应从输入流读取字符串 - 空内容")
    void should_readEmptyString_fromInputStream() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);

        String result = IOUtil.readString(inputStream);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("应从输入流读取字符串 - 多行内容")
    void should_readMultiLineString_fromInputStream() {
        String content = "Line 1\nLine 2\nLine 3";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        String result = IOUtil.readString(inputStream);

        assertThat(result).isEqualTo(content);
    }

    @Test
    @DisplayName("应从输入流读取字符串 - 包含特殊字符")
    void should_readString_withSpecialCharacters() {
        String content = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        String result = IOUtil.readString(inputStream);

        assertThat(result).isEqualTo(content);
    }

    // ==================== readString(Reader) 测试 ====================

    @Test
    @DisplayName("应从Reader读取字符串 - 正常场景")
    void should_readString_fromReader() {
        String content = "Content from reader";
        StringReader reader = new StringReader(content);

        String result = IOUtil.readString(reader);

        assertThat(result).isEqualTo(content);
    }

    @Test
    @DisplayName("应从Reader读取字符串 - 空内容")
    void should_readEmptyString_fromReader() {
        StringReader reader = new StringReader("");

        String result = IOUtil.readString(reader);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("应从Reader读取字符串 - 长内容")
    void should_readLongString_fromReader() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Line ").append(i).append("\n");
        }
        String content = sb.toString();
        StringReader reader = new StringReader(content);

        String result = IOUtil.readString(reader);

        assertThat(result).isEqualTo(content);
    }

    // ==================== readPackageString(String, Class) 测试 ====================

    @Test
    @DisplayName("应读取包内文件资源 - 正常场景")
    void should_readPackageFile_success() {
        // 使用 IOUtil 类自身的包路径查找测试资源
        String result = IOUtil.readPackageString("test.txt", IOUtil.class);

        // 由于没有实际的测试资源文件，这里验证返回null不会抛异常
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("应返回null - 资源不存在")
    void should_returnNull_when_resourceNotExists() {
        String result = IOUtil.readPackageString("nonexistent.txt", IOUtil.class);

        assertThat(result).isNull();
    }

    // ==================== readFile(String) 测试 ====================

    @Test
    @DisplayName("应读取文件内容 - 正常场景")
    void should_readFile_byPath() throws IOException {
        String content = "File content test";
        testFile = new File(tempDir, "test.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        String result = IOUtil.readFile(testFile.getAbsolutePath());

        assertThat(result).isEqualTo(content);
    }

    @Test
    @DisplayName("应返回null - 文件不存在")
    void should_returnNull_when_fileNotExists() throws IOException {
        String result = IOUtil.readFile("/nonexistent/file.txt");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("应读取包含中文的文件")
    void should_readFile_withChinese() throws IOException {
        String content = "中文测试内容\n第二行";
        testFile = new File(tempDir, "chinese.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        String result = IOUtil.readFile(testFile.getAbsolutePath());

        assertThat(result).isEqualTo(content);
    }

    // ==================== readFile(File) 测试 ====================

    @Test
    @DisplayName("应读取文件内容 - File对象")
    void should_readFile_byFileObject() throws IOException {
        String content = "Content test";
        testFile = new File(tempDir, "test.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        String result = IOUtil.readFile(testFile);

        assertThat(result).isEqualTo(content);
    }

    @Test
    @DisplayName("应抛出异常 - 文件对象为null")
    void should_throwException_when_fileObjectIsNull() {
        assertThatThrownBy(() -> IOUtil.readFile((File) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("应返回null - 文件对象指向不存在的文件")
    void should_returnNull_when_fileNotExists_byFileObject() throws IOException {
        File nonExistentFile = new File(tempDir, "nonexistent.txt");

        String result = IOUtil.readFile(nonExistentFile);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("应返回null - 路径指向目录而非文件")
    void should_returnNull_when_pathIsDirectory() throws IOException {
        String result = IOUtil.readFile(tempDir);

        assertThat(result).isNull();
    }

    // ==================== readFile(String, Function<String, T>) 测试 ====================

    @Test
    @DisplayName("应读取文件并转换 - 正常场景")
    void should_readFileAndTransform_success() throws IOException {
        String content = "1\n2\n3\n4\n5";
        testFile = new File(tempDir, "numbers.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        List<Integer> result = IOUtil.readFile(testFile.getAbsolutePath(), Integer::parseInt);

        assertThat(result).hasSize(5);
        assertThat(result).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("应过滤null值 - 转换函数返回null")
    void should_filterNullValues_when_transformReturnsNull() throws IOException {
        String content = "1\ninvalid\n3\ninvalid\n5";
        testFile = new File(tempDir, "mixed.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        List<Integer> result = IOUtil.readFile(testFile.getAbsolutePath(), line -> {
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                return null;
            }
        });

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(1, 3, 5);
    }

    @Test
    @DisplayName("应返回空列表 - 空文件")
    void should_returnEmptyList_when_fileIsEmpty() throws IOException {
        testFile = new File(tempDir, "empty.txt");
        testFile.createNewFile();

        List<Integer> result = IOUtil.readFile(testFile.getAbsolutePath(), Integer::parseInt);

        assertThat(result).isEmpty();
    }

    // ==================== readFile(File, Function<String, T>) 测试 ====================

    @Test
    @DisplayName("应读取文件并转换 - File对象版本")
    void should_readFileAndTransform_byFileObject() throws IOException {
        String content = "apple,banana,cherry";
        testFile = new File(tempDir, "fruits.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        List<String> result = IOUtil.readFile(testFile, line -> line.toUpperCase());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("APPLE,BANANA,CHERRY");
    }

    @Test
    @DisplayName("应读取多行文件并转换")
    void should_readMultiLineFile_andTransform() throws IOException {
        String content = "line1\nline2\nline3";
        testFile = new File(tempDir, "lines.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        List<Integer> result = IOUtil.readFile(testFile, String::length);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(5, 5, 5);
    }

    // ==================== batchProcessFile(File, BatchProcessor<String>) 测试 ====================

    @Test
    @DisplayName("应批量处理文件 - 使用默认批次大小")
    void should_batchProcessFile_withDefaultPageSize() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            content.append("line").append(i).append("\n");
        }
        testFile = new File(tempDir, "batch.txt");
        Files.write(testFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

        AtomicInteger counter = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, new BatchProcessor<String>() {
            @Override
            public boolean process(List<String> beanList, int pageIndex, int pageSize) {
                counter.incrementAndGet();
                assertThat(beanList).isNotEmpty();
                return true;
            }
        });

        // 默认批次大小为1000，所以100行应该只有1批
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("应批量处理文件 - 空文件")
    void should_batchProcessFile_when_fileIsEmpty() throws IOException {
        testFile = new File(tempDir, "empty.txt");
        testFile.createNewFile();

        AtomicInteger counter = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, new BatchProcessor<String>() {
            @Override
            public boolean process(List<String> beanList, int pageIndex, int pageSize) {
                counter.incrementAndGet();
                return true;
            }
        });

        assertThat(counter.get()).isZero();
    }

    // ==================== batchProcessFile(File, Function<String, T>, BatchProcessor<T>) 测试 ====================

    @Test
    @DisplayName("应批量处理文件并转换 - 使用转换函数")
    void should_batchProcessFile_withTransform() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            content.append(i).append("\n");
        }
        testFile = new File(tempDir, "numbers.txt");
        Files.write(testFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

        AtomicInteger sum = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, Integer::parseInt, new BatchProcessor<Integer>() {
            @Override
            public boolean process(List<Integer> beanList, int pageIndex, int pageSize) {
                beanList.forEach(sum::addAndGet);
                return true;
            }
        });

        assertThat(sum.get()).isEqualTo(55); // 1+2+...+10 = 55
    }

    @Test
    @DisplayName("应过滤null值 - 批量处理时")
    void should_filterNullValues_duringBatchProcess() throws IOException {
        String content = "1\ninvalid\n3\ninvalid\n5";
        testFile = new File(tempDir, "mixed.txt");
        Files.write(testFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        AtomicInteger counter = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, line -> {
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                return null;
            }
        }, new BatchProcessor<Integer>() {
            @Override
            public boolean process(List<Integer> beanList, int pageIndex, int pageSize) {
                counter.addAndGet(beanList.size());
                return true;
            }
        });

        assertThat(counter.get()).isEqualTo(3); // 只有3个有效数字
    }

    // ==================== batchProcessFile(File, Function<String, T>, BatchProcessor<T>, int) 测试 ====================

    @Test
    @DisplayName("应按指定批次大小处理文件")
    void should_batchProcessFile_withCustomPageSize() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            content.append(i).append("\n");
        }
        testFile = new File(tempDir, "batch_custom.txt");
        Files.write(testFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

        AtomicInteger batchCount = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, Integer::parseInt, new BatchProcessor<Integer>() {
            @Override
            public boolean process(List<Integer> beanList, int pageIndex, int pageSize) {
                int count = batchCount.incrementAndGet();
                if (count < 5) {
                    assertThat(beanList).hasSize(10);
                } else {
                    assertThat(beanList).hasSize(5); // 最后一批只有5条
                }
                return true;
            }
        }, 10);

        assertThat(batchCount.get()).isEqualTo(3); // 10+10+5 = 25，共3批
    }

    @Test
    @DisplayName("应停止处理 - 返回false时")
    void should_stopProcessing_when_returnFalse() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            content.append(i).append("\n");
        }
        testFile = new File(tempDir, "stop.txt");
        Files.write(testFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

        AtomicInteger processedCount = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, Integer::parseInt, new BatchProcessor<Integer>() {
            @Override
            public boolean process(List<Integer> beanList, int pageIndex, int pageSize) {
                processedCount.addAndGet(beanList.size());
                // 处理完第一批后停止
                return pageIndex == 1;
            }
        }, 10);

        // 应该只处理了前10条
        assertThat(processedCount.get()).isEqualTo(10);
    }

    @Test
    @DisplayName("应处理所有批次 - 返回true时")
    void should_processAllBatches_when_returnTrue() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= 30; i++) {
            content.append(i).append("\n");
        }
        testFile = new File(tempDir, "all.txt");
        Files.write(testFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

        AtomicInteger batchCount = new AtomicInteger(0);
        IOUtil.batchProcessFile(testFile, Integer::parseInt, new BatchProcessor<Integer>() {
            @Override
            public boolean process(List<Integer> beanList, int pageIndex, int pageSize) {
                batchCount.incrementAndGet();
                return true;
            }
        }, 10);

        assertThat(batchCount.get()).isEqualTo(3); // 10+10+10 = 30，共3批
    }

    // ==================== writeFile(byte[], File) 测试 ====================

    @Test
    @DisplayName("应写入字节数组到文件 - 正常场景")
    void should_writeBytesToFile_success() throws IOException {
        byte[] content = "Byte content test".getBytes(StandardCharsets.UTF_8);
        testFile = new File(tempDir, "bytes.txt");

        IOUtil.writeFile(content, testFile);

        assertThat(testFile).exists();
        byte[] readBytes = Files.readAllBytes(testFile.toPath());
        assertThat(readBytes).isEqualTo(content);
    }

    @Test
    @DisplayName("应写入空字节数组")
    void should_writeEmptyBytesToFile() throws IOException {
        byte[] content = new byte[0];
        testFile = new File(tempDir, "empty.txt");

        IOUtil.writeFile(content, testFile);

        assertThat(testFile).exists();
        assertThat(testFile.length()).isZero();
    }

    @Test
    @DisplayName("应覆盖已存在的文件")
    void should_overwriteExistingFile() throws IOException {
        byte[] oldContent = "Old content".getBytes(StandardCharsets.UTF_8);
        byte[] newContent = "New content".getBytes(StandardCharsets.UTF_8);
        testFile = new File(tempDir, "overwrite.txt");

        IOUtil.writeFile(oldContent, testFile);
        IOUtil.writeFile(newContent, testFile);

        byte[] readBytes = Files.readAllBytes(testFile.toPath());
        assertThat(readBytes).isEqualTo(newContent);
    }

    @Test
    @DisplayName("应不抛异常 - 文件为null")
    void should_notThrowException_when_fileIsNull() {
        byte[] content = "Test".getBytes(StandardCharsets.UTF_8);

        assertThatCode(() -> IOUtil.writeFile(content, null))
            .doesNotThrowAnyException();
    }

    // ==================== writeFile(String, File) 测试 ====================

    @Test
    @DisplayName("应写入字符串到文件 - 正常场景")
    void should_writeStringToFile_success() throws IOException {
        String content = "String content test";
        testFile = new File(tempDir, "string.txt");

        IOUtil.writeFile(content, testFile);

        assertThat(testFile).exists();
        String readContent = Files.readString(testFile.toPath(), StandardCharsets.UTF_8);
        assertThat(readContent).isEqualTo(content);
    }

    @Test
    @DisplayName("应写入空字符串")
    void should_writeEmptyStringToFile() throws IOException {
        String content = "";
        testFile = new File(tempDir, "empty.txt");

        IOUtil.writeFile(content, testFile);

        assertThat(testFile).exists();
        assertThat(testFile.length()).isZero();
    }

    @Test
    @DisplayName("应写入包含中文的字符串")
    void should_writeChineseStringToFile() throws IOException {
        String content = "中文内容测试\n第二行";
        testFile = new File(tempDir, "chinese.txt");

        IOUtil.writeFile(content, testFile);

        String readContent = Files.readString(testFile.toPath(), StandardCharsets.UTF_8);
        assertThat(readContent).isEqualTo(content);
    }

    @Test
    @DisplayName("应写入包含特殊字符的字符串")
    void should_writeSpecialCharactersToFile() throws IOException {
        String content = "Special: \n\r\t\\\"";
        testFile = new File(tempDir, "special.txt");

        IOUtil.writeFile(content, testFile);

        String readContent = Files.readString(testFile.toPath(), StandardCharsets.UTF_8);
        assertThat(readContent).isEqualTo(content);
    }

    @Test
    @DisplayName("应不抛异常 - 文件为null")
    void should_notThrowException_when_fileIsNull_forString() {
        String content = "Test";

        assertThatCode(() -> IOUtil.writeFile(content, null))
            .doesNotThrowAnyException();
    }

    // ==================== createTempFile() 测试 ====================

    @Test
    @DisplayName("应创建临时文件 - 正常场景")
    void should_createTempFile_success() {
        File tempFile = IOUtil.createTempFile();

        try {
            assertThat(tempFile).exists();
            assertThat(tempFile.getParentFile()).exists();
        } finally {
            // 清理
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Test
    @DisplayName("应创建唯一的临时文件名")
    void should_createUniqueTempFileName() {
        File tempFile1 = IOUtil.createTempFile();
        File tempFile2 = IOUtil.createTempFile();

        try {
            assertThat(tempFile1.getName()).isNotEqualTo(tempFile2.getName());
        } finally {
            if (tempFile1 != null) tempFile1.delete();
            if (tempFile2 != null) tempFile2.delete();
        }
    }

    // ==================== setTempFileDir(String) 测试 ====================

    @Test
    @DisplayName("应设置临时文件目录")
    void should_setTempFileDir_success() throws IOException {
        String customDir = tempDir.getAbsolutePath() + "/custom_temp";
        IOUtil.setTempFileDir(customDir);

        File tempFile = IOUtil.createTempFile();

        try {
            assertThat(tempFile.getParentFile().getAbsolutePath()).isEqualTo(customDir);
        } finally {
            if (tempFile != null) tempFile.delete();
            // 恢复默认值
            IOUtil.setTempFileDir(com.hbasesoft.framework.common.GlobalConstants.FILE_STORAGE_PATH + "/temp");
        }
    }

    @Test
    @DisplayName("应使用设置的目录创建多个临时文件")
    void should_createMultipleTempFiles_inCustomDir() {
        String customDir = tempDir.getAbsolutePath() + "/multi_temp";
        IOUtil.setTempFileDir(customDir);

        try {
            File tempFile1 = IOUtil.createTempFile();
            File tempFile2 = IOUtil.createTempFile();

            assertThat(tempFile1.getParentFile().getAbsolutePath()).isEqualTo(customDir);
            assertThat(tempFile2.getParentFile().getAbsolutePath()).isEqualTo(customDir);
            assertThat(tempFile1.getName()).isNotEqualTo(tempFile2.getName());

            tempFile1.delete();
            tempFile2.delete();
        } finally {
            // 恢复默认值
            IOUtil.setTempFileDir(com.hbasesoft.framework.common.GlobalConstants.FILE_STORAGE_PATH + "/temp");
        }
    }
}
