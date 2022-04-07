package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Dao.UserFileDetailDao;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Event.ImagePressEvent;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Model.CustomerMessage;
import com.cinle.wowcheat.Model.Friends;
import com.cinle.wowcheat.Model.UserFileDetail;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.EscapeHtmlUtils;
import com.cinle.wowcheat.Utils.ImageCompressUtils;
import com.cinle.wowcheat.Utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    ApplicationContext applicationContext;


    @Override
    public void sendToUser(SocketUserPrincipal principal, CustomerMessage customerMessage) {
        customerMessage.setMsgType("personal");
        customerMessage.setTime(new Date());
        coverMessage coverMessage = checkFriend(principal.getName(), customerMessage);
        coverMessage.getMessage().setType(SocketMessageType.cheat);
        if (coverMessage.isSuccess()) {
            sendTextMessage(principal, coverMessage.getMessage());
        }
        //destination 参数必须设置，前端订阅格式为 /user/ + uuid + /personal
        //无论是否通过，都反馈给发送者
        messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(coverMessage.getMessage(), true));

    }

    @Override
    public void sendToGroup(SocketUserPrincipal principal, CustomerMessage customerMessage) {
        customerMessage.setMsgType("group");
        customerMessage.setTime(new Date());
        coverMessage coverMessage = checkGroup(principal.getName(), customerMessage);
        coverMessage.getMessage().setType(SocketMessageType.cheat);
        if (coverMessage.isSuccess()) {
            //获取数据库群组成员列表，遍历发送
            for (int i = 0; i < 999; i++) {
                sendTextMessage(principal, coverMessage.getMessage());
            }
        } else {
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(coverMessage.getMessage(), true));
        }

    }

    @Override
    public void sendTopic(SocketUserPrincipal principal, CustomerMessage customerMessage) {
        SocketMessage message = new SocketMessage();
        message.setType(SocketMessageType.notice);
        List<String> roles = principal.getRoles();
        //非管理员不允许发送topic消息
        if (!roles.contains(RoleEnum.ADMIN.toString())) {
            message.error().setErrorMessage("您无权限操作！");
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
            return;
        }
        customerMessage.setTime(new Date());
        messagingTemplate.convertAndSend(SocketConstants.TOPIC_SUBSCRIBE, JSON.toJSONString(message, true));
    }

    @Override
    public void sendFile(SocketUserPrincipal principal, CustomerMessage customerMessage, MultipartFile file) {
        customerMessage.setFrom(principal.getName());
        customerMessage.setContentType("file");
        customerMessage.setTime(new Date());
        coverMessage coverMessage = null;
        //先验证身份
        if ("personal".equals(customerMessage.getMsgType())) {
            coverMessage = checkFriend(principal.getName(), customerMessage);
        }
        if ("group".equals(customerMessage.getMsgType())) {
            coverMessage = checkGroup(principal.getName(), customerMessage);
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
                customerMessage = uploadFile(file, customerMessage);
                //发送消息
                if ("personal".equals(customerMessage.getMsgType())) {
                    sendToUser(principal, customerMessage);
                }
                if ("group".equals(customerMessage.getMsgType())) {
                    sendToGroup(principal, customerMessage);
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

    private CustomerMessage uploadFile(MultipartFile file, CustomerMessage message) throws UploadFileException {
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
        userFileDetail.setUploadTime(new Timestamp(new Date().getTime()));
        fileDetailDao.insertSelective(userFileDetail);
        //发送图片压缩事件,
        ApplicationEvent event = new ImagePressEvent(userFileDetail);
        applicationContext.publishEvent(event);


        return message;
    }

    /**
     * 内部包装类
     */
    private class coverMessage {
        boolean success;
        SocketMessage message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public SocketMessage getMessage() {
            return message;
        }

        public void setMessage(SocketMessage message) {
            this.message = message;
        }
    }

    /**
     * @return 返回需要反馈给自己的消息
     * 如好友关系正常，则发送给对方并入库
     */
    private void sendTextMessage(SocketUserPrincipal principal, SocketMessage message) {
        CustomerMessage customerMessage = (CustomerMessage) message.getMessage();
        //xss清洗
        String mess = EscapeHtmlUtils.escapeHtml(JSON.toJSONString(customerMessage));
        CustomerMessage newMess = JSON.parseObject(mess, CustomerMessage.class);
        customerMessage = newMess;
        customerMessage.setTime(new Date());
        customerMessage.setFrom(principal.getName());
        //入库
        messageServices.saveMessage(customerMessage, customerMessage.getMsgType());
        //接受者为自己
        if (principal.getName().equals(customerMessage.getTo())) {
            return;
        }
        messagingTemplate.convertAndSendToUser(customerMessage.getTo(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));

    }


    private coverMessage checkFriend(String shelfUuid, CustomerMessage customerMessage) {
        SocketMessage socketMessage = new SocketMessage();
        coverMessage coverMessage = new coverMessage();
        if (customerMessage.getContent() !=null && customerMessage.getContent().trim().length() > SocketConstants.CONTENT_LIMIT) {
            socketMessage.error().setErrorMessage("文字超出限制长度！");
            coverMessage.setSuccess(false);
            coverMessage.setMessage(socketMessage);
            return coverMessage;
        }
        //自己
        if (shelfUuid.equals(customerMessage.getTo())) {
            socketMessage.success().setMessage(customerMessage);
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(true);
            return coverMessage;
        }
        Friends shelfInfo = friendsServices.findFriend(shelfUuid, customerMessage.getTo());
        if (shelfInfo == null) {
            socketMessage.error().setCode(404).setErrorMessage("对方不是您的好友！");
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(false);
            return coverMessage;
        }
        Friends friendsInfo = friendsServices.findFriend(customerMessage.getTo(), shelfUuid);
        if (friendsInfo == null){
            socketMessage.error().setCode(404).setErrorMessage("对方不是您的好友！");
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
                        socketMessage.success().setMessage(customerMessage);
                        coverMessage.setSuccess(true);
                        coverMessage.setMessage(socketMessage);
                        return coverMessage;
                    case 2:
                        socketMessage.success().setMessage(customerMessage);
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

    private coverMessage checkGroup(String shelfUuid, CustomerMessage customerMessage) {
        coverMessage coverMessage = new coverMessage();
        SocketMessage message = new SocketMessage();

        return coverMessage;
    }
}
