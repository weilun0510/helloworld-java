# 快速开始指南

## 📋 环境要求

- **JDK**: 21+
- **Maven**: 3.6+
- **MySQL**: 5.7+ 或 8.0+
- **IDE**: IntelliJ IDEA / Eclipse / VS Code

## 🚀 快速启动

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd helloworld
```

### 2. 创建数据库

```sql
CREATE DATABASE mybatisdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mybatisdb;

-- 创建用户表
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建订单表
CREATE TABLE `order` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_time` datetime DEFAULT NULL COMMENT '订单时间',
  `total` int DEFAULT NULL COMMENT '订单总额',
  `uid` int DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试数据
INSERT INTO `user` (`username`, `password`) VALUES ('admin', '123456');
INSERT INTO `user` (`username`, `password`) VALUES ('test', 'test123');

INSERT INTO `order` (`order_time`, `total`, `uid`) VALUES (NOW(), 100, 1);
INSERT INTO `order` (`order_time`, `total`, `uid`) VALUES (NOW(), 200, 1);
INSERT INTO `order` (`order_time`, `total`, `uid`) VALUES (NOW(), 150, 2);
```

### 3. 配置数据库连接

编辑 `src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/mybatisdb?useSSL=false
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 4. 编译项目

```bash
# Windows
mvnw.cmd clean package -DskipTests

# Linux/Mac
./mvnw clean package -DskipTests
```

### 5. 运行项目

```bash
# 方式1：使用 Maven
mvnw.cmd spring-boot:run

# 方式2：运行 JAR
java -jar target/helloworld-0.0.1-SNAPSHOT.jar

# 方式3：IDE 中直接运行 HelloworldApplication.java
```

### 6. 验证启动

访问 `http://localhost:8080`，如果看到以下信息说明启动成功：

```
LoginInterceptor 已注册，白名单接口不需要 Token 认证
```

访问 API 文档：`http://localhost:8080/doc.html`

---

## 🧪 API 测试

### 1. 用户注册

```bash
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "123456"
  }'
```

**响应：**
```json
{
  "success": true,
  "message": "注册成功",
  "code": 200,
  "data": {}
}
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**响应：**
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

### 3. 获取用户信息（需要 Token）

```bash
curl -X GET http://localhost:8080/user/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**响应：**
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

### 4. 查询用户列表（需要 Token）

```bash
curl -X GET http://localhost:8080/user/page?page=1&pageSize=10 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. 查询订单列表（需要 Token）

```bash
curl -X GET http://localhost:8080/order/findAll \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🔧 Postman 测试

### 1. 导入环境变量

创建环境变量 `BASE_URL` = `http://localhost:8080`

创建环境变量 `TOKEN` = `<留空>`

### 2. 测试登录

**请求：**
- Method: POST
- URL: `{{BASE_URL}}/user/login`
- Headers: `Content-Type: application/json`
- Body:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**设置 Token：**
在 Tests 标签中添加：
```javascript
pm.environment.set("TOKEN", pm.response.json().data.token);
```

### 3. 测试需要认证的接口

**请求：**
- Method: GET
- URL: `{{BASE_URL}}/user/info`
- Headers: `Authorization: Bearer {{TOKEN}}`

---

## 📊 使用 Knife4j 测试

### 1. 访问文档

打开浏览器访问：`http://localhost:8080/doc.html`

### 2. 登录获取 Token

在文档页面中：
1. 找到 `用户控制器` → `POST /user/login`
2. 点击 `调试`
3. 输入用户名和密码
4. 点击 `发送`
5. 复制返回的 Token

### 3. 配置全局认证

1. 点击页面右上角 `🔓 未授权`
2. 选择 `Bearer Token` 或 `Authorization`
3. 输入: `Bearer <your_token>`
4. 点击 `授权`

现在所有接口都会自动带上 Token！

---

## 🎯 常见问题

### Q1: 启动失败，报端口被占用

**解决方案：**
1. 修改端口：在 `application.properties` 中设置 `server.port=8081`
2. 或者杀死占用 8080 端口的进程

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i :8080
kill -9 <进程ID>
```

### Q2: 数据库连接失败

**检查项：**
1. MySQL 服务是否启动
2. 数据库名称是否正确
3. 用户名密码是否正确
4. 是否创建了数据库

### Q3: Token 验证失败

**原因：**
1. Token 格式不正确（应该是 `Bearer <token>`）
2. Token 已过期（有效期 7 天）
3. 请求头 Key 错误（应该是 `Authorization`）

### Q4: 接口返回 401 未认证

**检查：**
1. 是否携带了 Token
2. Token 是否有效
3. 接口是否在白名单中（登录、注册接口不需要 Token）

### Q5: Lombok 不生效

**解决：**
1. 确保 IDE 安装了 Lombok 插件
2. 重新编译项目：`mvnw clean compile`
3. IDEA 中 `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → 勾选 `Enable annotation processing`

---

## 📝 下一步

- 阅读 [项目结构说明](PROJECT_STRUCTURE.md) 了解项目架构
- 阅读 [开发规范](DEVELOPMENT_GUIDE.md) 学习开发标准
- 阅读 [API 文档](API_DOCUMENTATION.md) 查看完整 API 列表
- 阅读 [认证指南](../AUTHENTICATION_GUIDE.md) 了解认证机制

---

## 🎉 恭喜！

你已经成功启动了项目！现在可以开始开发自己的功能了。

如果遇到问题，请查看：
- 控制台日志
- API 文档
- 项目文档
- 或提交 Issue

