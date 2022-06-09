package com.cinle.wowcheat.Utils;

import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Exception.CustomerException;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

        String cachePathName = getCachePathName(outPut);

        long fileSize = file.length();
        //根据图片大小，动态计算需要压缩的质量及图片宽高比例
        float scale = getScale(fileSize);
        float quality = getQuality(fileSize);
        try {
            Thumbnails.of(source)
                    .scale(scale) //图片大小（长宽）压缩比例 从0-1，1表示原图
                    .outputQuality(quality) //图片质量压缩比例 从0-1，越接近1质量越好
                    .toFile(new File(cachePathName));

            //压缩成功，重命名原文件，将压缩文件命名为原文件
            reNameSourceFile(source);
            moveCacheFileAsSource(cachePathName, source);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomerException("图片压缩异常！");
        }


    }

    private static float getScale(long sourceSize) throws FileNotFoundException {
        long kb = 1024;
        long m = 1024 * kb;
        if (sourceSize > 2 * m) {
            return 0.55f;
        }
        if (sourceSize > 1 * m) {
            return 0.65f;
        }
        if (sourceSize > 200 * kb) {
            return 0.7f;
        }
        if (sourceSize > 100 * kb) {
            return 0.77f;
        }
        return 0.85f;

    }

    private static float getQuality(long sourceSize) {
        long kb = 1024;
        long m = 1024 * kb;
        if (sourceSize > 2 * m) {
            return 0.44f;
        }
        if (sourceSize > 1 * m) {
            return 0.52f;
        }
        if (sourceSize > 300 * kb) {
            return 0.65f;
        }
        if (sourceSize > 100 * kb) {
            return 0.75f;
        }
        return 0.85f;

    }

    private static String getCachePathName(String outPut){
        String name = outPut.substring(outPut.lastIndexOf("/") + 1);
        String cachePathName = FileConst.LOCAL_PATH + "cache/" + name;
        Path cachePath = Paths.get(cachePathName);

        //父目录不存在则创建父目录
        if (!cachePath.toFile().getParentFile().exists()) {
            cachePath.toFile().getParentFile().mkdirs();
        }
        return cachePathName;
    }

    /**
     * 重命名原文件
     *
     * @param source
     * @throws IOException
     */
    private static void reNameSourceFile(String source) throws IOException {
        String suffix = source.substring(source.indexOf("."));
        String newSuffix = "_source" + suffix;
        String newName = source.replaceFirst(suffix, newSuffix);
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(newName);
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 将压缩文件重命名为原文件
     *
     * @param cachePath
     * @param target
     * @throws IOException
     */
    private static void moveCacheFileAsSource(String cachePath, String target) throws IOException {
        Path sourcePath = Paths.get(cachePath);
        Path targetPath = Paths.get(target);
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

}
