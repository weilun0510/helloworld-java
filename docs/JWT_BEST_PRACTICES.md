# JWT 最佳实践指南

## 🔑 核心原则

**JWT 的 `subject` 字段应该存储用户的唯一标识（ID），而不是用户名。**

### 为什么？

1. **唯一性保证** - 用户ID是数据库主键，永远唯一且不可变
2. **用户名可能变更** - 用户名可能被修改，导致 Token 失效
3. **安全性** - 用户ID作为唯一标识更安全，减少信息泄露
4. **标准实践** - 符合 JWT 标准规范和行业最佳实践

---

## 📋 本项目的实现

### 1. **JwtUtil.java** - JWT 工具类

#### 生成 Token

```java
/**
 * 生成 JWT Token
 * 
 * @param userId 用户ID（作为 subject）
 * @param username 用户名（作为附加信息）
 * @return JWT Token 字符串
 */
public static String generateToken(Integer userId, String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", username);  // 用户名作为附加信息

    return Jwts.builder()
        .subject(userId.toString())     // ✅ subject 存储用户ID
        .claims(claims)                 // ✅ claims 存储其他信息
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(KEY)
        .compact();
}
```

**关键点：**
- ✅ `subject` 存储用户ID（唯一标识）
- ✅ `claims` 存储用户名等附加信息
- ✅ Token 包含完整的用户信息，无需额外查询

#### 提取用户ID

```java
/**
 * 从 Token 中提取用户ID
 * 
 * @param token JWT Token
 * @return 用户ID
 */
public static Integer getUserIdFromToken(String token) {
    String subject = Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
    return Integer.parseInt(subject);
}
```

#### 提取用户名

```java
/**
 * 从 Token 中提取用户名
 * 
 * @param token JWT Token
 * @return 用户名
 */
public static String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return claims.get("username", String.class);
}
```

---

### 2. **UserServiceImpl.java** - 登录逻辑

```java
@Override
public String login(String username, String password) {
    // 参数校验
    if (username == null || username.trim().isEmpty()) {
        throw new RuntimeException("用户名不能为空");
    }
    if (password == null || password.trim().isEmpty()) {
        throw new RuntimeException("密码不能为空");
    }

    // 查询用户
    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    UserEntity user = this.getOne(queryWrapper);

    // 用户不存在
    if (user == null) {
        throw new RuntimeException("用户名或密码错误");
    }

    // 验证密码
    if (!password.equals(user.getPassword())) {
        throw new RuntimeException("用户名或密码错误");
    }

    // ✅ 生成 token（subject 存储用户ID，username 作为附加信息）
    String token = JwtUtil.generateToken(user.getId(), user.getUsername());
    return token;
}
```

**关键变更：**
```java
// ❌ 旧版本：subject 存储用户名
String token = JwtUtil.generateToken(username);

// ✅ 新版本：subject 存储用户ID，username 作为附加信息
String token = JwtUtil.generateToken(user.getId(), user.getUsername());
```

---

### 3. **LoginInterceptor.java** - 拦截器

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
    // 1. 从请求头中获取 Token
    String token = request.getHeader("Authorization");

    // 支持 Bearer Token 格式
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
    }

    // 2. 检查 Token 是否存在
    if (token == null || token.trim().isEmpty()) {
        sendUnauthorizedResponse(response, "未登录，请先登录");
        return false;
    }

    // 3. 验证 Token 是否有效
    try {
        boolean isValid = JwtUtil.validateToken(token);
        if (!isValid) {
            sendUnauthorizedResponse(response, "Token 无效或已过期");
            return false;
        }

        // ✅ 4. Token 验证通过，从 Token 中提取用户ID和用户名并存储到请求中
        Integer userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        return true;

    } catch (Exception e) {
        sendUnauthorizedResponse(response, "Token 无效或已过期");
        return false;
    }
}
```

**关键变更：**
```java
// ❌ 旧版本：只提取用户名
String username = JwtUtil.getUsernameFromToken(token);
request.setAttribute("username", username);

// ✅ 新版本：提取用户ID和用户名
Integer userId = JwtUtil.getUserIdFromToken(token);
String username = JwtUtil.getUsernameFromToken(token);
request.setAttribute("userId", userId);
request.setAttribute("username", username);
```

---

### 4. **UserController.java** - /info 接口

```java
/**
 * 获取当前登录用户信息
 * 拦截器已验证 Token，直接从请求中获取用户ID
 * 
 * @param request HTTP 请求对象
 * @return 返回用户信息
 */
@Operation(summary = "获取当前用户信息", description = "根据Token获取当前登录用户的信息")
@GetMapping("/info")
public Result info(jakarta.servlet.http.HttpServletRequest request) {
    // ✅ 从请求属性中获取用户ID（由拦截器设置）
    Integer userId = (Integer) request.getAttribute("userId");

    if (userId == null) {
        return Result.error().message("获取用户信息失败");
    }

    // ✅ 根据用户ID查询用户信息
    UserEntity user = userService.getById(userId);
    if (user == null) {
        return Result.error().message("用户不存在");
    }

    // 设置默认头像
    String defaultAvatar = "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg";
    user.setAvatar(defaultAvatar);

    // 不返回密码
    user.setPassword(null);

    return Result.ok()
            .data("username", user.getUsername())
            .data("id", user.getId())
            .data("avatar", user.getAvatar());
}
```

**关键变更：**
```java
// ❌ 旧版本：从请求中获取用户名，然后根据用户名查询
String username = (String) request.getAttribute("username");
UserEntity user = userService.getUserInfo(username);

// ✅ 新版本：从请求中获取用户ID，然后根据ID查询
Integer userId = (Integer) request.getAttribute("userId");
UserEntity user = userService.getById(userId);
```

**优点：**
1. **性能提升** - 根据主键ID查询比根据用户名查询更快
2. **代码简化** - 直接使用 `getById()`，无需构建 `QueryWrapper`
3. **唯一性保证** - ID是主键，保证唯一性

---

## 🔄 完整流程图

```
用户登录
    ↓
1. 输入用户名和密码
    ↓
2. UserService.login() 验证
    ↓
3. 验证通过，查询用户信息（包括ID）
    ↓
4. JwtUtil.generateToken(userId, username)
    ↓
5. 返回 Token
    ↓
用户访问需要认证的接口
    ↓
6. LoginInterceptor 拦截请求
    ↓
7. 从 Header 获取 Token
    ↓
8. JwtUtil.validateToken(token) 验证
    ↓
9. JwtUtil.getUserIdFromToken(token) 提取用户ID
    ↓
10. JwtUtil.getUsernameFromToken(token) 提取用户名
    ↓
11. 将 userId 和 username 存入 request attributes
    ↓
12. Controller 从 request 获取 userId
    ↓
13. 根据 userId 查询用户信息
    ↓
14. 返回用户信息
```

---

## 📊 对比表

| 特性 | 旧实现（用户名） | 新实现（用户ID） |
|------|----------------|-----------------|
| **subject 存储** | 用户名 | 用户ID ✅ |
| **唯一性** | 可能重复 | 永远唯一 ✅ |
| **不可变性** | 用户名可能修改 | ID永不改变 ✅ |
| **查询效率** | 需要索引查询 | 主键查询 ✅ |
| **附加信息** | 无 | 用户名在 claims 中 ✅ |
| **安全性** | 较低 | 较高 ✅ |
| **符合标准** | 否 | 是 ✅ |

---

## ✅ 最佳实践总结

### 1. **Token 生成**
```java
// ✅ 正确：subject 存储ID，claims 存储其他信息
String token = JwtUtil.generateToken(user.getId(), user.getUsername());

// ❌ 错误：subject 存储用户名
String token = JwtUtil.generateToken(user.getUsername());
```

### 2. **Token 解析**
```java
// ✅ 正确：提取用户ID（主要标识）
Integer userId = JwtUtil.getUserIdFromToken(token);

// ✅ 正确：提取用户名（附加信息）
String username = JwtUtil.getUsernameFromToken(token);
```

### 3. **用户信息查询**
```java
// ✅ 正确：根据ID查询（主键查询，高效）
UserEntity user = userService.getById(userId);

// ❌ 不推荐：根据用户名查询（需要索引，效率低）
UserEntity user = userService.getUserByUsername(username);
```

### 4. **Token 内容设计**
```json
{
  "sub": "123",                    // ✅ subject: 用户ID
  "username": "zhangsan",          // ✅ claims: 用户名
  "iat": 1699000000,               // ✅ 签发时间
  "exp": 1699604800                // ✅ 过期时间
}
```

---

## 🔗 相关文档

- [分层架构指南](LAYERED_ARCHITECTURE_GUIDE.md)
- [项目结构说明](PROJECT_STRUCTURE.md)
- [开发指南](DEVELOPMENT_GUIDE.md)

---

## 📝 注意事项

1. **Token 安全性**
   - 使用 HTTPS 传输
   - 不要在 URL 中传递 Token
   - 使用 `Authorization: Bearer <token>` 头部

2. **Token 过期时间**
   - 根据业务需求设置合理的过期时间
   - 考虑实现 Token 刷新机制

3. **敏感信息**
   - 不要在 Token 中存储密码
   - 不要在 Token 中存储过多信息（Token 会变大）

4. **用户信息变更**
   - 用户名修改不影响 Token（因为 subject 是ID）
   - 如需强制用户重新登录，可以在服务端维护黑名单

