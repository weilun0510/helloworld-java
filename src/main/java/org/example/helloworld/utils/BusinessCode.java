package org.example.helloworld.utils;

import lombok.Getter;

/**
 * 业务错误码
 * 
 * 设计原则：
 * 1. HTTP 状态码用于协议层错误（400, 401, 403, 404, 500等）
 * 2. 业务 Code 用于业务逻辑错误（HTTP 200，但业务失败）
 * 
 * 业务码规范：
 * - 0: 成功
 * - 1xxxx: 用户相关错误
 * - 2xxxx: 项目相关错误
 * - 3xxxx: 订单相关错误
 * - 9xxxx: 系统相关错误
 */
@Getter
public enum BusinessCode {

    // ==================== 成功 ====================
    SUCCESS(0, "操作成功"),

    // ==================== 用户相关 1xxxx ====================
    /**
     * 登录认证失败（用户名或密码错误）
     * HTTP 200 + businessCode 10101
     * 注意：这不是 HTTP 401，因为请求本身成功了，只是业务校验失败
     */
    LOGIN_FAILED(10101, "用户名或密码错误"),

    /**
     * 权限不足
     * HTTP 200 + businessCode 10201
     * 注意：用户已认证，但权限不足，这不是 HTTP 403
     */
    PERMISSION_DENIED(10201, "无访问权限"),

    /**
     * 用户名已存在
     * HTTP 200 + businessCode 10301
     * 注意：这是业务逻辑错误，不是参数错误
     */
    USERNAME_ALREADY_EXISTS(10301, "用户名已存在");

    // /**
    // * 用户不存在
    // */
    // USER_NOT_FOUND(10301, "用户不存在"),

    // /**
    // * 用户已存在
    // */
    // USER_ALREADY_EXISTS(10302, "用户已存在"),

    // /**
    // * 用户已被禁用
    // */
    // USER_DISABLED(10303, "用户已被禁用"),

    // // ==================== 项目相关 2xxxx ====================
    // /**
    // * 项目不存在
    // */
    // PROJECT_NOT_FOUND(20101, "项目不存在"),

    // /**
    // * 项目名称已存在
    // */
    // PROJECT_NAME_EXISTS(20102, "项目名称已存在"),

    // /**
    // * 项目状态不允许修改
    // */
    // PROJECT_STATUS_INVALID(20103, "项目状态不允许修改"),

    // /**
    // * 已完成的项目不能修改
    // */
    // PROJECT_COMPLETED_READONLY(20104, "已完成的项目不能修改"),

    // // ==================== 订单相关 3xxxx ====================
    // /**
    // * 订单不存在
    // */
    // ORDER_NOT_FOUND(30101, "订单不存在"),

    // /**
    // * 订单状态不允许修改
    // */
    // ORDER_STATUS_INVALID(30102, "订单状态不允许修改"),

    // // ==================== 系统相关 9xxxx ====================
    // /**
    // * 参数错误（业务层面的参数错误）
    // */
    // PARAM_ERROR(90001, "参数错误"),

    // /**
    // * 操作失败
    // */
    // OPERATION_FAILED(90002, "操作失败"),

    // /**
    // * 数据已存在
    // */
    // DATA_ALREADY_EXISTS(90003, "数据已存在"),

    // /**
    // * 数据不存在
    // */
    // DATA_NOT_FOUND(90004, "数据不存在");

    /**
     * 业务错误码
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
