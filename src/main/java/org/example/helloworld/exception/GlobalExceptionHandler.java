package org.example.helloworld.exception;

import org.example.helloworld.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种异常，返回统一的响应格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 验证失败异常（用于 @RequestBody）
     * 
     * @param e 方法参数验证异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 提取所有字段错误信息
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        return Result.error()
                .code(40000)
                .message("参数验证失败: " + errorMessage);
    }

    /**
     * 处理 @Validated 验证失败异常（用于 @RequestParam 和 @PathVariable）
     * 
     * @param e 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        return Result.error()
                .code(40000)
                .message("参数验证失败: " + errorMessage);
    }

    /**
     * 处理表单绑定异常
     * 
     * @param e 绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleBindException(BindException e) {
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        return Result.error()
                .code(40000)
                .message("参数绑定失败: " + errorMessage);
    }

    /**
     * 处理业务异常（自定义）
     * 
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleBusinessException(BusinessException e) {
        return Result.error()
                .code(e.getCode())
                .message(e.getMessage());
    }

    /**
     * 处理资源不存在异常
     * 
     * @param e 资源不存在异常
     * @return 错误响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleResourceNotFoundException(ResourceNotFoundException e) {
        return Result.error()
                .code(40400)
                .message(e.getMessage());
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error()
                .code(40001)
                .message("参数错误: " + e.getMessage());
    }

    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleRuntimeException(RuntimeException e) {
        // 记录日志
        e.printStackTrace();
        
        return Result.error()
                .code(50000)
                .message("系统错误: " + e.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     * 
     * @param e 异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception e) {
        // 记录日志
        e.printStackTrace();
        
        return Result.error()
                .code(50000)
                .message("系统错误: " + e.getMessage());
    }
}
