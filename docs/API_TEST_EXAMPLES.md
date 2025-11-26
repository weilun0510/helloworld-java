# API 测试示例

## 📋 项目管理 API 测试

### 基础 URL
```
http://localhost:8080/project
```

## ✅ 1. 创建项目（成功）

```http
POST http://localhost:8080/project
Content-Type: application/json

{
  "name": "电商平台项目",
  "status": "进行中",
  "cover": "https://example.com/cover.jpg"
}
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "创建成功",
  "data": {
    "project": {
      "id": 1,
      "name": "电商平台项目",
      "status": "进行中",
      "cover": "https://example.com/cover.jpg",
      "createTime": "2024-11-24 14:30:00"
    }
  }
}
```

## ❌ 2. 创建项目（验证失败）

```http
POST http://localhost:8080/project
Content-Type: application/json

{
  "name": "",
  "status": "进行中"
}
```

**预期响应**（400 Bad Request）:
```json
{
  "success": false,
  "code": 40000,
  "message": "参数验证失败: 项目名称不能为空",
  "data": {}
}
```

## ✅ 3. 查询项目列表（分页）

```http
GET http://localhost:8080/project?page=1&pageSize=10
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "操作成功",
  "data": {
    "total": 25,
    "pages": 3,
    "records": [
      {
        "id": 1,
        "name": "电商平台项目",
        "status": "进行中",
        "cover": "https://example.com/cover.jpg",
        "createTime": "2024-11-24 14:30:00"
      },
      {
        "id": 2,
        "name": "内容管理系统",
        "status": "已完成",
        "cover": null,
        "createTime": "2024-11-23 10:15:00"
      }
    ]
  }
}
```

## ✅ 4. 查询项目详情（成功）

```http
GET http://localhost:8080/project/1
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "操作成功",
  "data": {
    "project": {
      "id": 1,
      "name": "电商平台项目",
      "status": "进行中",
      "cover": "https://example.com/cover.jpg",
      "createTime": "2024-11-24 14:30:00"
    }
  }
}
```

## ❌ 5. 查询项目详情（不存在）

```http
GET http://localhost:8080/project/999
```

**预期响应**（404 Not Found）:
```json
{
  "success": false,
  "code": 40400,
  "message": "项目 不存在: ID = 999",
  "data": {}
}
```

## ✅ 6. 更新项目（部分更新）

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "status": "已完成"
}
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "更新成功",
  "data": {
    "project": {
      "id": 1,
      "name": "电商平台项目",
      "status": "已完成",
      "cover": "https://example.com/cover.jpg",
      "createTime": "2024-11-24 14:30:00"
    }
  }
}
```

**注意**：只更新了 `status`，其他字段（`name`、`cover`、`createTime`）保持不变 ✅

## ✅ 7. 更新多个字段

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "name": "电商平台项目 V2",
  "status": "已完成",
  "cover": "https://example.com/new-cover.jpg"
}
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "更新成功",
  "data": {
    "project": {
      "id": 1,
      "name": "电商平台项目 V2",
      "status": "已完成",
      "cover": "https://example.com/new-cover.jpg",
      "createTime": "2024-11-24 14:30:00"
    }
  }
}
```

**验证**：`createTime` 没有被修改 ✅

## ❌ 8. 更新项目（不存在）

```http
PATCH http://localhost:8080/project/999
Content-Type: application/json

{
  "status": "已完成"
}
```

**预期响应**（404 Not Found）:
```json
{
  "success": false,
  "code": 40400,
  "message": "项目 不存在: ID = 999",
  "data": {}
}
```

## ✅ 9. 删除项目（成功）

```http
DELETE http://localhost:8080/project/1
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "删除成功",
  "data": {}
}
```

## ❌ 10. 删除项目（不存在）

```http
DELETE http://localhost:8080/project/999
```

**预期响应**（404 Not Found）:
```json
{
  "success": false,
  "code": 40400,
  "message": "项目 不存在: ID = 999",
  "data": {}
}
```

## ✅ 11. 批量删除项目

```http
DELETE http://localhost:8080/project/batch
Content-Type: application/json

[1, 2, 3]
```

**预期响应**（200 OK）:
```json
{
  "success": true,
  "code": 20000,
  "message": "批量删除成功，共删除 3 个项目",
  "data": {}
}
```

## ❌ 12. 批量删除（空列表）

```http
DELETE http://localhost:8080/project/batch
Content-Type: application/json

[]
```

**预期响应**（400 Bad Request）:
```json
{
  "success": false,
  "code": 40001,
  "message": "参数错误: 项目 ID 列表不能为空",
  "data": {}
}
```

## 🧪 使用 VS Code REST Client 测试

创建一个 `test.http` 文件：

```http
### 1. 创建项目
POST http://localhost:8080/project
Content-Type: application/json

{
  "name": "测试项目",
  "status": "进行中",
  "cover": "https://example.com/cover.jpg"
}

### 2. 查询列表
GET http://localhost:8080/project?page=1&pageSize=10

### 3. 查询详情
GET http://localhost:8080/project/1

### 4. 更新项目
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "status": "已完成"
}

### 5. 删除项目
DELETE http://localhost:8080/project/1
```

## 🐛 常见问题排查

### 1. 403 Forbidden - 被拦截器拦截
**问题**: 接口被 `LoginInterceptor` 拦截
**解决**: 在 `WebConfig.java` 的白名单中添加 `/project/**`

### 2. 400 Bad Request - 参数验证失败
**问题**: DTO 字段不符合验证规则
**检查**: 
- 必填字段是否为空
- 字段长度是否超限
- 数据类型是否正确

### 3. 404 Not Found - 资源不存在
**问题**: 请求的资源 ID 不存在
**检查**: 
- ID 是否正确
- 资源是否已被删除

### 4. 500 Internal Server Error - 系统错误
**问题**: 服务器内部错误
**检查**:
- 查看控制台日志
- 检查数据库连接
- 检查业务逻辑

## 📊 测试结果记录表

| 编号 | 测试场景 | 预期状态码 | 实际状态码 | 结果 |
|------|---------|-----------|-----------|------|
| 1 | 创建项目（成功） | 200 | | ⬜ |
| 2 | 创建项目（验证失败） | 400 | | ⬜ |
| 3 | 查询列表 | 200 | | ⬜ |
| 4 | 查询详情（成功） | 200 | | ⬜ |
| 5 | 查询详情（不存在） | 404 | | ⬜ |
| 6 | 更新项目（部分） | 200 | | ⬜ |
| 7 | 更新项目（多字段） | 200 | | ⬜ |
| 8 | 更新项目（不存在） | 404 | | ⬜ |
| 9 | 删除项目（成功） | 200 | | ⬜ |
| 10 | 删除项目（不存在） | 404 | | ⬜ |
| 11 | 批量删除 | 200 | | ⬜ |
| 12 | 批量删除（空列表） | 400 | | ⬜ |

## 🔗 相关文档

- [全局异常处理器指南](./GLOBAL_EXCEPTION_HANDLER_GUIDE.md)
- [项目增强功能总结](./PROJECT_ENHANCEMENT_SUMMARY.md)
- [架构最佳实践](./ARCHITECTURE_BEST_PRACTICES.md)

---

**测试工具推荐**：
- VS Code REST Client
- Postman
- Swagger UI (http://localhost:8080/doc.html)
- cURL

祝测试顺利！🚀

