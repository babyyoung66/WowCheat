package com.cinle.wowcheat.Event;

import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Model.UserFileDetail;
import com.cinle.wowcheat.Utils.ImageCompressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author JunLe
 * @Time 2022/3/29 23:20
 * 压缩事件监听
 */
@Component
public class ImagePressEventListener {
    private Logger log =  LoggerFactory.getLogger(ImagePressEventListener.class);

    @Async("AsyncExecutor")
    @EventListener
    public void PressImage(ImagePressEvent event){
        UserFileDetail fileDetail = (UserFileDetail) event.getSource();
        if (!FileType.image.toString().equals(fileDetail.getFileType())){
            return;
        }

        try {
            //异步时，前端得刷新一次,所以得在异步方法中加延迟，等前端第一次加载后再压缩
            //Thread.sleep(2000);
            ImageCompressUtils.DefaultCompress(fileDetail.getFilePath(),fileDetail.getFilePath());
        }catch (Exception e){
            log.error("{} 压缩失败，原因: {}",fileDetail.getFilePath(),e.getMessage());
        }

    }

}
