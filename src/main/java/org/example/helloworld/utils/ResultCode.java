package org.example.helloworld.utils;

public interface ResultCode {
  public static final Integer SUCCESS = 20000; // 成功
  public static final Integer ERROR = 20001; // 失败
  public static final Integer UNAUTHORIZED = 401; // 未授权
  public static final Integer FORBIDDEN = 403; // 禁止访问
  public static final Integer NOT_FOUND = 404; // 未找到
  public static final Integer INTERNAL_SERVER_ERROR = 500; // 内部服务器错误
}
