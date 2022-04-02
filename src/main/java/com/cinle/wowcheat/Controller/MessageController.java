package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Model.CustomerMessage;
import com.cinle.wowcheat.Model.FileDetail;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Utils.UploadUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.WebSocket.SendSocketMessageServices;
import com.cinle.wowcheat.WebSocket.SocketUserPrincipal;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/4 17:43
 * 非文本类消息发送、消息记录查询
 * @see MessageConst 消息查询相关静态参数
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageServices messageServices;

    @Autowired
    SendSocketMessageServices socketMessageServices;

    @PostMapping("/getAll")
    public AjaxResponse getMessageByUUID(@RequestBody CustomerMessage customerMessage) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        customerMessage.setFrom(uuid);
        List personalMess = messageServices.findAllByUUID(customerMessage, "personal");
        //有可能是群聊
        List GroupMess = messageServices.findAllByUUID(customerMessage, "group");
        if (!personalMess.isEmpty()) {
            response.setData(JSON.toJSON(personalMess));
        }
        if (!GroupMess.isEmpty()) {
            response.setData(JSON.toJSON(GroupMess));
        }
        return response.success();

    }


    /**
     * 暂时没用
     *
     * @param customerMessage
     * @return
     */
    @PostMapping("/save")
    public AjaxResponse saveMessage(@RequestBody CustomerMessage customerMessage) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        customerMessage.setFrom(uuid);
        int result = messageServices.saveMessage(customerMessage, "personal");
        if (result > 0) {
            return response.success().setMessage("保存成功！");
        }
        return response.error().setMessage("保存失败！");
    }

    /**
     * 分页获取消息记录
     *
     * @param customerMessage
     * @return
     * @see MessageConst
     */
    @PostMapping("/getByPage")
    public AjaxResponse getByPages(@RequestBody CustomerMessage customerMessage) {
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        customerMessage.setFrom(uuid);
        AjaxResponse response = new AjaxResponse();
        List personalMess = messageServices.findMessageByPages(customerMessage, "personal");
        //有可能是群聊
        List GroupMess = messageServices.findMessageByPages(customerMessage, "group");
        if (!personalMess.isEmpty()) {
            response.setData(JSON.toJSON(personalMess));
        }
        if (!GroupMess.isEmpty()) {
            response.setData(JSON.toJSON(GroupMess));
        }
        response.success();
        return response;
    }


    /**
     * 发送图片消息，验证双方身份成功后
     * 再由uploadUtils验证上传成功后
     * 将对应数据录入MySQL及MongoDB
     * 同时发送图片压缩事件
     * @param file
     * @param message
     * @return
     * @throws UploadFileException
     */
    @PostMapping("/sendImage")
    public AjaxResponse sendImage(MultipartFile file, CustomerMessage message) throws UploadFileException {
        AjaxResponse response = new AjaxResponse();
        FileDetail fileDetail = new FileDetail();
        message.setFileDetail(fileDetail);
        boolean checkType = UploadUtils.checkFileType(file, FileType.image);
        if (!checkType) {
            return response.error().setCode(501).setMessage("不允许上传该类型文件!");
        }
        String fileName = file.getOriginalFilename();
        if (fileName.length() > FileConst.NAME_LIMIT) {
            return response.error().setCode(501).setMessage(String.format("小伙子，你这文件名也太长了吧~~不得超过%d个字符哟！",FileConst.NAME_LIMIT));
        }

        SocketUserPrincipal principal = new SocketUserPrincipal();
        principal.setName(SecurityContextUtils.getCurrentUserUUID());
        message.getFileDetail().setFileType(FileType.image);
        try {
            socketMessageServices.sendFile(principal, message, file);
        } catch (Exception e) {
            e.printStackTrace();
            return response.error().setCode(505).setMessage(e.getMessage());
        }
        return response.success();
    }
}
