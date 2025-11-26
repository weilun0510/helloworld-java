package org.example.helloworld.exception;

/**
 * 资源不存在异常
 * 当请求的资源（如用户、项目等）不存在时抛出
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param resourceName 资源名称
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s 不存在: %s = %s", resourceName, fieldName, fieldValue));
    }
}

