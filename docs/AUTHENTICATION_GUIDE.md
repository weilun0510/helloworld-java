# 登录认证与白名单配置指南

## 📋 认证机制说明

本项目采用 **JWT Token** 进行身份认证，通过拦截器实现统一的权限校验。

## 🔐 认证流程

### 1. 用户登录获取 Token

```bash
POST /user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

**响应：**
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

### 2. 携带 Token 访问受保护的接口

**方式一：使用 Authorization 请求头（推荐）**
```bash
GET /user/info
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**方式二：直接传递 Token**
```bash
GET /user/info
Authorization: eyJhbGciOiJIUzI1NiJ9...
```

### 3. 拦截器自动验证 Token

拦截器会：
1. 从请求头中提取 Token
2. 验证 Token 是否有效
3. 从 Token 中解析出用户名
4. 将用户名存储到 `request.attribute` 中供 Controller 使用

## ✅ 白名单配置

### 当前白名单接口（无需 Token 即可访问）

#### **用户认证相关**
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册

#### **测试接口**
- `GET /hello`
- `GET /hello/**`
- `GET /getName`
- `GET /getName1`
- `POST /postTest1`
- `POST /postTest2`
- `GET /test/**`

#### **API 文档**
- `/swagger-ui.html`
- `/swagger-ui/**`
- `/swagger-resources/**`
- `/v3/api-docs/**`
- `/webjars/**`
- `/doc.html`
- `/favicon.ico`

#### **静态资源**
- `/static/**`
- `/css/**`
- `/js/**`
- `/images/**`

#### **文件上传**
- `/upload/**`

### 如何添加新的白名单接口

在 `WebConfig.java` 中修改 `excludePathPatterns` 方法：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(
                    "/user/login",
                    "/user/register",
                    // 添加新的白名单接口
                    "/your/new/api",
                    "/another/api/**"
            );
}
```

## 🚫 受保护的接口（需要 Token）

以下接口需要在请求头中携带有效的 Token：

### 用户相关
- `GET /user/info` - 获取当前登录用户信息
- `GET /user/{id}` - 根据ID获取用户信息
- `GET /user/findByUsername` - 条件查询用户
- `GET /user/page` - 分页查询用户
- `POST /user` - 创建用户
- `PUT /user/{id}` - 更新用户
- `DELETE /user/{id}` - 删除用户

### 订单相关
- `GET /order/findAll` - 查询所有订单
- `GET /order/{id}` - 根据ID查询订单
- `GET /order/user/{uid}` - 根据用户ID查询订单

## 📝 Token 验证失败的响应

### 401 未认证

**情况1：未携带 Token**
```json
{
  "success": false,
  "message": "未登录，请先登录",
  "code": 401,
  "data": {}
}
```

**情况2：Token 无效或已过期**
```json
{
  "success": false,
  "message": "Token 无效或已过期",
  "code": 401,
  "data": {}
}
```

## 🧪 测试示例

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

### 2. 使用 Token 访问受保护接口

```bash
# 方式一：Bearer Token（推荐）
curl -X GET http://localhost:8080/user/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 方式二：直接传递 Token
curl -X GET http://localhost:8080/user/info \
  -H "Authorization: YOUR_TOKEN_HERE"
```

### 3. 不携带 Token 访问（会被拦截）

```bash
curl -X GET http://localhost:8080/user/info
# 返回 401 未认证
```

### 4. 访问白名单接口（无需 Token）

```bash
curl -X GET http://localhost:8080/hello?name=world
# 正常返回，不需要 Token
```

## 🔧 在 Controller 中获取当前登录用户

拦截器会将用户名存储在 `request.attribute` 中，Controller 可以直接获取：

```java
@GetMapping("/info")
public Result info(HttpServletRequest request) {
    // 从请求属性中获取用户名（由拦截器设置）
    String username = (String) request.getAttribute("username");
    
    // 使用用户名查询用户信息
    UserEntity user = userService.getUserByUsername(username);
    
    return Result.ok().data("user", user);
}
```

## 📊 拦截器工作流程

```
请求到达
    ↓
LoginInterceptor.preHandle()
    ↓
检查请求路径是否在白名单中？
    ├─ 是 → 放行，继续执行
    └─ 否 → 检查 Token
              ↓
         Token 存在？
              ├─ 否 → 返回 401（未登录）
              └─ 是 → 验证 Token
                        ↓
                   Token 有效？
                        ├─ 否 → 返回 401（Token 无效）
                        └─ 是 → 提取用户名，存储到 request
                                    ↓
                               放行，继续执行
                                    ↓
                              Controller 处理
```

## ⚙️ 配置文件说明

### LoginInterceptor.java
```java
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        // 1. 获取 Token
        String token = request.getHeader("Authorization");
        
        // 2. 验证 Token
        if (token == null || !JwtUtil.validateToken(token)) {
            sendUnauthorizedResponse(response, "未登录或 Token 无效");
            return false;
        }
        
        // 3. 提取用户信息
        String username = JwtUtil.getUsernameFromToken(token);
        request.setAttribute("username", username);
        
        return true;
    }
}
```

### WebConfig.java
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")           // 拦截所有请求
                .excludePathPatterns(             // 白名单
                        "/user/login",
                        "/user/register"
                );
    }
}
```

## 🎯 最佳实践

### 1. Token 存储
- **前端**：存储在 localStorage 或 sessionStorage
- **移动端**：存储在本地安全存储（如 SharedPreferences、Keychain）

### 2. Token 传递
- 推荐使用 `Authorization: Bearer <token>` 格式
- 所有需要认证的请求都要携带 Token

### 3. Token 刷新
- Token 有效期：7天
- 建议在 Token 即将过期前刷新（可以添加刷新 Token 接口）

### 4. 安全建议
- ✅ 使用 HTTPS 传输 Token
- ✅ Token 不要在 URL 中传递
- ✅ 定期更换密钥
- ✅ 设置合理的过期时间
- ✅ 敏感操作需要二次验证

## 🔍 调试技巧

### 查看拦截器日志
拦截器会打印以下日志：
```
LoginInterceptor - 请求路径: /user/info
LoginInterceptor - Token 验证通过，用户: testuser
```

### 常见问题

**Q1: 为什么我的接口一直返回 401？**
- 检查是否携带了 Token
- 检查 Token 是否正确
- 检查接口是否在白名单中

**Q2: 如何在 Postman 中测试？**
1. 先调用登录接口获取 Token
2. 在后续请求的 Headers 中添加：
   - Key: `Authorization`
   - Value: `Bearer <your_token>`

**Q3: 如何临时关闭认证？**
在 `WebConfig.java` 中注释掉拦截器注册：
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // 注释掉以下代码即可临时关闭认证
    // registry.addInterceptor(new LoginInterceptor())...
}
```

## 📌 注意事项

1. **白名单配置**：确保登录和注册接口在白名单中，否则无法登录
2. **Token 格式**：支持 `Bearer <token>` 和直接传递 Token 两种格式
3. **跨域问题**：如果有跨域需求，需要在 WebConfig 中配置 CORS
4. **性能考虑**：拦截器会拦截所有请求，Token 验证操作要尽量高效

## 🚀 总结

✅ **已实现功能：**
- JWT Token 生成与验证
- 拦截器统一权限校验
- 白名单机制
- 401 未认证响应
- 用户信息自动注入

✅ **安全特性：**
- Token 过期时间控制
- 统一的错误响应
- 灵活的白名单配置
- 用户信息自动提取

现在你的 API 已经具备完整的认证和授权机制！🎉

