# Spring Boot 后端 CRUD 模板

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg)](https://baomidou.com/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个开箱即用的 Spring Boot + MyBatis-Plus 后端 CRUD 模板，集成了 JWT 认证、Swagger 文档、全局异常处理等常用功能。

## ✨ 特性

- 🚀 **开箱即用** - 克隆即用，快速启动项目
- 🏗️ **分层架构** - Controller → Service → Mapper 清晰分层
- 🔐 **JWT 认证** - 基于 Token 的身份认证机制
- 📁 **文件上传** - 支持阿里云 OSS 存储，可降级到本地
- 📝 **API 文档** - 集成 Swagger/Knife4j，分组自动生成文档
- 🛡️ **全局异常处理** - 统一的错误响应格式（HTTP 标准状态码）
- 💾 **MyBatis-Plus** - 强大的 ORM 框架，简化 CRUD 操作
- 📊 **分页查询** - 内置分页插件
- 🔧 **代码规范** - 完整的开发规范和注释

## 🏗️ 技术栈

| 技术         | 版本   | 说明             |
| ------------ | ------ | ---------------- |
| Spring Boot  | 3.5.6  | 基础框架         |
| MyBatis-Plus | 3.5.7  | ORM 框架         |
| MySQL        | 8.0+   | 数据库           |
| Druid        | 1.1.20 | 数据库连接池     |
| JWT          | 0.12.6 | Token 认证       |
| 阿里云 OSS   | 3.17.4 | 对象存储         |
| Knife4j      | 4.5.0  | API 文档（分组） |
| Lombok       | latest | 简化代码         |

## 📁 项目结构

```
helloworld/
├── src/main/java/org/example/helloworld/
│   ├── config/              # 配置类
│   │   ├── MybatisPlusConfig.java    # MyBatis-Plus 配置
│   │   ├── OpenApiConfig.java        # API 文档配置（分组）
│   │   └── WebConfig.java            # Web 配置
│   ├── controller/          # 控制器层
│   │   ├── UserController.java       # 用户管理
│   │   ├── OrderController.java      # 订单管理
│   │   └── FileController.java       # 文件上传
│   ├── entity/              # 实体类
│   ├── exception/           # 异常处理
│   ├── interceptor/         # 拦截器
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   │   └── impl/            # 服务实现
│   └── utils/               # 工具类
├── docs/                    # 项目文档
│   ├── PROJECT_STRUCTURE.md # 项目结构说明
│   ├── QUICK_START.md       # 快速开始
│   ├── DEVELOPMENT_GUIDE.md # 开发规范
│   └── API_DOCUMENTATION.md # API 文档
└── README.md                # 项目说明
```

## 🚀 快速开始

### 1. 环境准备

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- IDE (推荐 IntelliJ IDEA)

### 2. 克隆项目

```bash
git clone <your-repo-url>
cd helloworld
```

### 3. 创建数据库

```sql
CREATE DATABASE mybatisdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行 `docs/` 目录下的 SQL 脚本创建表结构。

### 4. 配置数据库

修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mybatisdb
spring.datasource.username=root
spring.datasource.password=your_password

# 阿里云 OSS（可选）
aliyun.oss.endpoint=oss-cn-hangzhou.aliyuncs.com
aliyun.oss.accessKeyId=your_access_key_id
aliyun.oss.accessKeySecret=your_access_key_secret
aliyun.oss.bucketName=your_bucket_name
aliyun.oss.urlPrefix=https://your-cdn-domain.com
```

### 5. 启动项目

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 6. 访问文档

- API 文档：http://localhost:8080/doc.html （支持分组：用户管理、订单管理、文件管理）
- 应用地址：http://localhost:8080

## 📚 文档

- [项目结构说明](docs/PROJECT_STRUCTURE.md) - 详细的项目结构和架构说明
- [快速开始指南](docs/QUICK_START.md) - 手把手教你启动项目
- [开发规范](docs/DEVELOPMENT_GUIDE.md) - 代码规范和最佳实践
- [API 文档](docs/API_DOCUMENTATION.md) - 完整的 API 接口文档
- [认证指南](AUTHENTICATION_GUIDE.md) - JWT 认证机制说明
- [ServiceImpl 指南](SERVICE_IMPL_GUIDE.md) - MyBatis-Plus Service 层使用

## 🔑 核心功能

### 1. JWT 认证

```java
// 登录获取 Token
POST /user/login
{
  "username": "admin",
  "password": "123456"
}

// 使用 Token 访问接口
GET /user/info
Headers: Authorization: Bearer <token>
```

### 2. CRUD 操作

```java
// Service 层继承 ServiceImpl，获得丰富的 CRUD 方法
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
                              implements UserService {
    // 使用内置方法
    public UserEntity getUser(Integer id) {
        return this.getById(id);  // MyBatis-Plus 提供
    }
}
```

### 3. 分页查询

```java
// 自动分页
Page<UserEntity> page = new Page<>(1, 10);
IPage<UserEntity> result = userService.page(page);
```

### 4. 统一响应

```java
// 成功响应
return Result.ok().data("user", user);

// 错误响应
return Result.error().message("用户不存在");
```

## 🛡️ 安全特性

- ✅ JWT Token 认证
- ✅ 拦截器权限控制
- ✅ 白名单机制
- ✅ 全局异常处理
- ✅ SQL 注入防护
- ✅ 统一响应格式

## 🔧 配置说明

### 白名单配置

在 `WebConfig.java` 中配置不需要认证的接口：

```java
.excludePathPatterns(
    "/user/login",    // 登录接口
    "/user/register", // 注册接口
    "/doc.html"       // API 文档
)
```

### 数据库配置

在 `application.properties` 中配置数据库连接：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/mybatisdb
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis-Plus 配置
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

## 📝 API 示例

### 用户登录

```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 获取用户信息

```bash
curl -X GET http://localhost:8080/user/info \
  -H "Authorization: Bearer <your_token>"
```

### 分页查询

```bash
curl -X GET "http://localhost:8080/user/page?page=1&pageSize=10" \
  -H "Authorization: Bearer <your_token>"
```

## 🎯 如何使用这个模板

### 1. 新增实体类

1. 在 `entity` 包下创建 `XxxEntity.java`
2. 添加 `@Data`、`@TableName` 注解
3. 定义字段和主键策略

### 2. 新增 Service

1. 在 `service` 包下创建 `XxxService.java` 接口
2. 继承 `IService<XxxEntity>`
3. 在 `service/impl` 下创建实现类
4. 继承 `ServiceImpl<XxxMapper, XxxEntity>`

### 3. 新增 Controller

1. 在 `controller` 包下创建 `XxxController.java`
2. 添加 `@RestController` 和 `@RequestMapping`
3. 注入 Service，编写接口方法

详细步骤请查看 [项目结构说明](docs/PROJECT_STRUCTURE.md)

## 🧪 测试

```bash
# 运行所有测试
mvnw.cmd test

# 运行单个测试
mvnw.cmd test -Dtest=UserServiceTest
```

## 📦 打包部署

```bash
# 打包
mvnw.cmd clean package -DskipTests

# 运行 JAR
java -jar target/helloworld-1.0.0.jar

# 指定配置文件
java -jar target/helloworld-1.0.0.jar --spring.config.location=config/application.properties
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 License

[MIT License](LICENSE)

## 👥 作者

- Your Name
- Email: your.email@example.com
- GitHub: [@yourusername](https://github.com/yourusername)

## 🙏 鸣谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Knife4j](https://doc.xiaominfo.com/)
- [JWT](https://jwt.io/)

---

⭐ 如果这个项目对你有帮助，请给一个 Star！
