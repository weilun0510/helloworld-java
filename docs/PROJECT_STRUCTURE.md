# 项目结构说明

## 📁 目录结构

```
helloworld/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/helloworld/
│   │   │       ├── config/              # 配置类
│   │   │       │   ├── MybatisPlusConfig.java    # MyBatis-Plus 配置
│   │   │       │   ├── OpenApiConfig.java        # Swagger/Knife4j 配置
│   │   │       │   └── WebConfig.java            # Web 配置（拦截器等）
│   │   │       │
│   │   │       ├── controller/          # 控制器层
│   │   │       │   ├── UserController.java       # 用户控制器
│   │   │       │   └── OrderController.java      # 订单控制器
│   │   │       │
│   │   │       ├── entity/              # 实体类
│   │   │       │   ├── UserEntity.java           # 用户实体
│   │   │       │   └── OrderEntity.java          # 订单实体
│   │   │       │
│   │   │       ├── exception/           # 异常处理
│   │   │       │   └── GlobalExceptionHandler.java  # 全局异常处理器
│   │   │       │
│   │   │       ├── interceptor/         # 拦截器
│   │   │       │   └── LoginInterceptor.java     # 登录认证拦截器
│   │   │       │
│   │   │       ├── mapper/              # Mapper 接口
│   │   │       │   ├── UserMapper.java           # 用户 Mapper
│   │   │       │   └── OrderMapper.java          # 订单 Mapper
│   │   │       │
│   │   │       ├── service/             # 服务层接口
│   │   │       │   ├── UserService.java          # 用户服务接口
│   │   │       │   ├── OrderService.java         # 订单服务接口
│   │   │       │   └── impl/                     # 服务实现类
│   │   │       │       ├── UserServiceImpl.java  # 用户服务实现
│   │   │       │       └── OrderServiceImpl.java # 订单服务实现
│   │   │       │
│   │   │       ├── utils/               # 工具类
│   │   │       │   ├── JwtUtil.java             # JWT 工具类
│   │   │       │   ├── Result.java              # 统一响应类
│   │   │       │   └── ResultCode.java          # 响应状态码
│   │   │       │
│   │   │       └── HelloworldApplication.java   # 启动类
│   │   │
│   │   └── resources/
│   │       ├── application.properties    # 应用配置
│   │       └── static/                   # 静态资源
│   │
│   └── test/                             # 测试代码
│
├── docs/                                 # 项目文档
│   ├── PROJECT_STRUCTURE.md             # 项目结构说明
│   ├── QUICK_START.md                   # 快速开始指南
│   ├── DEVELOPMENT_GUIDE.md             # 开发规范
│   └── API_DOCUMENTATION.md             # API 文档
│
├── pom.xml                               # Maven 配置文件
└── README.md                             # 项目说明
```

## 🏗️ 分层架构

### Controller 层（控制器层）
**职责：** 处理 HTTP 请求，参数校验，调用 Service 层，返回响应

**命名规范：** `XxxController.java`

**示例：**
```java
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result login(@RequestBody UserEntity user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        return Result.ok().data("token", token);
    }
}
```

**关键点：**
- 使用 `@RestController` 注解
- 使用 `@RequestMapping` 定义基础路径
- 只调用 Service 层，不直接操作 Mapper
- 返回统一的 `Result` 对象

---

### Service 层（服务层）
**职责：** 业务逻辑处理，事务管理

**命名规范：** 
- 接口：`XxxService.java`
- 实现：`XxxServiceImpl.java`

**示例：**
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
        // 业务逻辑
        UserEntity user = this.getOne(queryWrapper);
        return JwtUtil.generateToken(username);
    }
}
```

**关键点：**
- 接口继承 `IService<Entity>`
- 实现类继承 `ServiceImpl<Mapper, Entity>`
- 使用 `@Service` 注解
- 包含业务逻辑和数据校验

---

### Mapper 层（数据访问层）
**职责：** 数据库操作

**命名规范：** `XxxMapper.java`

**示例：**
```java
public interface UserMapper extends BaseMapper<UserEntity> {
    // 自定义SQL方法
    @Select("select * from user where username = #{username}")
    UserEntity selectByUsername(@Param("username") String username);
}
```

**关键点：**
- 继承 `BaseMapper<Entity>`
- 可以添加自定义 SQL 方法
- 无需实现类（MyBatis-Plus 自动生成）

---

### Entity 层（实体层）
**职责：** 数据库表映射

**命名规范：** `XxxEntity.java`

**示例：**
```java
@Data
@TableName("user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
}
```

**关键点：**
- 使用 `@Data` 注解（Lombok）
- 使用 `@TableName` 指定表名
- 使用 `@TableId` 指定主键策略
- 使用 `@TableField` 处理字段映射

---

## 🔧 配置类说明

### MybatisPlusConfig.java
**功能：** 配置 MyBatis-Plus 分页插件等

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### OpenApiConfig.java
**功能：** 配置 Swagger/Knife4j API 文档

### WebConfig.java
**功能：** 配置拦截器、跨域等

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register");
    }
}
```

---

## 🛡️ 拦截器说明

### LoginInterceptor.java
**功能：** JWT Token 认证

**工作流程：**
1. 从请求头获取 Token
2. 验证 Token 有效性
3. 提取用户信息存储到 request
4. 放行或返回 401

**配置白名单：**
在 `WebConfig.java` 中使用 `.excludePathPatterns()` 添加不需要认证的接口

---

## 🔐 异常处理

### GlobalExceptionHandler.java
**功能：** 全局异常捕获，统一错误响应

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        return Result.error().message(e.getMessage());
    }
}
```

**捕获的异常：**
- `RuntimeException` - 业务异常
- `Exception` - 系统异常

---

## 🛠️ 工具类说明

### JwtUtil.java
**功能：** JWT Token 生成和验证

**主要方法：**
- `generateToken(username)` - 生成 Token
- `validateToken(token)` - 验证 Token
- `getUsernameFromToken(token)` - 提取用户名

### Result.java
**功能：** 统一响应格式

**响应结构：**
```json
{
  "success": true,
  "message": "成功",
  "code": 200,
  "data": {}
}
```

**使用方式：**
```java
// 成功响应
return Result.ok().data("user", user);

// 失败响应
return Result.error().message("用户不存在");
```

### ResultCode.java
**功能：** 定义响应状态码

---

## 📊 数据流转流程

```
HTTP 请求
    ↓
LoginInterceptor（Token 验证）
    ↓
Controller（参数接收）
    ↓
Service（业务处理）
    ↓
Mapper（数据库操作）
    ↓
返回数据
    ↓
Controller（封装 Result）
    ↓
GlobalExceptionHandler（异常处理）
    ↓
HTTP 响应
```

---

## 🎯 开发建议

### 1. 新增实体类
1. 创建 `XxxEntity.java` 在 `entity` 包下
2. 添加 `@Data`、`@TableName` 注解
3. 定义字段和主键策略

### 2. 新增 Mapper
1. 创建 `XxxMapper.java` 在 `mapper` 包下
2. 继承 `BaseMapper<XxxEntity>`
3. 添加自定义 SQL 方法（可选）

### 3. 新增 Service
1. 创建 `XxxService.java` 接口在 `service` 包下
2. 创建 `XxxServiceImpl.java` 在 `service/impl` 包下
3. 接口继承 `IService<XxxEntity>`
4. 实现类继承 `ServiceImpl<XxxMapper, XxxEntity>`

### 4. 新增 Controller
1. 创建 `XxxController.java` 在 `controller` 包下
2. 添加 `@RestController` 和 `@RequestMapping`
3. 注入对应的 Service
4. 编写接口方法

### 5. 配置白名单
在 `WebConfig.java` 中添加不需要认证的接口路径

---

## 📝 注意事项

1. **Controller 层** 不要直接注入 Mapper，应该注入 Service
2. **Service 实现类** 必须继承 `ServiceImpl`
3. **Entity 类** 建议使用 `Entity` 后缀
4. **所有接口** 都返回统一的 `Result` 对象
5. **异常处理** 使用 `throw new RuntimeException("错误信息")
`
6. **Token 认证** 在请求头中使用 `Authorization: Bearer <token>`

---

## 🚀 扩展功能

### 添加跨域配置
在 `WebConfig.java` 中：
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*");
}
```

### 添加日志
使用 Lombok 的 `@Slf4j` 注解：
```java
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> {
    public void someMethod() {
        log.info("日志信息");
    }
}
```

### 添加缓存
使用 Spring Cache：
```java
@Cacheable(value = "users", key = "#id")
public UserEntity getById(Integer id) {
    return super.getById(id);
}
```

---

这个项目结构清晰、分层明确，适合作为 Spring Boot + MyBatis-Plus 的标准模板！

