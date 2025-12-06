package org.example.helloworld.utils;

import lombok.Getter;

/**
 * 统一业务状态码
 * 
 * 设计原则：
 * - 统一使用 HTTP 200 + 业务code 的方式返回响应
 * - 前端只需判断 code 即可，简化处理逻辑
 * 
 * 业务码规范：
 * - 0: 成功
 * - 1xxxx: 用户相关
 * - 2xxxx: 项目相关
 * - 3xxxx: 订单相关
 * - 4xxxx: 请求相关错误
 * - 5xxxx: 服务器相关错误
 */
@Getter
public enum BusinessCode {

    // ==================== 成功 ====================
    SUCCESS(0, "操作成功"),

    // ==================== 用户相关 1xxxx ====================
    /**
     * Token 无效或已过期，包括Token不存在或为空
     */
    TOKEN_INVALID(10002, "Token 无效或已过期，请重新登录"),

    /**
     * 登录认证失败（用户名或密码错误）
     */
    LOGIN_FAILED(10101, "用户名或密码错误"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(10201, "无访问权限"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS(10301, "用户名已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(10401, "用户不存在"),

    // ==================== 项目相关 2xxxx ====================
    /**
     * 项目不存在
     */
    PROJECT_NOT_FOUND(20001, "项目不存在"),

    // ==================== 订单相关 3xxxx ====================
    /**
     * 订单不存在
     */
    ORDER_NOT_FOUND(30001, "订单不存在"),

    // ==================== 请求相关错误 4xxxx ====================
    /**
     * 请求参数验证失败
     */
    PARAM_VALIDATION_ERROR(40001, "参数验证失败"),

    /**
     * 请求参数格式错误
     */
    PARAM_FORMAT_ERROR(40002, "请求参数格式错误"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(40003, "参数类型错误"),

    /**
     * 缺少必需参数
     */
    PARAM_MISSING(40004, "缺少必需参数"),

    // ==================== 服务器相关错误 5xxxx ====================
    /**
     * 系统内部错误
     */
    INTERNAL_ERROR(50001, "系统错误，请稍后重试"),

    /**
     * 操作失败
     */
    OPERATION_FAILED(50002, "操作失败");

    /**
     * 业务状态码
     */
    private final int code;

    /**
     * 错误描述
     */
    private final String message;

    BusinessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
