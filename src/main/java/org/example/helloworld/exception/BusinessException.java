package org.example.helloworld.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑中抛出的可预期异常
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 40000;
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 40000;
    }
}

