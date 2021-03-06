package com.cinle.wowcheat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Constants.MyConst;
import com.cinle.wowcheat.Dao.UserFileDetailDao;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Enum.MessageContentType;
import com.cinle.wowcheat.Enum.MessageType;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Model.*;
import com.cinle.wowcheat.Redis.GroupMemberCache;
import com.cinle.wowcheat.Service.FriendsServices;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Utils.EscapeHtmlUtils;
import com.cinle.wowcheat.Utils.ImageCompressUtils;
import com.cinle.wowcheat.Utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
        if ("personal".equals(cheatMessage.getMsgType())) {
            coverMessage coverMessage = checkFriend(principal.getName(), cheatMessage);
            sendToUser(principal, coverMessage);
        }
        if ("group".equals(cheatMessage.getMsgType())) {
            coverMessage coverMessage1 = checkGroup(principal.getName(), cheatMessage);
            sendToGroup(principal, coverMessage1);
        }

    }

    @Override
    public void sendTopic(SocketUserPrincipal principal, CheatMessage cheatMessage) {
        SocketMessage message = new SocketMessage();
        message.setType(SocketMessageType.notice);
        List<String> roles = principal.getRoles();
        //???????????????????????????topic??????
        if (!roles.contains(RoleEnum.ADMIN.toString())) {
            message.error().setErrorMessage("?????????????????????");
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
        //???????????????
        if ("personal".equals(cheatMessage.getMsgType())) {
            coverMessage = checkFriend(principal.getName(), cheatMessage);
        }
        if ("group".equals(cheatMessage.getMsgType())) {
            coverMessage = checkGroup(principal.getName(), cheatMessage);
        }
        if (coverMessage == null) {
            SocketMessage socketMessage = new SocketMessage();
            socketMessage.error().setErrorMessage("???????????????????????????");
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
            return;
        }
        SocketMessage socketMessage = coverMessage.getMessage();
        if (coverMessage.isSuccess()) {
            try {
                //????????????
                cheatMessage = uploadFile(file, cheatMessage);
                //????????????
                socketMessage.setMessage(cheatMessage);
                if ("personal".equals(cheatMessage.getMsgType())) {
                    sendToUser(principal, coverMessage);
                }
                if ("group".equals(cheatMessage.getMsgType())) {
                    sendToGroup(principal, coverMessage);
                }

            } catch (Exception e) {
                //????????????
                socketMessage.error().setErrorMessage(e.getMessage());
                messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
            }

        } else {
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(socketMessage, true));
        }

    }

    @Override
    public void sendWelcomeMessage(MyUserDetail user) {
        //??????????????????
        SocketMessage message = new SocketMessage();
        message.setType(SocketMessageType.cheat);
        CheatMessage cheatMessage = new CheatMessage();
        cheatMessage.setFrom(MyConst.DEFAULT_ADMIN_ID);
        cheatMessage.setTo(user.getUuid());
        cheatMessage.setTime(new Date());
        cheatMessage.setContentType(MessageContentType.text.toString());
        cheatMessage.setContent("??????????????????WowCheat~~");
        cheatMessage.setMsgType(MessageType.personal.toString());
        message.success().setMessage(cheatMessage);
        messageServices.saveMessage(cheatMessage, cheatMessage.getMsgType());
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
        //destination ?????????????????????????????????????????? /user/ + uuid + /personal
        //??????????????????????????????????????????
        messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(coverMessage.getMessage(), true));

    }


    private void sendToGroup(SocketUserPrincipal principal, coverMessage coverMessage) {
        //coverMessage coverMessage = checkGroup(principal.getName(), cheatMessage);
        coverMessage.getMessage().setType(SocketMessageType.cheat);
        if (coverMessage.isSuccess()) {
            CheatMessage cheatMessage = (CheatMessage) coverMessage.getMessage().getMessage();
            cheatMessage.setMsgType("group");
            sendTextMessage(principal, coverMessage.getMessage());
        } else {
            //??????????????????????????????????????????
            messagingTemplate.convertAndSendToUser(principal.getName(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(coverMessage.getMessage(), true));
        }

    }


    private CheatMessage uploadFile(MultipartFile file, CheatMessage message) throws UploadFileException {
        String name = file.getOriginalFilename();
        String suffixName = name.substring(name.lastIndexOf("."));       //??????????????????
        //?????????????????????????????????jpg??????????????????????????????????????????????????????
        if (FileType.image.equals(message.getFileDetail().getFileType()) && !suffixName.equalsIgnoreCase(".gif")) {
            suffixName = ".jpg";
        }
        String uuName = UUID.randomUUID().toString();
        //??????????????????
        String RealName = uuName + suffixName;
        String path = UploadUtils.getCheatFilesPath(message.getFrom(), RealName);
        UploadUtils.uploadFile(file, path, message.getFileDetail().getFileType());
        //????????????????????????
        message.getFileDetail().setFileUrl(FileConst.UPLOAD_BASE_PATH + path);
        message.getFileDetail().setFileName(name);
        //MySQL??????
        UserFileDetail userFileDetail = new UserFileDetail();
        userFileDetail.setFileName(name);
        userFileDetail.setFilePath(FileConst.LOCAL_PATH + path);
        userFileDetail.setUuid(message.getFrom());
        userFileDetail.setFileType(message.getFileDetail().getFileType().toString());
        userFileDetail.setUploadTime(new Date());
        fileDetailDao.insertSelective(userFileDetail);
        //???GIF??????????????????????????????,
        if (!suffixName.equalsIgnoreCase(".gif")) {
            //??????????????????????????????????????????????????????????????????????????????????????????????????????
            try {
                ImageCompressUtils.DefaultCompress(userFileDetail.getFilePath(),userFileDetail.getFilePath());
            }catch (Exception e){
                //TODO
            }
            //ApplicationEvent event = new ImagePressEvent(userFileDetail);
            //applicationContext.publishEvent(event);
        }

        return message;
    }


    private void sendTextMessage(SocketUserPrincipal principal, SocketMessage message) {
        CheatMessage cheatMessage = (CheatMessage) message.getMessage();
        //xss??????
        String mess = EscapeHtmlUtils.escapeHtml(JSON.toJSONString(cheatMessage));
        CheatMessage newMess = JSON.parseObject(mess, CheatMessage.class);
        cheatMessage = newMess;
        cheatMessage.setFrom(principal.getName());
        //??????
        messageServices.saveMessage(cheatMessage, cheatMessage.getMsgType());
        //??????????????????
        if (principal.getName().equals(cheatMessage.getTo())) {
            return;
        }
        if ("personal".equals(cheatMessage.getMsgType())) {
            messagingTemplate.convertAndSendToUser(cheatMessage.getTo(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
        }
        if ("group".equals(cheatMessage.getMsgType())) {
            //????????????????????????????????????????????????
            Set<GroupMember> members = groupMemberCache.getGroupMembersByGroupId(cheatMessage.getTo());
            Iterator<GroupMember> it = members.iterator();
            while (it.hasNext()) {
                GroupMember member = it.next();
                if (0 == member.getNotifyStatus()) {
                    messagingTemplate.convertAndSendToUser(member.getUserUuid(), SocketConstants.USER_SUBSCRIBE_Suffix, JSON.toJSONString(message, true));
                }
            }
        }

    }


    private coverMessage checkFriend(String shelfUuid, CheatMessage cheatMessage) {
        SocketMessage socketMessage = new SocketMessage();
        coverMessage coverMessage = new coverMessage();
        if (cheatMessage.getContent() != null && cheatMessage.getContent().trim().length() > SocketConstants.CONTENT_LIMIT) {
            socketMessage.error().setErrorMessage("???????????????????????????");
            coverMessage.setSuccess(false);
            coverMessage.setMessage(socketMessage);
            return coverMessage;
        }
        //??????
        if (shelfUuid.equals(cheatMessage.getTo())) {
            socketMessage.success().setMessage(cheatMessage);
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(true);
            return coverMessage;
        }
        Friends shelfInfo = friendsServices.findFriend(shelfUuid, cheatMessage.getTo());
        if (shelfInfo == null) {
            socketMessage.error().setCode(404).setErrorMessage("???????????????????????????");
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(false);
            return coverMessage;
        }
        Friends friendsInfo = friendsServices.findFriend(cheatMessage.getTo(), shelfUuid);
        if (friendsInfo == null) {
            socketMessage.error().setCode(404).setErrorMessage("???????????????????????????");
            coverMessage.setMessage(socketMessage);
            coverMessage.setSuccess(false);
            return coverMessage;
        }
        switch (shelfInfo.getStatus()) {
            //???????????????1?????????2?????????3?????????4????????????????????????1
            case 1:
                switch (friendsInfo.getStatus()) {
                    //????????????1????????????
                    case 1:
                        socketMessage.success().setMessage(cheatMessage);
                        coverMessage.setSuccess(true);
                        coverMessage.setMessage(socketMessage);
                        return coverMessage;
                    case 2:
                        socketMessage.success().setMessage(cheatMessage);
                        break;
                    case 3:
                        socketMessage.error().setErrorMessage("????????????????????????");
                        break;
                    default:
                        socketMessage.error().setErrorMessage("???????????????????????????");
                        break;
                }
                break;
            case 2:
                socketMessage.error().setErrorMessage("????????????????????????");
                break;
            case 3:
                socketMessage.error().setErrorMessage("????????????????????????");
                break;
            case 4:
                socketMessage.error().setErrorMessage("???????????????????????????");
                break;
            default:
                socketMessage.error().setErrorMessage("???????????????????????????");
                break;
        }
        //????????????????????????????????????
        coverMessage.setSuccess(false);
        coverMessage.setMessage(socketMessage);
        return coverMessage;
    }

    private coverMessage checkGroup(String shelfUuid, CheatMessage cheatMessage) {
        coverMessage coverMessage = new coverMessage();
        SocketMessage message = new SocketMessage();
        Set<GroupMember> members = groupMemberCache.getGroupMembersByGroupId(cheatMessage.getTo());
        boolean inGroup = false;
        Iterator<GroupMember> it = members.iterator();
        while (it.hasNext()){
            GroupMember m = it.next();
            if (m.getUserUuid().equals(shelfUuid)){
                inGroup = true;
                break;
            }
        }
        if (!inGroup) {
            coverMessage.setSuccess(false);
            message.error().setErrorMessage("????????????????????????");
            coverMessage.setMessage(message);
            return coverMessage;
        }
        coverMessage.setSuccess(true);
        message.success().setMessage(cheatMessage);
        coverMessage.setMessage(message);
        return coverMessage;
    }

    /**
     * ???????????????
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
