package org.example.helloworld.exception;

import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 从配置文件读取文件大小限制
     */
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    /**
     * 处理文件上传大小超限异常
     * Spring Boot 在接收文件时，如果超过配置的大小限制，会抛出此异常
     * 
     * @param e 文件大小超限异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        String message;

        // 获取完整的异常信息（包括异常链）
        String fullMessage = getFullExceptionMessage(e);

        // 调试日志（生产环境可以删除）
        System.err.println("===== 文件上传异常详情 =====");
        System.err.println("异常消息: " + e.getMessage());
        System.err.println("完整消息: " + fullMessage);
        System.err.println("==========================");

        // 判断是单个文件超限还是整个请求超限
        // 关键词判断：FileSizeLimitExceededException 表示单个文件，SizeLimitExceededException
        // 表示总请求大小
        if (fullMessage.contains("FileSizeLimitExceededException")) {
            // 单个文件超限
            message = "上传文件大小超过限制！单个文件最大允许：" + maxFileSize;
        } else if (fullMessage.contains("SizeLimitExceededException")) {
            // 单次请求总大小超限
            message = "上传文件总大小超过限制！单次请求最大允许：" + maxRequestSize;
        } else {
            // 无法判断具体类型，提供通用提示
            message = "上传文件大小超过限制！单个文件最大：" + maxFileSize + "，单次请求最大：" + maxRequestSize;
        }

        return Result.error().message(message);
    }

    /**
     * 获取完整的异常信息（包括异常链中的所有信息）
     * 
     * @param e 异常对象
     * @return 完整异常信息
     */
    private String getFullExceptionMessage(Throwable e) {
        StringBuilder sb = new StringBuilder();
        Throwable current = e;

        // 遍历整个异常链
        while (current != null) {
            // 添加异常类名
            sb.append(current.getClass().getName()).append(": ");
            // 添加异常消息
            if (current.getMessage() != null) {
                sb.append(current.getMessage());
            }
            sb.append(" | ");

            // 移动到下一个异常
            current = current.getCause();
        }

        return sb.toString();
    }

    /**
     * 处理运行时异常
     * 
     * @param e 异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        return Result.error().message(e.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     * 
     * @param e 异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error().message("系统错误，请联系管理员");
    }
}
