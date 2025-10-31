# 用户 API 测试文档

## 📋 API 接口列表

### 1. 用户注册
**POST** `/user/register`

**请求体：**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**成功响应：**
```json
{
  "success": true,
  "message": "注册成功",
  "code": 200,
  "data": {}
}
```

**错误响应：**
```json
{
  "success": false,
  "message": "用户名已存在",
  "code": 500,
  "data": {}
}
```

---

### 2. 用户登录
**POST** `/user/login`

**请求体：**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**成功响应：**
```json
{
  "success": true,
  "message": "登录成功",
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTczMDM2...",
    "username": "testuser"
  }
}
```

**错误响应：**
```json
{
  "success": false,
  "message": "用户名或密码错误",
  "code": 500,
  "data": {}
}
```

---

### 3. 获取用户信息
**GET** `/user/info?token={token}`

**请求参数：**
- token: JWT Token（必填）

**成功响应：**
```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "username": "testuser",
    "id": 1,
    "avatar": "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg"
  }
}
```

**错误响应：**
```json
{
  "success": false,
  "message": "Token 无效或已过期",
  "code": 500,
  "data": {}
}
```

---

### 4. 根据用户名查询用户
**GET** `/user/findByUsername?username={username}`

**请求参数：**
- username: 用户名（可选）

**成功响应：**
```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "users": [
      {
        "id": 1,
        "username": "testuser",
        "password": null
      }
    ]
  }
}
```

---

### 5. 根据ID获取用户
**GET** `/user/{id}`

**路径参数：**
- id: 用户ID

**成功响应：**
```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "user": {
      "id": 1,
      "username": "testuser",
      "password": null
    }
  }
}
```

---

### 6. 分页查询用户
**GET** `/user/page?page={page}&pageSize={pageSize}`

**请求参数：**
- page: 页码，默认 1
- pageSize: 每页大小，默认 10

**成功响应：**
```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "total": 100,
    "pages": 10,
    "records": [
      {
        "id": 1,
        "username": "testuser",
        "password": "123456"
      }
    ]
  }
}
```

---

### 7. 更新用户
**PUT** `/user/{id}`

**路径参数：**
- id: 用户ID

**请求体：**
```json
{
  "username": "newusername",
  "password": "newpassword"
}
```

**成功响应：**
```json
{
  "success": true,
  "message": "更新成功",
  "code": 200,
  "data": {}
}
```

---

### 8. 删除用户
**DELETE** `/user/{id}`

**路径参数：**
- id: 用户ID

**成功响应：**
```json
{
  "success": true,
  "message": "删除成功",
  "code": 200,
  "data": {}
}
```

---

## 🧪 测试流程

### 1. 注册用户
```bash
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

### 2. 登录获取 Token
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

### 3. 使用 Token 获取用户信息
```bash
curl -X GET "http://localhost:8080/user/info?token=YOUR_TOKEN_HERE"
```

---

## ⚠️ 错误处理

所有接口都通过全局异常处理器统一处理错误：

**错误类型：**
1. 参数校验失败：返回具体的错误提示
2. 用户不存在：返回 "用户名或密码错误"
3. Token 无效：返回 "Token 无效或已过期"
4. 系统错误：返回 "系统错误，请联系管理员"

**错误响应格式：**
```json
{
  "success": false,
  "message": "具体错误信息",
  "code": 500,
  "data": {}
}
```

---

## 📝 注意事项

1. 密码目前为明文存储，生产环境建议使用 BCrypt 加密
2. Token 有效期为 7 天
3. 所有接口都返回统一的 Result 格式
4. 查询用户信息时不会返回密码字段
5. 全局异常处理器会捕获所有异常并返回统一格式的错误响应

