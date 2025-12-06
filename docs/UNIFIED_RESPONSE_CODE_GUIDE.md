# 统一响应码设计指南

## 设计原则

为简化前端处理逻辑，本项目采用 **HTTP 200 + 业务 code** 的统一响应方式：

- ✅ **统一返回 HTTP 200**：所有请求（无论成功或失败）都返回 HTTP 200
- ✅ **通过 code 区分状态**：前端只需判断响应体中的 `code` 字段即可
- ✅ **简化错误处理**：前端无需区分 HTTP 状态码和业务错误码

## 响应格式

所有接口统一返回以下格式：

```json
{
  "code": 0, // 业务状态码（0=成功，非0=失败）
  "message": "操作成功", // 提示信息
  "data": {} // 业务数据
}
```

## 业务状态码规范

### 成功状态码

| Code | 说明     |
| ---- | -------- |
| 0    | 操作成功 |

### 用户相关（1xxxx）

| Code  | 说明               | 场景             |
| ----- | ------------------ | ---------------- |
| 10002 | Token 无效或已过期 | Token 验证失败   |
| 10101 | 用户名或密码错误   | 登录失败         |
| 10201 | 无访问权限         | 权限不足         |
| 10301 | 用户名已存在       | 注册时用户名重复 |
| 10401 | 用户不存在         | 查询用户失败     |

### 项目相关（2xxxx）

| Code  | 说明       | 场景                   |
| ----- | ---------- | ---------------------- |
| 20001 | 项目不存在 | 查询/更新/删除项目失败 |

### 订单相关（3xxxx）

| Code  | 说明       | 场景         |
| ----- | ---------- | ------------ |
| 30001 | 订单不存在 | 查询订单失败 |

### 请求相关错误（4xxxx）

| Code  | 说明             | 场景                               |
| ----- | ---------------- | ---------------------------------- |
| 40001 | 参数验证失败     | @Valid/@Validated 验证失败         |
| 40002 | 请求参数格式错误 | JSON 解析失败、Content-Type 不支持 |
| 40003 | 参数类型错误     | 参数类型不匹配                     |
| 40004 | 缺少必需参数     | 必需参数未传递                     |

### 系统相关错误（5xxxx）

| Code  | 说明                 | 场景         |
| ----- | -------------------- | ------------ |
| 50001 | 系统错误，请稍后重试 | 系统内部异常 |
| 50002 | 操作失败             | 通用操作失败 |

## 使用示例

### 成功响应

```java
// 简单成功
return Result.ok();

// 带消息
return Result.ok().message("登录成功");

// 带数据
return Result.ok()
    .data("token", token)
    .data("username", username);
```

**响应示例：**

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin"
  }
}
```

### 业务失败响应

```java
// 使用枚举（推荐）
return Result.fail(BusinessCode.LOGIN_FAILED);

// 自定义消息
return Result.fail(BusinessCode.USER_NOT_FOUND).message("用户ID为" + id + "的用户不存在");

// 抛出业务异常（由全局异常处理器处理）
throw new BusinessException(BusinessCode.PERMISSION_DENIED);
```

**响应示例：**

```json
{
  "code": 10101,
  "message": "用户名或密码错误",
  "data": {}
}
```

## 前端处理建议

### 统一拦截器示例（Axios）

```javascript
// 响应拦截器
axios.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data;

    // 判断业务状态码
    if (code === 0) {
      // 成功
      return data;
    } else if (code === 10002) {
      // Token 相关错误，跳转登录
      router.push('/login');
      return Promise.reject(new Error(message));
    } else {
      // 其他错误，提示用户
      Message.error(message);
      return Promise.reject(new Error(message));
    }
  },
  (error) => {
    // 网络错误或其他异常
    Message.error('网络错误，请稍后重试');
    return Promise.reject(error);
  },
);
```

## 常见问题

### Q1: 为什么不使用 HTTP 状态码？

A: HTTP 状态码设计用于表示协议层的状态，而业务逻辑的错误应该用业务状态码表示。统一使用 HTTP 200 + 业务 code 可以简化前端处理逻辑，避免混淆。

### Q2: 如何处理网络错误？

A: 真正的网络错误（如超时、连接失败）会返回非 200 状态码或根本无响应，前端可以在拦截器的 `error` 回调中统一处理。

### Q3: RESTful 规范不是要用 HTTP 状态码吗？

A: RESTful 只是一种设计风格，并非强制标准。在实际项目中，根据团队情况选择合适的方案更重要。很多大厂（如阿里、腾讯）的开放平台都采用 HTTP 200 + 业务 code 的方式。

### Q4: 如何区分参数错误和业务错误？

A: 通过 `code` 的范围区分：

- 4xxxx：参数相关错误（参数验证失败、格式错误等）
- 1xxxx/2xxxx/3xxxx：业务逻辑错误（登录失败、资源不存在等）
- 5xxxx：系统错误（内部异常、数据库错误等）

## 参考资料

- [阿里云 API 错误码规范](https://help.aliyun.com/document_detail/25693.html)
- [微信开放平台错误码](https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Global_Return_Code.html)
- [HTTP 状态码 vs 业务状态码](https://www.zhihu.com/question/58686782)
