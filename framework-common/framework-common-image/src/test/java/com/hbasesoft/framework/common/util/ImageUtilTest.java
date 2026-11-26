package com.hbasesoft.framework.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * ImageUtil 单元测试
 *
 * @author 王伟
 * @version 1.0
 * @since 2024-03-31
 */
@DisplayName("ImageUtil 单元测试")
class ImageUtilTest {

    @TempDir
    Path tempDir;

    private File sourceFile;
    private File destFile;

    @AfterEach
    void tearDown() {
        // 清理测试文件
        if (sourceFile != null && sourceFile.exists()) {
            sourceFile.delete();
        }
        if (destFile != null && destFile.exists()) {
            destFile.delete();
        }
    }

    // ==================== pictureZoom(String, String, int) 测试 ====================

    @Test
    @DisplayName("应成功缩放图片 - JPG格式，指定高度")
    void should_pictureZoom_success_withJpgFormat() throws Exception {
        // 准备测试图片 - 200x300 的 JPG 图片
        sourceFile = createTestImage("source.jpg", 200, 300, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        // 执行缩放 - 指定高度为 150
        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 150);

        // 验证结果
        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        assertNotNull(result);
        assertEquals(150, result.getHeight());
        // 宽度应该按比例缩放，原图片是正方形裁剪后，所以应该也是 150
        assertEquals(150, result.getWidth());
    }

    @Test
    @DisplayName("应成功缩放图片 - PNG格式，转换为JPG")
    void should_pictureZoom_success_convertPngToJpg() throws Exception {
        // 准备测试图片 - PNG 格式
        sourceFile = createTestImage("source.png", 300, 200, "png");
        destFile = tempDir.resolve("output.png").toFile();

        // 执行缩放
        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 100);

        // 验证结果 - PNG 被转换为 JPG，thumbnailator 添加 .jpg 扩展名
        File actualOutput = tempDir.resolve("output.jpg").toFile();
        assertTrue(actualOutput.exists());
        BufferedImage result = ImageIO.read(actualOutput);
        assertNotNull(result);
        assertEquals(100, result.getHeight());
    }

    @Test
    @DisplayName("应成功缩放图片 - JPEG格式")
    void should_pictureZoom_success_withJpegFormat() throws Exception {
        // 准备测试图片 - JPEG 格式
        sourceFile = createTestImage("source.jpeg", 150, 250, "jpeg");
        destFile = tempDir.resolve("output.jpeg").toFile();

        // 执行缩放
        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 80);

        // 验证结果 - 输出为 JPG 格式
        File actualOutput = tempDir.resolve("output.jpg").toFile();
        assertTrue(actualOutput.exists());
        BufferedImage result = ImageIO.read(actualOutput);
        assertNotNull(result);
        assertEquals(80, result.getHeight());
    }

    @Test
    @DisplayName("应裁剪并缩放图片 - 宽度大于高度")
    void should_cropAndZoom_when_widthGreaterThanHeight() throws Exception {
        // 创建宽度大于高度的图片
        sourceFile = createTestImage("wide.jpg", 400, 200, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 100);

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        // 原图 400x200，应该裁剪为 200x200 的正方形，然后缩放到 100x100
        assertEquals(100, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    @Test
    @DisplayName("应裁剪并缩放图片 - 高度大于宽度")
    void should_cropAndZoom_when_heightGreaterThanWidth() throws Exception {
        // 创建高度大于宽度的图片
        sourceFile = createTestImage("tall.jpg", 200, 400, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 100);

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        // 原图 200x400，应该裁剪为 200x200 的正方形，然后缩放到 100x100
        assertEquals(100, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    @Test
    @DisplayName("应处理正方形图片")
    void should_handleSquareImage() throws Exception {
        // 创建正方形图片
        sourceFile = createTestImage("square.jpg", 300, 300, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 120);

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        assertEquals(120, result.getWidth());
        assertEquals(120, result.getHeight());
    }

    @Test
    @DisplayName("应抛出异常 - 源文件不存在")
    void should_throwException_when_sourceFileNotExists() {
        sourceFile = tempDir.resolve("nonexistent.jpg").toFile();
        destFile = tempDir.resolve("output.jpg").toFile();

        UtilException ex = assertThrows(UtilException.class, () -> ImageUtil.pictureZoom(
            sourceFile.getAbsolutePath(),
            destFile.getAbsolutePath(),
            100
        ));
        assertTrue(ex.getMessage().contains("缩放图片失败"));
    }

    @Test
    @DisplayName("应抛出异常 - 源文件路径无效")
    void should_throwException_when_sourcePathInvalid() {
        destFile = tempDir.resolve("output.jpg").toFile();

        UtilException ex = assertThrows(UtilException.class, () -> ImageUtil.pictureZoom(
            "/invalid/path/to/image.jpg",
            destFile.getAbsolutePath(),
            100
        ));
        assertTrue(ex.getMessage().contains("缩放图片失败"));
    }

    @Test
    @DisplayName("应抛出异常 - 目标路径无效")
    void should_throwException_when_destPathInvalid() throws Exception {
        sourceFile = createTestImage("source.jpg", 200, 200, "jpg");

        UtilException ex = assertThrows(UtilException.class, () -> ImageUtil.pictureZoom(
            sourceFile.getAbsolutePath(),
            "/invalid/dest/path/output.jpg",
            100
        ));
        assertTrue(ex.getMessage().contains("缩放图片失败"));
    }

    @Test
    @DisplayName("应缩放到很小的高度")
    void should_zoomToVerySmallHeight() throws Exception {
        sourceFile = createTestImage("source.jpg", 200, 200, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 10);

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        assertEquals(10, result.getHeight());
        assertEquals(10, result.getWidth());
    }

    @Test
    @DisplayName("应缩放到很大的高度")
    void should_zoomToLargeHeight() throws Exception {
        sourceFile = createTestImage("source.jpg", 200, 200, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 500);

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        assertEquals(500, result.getHeight());
        assertEquals(500, result.getWidth());
    }

    @Test
    @DisplayName("应覆盖已存在的目标文件")
    void should_overwriteExistingDestFile() throws Exception {
        sourceFile = createTestImage("source.jpg", 200, 200, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        // 先创建一个目标文件
        BufferedImage initialImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        initialImage.setRGB(0, 0, Color.RED.getRGB());
        ImageIO.write(initialImage, "jpg", destFile);

        long initialSize = destFile.length();

        // 执行缩放
        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), 100);

        // 验证文件被覆盖
        assertTrue(destFile.exists());
        assertTrue(destFile.length() != initialSize);
        BufferedImage result = ImageIO.read(destFile);
        assertEquals(100, result.getHeight());
    }

    // ==================== pictureZoom(String, String) 测试 ====================

    @Test
    @DisplayName("应使用默认高度缩放图片")
    void should_pictureZoom_withDefaultHeight() throws Exception {
        sourceFile = createTestImage("source.jpg", 200, 200, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        // 使用默认高度（100）
        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        assertEquals(100, result.getHeight());
        assertEquals(100, result.getWidth());
    }

    @Test
    @DisplayName("应使用默认高度处理非正方形图片")
    void should_pictureZoom_withDefaultHeight_nonSquare() throws Exception {
        sourceFile = createTestImage("source.jpg", 300, 150, "jpg");
        destFile = tempDir.resolve("output.jpg").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        assertTrue(destFile.exists());
        BufferedImage result = ImageIO.read(destFile);
        // 原图 300x150，裁剪为 150x150，缩放到 100x100
        assertEquals(100, result.getHeight());
        assertEquals(100, result.getWidth());
    }

    @Test
    @DisplayName("应使用默认高度 - PNG格式")
    void should_pictureZoom_withDefaultHeight_pngFormat() throws Exception {
        sourceFile = createTestImage("source.png", 250, 250, "png");
        destFile = tempDir.resolve("output.png").toFile();

        ImageUtil.pictureZoom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        // PNG 被转换为 JPG，thumbnailator 添加 .jpg 扩展名
        File actualOutput = tempDir.resolve("output.jpg").toFile();
        assertTrue(actualOutput.exists());
        BufferedImage result = ImageIO.read(actualOutput);
        assertEquals(100, result.getHeight());
    }

    @Test
    @DisplayName("应抛出异常 - 使用默认高度但源文件不存在")
    void should_throwException_withDefaultHeight_when_sourceNotExists() {
        sourceFile = tempDir.resolve("nonexistent.jpg").toFile();
        destFile = tempDir.resolve("output.jpg").toFile();

        UtilException ex = assertThrows(UtilException.class, () -> ImageUtil.pictureZoom(
            sourceFile.getAbsolutePath(),
            destFile.getAbsolutePath()
        ));
        assertTrue(ex.getMessage().contains("缩放图片失败"));
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试图片
     *
     * @param filename 文件名
     * @param width 宽度
     * @param height 高度
     * @param format 图片格式（jpg、png等）
     * @return 创建的图片文件
     * @throws Exception 创建失败时抛出异常
     */
    private File createTestImage(String filename, int width, int height, String format) throws Exception {
        File imageFile = tempDir.resolve(filename).toFile();

        // 创建图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 填充渐变色背景以便于观察
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 根据位置生成颜色
                int red = (x * 255) / width;
                int green = (y * 255) / height;
                int blue = 128;
                Color color = new Color(red, green, blue);
                image.setRGB(x, y, color.getRGB());
            }
        }

        // 保存图片
        ImageIO.write(image, format, imageFile);

        return imageFile;
    }
}
