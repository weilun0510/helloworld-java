package org.example.helloworld.utils;

import org.example.helloworld.utils.ResultCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回统一结果的类
 */
public class Result {
  private Boolean success;
  private String message;
  private Integer code;
  private Map<String, Object> data = new HashMap<>();

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

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

  // 把构造方法私有
  private Result() {
  }

  // 成功静态方法
  public static Result ok() {
    Result result = new Result();
    result.setSuccess(true);
    result.setMessage("成功");
    result.setCode(ResultCode.SUCCESS);
    return result;
  }

  // 失败静态方法
  public static Result error() {
    Result result = new Result();
    result.setSuccess(false);
    result.setMessage("失败");
    result.setCode(ResultCode.ERROR);
    return result;
  }

  public Result success(Boolean success) {
    this.setSuccess(success);
    return this;
  }

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
