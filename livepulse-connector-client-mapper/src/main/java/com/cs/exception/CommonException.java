package com.cs.exception;

/**
 * @ClassName CommonException
 * @Description TODO
 * @Author wending.wang
 * @Date 2026/3/11 15:56
 * @Version 1.0.0
 **/
public class CommonException extends Exception {

    private static final long serialVersionUID = 7949576462539734036L;

    /**
     * 异常编码
     */
    private String errorCode;

    /**
     * 异常数据
     */
    private Object errorData;

    /**
     * 异常显示参数
     */
    private Object[] messageArgs;

    public CommonException(String errorData) {
        super(errorData);
        this.errorData = errorData;
    }

    public CommonException(String errCode, Object... messageArgs) {
        super(errCode);
        this.messageArgs = messageArgs;
        this.errorCode = errCode;
    }


    public CommonException(String errCode, String errMessage, Object errData) {
        super(errMessage);
        this.errorCode = errCode;
        this.errorData = errData;
    }

    public String getErrorCode() {
        return this.errorCode;
    }


    public Object getErrorData() {
        return this.errorData;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }

}
