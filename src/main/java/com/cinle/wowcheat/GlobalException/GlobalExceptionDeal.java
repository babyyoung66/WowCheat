package com.cinle.wowcheat.GlobalException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.mongodb.MongoSocketReadTimeoutException;
import io.lettuce.core.RedisCommandTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Iterator;
import java.util.Set;

/**
 * controller全局异常处理
 *
 * @author BabyYoung
 * <p>
 * RestControllerAdvice 传入对应的注解类
 */
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionDeal {


    /**
     * validate 数据校验异常统一返回
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ConstraintViolationException.class, BindException.class, MethodArgumentNotValidException.class})
    public AjaxResponse validateException(Exception ex, HttpServletRequest request) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        //ex.printStackTrace();
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException =
                    (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
            Iterator it = violations.iterator();
            StringBuffer bf = new StringBuffer();
            int count = 0;
            int size = violations.size();
            while (it.hasNext()) {
                ConstraintViolation mess = (ConstraintViolation) it.next();
                bf.append(mess.getMessage());
                count++;
                if (count < size) {
                    bf.append(",");
                }
            }
            return ajaxResponse.error().setMessage(String.valueOf(bf));
        }
        if (ex instanceof BindException) {
            BindException bindException = (BindException) ex;
            String msg = bindException.getBindingResult().getFieldError().getDefaultMessage();
            return ajaxResponse.error().setMessage(msg);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException Exception = (MethodArgumentNotValidException) ex;
            String msg = Exception.getBindingResult().getFieldError().getDefaultMessage();
            return ajaxResponse.error().setMessage(msg);
        }
        return null;

    }


    /**
     * 主键冲突
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    public AjaxResponse keysNotNull(Exception ex) {
        ex.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage("SQL执行错误，请联系管理员！" + ex.getMessage());
    }


    /**
     * SQL错误
     */
    @ExceptionHandler({SQLException.class})
    public AjaxResponse SQLException(Exception e) {
        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage("SQL执行错误，请联系管理员！");
    }

    /**
     * jwt解析错误
     */
    @ExceptionHandler({JWTVerificationException.class})
    public AjaxResponse JWTVerificationException(Exception e, HttpServletResponse response) {
        //e.printStackTrace();
        response.setStatus(401);
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setCode(401).setMessage(e.getMessage());
    }


    /**
     * Redis连接异常
     */
    @ExceptionHandler({RedisConnectionFailureException.class, RedisCommandTimeoutException.class})
    public AjaxResponse RedisConnectionFailureException(Exception e) {
        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage("服务器Redis缓存连接失败！");
    }

    /**
     * MongoDB连接异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({MongoSocketReadTimeoutException.class})
    public AjaxResponse MongoSocketReadTimeoutException(Exception e) {
        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage("服务器MongoDB连接失败！");
    }

    /**
     * 文件上传异常类
     *
     * @param e
     * @return
     */
    @ExceptionHandler({UploadFileException.class})
    public AjaxResponse UploadFileException(Exception e) {
        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage(e.getMessage());
    }
}