package com.hbasesoft.framework.ai.demo.util;

import com.hbasesoft.framework.common.utils.UtilException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> 图片显示工具 <br>
 *
 * @author 王伟
 * @version 1.0
 * @date 2023/03/09 11:01:05
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageShow {

    /**
     * Description: 显示网络图片 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param imageUrl <br>
     */
    public static void showImage(final String imageUrl) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(imageUrl));
        }
        catch (Exception e) {
            throw new UtilException(e);
        }
    }
}
