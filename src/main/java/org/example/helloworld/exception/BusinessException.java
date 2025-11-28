package org.example.helloworld.exception;

import lombok.Getter;
import org.example.helloworld.utils.BusinessCode;

/**
 * 业务异常
 * 
 * 用于业务逻辑中的可预期错误，如：
 * - 登录失败（用户名或密码错误）
 * - 权限不足
 * - 数据重复
 * - 状态不允许操作
 * 
 * 处理方式：
 * - 返回 HTTP 200 + businessCode（非0）
 * - 前端正常处理，显示错误提示
 * - 不触发全局错误拦截
 * 
 * 与 HTTP 异常的区别：
 * - BusinessException: 业务逻辑失败（HTTP 200）
 * - IllegalArgumentException: 参数错误（HTTP 400）
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码
     */
    private final BusinessCode businessCode;

    /**
     * 构造函数
     * 
     * @param businessCode 业务错误码
     */
    public BusinessException(BusinessCode businessCode) {
        super(businessCode.getMessage());
        this.businessCode = businessCode;
    }

    /**
     * 构造函数（自定义消息）
     * 
     * @param businessCode 业务错误码
     * @param message      自定义错误消息
     */
    public BusinessException(BusinessCode businessCode, String message) {
        super(message);
        this.businessCode = businessCode;
    }

    /**
     * 构造函数（带原因）
     * 
     * @param businessCode 业务错误码
     * @param message      自定义错误消息
     * @param cause        原因
     */
    public BusinessException(BusinessCode businessCode, String message, Throwable cause) {
        super(message, cause);
        this.businessCode = businessCode;
    }

    // ==================== 快捷方法 ====================

    /**
     * 登录失败
     */
    public static BusinessException loginFailed() {
        return new BusinessException(BusinessCode.LOGIN_FAILED);
    }

    /**
     * 权限不足
     */
    public static BusinessException permissionDenied() {
        return new BusinessException(BusinessCode.PERMISSION_DENIED);
    }
}
