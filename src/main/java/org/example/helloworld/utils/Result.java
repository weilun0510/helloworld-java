package org.example.helloworld.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果类
 * 
 * 设计原则：
 * 1. HTTP 状态码表示协议层状态（由 Spring 自动设置或通过 @ResponseStatus 设置）
 * 2. code 字段：
 * - 业务成功：code = 0
 * - 业务失败：code = 业务错误码（如 10101 登录失败）
 * - 协议错误：code = HTTP 状态码（如 400、404、500）
 * 
 * 响应结构：
 * {
 * "code": 0, // 业务错误码（0=成功）或 HTTP 状态码（协议错误）
 * "message": "操作成功", // 提示信息
 * "data": {} // 业务数据
 * }
 * 
 * 使用场景：
 * 1. 业务成功：HTTP 200 + code 0
 * 2. 业务失败：HTTP 200 + code 非0（如 10101 登录失败）
 * 3. 协议错误：HTTP 4xx/5xx + code = HTTP 状态码（如 HTTP 400 + code 400）
 */
public class Result {

    /**
     * 错误码
     * 0 = 业务成功
     * 业务错误码 = 业务失败（见 BusinessCode）
     * HTTP 状态码 = 协议错误（如 400、404、500）
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 业务数据
     */
    private Map<String, Object> data = new HashMap<>();

    // ==================== Getters & Setters ====================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // ==================== 构造方法 ====================

    private Result() {
    }

    // ==================== 成功响应 ====================

    /**
     * 操作成功
     * HTTP 200 + code 0
     * 
     * @return Result 对象
     */
    public static Result ok() {
        Result result = new Result();
        result.setCode(BusinessCode.SUCCESS.getCode());
        result.setMessage(BusinessCode.SUCCESS.getMessage());
        return result;
    }

    // ==================== 业务失败响应（HTTP 200，但业务失败）====================

    /**
     * 业务失败（通用）
     * HTTP 200 + code 非0
     * 
     * @param businessCode 业务错误码
     * @return Result 对象
     */
    public static Result fail(BusinessCode businessCode) {
        Result result = new Result();
        result.setCode(businessCode.getCode());
        result.setMessage(businessCode.getMessage());
        return result;
    }

    /**
     * 业务失败（自定义消息）
     * HTTP 200 + code 非0
     * 
     * @param businessCode 业务错误码
     * @param message      自定义消息
     * @return Result 对象
     */
    public static Result fail(BusinessCode businessCode, String message) {
        Result result = new Result();
        result.setCode(businessCode.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 登录失败
     * HTTP 200 + code 10101
     * 
     * @return Result 对象
     */
    public static Result loginFailed() {
        return fail(BusinessCode.LOGIN_FAILED);
    }

    /**
     * 权限不足
     * HTTP 200 + code 10201
     * 
     * @return Result 对象
     */
    public static Result permissionDenied() {
        return fail(BusinessCode.PERMISSION_DENIED);
    }

    // ==================== 协议错误响应（HTTP 4xx/5xx）====================

    /**
     * 错误响应（协议层错误）
     * 用于 HTTP 协议层错误，code 与 HTTP 状态码保持一致
     * 
     * @param httpStatusCode HTTP 状态码（如 400、404、500）
     * @return Result 对象
     */
    public static Result error(int httpStatusCode) {
        Result result = new Result();
        result.setCode(httpStatusCode);
        result.setMessage("系统错误");
        return result;
    }

    // ==================== 链式调用方法 ====================

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result code(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Result data(Map<String, Object> data) {
        this.setData(data);
        return this;
    }
}
