# 分层架构指南

## 📐 分层架构原则

本项目采用标准的三层架构（MVC + Service）：

```
Controller (控制器层)
    ↓
Service (业务逻辑层)
    ↓
Mapper (数据访问层)
    ↓
Database (数据库)
```

---

## 🎯 各层职责划分

### 1. **Controller 层** - 控制器

**职责：**

- ✅ 接收 HTTP 请求
- ✅ 基础参数验证（null、empty 检查）
- ✅ 调用 Service 层方法
- ✅ 返回统一响应（Result）
- ✅ 异常处理（try-catch）

**禁止：**

- ❌ 业务逻辑处理
- ❌ 数据库操作
- ❌ 复杂的数据处理
- ❌ 构建查询条件（QueryWrapper）
- ❌ 硬编码业务规则

**示例代码：**

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserEntity user) {
        // ✅ 基础参数校验（可选，Service 层也会校验）
        if (user == null) {
            return Result.error().message("请求参数不能为空");
        }

        try {
            // ✅ 调用 Service 层处理业务逻辑
            String token = userService.login(user.getUsername(), user.getPassword());

            // ✅ 返回统一响应
            return Result.ok()
                    .message("登录成功")
                    .data("token", token);
        } catch (IllegalArgumentException e) {
            // ✅ 捕获业务异常
            return Result.error().message(e.getMessage());
        } catch (Exception e) {
            // ✅ 捕获系统异常
            return Result.error().message("系统错误");
        }
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String username) {
        // ✅ 直接调用 Service，不构建 QueryWrapper
        List<UserEntity> users = userService.findByUsername(username);
        return Result.ok().data("users", users);
    }
}
```

---

### 2. **Service 层** - 业务逻辑

**职责：**

- ✅ 业务逻辑处理
- ✅ 数据验证（业务规则）
- ✅ 事务管理（@Transactional）
- ✅ 调用 Mapper 层
- ✅ 数据转换和组装
- ✅ 业务规则判断
- ✅ 抛出业务异常

**禁止：**

- ❌ HTTP 请求处理
- ❌ 返回 Result 对象（返回具体数据类型）
- ❌ 直接返回 null（应抛出异常）

**示例代码：**

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public String login(String username, String password) {
        // ✅ 业务参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // ✅ 构建查询条件（业务逻辑）
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UserEntity user = this.getOne(queryWrapper);

        // ✅ 业务规则判断
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // ✅ 生成 Token（业务逻辑）
        return JwtUtil.generateToken(username);
    }

    @Override
    public UserEntity getUserInfo(String username) {
        UserEntity user = getUserByUsername(username);

        // ✅ 业务逻辑：设置默认头像
        String defaultAvatar = "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg";
        user.setAvatar(defaultAvatar);

        return user;
    }

    @Override
    public List<UserEntity> findByUsername(String username) {
        // ✅ 构建查询条件（业务逻辑在 Service 层）
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.eq("username", username);
        }

        return this.list(queryWrapper);
    }
}
```

---

### 3. **Mapper 层** - 数据访问

**职责：**

- ✅ 数据库 CRUD 操作
- ✅ 复杂 SQL 查询
- ✅ 继承 MyBatis-Plus 的 BaseMapper

**禁止：**

- ❌ 业务逻辑
- ❌ 数据验证
- ❌ 事务管理

**示例代码：**

```java
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    // 继承 BaseMapper 即可获得基础 CRUD 方法
    // 如需复杂查询，可自定义方法

    @Select("SELECT * FROM user WHERE username = #{username}")
    UserEntity selectByUsername(String username);
}
```

---

## 🔄 实际案例对比

### ❌ 错误示例 - 业务逻辑在 Controller

```java
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        // ❌ 业务逻辑（文件类型验证）在 Controller
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            return Result.error().message("不支持的文件类型");
        }

        String url = fileService.upload(file);
        return Result.ok().data("url", url);
    }

    // ❌ 业务规则方法在 Controller
    private boolean isAllowedType(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.startsWith("video/");
    }
}
```

**问题：**

1. 文件类型验证是**业务规则**，应该在 Service 层
2. `isAllowedType` 方法无法被其他地方复用
3. Controller 层变厚，违反单一职责原则

---

### ✅ 正确示例 - 业务逻辑在 Service

```java
// Controller 层
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        // ✅ 基础参数校验
        if (file == null || file.isEmpty()) {
            return Result.error().message("请选择要上传的文件");
        }

        try {
            // ✅ 业务逻辑交给 Service 处理
            String url = fileService.upload(file);
            return Result.ok().data("url", url);
        } catch (IllegalArgumentException e) {
            // ✅ 捕获业务异常
            return Result.error().message(e.getMessage());
        }
    }
}

// Service 层
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(MultipartFile file) throws Exception {
        // ✅ 业务规则验证在 Service 层
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            throw new IllegalArgumentException("不支持的文件类型：" + contentType);
        }

        // ✅ 执行上传逻辑
        // ...
        return fileUrl;
    }

    // ✅ 业务规则方法在 Service 层
    private boolean isAllowedType(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.startsWith("video/") ||
               contentType.equals("application/pdf");
    }
}
```

**优点：**

1. Controller 层薄，只负责请求响应
2. 业务规则集中在 Service 层，便于维护
3. 业务逻辑可以被其他 Service 复用
4. 易于单元测试

---

### ❌ 错误示例 - QueryWrapper 在 Controller

```java
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findByUsername")
    public Result findByUsername(@RequestParam String username) {
        // ❌ 构建查询条件在 Controller
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.eq("username", username);
        }
        List<UserEntity> users = userService.list(queryWrapper);
        return Result.ok().data("users", users);
    }
}
```

**问题：**

1. 查询条件的构建是**业务逻辑**
2. Controller 依赖了 MyBatis-Plus 的 `QueryWrapper`
3. 如果查询条件变复杂，Controller 会很臃肿

---

### ✅ 正确示例 - QueryWrapper 在 Service

```java
// Controller 层
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findByUsername")
    public Result findByUsername(@RequestParam(required = false) String username) {
        // ✅ 直接调用 Service 方法
        List<UserEntity> users = userService.findByUsername(username);
        return Result.ok().data("users", users);
    }
}

// Service 接口
public interface UserService extends IService<UserEntity> {
    List<UserEntity> findByUsername(String username);
}

// Service 实现
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public List<UserEntity> findByUsername(String username) {
        // ✅ 构建查询条件在 Service 层
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.eq("username", username);
        }

        return this.list(queryWrapper);
    }
}
```

**优点：**

1. Controller 不关心查询如何实现
2. 查询逻辑可以轻松修改（如添加更多条件）
3. Service 层完整封装了业务逻辑

---

## 📊 各层对比表

| 特性              | Controller | Service          | Mapper |
| ----------------- | ---------- | ---------------- | ------ |
| **HTTP 请求处理** | ✅         | ❌               | ❌     |
| **参数基础校验**  | ✅         | ✅               | ❌     |
| **业务逻辑**      | ❌         | ✅               | ❌     |
| **业务规则验证**  | ❌         | ✅               | ❌     |
| **QueryWrapper**  | ❌         | ✅               | ❌     |
| **数据库操作**    | ❌         | ✅ (通过 Mapper) | ✅     |
| **事务管理**      | ❌         | ✅               | ❌     |
| **返回 Result**   | ✅         | ❌               | ❌     |
| **抛出异常**      | ❌ (catch) | ✅               | ❌     |

---

## 🎯 判断业务逻辑的标准

**以下情况应该放在 Service 层：**

1. **数据验证**

   - 文件类型检查
   - 数据格式验证
   - 业务规则验证（如用户名重复检查）

2. **数据处理**

   - 数据转换
   - 密码加密
   - Token 生成

3. **查询条件构建**

   - QueryWrapper 构建
   - 复杂查询逻辑

4. **业务规则**

   - 权限判断
   - 状态转换
   - 业务流程控制

5. **默认值设置**
   - 默认头像
   - 默认状态
   - 初始化数据

---

## ✅ 代码审查清单

### Controller 层检查

- [ ] 是否只有基础参数校验？
- [ ] 是否没有 QueryWrapper？
- [ ] 是否没有业务规则判断？
- [ ] 是否返回统一的 Result？
- [ ] 是否正确处理异常？

### Service 层检查

- [ ] 是否包含所有业务逻辑？
- [ ] 是否正确抛出异常而不是返回 null？
- [ ] 是否使用 `@Transactional` 管理事务？
- [ ] 是否返回具体类型而不是 Result？

### Mapper 层检查

- [ ] 是否只有数据库操作？
- [ ] 是否没有业务逻辑？
- [ ] 是否正确继承 BaseMapper？

---

## 📚 总结

**核心原则：**

1. **Controller 层要薄** - 只做请求响应
2. **Service 层要厚** - 包含所有业务逻辑
3. **Mapper 层要纯** - 只做数据访问

**记住一句话：**

> **业务逻辑属于 Service，Controller 只是 Service 的调用者和响应包装器。**

---

## 🔗 相关文档

- [项目结构说明](PROJECT_STRUCTURE.md)
- [开发指南](DEVELOPMENT_GUIDE.md)
- [API 文档](API_DOCUMENTATION.md)
