package com.cinle.wowcheat.Controller;

import com.alibaba.fastjson.JSON;
import com.cinle.wowcheat.Model.Message;
import com.cinle.wowcheat.Service.MessageServices;
import com.cinle.wowcheat.Tools.SecurityContextUtils;
import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/4 17:43
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageServices messageServices;

    @PostMapping("/getAll")
    public AjaxResponse getMessageByUUID(@RequestBody Message message) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        message.setFrom(uuid);
        List personalMess = messageServices.findAllByUUID(message,"personal");
        //有可能是群聊
        List GroupMess = messageServices.findAllByUUID(message,"group");
        if(!personalMess.isEmpty()){
            response.setData(JSON.toJSON(personalMess));
        }
        if(!GroupMess.isEmpty()){
            response.setData(JSON.toJSON(GroupMess));
        }
        return response.success();

    }

    //不使用，后期得验证双方关系是否合法
    @PostMapping("/save")
    public AjaxResponse saveMessage(@RequestBody Message message) {
        AjaxResponse response = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        message.setFrom(uuid);
        int result = messageServices.saveMessage(message,"personal");
        if (result > 0) {
            return response.success().setMessage("保存成功！");
        }
        return response.error().setMessage("保存失败！");
    }

    @PostMapping("/getByPage")
    public AjaxResponse getByPages(@RequestBody Message message) {
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        message.setFrom(uuid);
        AjaxResponse response = new AjaxResponse();
        List personalMess = messageServices.findPersonalByPages(message,"personal");
        //有可能是群聊
        List GroupMess = messageServices.findPersonalByPages(message,"group");
        if(!personalMess.isEmpty()){
            response.setData(JSON.toJSON(personalMess));
        }
        if(!GroupMess.isEmpty()){
            response.setData(JSON.toJSON(GroupMess));
        }
        response.success();
        return response;
    }
}
