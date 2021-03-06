package com.idianyou.lifecircle.exception;

import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/20 18:44
 */
@Data
public class LifeCircleException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public LifeCircleException(String errorCode, String errorMessage) {
        super(errorCode + ":" + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public LifeCircleException() {
    }

    public LifeCircleException(String message) {
        super(message);
        this.errorCode = "501";
        this.errorMessage = message;
    }

    public LifeCircleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LifeCircleException(Throwable cause) {
        super(cause);
    }

    public LifeCircleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
