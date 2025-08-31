package com.hbasesoft.framework.ai.demo.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import jakarta.servlet.http.HttpServletResponse;

/**
 * <Description> 生成图片的演示 <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年03月02日 <br>
 * @see com.hbasesoft.framework.ai.demo.simple <br>
 * @since V1.0<br>
 */
@RestController
@RequestMapping("/simple/image")
public class ImageController {

    /** 图片模型 */
    private final ImageModel imageModel;

    /**
     * Description: <br>
     *
     * @param imageModel <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public ImageController(final ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    /**
     * Description: 生成图片 <br>
     *
     * @param input    提示词 <br>
     * @param response <br>
     */
    @GetMapping("/generate")
    public void generateImage(final String input, final HttpServletResponse response) {
        ImageResponse imageResponse = imageModel.call(new ImagePrompt(input));
        String imageUrl = imageResponse.getResult().getOutput().getUrl();
        System.out.println(imageUrl);
        try {
            URL url = URI.create(imageUrl).toURL();
            try (InputStream in = url.openStream()) {

                response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
                response.getOutputStream().write(in.readAllBytes());
                response.getOutputStream().flush();
            }
        } 
        catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Description: 多条件生成图片 <br>
     *
     * @param subject
     * @param environment
     * @param height
     * @param width
     * @param style
     * @return 图片返回
     */
    @GetMapping("/multipleConditions")
    public ResponseEntity<?> multipleConditions(
            final @RequestParam(value = "subject", defaultValue = "一只会编程的猫") String subject,
            final @RequestParam(value = "environment", defaultValue = "办公室") String environment,
            final @RequestParam(value = "height", defaultValue = "1024") Integer height,
            final @RequestParam(value = "width", defaultValue = "1024") Integer width,
            final @RequestParam(value = "style", defaultValue = "生动") String style) {

        String prompt = String.format(
            "一个%s，置身于%s的环境中，使用%s的艺术风格，高清4K画质，细节精致", 
            subject, 
            environment, 
            style
        );

        ImageOptions options = ImageOptionsBuilder.builder().height(height).width(width).build();

        try {
            ImageResponse response = imageModel.call(new ImagePrompt(prompt, options));
            String imageUrl = response.getResult().getOutput().getUrl();
            System.out.println(imageUrl);
            return ResponseEntity.ok(response.getResult().getOutput().getUrl());
        } 
        catch (Exception e) {
            LoggerUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "图像生成失败", 
                    "message", e.getMessage(), 
                    "timestamp", LocalDateTime.now()
                ));
        }
    }
}
