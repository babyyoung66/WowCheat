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

    @PostMapping("/get")
    public AjaxResponse getMessageByUUID(@RequestBody Message message) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        String uuid = SecurityContextUtils.getCurrentUserUUID();
        message.setFrom(uuid);
        List mess = messageServices.findAllByUUID(message,"personal");
        return ajaxResponse.success().setData(JSON.toJSON(mess));

    }

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
        List mess = messageServices.findPersonalByPages(message,"personal");
        if(!mess.isEmpty()){
            response.setData(JSON.toJSON(mess));
        }
        response.success();
        return response;
    }
}
