package com.cs.advice;

import com.cs.exception.CommonException;
import com.cs.resp.RespResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName ExceptionAdvice
 * @Description TODO
 * @Author wending.wang
 * @Date 2026/3/7 15:06
 * @Version 1.0.0
 **/
@ControllerAdvice()
@RestControllerAdvice()
public class ExceptionAdvice {

    @ExceptionHandler(value = CommonException.class)
    public RespResult sqlErrorHandler(CommonException ex) {
        return handler(ex);
    }

    /**
     * 异常信息捕获，封装ResultVO
     *
     * @param e
     * @return
     */
    private RespResult handler(CommonException e) {
        RespResult resultVO = new RespResult(false);
        String msg = e.getMessage();
        resultVO.setErrorMsg(msg);
        resultVO.setCode(e.getErrorCode());
        return resultVO;
    }
}
