package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Constants.MyConst;
import com.cinle.wowcheat.Dao.UserFileDetailDao;
import com.cinle.wowcheat.Enum.ContentType;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Enum.MessageType;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Event.ImagePressEvent;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Model.CheatMessage;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.MyUserDetail;
import com.cinle.wowcheat.Model.UserFileDetail;
import com.cinle.wowcheat.Redis.GroupMemberCache;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.EscapeHtmlUtils;
import com.cinle.wowcheat.Utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @Author JunLe
 * @Time 2022/3/23 17:51
 */
@Service
public class SendSocketMessageServicesImpl implements SendSocketMessageServices {
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    FriendsServices friendsServices;
    @Autowired
    MessageServices messageServices;

    @Autowired
    UserFileDetailDao fileDetailDao;

    @Autowired
    GroupMemberCache groupMemberCache;

    @Autowired
    ApplicationContext applicationContext;


    @Override
    public void sendText(SocketUserPrincipal principal, CheatMessage cheatMessage) {
        cheatMessage.setTime(new Date());
        if ("personal".equals(cheatMessage.getMsgType())){
            coverMessage coverMessage = checkFriend(principal.getName(), cheatMessage);
            sendToUser(principal,coverMessage);
        }
        if ("group".equals(cheatMessage.getMsgType())){
            coverMessage coverMessage1 = checkGroup(principal.getName(),cheatMessage);
            sendToGroup(principal,coverMessage1);
        }

    }
    @Override
    public void sendTopic(SocketUserPrincipal principal, CheatMessage cheatMessage) {
        SocketMessage message = new SocketMessage();
        message.setType(SocketMessageType.notice);
        List<String> roles = principal.getRoles();
        //非管理员不允许发送topic消息
        if (!roles.contains(RoleEnum.ADMIN.toString())) {
            message.error().setErrorMessage("您无权限操作！");
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
            return;
        }
        cheatMessage.setTime(new Date());
        messagingTemplate.convertAndSend(SocketConstants.TOPIC_SUBSCRIBE, JSON.toJSONString(message, true));
    }

    @Override
    public void sendFile(SocketUserPrincipal principal, CheatMessage cheatMessage, MultipartFile file) {
        cheatMessage.setTime(new Date());
        cheatMessage.setFrom(principal.getName());
        cheatMessage.setContentType("file");
        coverMessage coverMessage = null;
        //先验证身份
        if ("personal".equals(cheatMessage.getMsgType())) {
            coverMessage = checkFriend(principal.getName(), cheatMessage);
        }
        if ("group".equals(cheatMessage.getMsgType())) {
            coverMessage = checkGroup(principal.getName(), cheatMessage);
        }
        if (coverMessage == null) {
            SocketMessage socketMessage = new SocketMessage();
            socketMessage.error().setErrorMessage("无法验证您的请求！");
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
            return;
        }
        SocketMessage socketMessage = coverMessage.getMessage();
        if (coverMessage.isSuccess()) {
            try {
                //上传文件
                cheatMessage = uploadFile(file, cheatMessage);
                //发送消息
                socketMessage.setMessage(cheatMessage);
                if ("personal".equals(cheatMessage.getMsgType())) {
                    sendToUser(principal, coverMessage);
                }
                if ("group".equals(cheatMessage.getMsgType())) {
                    sendToGroup(principal, coverMessage);
                }

            } catch (Exception e) {
                //上传异常
                socketMessage.error().setErrorMessage(e.getMessage());
                messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
            }

        } else {
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
        }

    }

    @Override
    public void sendWelcomeMessage(MyUserDetail user) {
        //发送欢迎信息
        SocketMessage message = new SocketMessage();
        message.setType(SocketMessageType.cheat);
        CheatMessage cheatMessage = new CheatMessage();
        cheatMessage.setFrom(MyConst.DEFAULT_ADMIN_ID);
        cheatMessage.setTo(user.getUuid());
        cheatMessage.setTime(new Date());
        cheatMessage.setContentType(ContentType.text.toString());
        cheatMessage.setContent("欢迎注册使用WowCheat~~");
        cheatMessage.setMsgType(MessageType.personal.toString());
        message.success().setMessage(cheatMessage);
        messageServices.saveMessage(cheatMessage,cheatMessage.getMsgType());
        messagingTemplate.convertAndSendToUser(cheatMessage.getTo(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
    }

    private void sendToUser(SocketUserPrincipal principal, coverMessage coverMessage) {
        //coverMessage coverMessage = checkFriend(principal.getName(), cheatMessage);
        coverMessage.getMessage().setType(SocketMessageType.cheat);
        if (coverMessage.isSuccess()) {
            CheatMessage cheatMessage = (CheatMessage) coverMessage.getMessage().getMessage();
            cheatMessage.setMsgType("personal");
            sendTextMessage(principal, coverMessage.getMessage());
        }
        //destination 参数必须设置，前端订阅格式为 /user/ + uuid + /personal
        //无论是否通过，都反馈给发送者
        messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(coverMessage.getMessage(), true));

    }


    private void sendToGroup(SocketUserPrincipal principal, coverMessage coverMessage) {
        //coverMessage coverMessage = checkGroup(principal.getName(), cheatMessage);
        coverMessage.getMessage().setType(SocketMessageType.cheat);
        if (coverMessage.isSuccess()) {
            CheatMessage cheatMessage = (CheatMessage) coverMessage.getMessage().getMessage();
            cheatMessage.setMsgType("group");
            sendTextMessage(principal,coverMessage.getMessage());
        } else {
            //错误时只反馈错误消息给发送者
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(coverMessage.getMessage(), true));
        }

    }


    private CheatMessage uploadFile(MultipartFile file, CheatMessage message) throws UploadFileException {
        String name = file.getOriginalFilename();
        String suffixName = name.substring(name.lastIndexOf("."));       //获取文件后缀
        //如果是图片，则统一改为jpg，后续开启一个线程去压缩已上传的图片
        if(FileType.image.equals(message.getFileDetail().getFileType())){
            suffixName = ".jpg";
        }
        String uuName = UUID.randomUUID().toString();
        //真实保存路径
        String RealName = uuName + suffixName;
        String path = UploadUtils.getCheatFilesPath(message.getFrom(), RealName);
        UploadUtils.uploadFile(file, path, message.getFileDetail().getFileType());
        //存入文件相关信息
        message.getFileDetail().setFileUrl(FileConst.UPLOAD_BASE_PATH + path);
        message.getFileDetail().setFileName(name);
        //MySQL记录
        UserFileDetail userFileDetail = new UserFileDetail();
        userFileDetail.setFileName(name);
        userFileDetail.setFilePath(FileConst.LOCAL_PATH + path);
        userFileDetail.setUuid(message.getFrom());
        userFileDetail.setFileType(message.getFileDetail().getFileType().toString());
        userFileDetail.setUploadTime(new Date());
        fileDetailDao.insertSelective(userFileDetail);
        //发送图片压缩事件,
        ApplicationEvent event = new ImagePressEvent(userFileDetail);
        applicationContext.publishEvent(event);


        return message;
    }


    private void sendTextMessage(SocketUserPrincipal principal, SocketMessage message) {
        CheatMessage cheatMessage = (CheatMessage) message.getMessage();
        //xss清洗
        String mess = EscapeHtmlUtils.escapeHtml(JSON.toJSONString(cheatMessage));
        CheatMessage newMess = JSON.parseObject(mess, CheatMessage.class);
        cheatMessage = newMess;
        cheatMessage.setFrom(principal.getName());
        //入库
        messageServices.saveMessage(cheatMessage, cheatMessage.getMsgType());
        //接受者为自己
        if (principal.getName().equals(cheatMessage.getTo())) {
            return;
        }
        if ("personal".equals(cheatMessage.getMsgType())){
            messagingTemplate.convertAndSendToUser(cheatMessage.getTo(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
        }
        if ("group".equals(cheatMessage.getMsgType())){
            //获取数据库群组成员列表，遍历发送
            Set<String> members = groupMemberCache.getGroupMemberIdsByGroupId(cheatMessage.getTo());
            Iterator<String> it = members.iterator();
            while (it.hasNext()) {
                messagingTemplate.convertAndSendToUser(it.next(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
            }
        }

    }


    private coverMessage checkFriend(String shelfUuid, CheatMessage cheatMessage) {
        SocketMessage socketMessage = new SocketMessage();
        coverMessage coverMessage = new coverMessage();
        if (cheatMessage.getContent() !=null && cheatMessage.getContent().trim().length() > SocketConstants.CONTENT_LIMIT) {
            socketMessage.error().setErrorMessage("文字超出限制长度！");
            coverMessage.setSuccess(false);
            coverMessage.setMessage(socketMessage);
            return coverMessage;
        }
        //自己
        if (shelfUuid.equals(cheatMessage.getTo())) {
            socketMessage.success().setMessage(cheatMessage);
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(true);
            return coverMessage;
        }
        Friends shelfInfo = friendsServices.findFriend(shelfUuid, cheatMessage.getTo());
        if (shelfInfo == null) {
            socketMessage.error().setCode(404).setErrorMessage("对方不是您的好友！");
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(false);
            return coverMessage;
        }
        Friends friendsInfo = friendsServices.findFriend(cheatMessage.getTo(), shelfUuid);
        if (friendsInfo == null){
            socketMessage.error().setCode(404).setErrorMessage("您不是对方的好友！");
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(false);
            return coverMessage;
        }
        switch (shelfInfo.getStatus()) {
            //好友状态（1正常，2屏蔽，3拉黑，4被对方删除）默认1
            case 1:
                switch (friendsInfo.getStatus()) {
                    //双方都为1时则发送
                    case 1:
                        socketMessage.success().setMessage(cheatMessage);
                        coverMessage.setSuccess(true);
                        coverMessage.setMessage(socketMessage);
                        return coverMessage;
                    case 2:
                        socketMessage.success().setMessage(cheatMessage);
                        break;
                    case 3:
                        socketMessage.error().setErrorMessage("您已被对方拉黑！");
                        break;
                    default:
                        socketMessage.error().setErrorMessage("无法满足您的请求！");
                        break;
                }
                break;
            case 2:
                socketMessage.error().setErrorMessage("您已将对方屏蔽！");
                break;
            case 3:
                socketMessage.error().setErrorMessage("对方已被您拉黑！");
                break;
            case 4:
                socketMessage.error().setErrorMessage("您不是对方的好友！");
                break;
            default:
                socketMessage.error().setErrorMessage("无法满足您的请求！");
                break;
        }
        //走到这里的都是验证不通过
        coverMessage.setSuccess(false);
        coverMessage.setMessage(socketMessage);
        return coverMessage;
    }

    private coverMessage checkGroup(String shelfUuid, CheatMessage cheatMessage) {
        coverMessage coverMessage = new coverMessage();
        SocketMessage message = new SocketMessage();
        Set<String> members = groupMemberCache.getGroupMemberIdsByGroupId(cheatMessage.getTo());
        if (!members.contains(shelfUuid)){
            coverMessage.setSuccess(false);
            message.error().setErrorMessage("您未加入该群聊！");
            coverMessage.setMessage(message);
            return coverMessage;
        }
        coverMessage.setSuccess(true);
        message.success().setMessage(cheatMessage);
        coverMessage.setMessage(message);
        return coverMessage;
    }

    /**
     * 内部包装类
     */
    private class coverMessage {
        boolean success;
        SocketMessage message;

        protected boolean isSuccess() {
            return success;
        }

        protected void setSuccess(boolean success) {
            this.success = success;
        }

        protected SocketMessage getMessage() {
            return message;
        }

        protected void setMessage(SocketMessage message) {
            this.message = message;
        }
    }

}
