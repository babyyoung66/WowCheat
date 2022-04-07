package com.cinle.wowcheat.Controller;

import com.cinle.wowcheat.Vo.AjaxResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author JunLe
 * @Time 2022/3/10 22:41
 */
@RestController
@RequestMapping("/")
public class TestConnection {
    private AjaxResponse response = new AjaxResponse().success().setMessage("服务连接正常！");

    @PostMapping("/ping")
    public AjaxResponse ping(){
        return response;
    }
}
