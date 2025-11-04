# API 接口文档

## 📋 接口概览

### 基础信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Token (Bearer Token)
- **响应格式**: JSON
- **字符编码**: UTF-8

### 接口列表

| 模块 | 接口                 | 方法   | 路径                 | 是否需要认证 |
| ---- | -------------------- | ------ | -------------------- | ------------ |
| 用户 | 用户登录             | POST   | /user/login          | ❌           |
| 用户 | 用户注册             | POST   | /user/register       | ❌           |
| 用户 | 获取当前用户信息     | GET    | /user/info           | ✅           |
| 用户 | 根据 ID 获取用户     | GET    | /user/{id}           | ✅           |
| 用户 | 条件查询用户         | GET    | /user/findByUsername | ✅           |
| 用户 | 分页查询用户         | GET    | /user/page           | ✅           |
| 用户 | 创建用户             | POST   | /user                | ✅           |
| 用户 | 更新用户             | PUT    | /user/{id}           | ✅           |
| 用户 | 删除用户             | DELETE | /user/{id}           | ✅           |
| 订单 | 查询所有订单         | GET    | /order/findAll       | ✅           |
| 订单 | 根据 ID 查询订单     | GET    | /order/{id}          | ✅           |
| 订单 | 根据用户 ID 查询订单 | GET    | /order/user/{uid}    | ✅           |
| 文件 | 上传文件             | POST   | /file/upload         | ❌           |
| 文件 | 批量上传文件         | POST   | /file/batch-upload   | ❌           |
| 文件 | 删除文件             | DELETE | /file/delete         | ✅           |

---

## 🔐 认证相关

### 1. 用户登录

**接口地址**: `POST /user/login`

**是否需要认证**: ❌

**请求参数**:

```json
{
  "username": "string  // 用户名（必填）",
  "password": "string  // 密码（必填）"
}
```

**成功响应**:

```json
{
  "success": true,
  "message": "登录成功",
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin"
  }
}
```

**错误响应**:

```json
{
  "success": false,
  "message": "用户名或密码错误",
  "code": 500,
  "data": {}
}
```

**示例**:

```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

---

### 2. 用户注册

**接口地址**: `POST /user/register`

**是否需要认证**: ❌

**请求参数**:

```json
{
  "username": "string  // 用户名（必填，唯一）",
  "password": "string  // 密码（必填）"
}
```

**成功响应**:

```json
{
  "success": true,
  "message": "注册成功",
  "code": 200,
  "data": {}
}
```

**错误响应**:

```json
{
  "success": false,
  "message": "用户名已存在",
  "code": 500,
  "data": {}
}
```

---

## 👤 用户管理

### 3. 获取当前用户信息

**接口地址**: `GET /user/info`

**是否需要认证**: ✅

**请求头**:

```
Authorization: Bearer <token>
```

**成功响应**:

```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "username": "admin",
    "id": 1,
    "avatar": "https://..."
  }
}
```

---

### 4. 根据 ID 获取用户

**接口地址**: `GET /user/{id}`

**是否需要认证**: ✅

**路径参数**:

- `id`: 用户 ID (Integer)

**成功响应**:

```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "user": {
      "id": 1,
      "username": "admin",
      "password": null
    }
  }
}
```

---

### 5. 条件查询用户

**接口地址**: `GET /user/findByUsername`

**是否需要认证**: ✅

**查询参数**:

- `username`: 用户名（可选）

**成功响应**:

```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "users": [
      {
        "id": 1,
        "username": "admin",
        "password": null
      }
    ]
  }
}
```

---

### 6. 分页查询用户

**接口地址**: `GET /user/page`

**是否需要认证**: ✅

**查询参数**:

- `page`: 页码，默认 1
- `pageSize`: 每页大小，默认 10

**成功响应**:

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
        "username": "admin",
        "password": "123456"
      }
    ]
  }
}
```

---

### 7. 创建用户

**接口地址**: `POST /user`

**是否需要认证**: ✅

**请求参数**:

```json
{
  "username": "string",
  "password": "string"
}
```

**成功响应**:

```json
{
  "success": true,
  "message": "插入成功",
  "code": 200,
  "data": {
    "id": 3
  }
}
```

---

### 8. 更新用户

**接口地址**: `PUT /user/{id}`

**是否需要认证**: ✅

**路径参数**:

- `id`: 用户 ID

**请求参数**:

```json
{
  "username": "string",
  "password": "string"
}
```

**成功响应**:

```json
{
  "success": true,
  "message": "更新成功",
  "code": 200,
  "data": {}
}
```

---

### 9. 删除用户

**接口地址**: `DELETE /user/{id}`

**是否需要认证**: ✅

**路径参数**:

- `id`: 用户 ID

**成功响应**:

```json
{
  "success": true,
  "message": "删除成功",
  "code": 200,
  "data": {}
}
```

---

## 📦 订单管理

### 10. 查询所有订单

**接口地址**: `GET /order/findAll`

**是否需要认证**: ✅

**成功响应**:

```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "orders": [
      {
        "id": 1,
        "orderTime": "2025-11-03T12:00:00",
        "total": 100,
        "uid": 1,
        "user": {
          "id": 1,
          "username": "admin"
        }
      }
    ]
  }
}
```

---

### 11. 根据 ID 查询订单

**接口地址**: `GET /order/{id}`

**是否需要认证**: ✅

**路径参数**:

- `id`: 订单 ID

**成功响应**:

```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "order": {
      "id": 1,
      "orderTime": "2025-11-03T12:00:00",
      "total": 100,
      "uid": 1
    }
  }
}
```

---

### 12. 根据用户 ID 查询订单

**接口地址**: `GET /order/user/{uid}`

**是否需要认证**: ✅

**路径参数**:

- `uid`: 用户 ID

**成功响应**:

```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {
    "orders": [
      {
        "id": 1,
        "orderTime": "2025-11-03T12:00:00",
        "total": 100,
        "uid": 1
      }
    ]
  }
}
```

---

## ⚠️ 错误码说明

| 错误码 | 说明       | 示例                   |
| ------ | ---------- | ---------------------- |
| 200    | 成功       | 操作成功               |
| 401    | 未认证     | Token 无效或已过期     |
| 403    | 无权限     | 没有访问权限           |
| 404    | 资源不存在 | 用户不存在             |
| 500    | 服务器错误 | 系统错误，请联系管理员 |

---

## 📝 通用响应格式

### 成功响应

```json
{
  "success": true,
  "message": "操作成功",
  "code": 200,
  "data": {}
}
```

### 错误响应

```json
{
  "success": false,
  "message": "错误信息",
  "code": 500,
  "data": {}
}
```

---

## 🔑 认证说明

### 1. 获取 Token

调用登录接口获取 Token

### 2. 使用 Token

在请求头中添加：

```
Authorization: Bearer <your_token>
```

### 3. Token 有效期

Token 有效期为 7 天，过期后需要重新登录

---

## 📁 文件管理接口

### 1. 上传文件

**接口地址**: `POST /file/upload`

**是否需要认证**: ❌ （可根据需求修改）

**请求方式**: `multipart/form-data`

**请求参数**:

- `file`: 文件（必填，最大 10MB）

**成功响应**:

```json
{
  "success": true,
  "message": "上传成功",
  "code": 200,
  "data": {
    "url": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/uploads/2025/11/03/xxx.jpg",
    "fileName": "test.jpg",
    "size": 102400
  }
}
```

**失败响应**:

```json
{
  "success": false,
  "message": "文件大小不能超过10MB",
  "code": 500
}
```

**支持的文件类型**:

- 图片：image/\*
- 视频：video/\*
- 音频：audio/\*
- 文档：PDF, Word, Excel

**示例 (curl)**:

```bash
curl -X POST http://localhost:8080/file/upload \
  -F "file=@/path/to/file.jpg"
```

---

### 2. 批量上传文件

**接口地址**: `POST /file/batch-upload`

**是否需要认证**: ❌ （可根据需求修改）

**请求方式**: `multipart/form-data`

**请求参数**:

- `files`: 文件数组（必填）

**成功响应**:

```json
{
  "success": true,
  "message": "批量上传成功",
  "code": 200,
  "data": {
    "urls": [
      "https://xxx.com/uploads/2025/11/03/file1.jpg",
      "https://xxx.com/uploads/2025/11/03/file2.jpg"
    ],
    "count": 2
  }
}
```

**示例 (curl)**:

```bash
curl -X POST http://localhost:8080/file/batch-upload \
  -F "files=@/path/to/file1.jpg" \
  -F "files=@/path/to/file2.jpg"
```

---

### 3. 删除文件

**接口地址**: `DELETE /file/delete`

**是否需要认证**: ✅

**请求参数**:

- `fileUrl`: 文件 URL（必填）

**成功响应**:

```json
{
  "success": true,
  "message": "删除成功",
  "code": 200
}
```

**失败响应**:

```json
{
  "success": false,
  "message": "文件URL不能为空",
  "code": 500
}
```

**示例 (curl)**:

```bash
curl -X DELETE "http://localhost:8080/file/delete?fileUrl=https://xxx.com/uploads/xxx.jpg" \
  -H "Authorization: Bearer <token>"
```

---

## 🧪 在线测试

访问 Swagger 文档进行在线测试（支持分组查看）：

```
http://localhost:8080/doc.html
```

**Swagger 分组**:

- 用户管理：所有用户相关接口
- 订单管理：所有订单相关接口
- 文件管理：所有文件上传相关接口
- 所有接口：查看全部接口

---

## 📌 注意事项

1. 所有需要认证的接口必须携带 Token
2. Token 格式：`Bearer <token>`
3. 请求头 `Content-Type` 必须为 `application/json`（文件上传除外）
4. 文件上传大小限制：单个文件最大 10MB，批量上传总大小最大 50MB
5. 密码建议使用 HTTPS 传输
6. 敏感操作建议添加二次验证
7. 阿里云 OSS 配置为空时，文件上传将降级到本地存储模拟

---

## 🔧 阿里云 OSS 配置

在 `application.properties` 中配置：

```properties
# 阿里云 OSS 配置
aliyun.oss.endpoint=oss-cn-hangzhou.aliyuncs.com
aliyun.oss.accessKeyId=your_access_key_id
aliyun.oss.accessKeySecret=your_access_key_secret
aliyun.oss.bucketName=your_bucket_name
aliyun.oss.urlPrefix=https://your-cdn-domain.com
```

如果不配置，文件上传功能仍可使用（本地存储模式）。

---

更多详细信息请查看项目文档
