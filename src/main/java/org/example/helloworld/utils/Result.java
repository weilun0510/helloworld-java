package org.example.helloworld.utils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一响应结果类（泛型版本）
 * 
 * 设计原则：
 * - 统一返回 HTTP 200 + 业务code
 * - 前端只需判断 code 即可，无需关注 HTTP 状态码
 * - 使用泛型支持类型安全和 Swagger 文档生成
 * 
 * 响应结构：
 * {
 * "code": 0, // 业务状态码（0=成功，非0=失败）
 * "message": "操作成功", // 提示信息
 * "data": {} // 业务数据（泛型）
 * }
 * 
 * @param <T> 业务数据类型
 */
@Schema(description = "统一响应结果")
public class Result<T> {

    /**
     * 业务状态码
     * 0 = 成功
     * 非0 = 失败（见 BusinessCode 枚举）
     */
    @Schema(description = "业务状态码，0表示成功，非0表示失败", example = "0")
    private Integer code;

    /**
     * 提示信息
     */
    @Schema(description = "提示信息", example = "操作成功")
    private String message;

    /**
     * 业务数据
     */
    @Schema(description = "业务数据")
    private T data;

    // ==================== Getters & Setters ====================

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // ==================== 构造方法 ====================

    public Result() {
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 操作成功（无数据）
     * HTTP 200 + code 0
     * 
     * @return Result 对象
     */
    public static <T> Result<T> ok() {
        return new Result<>(BusinessCode.SUCCESS.getCode(), BusinessCode.SUCCESS.getMessage(), null);
    }

    /**
     * 操作成功（带数据）
     * HTTP 200 + code 0
     * 
     * @param data 业务数据
     * @return Result 对象
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(BusinessCode.SUCCESS.getCode(), BusinessCode.SUCCESS.getMessage(), data);
    }

    /**
     * 操作成功（带消息和数据）
     * HTTP 200 + code 0
     * 
     * @param message 提示消息
     * @param data    业务数据
     * @return Result 对象
     */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(BusinessCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 业务失败（使用枚举）
     * HTTP 200 + code 非0
     * 
     * @param businessCode 业务错误码
     * @return Result 对象
     */
    public static <T> Result<T> fail(BusinessCode businessCode) {
        return new Result<>(businessCode.getCode(), businessCode.getMessage(), null);
    }

    /**
     * 业务失败（自定义消息）
     * HTTP 200 + code 非0
     * 
     * @param businessCode 业务错误码
     * @param message      自定义消息
     * @return Result 对象
     */
    public static <T> Result<T> fail(BusinessCode businessCode, String message) {
        return new Result<>(businessCode.getCode(), message, null);
    }

    /**
     * 业务失败（带数据）
     * HTTP 200 + code 非0
     * 
     * @param businessCode 业务错误码
     * @param message      自定义消息
     * @param data         业务数据
     * @return Result 对象
     */
    public static <T> Result<T> fail(BusinessCode businessCode, String message, T data) {
        return new Result<>(businessCode.getCode(), message, data);
    }

    // ==================== 链式调用方法（向后兼容）====================

    /**
     * 设置消息（链式调用）
     * 
     * @param message 提示消息
     * @return Result 对象
     */
    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 设置状态码（链式调用）
     * 
     * @param code 状态码
     * @return Result 对象
     */
    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 设置数据（链式调用）
     * 
     * @param data 业务数据
     * @return Result 对象
     */
    public Result<T> data(T data) {
        this.setData(data);
        return this;
    }
}
