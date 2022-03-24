package com.cinle.wowcheat.Exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cinle.wowcheat.Vo.AjaxResponse;
import com.mongodb.MongoSocketReadTimeoutException;
import io.lettuce.core.RedisCommandTimeoutException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
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
public class GlobalExceptionHandler {


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
    @ExceptionHandler({RedisConnectionFailureException.class, RedisCommandTimeoutException.class, QueryTimeoutException.class})
    public AjaxResponse RedisConnectionFailureException(Exception e) {
        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage("数据库查询异常！" + e.getMessage());
    }

    @ExceptionHandler(RedisOptionsException.class)
    public AjaxResponse RedisOptionsException(Exception e) {
        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage(e.getMessage());
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
//        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        return ajaxResponse.error().setMessage(e.getMessage());
    }

    @ExceptionHandler({MultipartException.class})
    public AjaxResponse MultipartException(Exception e) {
//        e.printStackTrace();
        AjaxResponse ajaxResponse = new AjaxResponse();
        if (e instanceof SizeLimitExceededException || e instanceof MaxUploadSizeExceededException){
            ajaxResponse.error().setMessage("文件上传超过限制大小(10M)！");
        }else {
            ajaxResponse.error().setMessage(e.getMessage());
            e.printStackTrace();
        }
        return ajaxResponse;
    }
}