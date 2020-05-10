package com.item_backend.exception;

import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;


/**
 * 异常处理
 * @RestControllerAdvice 是全局异常处理并返回json
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 未找到处理器 异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result noHandlerFoundExceptionHandler(Exception e) {
//        e.printStackTrace();
        return Result.create(StatusCode.NOTFOUND, "接口不存在");
    }


    /**
     * 权限不足
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result accessDeniedExceptionHandler(Exception e) {
//        e.printStackTrace();
        return Result.create(StatusCode.ACCESSERROR, "拒绝访问");
    }

    /**
     * 请求方式错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedExceptionHandler(Exception e) {
//        e.printStackTrace();
        return Result.create(StatusCode.ERROR, "请求方式错误");
    }


    /**
     * controller参数异常/缺少（需要security配合）
     *
     * @param e
     * @return
     */
//    @ExceptionHandler({
//            MissingServletRequestParameterException.class,
//            MethodArgumentTypeMismatchException.class,
//            RequestRejectedException.class}
//    )
//    public Result missingServletRequestParameterException(Exception e) {
//        return Result.create(StatusCode.ERROR, "参数异常");
//
//    }

    /**
     * 单次上传文件过大
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result maxUploadSizeExceededException(Exception e) {
//        e.printStackTrace();
        return Result.create(StatusCode.ERROR, "文件过大");
    }

    /**
     * 客户端错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ClientAbortException.class)
    public Result clientAbortExceptionException(Exception e) {
//        e.printStackTrace();
        return Result.create(StatusCode.ERROR, "客户端错误");
    }


    /**
     * 其他异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        e.printStackTrace();
        return Result.create(StatusCode.SERVICEERROR, "服务异常 请联系管理员");
    }

}

