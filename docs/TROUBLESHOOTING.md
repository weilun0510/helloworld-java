# 常见问题排查指南

## 🔍 配置问题排查

### 问题 1：配置冲突导致启动失败

**症状**: 应用启动时报错，提示配置相关问题

**原因**: 主配置文件和环境配置文件中有冲突的配置项

**解决方案**:
1. 确保主配置文件 `application.properties` 只包含公共配置
2. 环境特定的配置（如数据库连接、日志级别等）应放在对应的环境配置文件中
3. 避免在多个配置文件中重复配置同一个属性

### 问题 2：数据库连接失败

**症状**: 
```
Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
```

**可能原因**:
1. 数据库服务未启动
2. 数据库地址或端口错误
3. 数据库用户名或密码错误
4. 防火墙阻止连接
5. 数据库不存在

**排查步骤**:

1. **检查当前使用的环境**:
```properties
# application.properties
spring.profiles.active=test  # 查看激活的是哪个环境
```

2. **检查对应环境的数据库配置**:
```bash
# 开发环境
cat src/main/resources/application-dev.properties

# 测试环境
cat src/main/resources/application-test.properties

# 生产环境
cat src/main/resources/application-prod.properties
```

3. **测试数据库连接**:
```bash
# Windows
mysql -h 8.130.142.163 -P 3306 -u mybatis -p

# 输入密码后测试
USE mybatis;
SHOW TABLES;
```

4. **查看启动日志**:
```
启动应用时查看日志中的数据库连接信息：
- HikariPool-1 - Starting...
- HikariPool-1 - Start completed.
```

### 问题 3：端口被占用

**症状**: 
```
Port 8080 was already in use.
```

**解决方案**:

**Windows**:
```bash
# 查找占用 8080 端口的进程
netstat -ano | findstr :8080

# 杀死进程（替换 PID 为实际的进程 ID）
taskkill /PID <PID> /F
```

**Linux/Mac**:
```bash
# 查找占用 8080 端口的进程
lsof -i :8080

# 杀死进程
kill -9 <PID>
```

或者修改配置文件中的端口：
```properties
# application.properties
server.port=8081
```

### 问题 4：热部署不生效

**症状**: 修改代码后应用没有自动重启

**原因**: 
1. 当前环境禁用了热部署
2. IDEA 没有开启自动编译

**解决方案**:

1. **检查环境配置**:
```properties
# application-dev.properties (开发环境应该启用)
spring.devtools.restart.enabled=true

# application-test.properties (测试环境通常关闭)
spring.devtools.restart.enabled=false

# application-prod.properties (生产环境必须关闭)
spring.devtools.restart.enabled=false
```

2. **IDEA 设置**:
- File → Settings → Build, Execution, Deployment → Compiler
- ✅ 勾选 "Build project automatically"
- File → Settings → Advanced Settings
- ✅ 勾选 "Allow auto-make to start even if developed application is currently running"

### 问题 5：Swagger/Knife4j 无法访问

**症状**: 
- 访问 http://localhost:8080/doc.html 返回 404
- 访问 http://localhost:8080/swagger-ui.html 返回 404

**原因**: 当前环境禁用了 API 文档

**解决方案**:

1. **检查当前环境**:
```properties
# 开发环境和测试环境应该启用
knife4j.enable=true
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true

# 生产环境应该禁用（安全考虑）
knife4j.enable=false
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

2. **检查拦截器白名单**:
确保 `WebConfig.java` 中包含文档路径：
```java
.excludePathPatterns(
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/doc.html",
    // ...
)
```

### 问题 6：跨域请求失败

**症状**: 
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**解决方案**:

1. **检查跨域配置**:
确保 `WebConfig.java` 中有跨域配置：
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

2. **重启应用**: 配置修改后需要重启应用

### 问题 7：JWT Token 验证失败

**症状**: 
```
401 Unauthorized
Token 验证失败
```

**排查步骤**:

1. **检查 Token 是否存在**:
```
请求头中应包含: Authorization: Bearer <token>
```

2. **检查 Token 是否过期**:
```properties
# application.properties
jwt.expiration=86400000  # 24小时，单位：毫秒
```

3. **检查 JWT 密钥**:
```properties
# application.properties
jwt.secret=MySecretKeyForJWT2024SpringBootMyBatisPlusTemplate
```

4. **查看拦截器日志**:
```
Token 验证失败: xxx
```

### 问题 8：MyBatis-Plus SQL 日志不显示

**症状**: 控制台看不到 SQL 语句

**原因**: 当前环境关闭了 SQL 日志

**解决方案**:

检查对应环境的配置：
```properties
# 开发环境和测试环境（应该启用）
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# 生产环境（应该关闭，或注释掉）
# mybatis-plus.configuration.log-impl=
```

## 🛠️ 常用排查命令

### 查看应用日志
```bash
# 实时查看日志（如果有日志文件）
tail -f logs/application.log

# Maven 运行时的日志直接在控制台输出
```

### 检查 Maven 依赖
```bash
# 查看依赖树
mvn dependency:tree

# 更新依赖
mvn clean install -U
```

### 清理并重新编译
```bash
# 清理并打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 清理 IDEA 缓存
File → Invalidate Caches → Invalidate and Restart
```

### 检查配置是否生效
在启动日志中查找：
```
The following 1 profile is active: "test"
```

### 查看应用进程
```bash
# Windows
jps -l | findstr helloworld

# Linux/Mac
jps -l | grep helloworld
ps aux | grep java
```

## 📋 启动问题检查清单

当应用启动失败时，按以下步骤排查：

- [ ] 1. 检查 Java 版本是否正确（JDK 17+）
- [ ] 2. 检查 `spring.profiles.active` 设置的环境
- [ ] 3. 检查对应环境配置文件是否存在
- [ ] 4. 检查数据库连接信息是否正确
- [ ] 5. 检查数据库是否可连接
- [ ] 6. 检查端口是否被占用
- [ ] 7. 检查 Maven 依赖是否正确下载
- [ ] 8. 清理并重新编译项目
- [ ] 9. 查看完整的错误堆栈信息
- [ ] 10. 检查是否有配置冲突

## 🔐 生产环境问题排查

### 日志级别调整
如果生产环境出现问题，临时调整日志级别：

```properties
# application-prod.properties
logging.level.root=INFO
logging.level.org.example.helloworld=DEBUG
```

记得问题解决后改回 WARN 级别。

### 数据库连接池监控
```properties
# 启用 Druid 监控（仅排查时使用）
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
```

访问: http://localhost:8080/druid/

## 📞 获取帮助

1. **查看启动日志**: 最重要的排查信息来源
2. **复制完整错误信息**: 包括堆栈跟踪
3. **确认环境信息**: 
   - 当前激活的 Profile
   - Java 版本
   - Maven 版本
   - 数据库版本
4. **提供配置文件**: 相关的配置内容（脱敏后）

## 🎯 快速诊断脚本

创建一个诊断脚本 `diagnose.sh`:

```bash
#!/bin/bash
echo "=== 环境诊断 ==="
echo "Java 版本:"
java -version

echo -e "\n当前激活的 Profile:"
grep "spring.profiles.active" src/main/resources/application.properties

echo -e "\n端口占用情况:"
netstat -ano | findstr :8080

echo -e "\n数据库连接测试:"
# 根据实际情况调整
mysql -h localhost -u root -p -e "SELECT 1"

echo -e "\n Maven 依赖检查:"
mvn dependency:tree | head -20
```

## 📚 相关文档

- [Spring Boot 配置文档](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
- [MyBatis-Plus 配置文档](https://baomidou.com/pages/56bac0/)
- [项目环境配置指南](./ENVIRONMENT_CONFIG_GUIDE.md)
- [开发指南](./DEVELOPMENT_GUIDE.md)

