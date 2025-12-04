package org.example.helloworld.exception;

import org.example.helloworld.utils.Result;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * 设计原则：
 * 1. HTTP 状态码用于协议层错误（400, 401, 404, 500等）
 * 2. 业务 Code 用于业务逻辑错误（HTTP 200，但业务失败）
 * 
 * 分类：
 * - HTTP 4xx：客户端错误（参数错误、认证失败等）
 * - HTTP 5xx：服务器错误（系统异常、数据库错误等）
 * - HTTP 200 + businessCode：业务逻辑错误（登录失败、权限不足等）
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

        // ==================== HTTP 400 Bad Request ====================

        /**
         * 处理 @Valid 验证失败异常（用于 @RequestBody）
         * 场景：请求体参数验证失败
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
                // 提取所有字段错误信息
                String errorMessage = e.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining("; "));

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message("参数验证失败: " + errorMessage);
        }

        /**
         * 处理 HandlerMethodValidationException (Spring 6.1+)
         * 场景：方法级别的验证失败（@Validated + @ParameterObject）
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(HandlerMethodValidationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleHandlerMethodValidationException(HandlerMethodValidationException e) {
                // 提取所有验证错误信息
                String errorMessage = e.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .filter(msg -> msg != null && !msg.isEmpty())
                                .collect(Collectors.joining("; "));

                // 如果没有提取到错误消息，使用默认消息
                if (errorMessage.isEmpty()) {
                        errorMessage = "请求参数验证失败";
                }

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message("参数验证失败: " + errorMessage);
        }

        /**
         * 处理 @Validated 验证失败异常（用于 @RequestParam 和 @PathVariable）
         * 场景：请求参数验证失败
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(ConstraintViolationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleConstraintViolationException(ConstraintViolationException e) {
                String errorMessage = e.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining("; "));

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message("参数验证失败: " + errorMessage);
        }

        /**
         * 处理表单绑定异常
         * 场景：表单参数绑定失败
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(BindException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleBindException(BindException e) {
                String errorMessage = e.getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining("; "));

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message("参数绑定失败: " + errorMessage);
        }

        /**
         * 处理非法参数异常
         * 场景：业务层参数验证失败
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleIllegalArgumentException(IllegalArgumentException e) {
                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message(e.getMessage());
        }

        /**
         * 处理 HTTP 消息不可读异常（JSON 解析错误）
         * 场景：JSON 格式错误、类型不匹配、字段类型错误等
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
                String message = e.getMessage();
                Throwable cause = e.getCause();

                // 处理 Jackson 类型不匹配异常（格式转换失败，如 Number -> String）
                if (cause instanceof InvalidFormatException) {
                        InvalidFormatException ife = (InvalidFormatException) cause;
                        String fieldName = extractFieldName(ife.getPath());
                        String targetType = ife.getTargetType() != null ? getTypeName(ife.getTargetType()) : "未知类型";

                        return Result.error(HttpStatus.BAD_REQUEST.value())
                                        .message(String.format(
                                                        "参数类型错误: 字段 '%s' 期望类型为 %s，但实际类型不匹配",
                                                        fieldName, targetType));
                }

                // 处理 Jackson 输入不匹配异常（类型不匹配，如 Number -> String）
                if (cause instanceof MismatchedInputException) {
                        MismatchedInputException mie = (MismatchedInputException) cause;
                        String fieldName = extractFieldName(mie.getPath());
                        String targetType = mie.getTargetType() != null ? getTypeName(mie.getTargetType()) : "未知类型";

                        // 尝试从错误消息中提取实际类型
                        String actualType = extractActualTypeFromMessage(message);
                        String friendlyMessage = String.format("参数类型错误: 字段 '%s' 期望类型为 %s", fieldName, targetType);

                        if (actualType != null) {
                                friendlyMessage += String.format("，但实际传入的是 %s 类型", actualType);
                        } else {
                                friendlyMessage += "，但实际类型不匹配";
                        }

                        // 如果期望 String 类型，给出提示
                        if ("String".equals(targetType)) {
                                friendlyMessage += "。请使用字符串格式，例如 \"值\"";
                        }

                        return Result.error(HttpStatus.BAD_REQUEST.value())
                                        .message(friendlyMessage);
                }

                // 处理 Jackson 值实例化异常
                if (cause instanceof ValueInstantiationException) {
                        ValueInstantiationException vie = (ValueInstantiationException) cause;
                        String fieldName = extractFieldName(vie.getPath());

                        return Result.error(HttpStatus.BAD_REQUEST.value())
                                        .message(String.format("参数值错误: 字段 '%s' 的值无效", fieldName));
                }

                // 处理其他 JSON 解析错误
                if (message != null && message.contains("JSON parse error")) {
                        return Result.error(HttpStatus.BAD_REQUEST.value())
                                        .message("请求参数格式错误: JSON 解析失败，请检查字段类型和格式");
                } else if (message != null && message.contains("Required request body is missing")) {
                        return Result.error(HttpStatus.BAD_REQUEST.value())
                                        .message("请求参数错误: 缺少请求体");
                } else {
                        // 尝试从原始消息中提取有用信息
                        String friendlyMessage = extractFriendlyMessage(message);
                        return Result.error(HttpStatus.BAD_REQUEST.value())
                                        .message("请求参数格式错误: " + friendlyMessage);
                }
        }

        /**
         * 从 Jackson 路径中提取字段名
         * 
         * @param path Jackson 路径列表
         * @return 字段名
         */
        private String extractFieldName(
                        java.util.List<com.fasterxml.jackson.databind.JsonMappingException.Reference> path) {
                if (path == null || path.isEmpty()) {
                        return "未知字段";
                }

                // 获取路径中的最后一个字段名
                return path.stream()
                                .map(ref -> ref.getFieldName())
                                .filter(name -> name != null)
                                .reduce((first, second) -> second) // 获取最后一个字段名
                                .orElse("未知字段");
        }

        /**
         * 获取类型的友好名称
         * 
         * @param type 类型
         * @return 类型名称
         */
        private String getTypeName(Class<?> type) {
                if (type == null) {
                        return "未知类型";
                }

                // 基本类型映射
                if (type == String.class) {
                        return "String（字符串）";
                } else if (type == Integer.class || type == int.class) {
                        return "Integer（整数）";
                } else if (type == Long.class || type == long.class) {
                        return "Long（长整数）";
                } else if (type == Double.class || type == double.class) {
                        return "Double（浮点数）";
                } else if (type == Boolean.class || type == boolean.class) {
                        return "Boolean（布尔值）";
                }

                return type.getSimpleName();
        }

        /**
         * 获取实际值的类型名称
         * 
         * @param value 实际值
         * @return 类型名称
         */
        private String getActualTypeName(Object value) {
                if (value == null) {
                        return "null";
                }

                Class<?> valueClass = value.getClass();
                if (value instanceof Number) {
                        if (value instanceof Integer) {
                                return "Integer（整数）";
                        } else if (value instanceof Long) {
                                return "Long（长整数）";
                        } else if (value instanceof Double || value instanceof Float) {
                                return "Double（浮点数）";
                        }
                        return "Number（数字）";
                } else if (value instanceof String) {
                        return "String（字符串）";
                } else if (value instanceof Boolean) {
                        return "Boolean（布尔值）";
                }

                return valueClass.getSimpleName();
        }

        /**
         * 从错误消息中提取实际类型信息
         * 
         * @param message 错误消息
         * @return 实际类型名称，如果无法提取则返回 null
         */
        private String extractActualTypeFromMessage(String message) {
                if (message == null) {
                        return null;
                }

                // 匹配 "from Number input" 或 "from Integer input" 等
                if (message.contains("from Number input") || message.contains("from Integer input")) {
                        return "Integer（整数）";
                } else if (message.contains("from Long input")) {
                        return "Long（长整数）";
                } else if (message.contains("from Double input") || message.contains("from Float input")) {
                        return "Double（浮点数）";
                } else if (message.contains("from Boolean input")) {
                        return "Boolean（布尔值）";
                } else if (message.contains("from String input")) {
                        return "String（字符串）";
                }

                return null;
        }

        /**
         * 从错误消息中提取友好的错误信息
         * 
         * @param message 原始错误消息
         * @return 友好的错误消息
         */
        private String extractFriendlyMessage(String message) {
                if (message == null) {
                        return "未知错误";
                }

                // 提取字段名和类型信息
                if (message.contains("Cannot deserialize value of type")) {
                        // 格式: Cannot deserialize value of type `java.lang.String` from Number input
                        return "字段类型不匹配，请检查字段类型是否正确";
                }

                if (message.contains("Unexpected token")) {
                        return "JSON 格式错误，请检查 JSON 语法";
                }

                // 返回原始消息（截取前200个字符避免过长）
                return message.length() > 200 ? message.substring(0, 200) + "..." : message;
        }

        /**
         * 处理不支持的媒体类型异常
         * 场景：Content-Type 不支持（如使用 application/x-www-form-urlencoded 而不是
         * application/json）
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
                String contentType = e.getContentType() != null ? e.getContentType().toString() : "未知类型";
                String supportedTypes = e.getSupportedMediaTypes() != null && !e.getSupportedMediaTypes().isEmpty()
                                ? e.getSupportedMediaTypes().toString()
                                : "application/json";

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message(String.format("请求参数格式错误: Content-Type '%s' 不支持，请使用 %s",
                                                contentType, supportedTypes));
        }

        /**
         * 处理参数类型不匹配异常
         * 场景：路径参数或请求参数类型不匹配（如期望 Integer 但传入 String）
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
                String parameterName = e.getName();
                String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型";
                String actualValue = e.getValue() != null ? e.getValue().toString() : "null";

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message(String.format("参数类型错误3: 参数 '%s' 期望类型为 %s，但实际值为 '%s'",
                                                parameterName, requiredType, actualValue));
        }

        /**
         * 处理缺少必需请求参数异常
         * 场景：缺少必需的请求参数（@RequestParam(required = true)）
         * 返回：HTTP 400 + code 400（协议层错误，code 与 HTTP 状态码一致）
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
                String parameterName = e.getParameterName();
                String parameterType = e.getParameterType();

                return Result.error(HttpStatus.BAD_REQUEST.value())
                                .message(String.format("缺少必需参数: 参数 '%s' (类型: %s) 是必需的",
                                                parameterName, parameterType));
        }

        // ==================== HTTP 200 + Business Code（业务逻辑错误）====================

        /**
         * 处理业务异常
         * 场景：业务逻辑校验失败（如登录失败、权限不足等）
         * 返回：HTTP 200 + businessCode（非0）
         * 
         * 关键区别：
         * - HTTP 200 表示请求本身成功
         * - businessCode 表示业务逻辑失败
         * - 前端正常处理，不会触发全局错误拦截
         */
        @ExceptionHandler(BusinessException.class)
        @ResponseStatus(HttpStatus.OK) // 注意：这里是 HTTP 200
        public Result handleBusinessException(BusinessException e) {
                return Result.fail(e.getBusinessCode(), e.getMessage());
        }

        // ==================== HTTP 500 Internal Server Error ====================

        /**
         * 处理所有未捕获的异常（兜底处理）
         * 场景：所有未被上面特定处理器捕获的异常
         * 返回：HTTP 500 + code 500（协议层错误，code 与 HTTP 状态码一致）
         * 
         * 注意：这个处理器会捕获所有异常，包括 RuntimeException
         * 因此放在最后，让更具体的处理器先处理
         */
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public Result handleException(Exception e) {
                // 记录日志
                System.err.println("系统异常: " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();

                return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("系统错误，请稍后重试");
        }
}
