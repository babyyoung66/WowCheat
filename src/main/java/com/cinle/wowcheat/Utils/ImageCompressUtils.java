package com.cinle.wowcheat.Utils;

import com.cinle.wowcheat.Exception.UploadFileException;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

/**
 * @Author JunLe
 * @Time 2022/3/29 22:22
 * 图片压缩类
 */
public class ImageCompressUtils {
    private ImageCompressUtils() {
    }
    public static void DefaultCompress(String source , String outPut) throws UploadFileException {
        try {
            Thumbnails.of(source)
                    .scale(0.6f) //图片大小（长宽）压缩比例 从0-1，1表示原图
                    .outputQuality(0.4f) //图片质量压缩比例 从0-1，越接近1质量越好
                    .toFile(new File(outPut));
        }catch (Exception e){
            e.printStackTrace();
            throw new UploadFileException("图片压缩异常！");
        }

    }

}
