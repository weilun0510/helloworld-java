# 开发规范文档

## 📝 代码规范

### 1. 命名规范

#### 包命名

- 全部小写
- 使用域名反写：`com.company.project.module`
- 示例：`org.example.helloworld.controller`

#### 类命名

- 大驼峰（PascalCase）
- 实体类：`XxxEntity`
- 控制器：`XxxController`
- 服务接口：`XxxService`
- 服务实现：`XxxServiceImpl`
- Mapper：`XxxMapper`
- 工具类：`XxxUtil`

#### 方法命名

- 小驼峰（camelCase）
- 获取：`getXxx()`
- 设置：`setXxx()`
- 布尔：`isXxx()` 或 `hasXxx()`
- 转换：`toXxx()`
- 示例：`getUserById()`, `isActive()`, `toJson()`

#### 变量命名

- 小驼峰（camelCase）
- 常量：全大写，下划线分隔 `MAX_SIZE`
- 集合类型添加后缀：`userList`, `userMap`, `userSet`
- 布尔类型：`isXxx`, `hasXxx`, `canXxx`

#### 数据库命名

- 表名：小写，下划线分隔 `user_info`
- 字段：小写，下划线分隔 `user_name`
- 主键：`id`
- 外键：`xxx_id`

---

### 2. 注释规范

#### 类注释

```java
/**
 * 用户服务实现类
 * 继承 ServiceImpl 获得 MyBatis-Plus 提供的 CRUD 方法
 *
 * @author YourName
 * @since 2025-11-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
}
```

#### 方法注释（JSDoc 风格）

```java
/**
 * 用户登录
 *
 * @param username 用户名
 * @param password 密码
 * @return 登录成功返回 token，失败抛出异常
 * @throws RuntimeException 用户名或密码错误
 */
public String login(String username, String password) {
    // 方法实现
}
```

#### 字段注释

```java
/** Token 过期时间：7天 */
private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

/** 用户名 */
private String username;
```

#### 单行注释

```java
// 验证密码
if (!password.equals(user.getPassword())) {
    throw new RuntimeException("密码错误");
}
```

---

### 3. 代码格式

#### 缩进

- 使用 **4 个空格**（不要使用 Tab）
- IDE 设置：IDEA → Settings → Editor → Code Style → Java → Tabs and Indents

#### 空行

- 方法之间：1 行
- 逻辑块之间：1 行
- 类成员之间：1 行

#### 每行长度

- 建议不超过 120 字符
- 超长的表达式需要换行

#### 括号

- `{` 不单独占一行
- `}` 单独占一行

```java
// 正确
if (condition) {
    // code
}

// 错误
if (condition)
{
    // code
}
```

---

## 🏗️ 分层开发规范

### Controller 层

#### 职责

- 接收 HTTP 请求
- 参数验证
- 调用 Service 层
- 返回统一响应

#### 规范

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param user 用户登录信息
     * @return 返回 token 和用户信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserEntity user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        return Result.ok()
                .message("登录成功")
                .data("token", token);
    }
}
```

#### 注意事项

- ✅ 只注入 Service，不注入 Mapper
- ✅ 使用 `@RequestBody` 接收 JSON
- ✅ 使用 `@RequestParam` 接收参数
- ✅ 使用 `@PathVariable` 接收路径变量
- ✅ 所有接口返回 `Result` 对象
- ❌ 不要在 Controller 中写业务逻辑
- ❌ 不要直接操作数据库

---

### Service 层

#### 职责

- 业务逻辑处理
- 数据校验
- 事务管理
- 调用 Mapper 层

#### 规范

```java
// 接口
public interface UserService extends IService<UserEntity> {
    String login(String username, String password);
}

// 实现
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
                              implements UserService {

    @Override
    public String login(String username, String password) {
        // 1. 参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }

        // 2. 业务逻辑
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        UserEntity user = this.getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 返回结果
        return JwtUtil.generateToken(username);
    }
}
```

#### 注意事项

- ✅ 接口继承 `IService<Entity>`
- ✅ 实现类继承 `ServiceImpl<Mapper, Entity>`
- ✅ 使用 `@Service` 注解
- ✅ 使用 `this.xxx()` 调用 MyBatis-Plus 方法
- ✅ 使用 `baseMapper.xxx()` 调用自定义 Mapper 方法
- ✅ 参数校验要全面
- ✅ 异常要有明确的提示信息
- ❌ 不要返回 null，抛出异常
- ❌ 不要在 Service 中处理 HTTP 相关逻辑

---

### Mapper 层

#### 职责

- 数据库操作
- 自定义 SQL

#### 规范

```java
public interface UserMapper extends BaseMapper<UserEntity> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Select("select * from user where username = #{username}")
    UserEntity selectByUsername(@Param("username") String username);
}
```

#### 注意事项

- ✅ 继承 `BaseMapper<Entity>`
- ✅ 使用 `@Select`, `@Insert`, `@Update`, `@Delete` 注解
- ✅ 参数使用 `@Param` 注解
- ✅ 复杂 SQL 建议使用 XML 映射文件
- ❌ 不要在 Mapper 中写业务逻辑

---

### Entity 层

#### 职责

- 数据库表映射
- 字段定义

#### 规范

```java
@Data
@TableName("user")
public class UserEntity {

    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 不存在于数据库的字段 */
    @TableField(exist = false)
    private List<OrderEntity> orders;
}
```

#### 注意事项

- ✅ 使用 `@Data` 注解
- ✅ 使用 `@TableName` 指定表名
- ✅ 使用 `@TableId` 指定主键策略
- ✅ 使用 `@TableField` 处理字段映射
- ✅ 实体类命名使用 `Entity` 后缀
- ✅ 字段添加注释
- ❌ 不要在实体类中添加业务方法

---

## 🔐 异常处理规范

### 1. 业务异常

```java
// Service 层抛出异常
if (user == null) {
    throw new RuntimeException("用户不存在");
}

// 全局异常处理器捕获
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        return Result.error().message(e.getMessage());
    }
}
```

### 2. 异常信息规范

- ✅ 清晰明确：`"用户名不能为空"`
- ✅ 用户友好：`"登录失败，用户名或密码错误"`
- ❌ 不暴露敏感信息：~~`"SQL 语法错误"`~~
- ❌ 不使用英文：~~`"User not found"`~~

---

## 📊 数据库规范

### 1. 表设计

- 每个表必须有主键 `id`
- 主键使用自增 `AUTO_INCREMENT`
- 使用 `InnoDB` 引擎
- 字符集使用 `utf8mb4`
- 添加表注释和字段注释

```sql
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. 字段规范

- 主键：`id` int AUTO_INCREMENT
- 外键：`xxx_id` int
- 时间：`xxx_time` datetime
- 金额：`xxx_amount` decimal(10,2)
- 状态：`xxx_status` tinyint
- 布尔：`is_xxx` tinyint(1)

### 3. 索引规范

- 主键索引：PRIMARY KEY
- 唯一索引：UNIQUE KEY `uk_xxx`
- 普通索引：KEY `idx_xxx`
- 复合索引：KEY `idx_xxx_yyy`

---

## 🎯 RESTful API 规范

### 1. URL 规范

- 使用名词复数：`/users`, `/orders`
- 使用小写字母和短横线：`/user-info`
- 不要使用动词：❌ `/getUser`, ✅ `/users/{id}`

### 2. HTTP 方法

- `GET`：查询资源
- `POST`：创建资源
- `PUT`：更新资源（全量）
- `PATCH`：更新资源（部分）
- `DELETE`：删除资源

### 3. 响应码

- `200 OK`：成功
- `201 Created`：创建成功
- `400 Bad Request`：请求参数错误
- `401 Unauthorized`：未认证
- `403 Forbidden`：无权限
- `404 Not Found`：资源不存在
- `500 Internal Server Error`：服务器错误

### 4. 响应格式

```json
{
  "success": true,
  "message": "操作成功",
  "code": 200,
  "data": {}
}
```

---

## 🛡️ 安全规范

### 1. 密码处理

```java
// ❌ 错误：明文存储
user.setPassword("123456");

// ✅ 正确：加密存储
String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
user.setPassword(encryptedPassword);

// ✅ 验证密码
boolean isValid = BCrypt.checkpw(inputPassword, user.getPassword());
```

### 2. SQL 注入防护

```java
// ✅ 正确：使用参数绑定
@Select("select * from user where username = #{username}")
UserEntity selectByUsername(@Param("username") String username);

// ❌ 错误：字符串拼接
String sql = "select * from user where username = '" + username + "'";
```

### 3. XSS 防护

- 前端输入验证
- 后端数据转义
- 使用安全的模板引擎

### 4. Token 安全

- 使用 HTTPS 传输
- 设置合理的过期时间
- 不要在 URL 中传递 Token
- 定期更换密钥

---

## 🧪 测试规范

### 1. 单元测试

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testLogin() {
        String token = userService.login("admin", "123456");
        assertNotNull(token);
    }
}
```

### 2. 集成测试

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoginApi() throws Exception {
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());
    }
}
```

---

## 📝 Git 规范

### 1. 分支规范

- `master`：主分支，生产环境
- `develop`：开发分支
- `feature/xxx`：功能分支
- `bugfix/xxx`：bug 修复分支
- `hotfix/xxx`：紧急修复分支

### 2. Commit 规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型：**

- `feat`：新功能
- `fix`：修复 bug
- `docs`：文档更新
- `style`：代码格式调整
- `refactor`：重构
- `test`：测试相关
- `chore`：构建/工具相关

**示例：**

```
feat(user): 添加用户登录功能

- 实现用户登录接口
- 添加 Token 验证
- 完善异常处理

Closes #123
```

---

## 📦 版本规范

### 语义化版本

- `主版本号.次版本号.修订号`
- 例如：`1.2.3`

**规则：**

- 主版本号：不兼容的 API 修改
- 次版本号：向下兼容的功能新增
- 修订号：向下兼容的问题修正

---

## ✅ Code Review 检查清单

### 代码质量

- [ ] 是否符合命名规范
- [ ] 是否有充分的注释
- [ ] 是否有重复代码
- [ ] 是否有魔法数字
- [ ] 异常处理是否完善

### 功能实现

- [ ] 是否实现了需求
- [ ] 是否考虑了边界条件
- [ ] 是否处理了异常情况
- [ ] 是否有安全隐患

### 性能优化

- [ ] 是否有 N+1 查询问题
- [ ] 是否使用了合适的索引
- [ ] 是否有内存泄漏
- [ ] 是否有不必要的循环

---

遵循这些规范可以提高代码质量，增强团队协作效率！
