package com.cinle.wowcheat.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author JunLe
 * @Time 2022/2/20 18:59
 */

@ApiModel(description = "通用消息反馈实体")
public class AjaxResponse {

    @ApiModelProperty(value = "反馈结果（指请求的内容是否可用）")
    private boolean success;//请求是否处理成功
    @ApiModelProperty(value = "反馈的状态码")
    private int code;//请求响应状态码（200、400、500)
    @ApiModelProperty(value = "系统反馈的消息")
    private String message;//请求结果描述信息
    @ApiModelProperty(value = "请求返回的资源")
    private Object data;//请求结果数据（通常用于查询操作）


    //请求成功的响应，不带查询数据（用于删除、修改、新增接口>
    public  AjaxResponse success( ){
        this.setSuccess( true);
        this.setCode ( 200);
        this.setMessage("请求响应成功!");
        return this;
    }
    //请求失败的响应，不带查询数据（用于删除、修改、新增接口>
    public AjaxResponse error(){
        this.setSuccess( false);
        this.setCode ( 500);
        this.setMessage("请求响应失败!");
        return this;
    }

    public boolean getSuccess() {
        return success;
    }

    public AjaxResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public AjaxResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public AjaxResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public AjaxResponse setData(Object data) {
        this.data = data;
        return this;
    }
}
