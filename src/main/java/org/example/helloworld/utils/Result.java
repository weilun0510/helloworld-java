package org.example.helloworld.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果类
 * 
 * 设计原则：
 * - 统一返回 HTTP 200 + 业务code
 * - 前端只需判断 code 即可，无需关注 HTTP 状态码
 * 
 * 响应结构：
 * {
 * "code": 0, // 业务状态码（0=成功，非0=失败）
 * "message": "操作成功", // 提示信息
 * "data": {} // 业务数据
 * }
 * 
 * 使用场景：
 * 1. 业务成功：HTTP 200 + code 0
 * 2. 业务失败：HTTP 200 + code 非0（见 BusinessCode 枚举）
 * 3. 系统错误：HTTP 200 + code 5xxxx
 */
public class Result {

    /**
     * 业务状态码
     * 0 = 成功
     * 非0 = 失败（见 BusinessCode 枚举）
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

    // ==================== 静态工厂方法 ====================

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

    /**
     * 业务失败（使用枚举）
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
