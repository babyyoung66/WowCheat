package com.cinle.wowcheat.Utils;

import com.cinle.wowcheat.Exception.CustomerException;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Author JunLe
 * @Time 2022/3/29 22:22
 * 图片压缩类
 */
public class ImageCompressUtils {
    private static Logger logger = LoggerFactory.getLogger(ImageCompressUtils.class);

    private ImageCompressUtils() {
    }

    public static void DefaultCompress(String source, String outPut) throws FileNotFoundException, CustomerException {
        File file = new File(source);
        if (!file.exists()) {
            return;
        }
        long fileSize = file.length();
        //根据图片大小，动态计算需要压缩的质量及图片宽高比例
        float scale = getScale(fileSize);
        float quality = getQuality(fileSize);
        try {
            Thumbnails.of(source)
                    .scale(scale) //图片大小（长宽）压缩比例 从0-1，1表示原图
                    .outputQuality(quality) //图片质量压缩比例 从0-1，越接近1质量越好
                    .toFile(new File(outPut));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomerException("图片压缩异常！");
        }
    }

    private static float getScale(long sourceSize) throws FileNotFoundException {
        long kb = 1024 ;
        long m = 1024 * kb;
        if (sourceSize > 2*m){
            return 0.55f;
        }
        if (sourceSize > 1*m){
            return 0.65f;
        }
        if (sourceSize > 200*kb){
            return 0.7f;
        }
        if (sourceSize > 100*kb){
            return 0.77f;
        }
        return 0.85f;

    }

    private static float getQuality(long sourceSize) throws FileNotFoundException {
        long kb = 1024 ;
        long m = 1024 * kb ;
        if (sourceSize > 2*m){
            return 0.44f;
        }
        if (sourceSize > 1*m){
            return 0.52f;
        }
        if (sourceSize > 300*kb){
            return 0.65f;
        }
        if (sourceSize > 100*kb){
            return 0.75f;
        }
        return 0.85f;

    }

}
