package com.cinle.wowcheat.WebSocket;

/**
 * @Author JunLe
 * @Time 2022/3/24 22:08
 */
public class SocketMessage {
    private int code;

    private boolean success;

    private String errorMessage;

    private Object message;

    public SocketMessage success(){
        this.code = 200;
        this.success = true;
        return this;
    }
    public SocketMessage error(){
        this.code = 500;
        this.success = false;
        this.errorMessage = "服务器暂时无法处理您的请求！";
        return this;
    }

    public int getCode() {
        return code;
    }

    public SocketMessage setCode(int code) {
        this.code = code;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public SocketMessage setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public SocketMessage setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Object getMessage() {
        return message;
    }

    public SocketMessage setMessage(Object message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "SocketMessage{" +
                "code=" + code +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", message=" + message +
                '}';
    }
}
