/**
 * 
 */
package com.hbasesoft.framework.common.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageUtil {

    /** DEFAULT_QUALITY */
    private static final float DEFAULT_QUALITY = 0.25f;

    /** DEFAULT_HEIGHT */
    private static final int DEFAULT_HEIGHT = 100;

    /**
     * Description: 缩放图片<br>
     * 
     * @author 王伟 <br>
     * @param source <br>
     * @param dist <br>
     * @param height <br>
     * @throws UtilException <br>
     */
    public static void pictureZoom(final String source, final String dist, final int height) throws UtilException {
        String tempDist = dist;
        try {
            Builder<BufferedImage> file = Thumbnails.of(cutByShort(source));
            if (source.toLowerCase().indexOf("jpg") == -1 || source.toLowerCase().indexOf("jpeg") == -1) {
                file.outputFormat("jpg");
                tempDist = dist.substring(0, dist.lastIndexOf("."));
            }
            file.outputQuality(DEFAULT_QUALITY).height(height).toFile(tempDist);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.IMAGE_ZOOM_ERROR, e);
        }
    }

    /**
     * Description: 缩放图片<br>
     * 
     * @author 王伟 <br>
     * @param source <br>
     * @param dist <br>
     * @throws UtilException <br>
     */
    public static void pictureZoom(final String source, final String dist) throws UtilException {
        pictureZoom(source, dist, DEFAULT_HEIGHT);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param source <br>
     * @return <br>
     * @throws UtilException <br>
     */
    private static BufferedImage cutByShort(final String source) throws UtilException {

        try {
            BufferedImage src = ImageIO.read(new File(source));
            int width = src.getWidth();
            int height = src.getHeight();
            int size = width > height ? height : width;
            BufferedImage dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics g = dest.getGraphics();
            g.drawImage(src, 0, 0, size, size, 0, 0, size, size, null);

            return dest;
        }
        catch (IOException e) {
            throw new UtilException(ErrorCodeDef.IMAGE_ZOOM_ERROR, e);
        }
    }
}
