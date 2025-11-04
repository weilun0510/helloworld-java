package org.example.helloworld.utils;

/**
 * 响应状态码
 * 遵循 HTTP 标准状态码规范
 */
public interface ResultCode {
  /** 200 - 成功 */
  Integer SUCCESS = 200;

  /** 201 - 创建成功 */
  Integer CREATED = 201;

  /** 400 - 请求参数错误 */
  Integer BAD_REQUEST = 400;

  /** 401 - 未认证 */
  Integer UNAUTHORIZED = 401;

  /** 403 - 无权限 */
  Integer FORBIDDEN = 403;

  /** 404 - 资源不存在 */
  Integer NOT_FOUND = 404;

  /** 500 - 服务器错误 */
  Integer ERROR = 500;

  /** 500 - 内部服务器错误 */
  Integer INTERNAL_SERVER_ERROR = 500;
}
