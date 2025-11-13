# Spring Boot 多环境配置指南

## 📁 配置文件说明

项目现在使用 Spring Boot 的 Profile 功能实现多环境配置管理，配置文件结构如下：

```
src/main/resources/
├── application.properties           # 主配置文件（公共配置）
├── application-dev.properties       # 开发环境配置
├── application-test.properties      # 测试环境配置
└── application-prod.properties      # 生产环境配置
```

## 🎯 环境配置说明

### 1. 开发环境 (dev)
- **配置文件**: `application-dev.properties`
- **数据库**: 本地 MySQL (localhost:3306/mybatisdb)
- **特点**:
  - ✅ 启用 SQL 日志输出
  - ✅ 启用热部署
  - ✅ 启用 Swagger/Knife4j 文档
  - ✅ DEBUG 级别日志

### 2. 测试环境 (test)
- **配置文件**: `application-test.properties`
- **数据库**: 独立测试数据库 (localhost:3306/mybatisdb_test)
- **特点**:
  - ✅ 启用 SQL 日志输出
  - ✅ 启用 Swagger/Knife4j 文档
  - ✅ INFO 级别日志
  - ❌ 关闭热部署

### 3. 生产环境 (prod)
- **配置文件**: `application-prod.properties`
- **数据库**: 生产 MySQL (8.130.142.163:3306/mybatis)
- **特点**:
  - ❌ 关闭 SQL 日志输出（提高性能）
  - ❌ 关闭热部署
  - ❌ 关闭 Swagger/Knife4j 文档（安全考虑）
  - ⚠️ WARN 级别日志（减少日志输出）

## 🚀 如何切换环境

### 方式一：修改 application.properties（推荐开发时使用）

在 `application.properties` 文件中修改：

```properties
# 开发环境
spring.profiles.active=dev

# 测试环境
spring.profiles.active=test

# 生产环境
spring.profiles.active=prod
```

### 方式二：启动参数（推荐生产部署使用）

**IDEA 运行配置**:
```
Program arguments: --spring.profiles.active=dev
```

**命令行运行**:
```bash
# 开发环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 测试环境
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 生产环境
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**JAR 包运行**:
```bash
# 开发环境
java -jar helloworld-1.0.0.jar --spring.profiles.active=dev

# 测试环境
java -jar helloworld-1.0.0.jar --spring.profiles.active=test

# 生产环境
java -jar helloworld-1.0.0.jar --spring.profiles.active=prod
```

### 方式三：环境变量

```bash
# Windows
set SPRING_PROFILES_ACTIVE=prod
java -jar helloworld-1.0.0.jar

# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
java -jar helloworld-1.0.0.jar
```

### 方式四：JVM 参数

```bash
java -Dspring.profiles.active=prod -jar helloworld-1.0.0.jar
```

## 📋 配置优先级

Spring Boot 配置加载优先级（从高到低）：

1. 命令行参数 `--spring.profiles.active=prod`
2. JVM 系统属性 `-Dspring.profiles.active=prod`
3. 操作系统环境变量 `SPRING_PROFILES_ACTIVE=prod`
4. `application.properties` 中的 `spring.profiles.active`

## 🔒 生产环境安全建议

### 1. 使用环境变量管理敏感信息

修改 `application-prod.properties`：

```properties
# 使用环境变量
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

然后设置环境变量：
```bash
export DB_URL=jdbc:mysql://8.130.142.163:3306/mybatis
export DB_USERNAME=mybatis
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_super_secure_jwt_secret_key
```

### 2. 使用配置中心

推荐使用：
- **Nacos**: 阿里巴巴开源配置中心
- **Apollo**: 携程开源配置中心
- **Spring Cloud Config**: Spring 官方配置中心

### 3. 加密敏感配置

使用 Jasypt 加密配置：

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

```properties
# 加密后的密码
spring.datasource.password=ENC(encrypted_password_here)
```

## 🎨 IDEA 配置多环境运行

1. 打开 **Run/Debug Configurations**
2. 点击 **+** 添加新的 **Spring Boot** 配置
3. 设置不同的环境：

**开发环境配置**:
- Name: `Application (dev)`
- Program arguments: `--spring.profiles.active=dev`

**测试环境配置**:
- Name: `Application (test)`
- Program arguments: `--spring.profiles.active=test`

**生产环境配置**:
- Name: `Application (prod)`
- Program arguments: `--spring.profiles.active=prod`

## 📊 验证当前环境

在应用启动日志中查看：

```
The following 1 profile is active: "dev"
```

或者添加一个接口查看：

```java
@RestController
public class EnvController {
    
    @Value("${spring.profiles.active}")
    private String activeProfile;
    
    @GetMapping("/env")
    public String getEnvironment() {
        return "当前环境: " + activeProfile;
    }
}
```

## 🌐 Docker 部署

**Dockerfile**:
```dockerfile
FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**docker-compose.yml**:
```yaml
version: '3.8'
services:
  app-dev:
    build: .
    environment:
      - SPRING_PROFILES_ACTIVE=dev
    ports:
      - "8080:8080"
  
  app-prod:
    build: .
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:mysql://db:3306/mybatis
      - DB_USERNAME=mybatis
      - DB_PASSWORD=secure_password
    ports:
      - "8080:8080"
```

## 📝 配置文件内容对比

| 配置项 | 开发环境 (dev) | 测试环境 (test) | 生产环境 (prod) |
|-------|---------------|----------------|----------------|
| 数据库 | localhost | localhost_test | 远程服务器 |
| SQL日志 | ✅ 启用 | ✅ 启用 | ❌ 关闭 |
| 热部署 | ✅ 启用 | ❌ 关闭 | ❌ 关闭 |
| API文档 | ✅ 启用 | ✅ 启用 | ❌ 关闭 |
| 日志级别 | DEBUG | INFO | WARN |

## 🔧 常见问题

### Q: 如何知道当前使用的是哪个环境？
A: 查看启动日志中的 `The following 1 profile is active: "xxx"`

### Q: 可以同时激活多个环境吗？
A: 可以，使用逗号分隔：`spring.profiles.active=dev,debug`

### Q: 如何在代码中判断当前环境？
A: 使用 `@Value("${spring.profiles.active}")` 或 `Environment` 对象

```java
@Autowired
private Environment environment;

public void checkEnvironment() {
    String[] profiles = environment.getActiveProfiles();
    if (Arrays.asList(profiles).contains("prod")) {
        // 生产环境逻辑
    }
}
```

### Q: 配置文件中的密码会被提交到 Git 吗？
A: 建议生产环境密码使用环境变量，不要直接写在配置文件中。开发环境可以提交。

## 📚 相关文档

- [Spring Boot Profiles 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [外部化配置](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

## ⚡ 快速命令参考

```bash
# 开发环境运行
mvn spring-boot:run

# 生产环境运行
java -jar target/helloworld-1.0.0.jar --spring.profiles.active=prod

# 查看配置
mvn spring-boot:run -Ddebug

# 打包（跳过测试）
mvn clean package -DskipTests
```

---

**当前默认环境**: `dev` (开发环境)

如需切换到生产环境，修改 `application.properties` 中的 `spring.profiles.active=prod` 或使用启动参数。

