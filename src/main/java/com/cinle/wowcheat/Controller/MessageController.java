package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Model.*;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.GroupMemberService;
import com.cinle.wowcheat.Service.GroupServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import com.cinle.wowcheat.Utils.UploadUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.cinle.wowcheat.WebSocket.SendSocketMessageServices;
import com.cinle.wowcheat.WebSocket.SocketUserPrincipal;
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

    @Autowired
    FriendsServices friendsServices;

    @Autowired
    GroupMemberService groupMemberService;

    @PostMapping("/getAll")
    public AjaxResponse getMessageByUUID(@RequestBody CheatMessage cheatMessage) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        cheatMessage.setFrom(uuid);
        List personalMess = messageServices.findAllByUUID(cheatMessage, "personal");
        //有可能是群聊
        List GroupMess = messageServices.findAllByUUID(cheatMessage, "group");
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
     * @param cheatMessage
     * @return
     */
    @PostMapping("/save")
    public AjaxResponse saveMessage(@RequestBody CheatMessage cheatMessage) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        cheatMessage.setFrom(uuid);
        int result = messageServices.saveMessage(cheatMessage, "personal");
        if (result > 0) {
            return response.success().setMessage("保存成功！");
        }
        return response.error().setMessage("保存失败！");
    }

    /**
     * 分页获取消息记录
     *  传入的to为对方uuid
     *  group消息的to永远是群组uuid
     * @param cheatMessage
     * @return
     * @see MessageConst
     */
    @PostMapping("/getByPage")
    public AjaxResponse getByPages(@RequestBody CheatMessage cheatMessage) {
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        cheatMessage.setFrom(uuid);
        AjaxResponse response = new AjaxResponse();
        //验证身份,非自己时
        if ("personal".equals(cheatMessage.getMsgType()) && !uuid.equals(cheatMessage.getTo())){
            //个人
            Friends friends = friendsServices.findFriendNonCache(uuid, cheatMessage.getTo());
            if (friends == null){
                return response.error().setMessage("您未添加该用户！");
            }
        }
        if ("group".equals(cheatMessage.getMsgType())) {
            /**待办！！！*/
            GroupMember member = groupMemberService.selectByGroupIdAndMemberId(cheatMessage.getTo(),cheatMessage.getFrom());
            if (member == null){
                return response.error().setMessage("您未添加该群聊！");
            }
        }
        List mess = messageServices.findMessageByPages(cheatMessage, cheatMessage.getMsgType());
        if (mess != null) {
            response.setData(JSON.toJSON(mess));
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
    public AjaxResponse sendImage(MultipartFile file, CheatMessage message) throws UploadFileException {
        AjaxResponse response = new AjaxResponse();
        FileDetail fileDetail = new FileDetail();
        message.setFileDetail(fileDetail);
        boolean checkType = UploadUtils.checkFileType(file, FileType.image);
        if (!checkType) {
            return response.error().setCode(501).setMessage("不允许上传该类型文件!");
        }
        String fileName = file.getOriginalFilename();
        if (fileName.length() > FileConst.NAME_LIMIT) {
            return response.error().setCode(501).setMessage(String.format("这文件名也太长了，不超过%d个字符！",FileConst.NAME_LIMIT));
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
