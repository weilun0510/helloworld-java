# 全局异常处理器使用指南

## 📋 概述

项目现在使用全局异常处理器来统一处理所有异常，提供一致的错误响应格式。

## 🏗️ 架构组成

### 1. 全局异常处理器

**文件**: `exception/GlobalExceptionHandler.java`

使用 `@RestControllerAdvice` 注解，自动捕获所有 Controller 抛出的异常。

### 2. 自定义异常类

#### BusinessException - 业务异常
- **用途**: 业务逻辑中的可预期错误
- **HTTP 状态码**: 400 Bad Request
- **示例**: 参数不符合业务规则、重复创建等

#### ResourceNotFoundException - 资源不存在异常
- **用途**: 请求的资源不存在
- **HTTP 状态码**: 404 Not Found
- **示例**: 查询不存在的项目、用户等

## 🎯 使用方法

### 1. 在 Service 层抛出异常

```java
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> 
        implements ProjectService {

    @Override
    public ProjectEntity createProject(CreateProjectDTO dto) {
        // 业务逻辑...
        boolean success = super.save(project);
        if (!success) {
            // 抛出业务异常
            throw new BusinessException("创建项目失败");
        }
        return project;
    }

    @Override
    public boolean updateProject(Integer id, UpdateProjectDTO dto) {
        // 检查资源是否存在
        ProjectEntity existingProject = baseMapper.selectById(id);
        if (existingProject == null) {
            // 抛出资源不存在异常
            throw new ResourceNotFoundException("项目", "ID", id);
        }
        // 更新逻辑...
    }
}
```

### 2. 在 Controller 层简化异常处理

```java
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 不需要 try-catch，由全局异常处理器处理
    @PostMapping
    public Result create(@Valid @RequestBody CreateProjectDTO dto) {
        ProjectEntity project = projectService.createProject(dto);
        return Result.ok().data("project", project);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        ProjectEntity project = projectService.getById(id);
        if (project == null) {
            // 直接抛出异常
            throw new ResourceNotFoundException("项目", "ID", id);
        }
        return Result.ok().data("project", project);
    }
}
```

## 📊 异常处理映射表

| 异常类型 | HTTP 状态码 | 错误码 | 说明 |
|---------|-----------|-------|------|
| `MethodArgumentNotValidException` | 400 | 40000 | `@Valid` 验证失败 |
| `ConstraintViolationException` | 400 | 40000 | `@Validated` 验证失败 |
| `BindException` | 400 | 40000 | 表单绑定失败 |
| `BusinessException` | 400 | 自定义 | 业务逻辑错误 |
| `ResourceNotFoundException` | 404 | 40400 | 资源不存在 |
| `IllegalArgumentException` | 400 | 40001 | 非法参数 |
| `RuntimeException` | 500 | 50000 | 运行时错误 |
| `Exception` | 500 | 50000 | 其他未知错误 |

## 🎨 响应格式示例

### 成功响应
```json
{
  "success": true,
  "code": 20000,
  "message": "操作成功",
  "data": {
    "project": {
      "id": 1,
      "name": "项目名称",
      "status": "进行中"
    }
  }
}
```

### 参数验证失败（400）
```json
{
  "success": false,
  "code": 40000,
  "message": "参数验证失败: 项目名称不能为空",
  "data": {}
}
```

### 资源不存在（404）
```json
{
  "success": false,
  "code": 40400,
  "message": "项目 不存在: ID = 999",
  "data": {}
}
```

### 业务异常（400）
```json
{
  "success": false,
  "code": 40000,
  "message": "创建项目失败",
  "data": {}
}
```

### 系统错误（500）
```json
{
  "success": false,
  "code": 50000,
  "message": "系统错误: 数据库连接失败",
  "data": {}
}
```

## 🔧 自定义异常

### 创建新的业务异常

```java
// 1. 创建异常类
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

// 2. 在全局异常处理器中添加处理方法
@ExceptionHandler(DuplicateResourceException.class)
@ResponseStatus(HttpStatus.CONFLICT)  // 409 冲突
public Result handleDuplicateResourceException(DuplicateResourceException e) {
    return Result.error()
            .code(40900)
            .message(e.getMessage());
}

// 3. 在 Service 中使用
if (existingProject != null) {
    throw new DuplicateResourceException("项目名称已存在");
}
```

## 📝 最佳实践

### 1. Service 层职责
- ✅ 抛出具体的业务异常
- ✅ 进行业务逻辑验证
- ❌ 不要返回 Result 对象（这是 Controller 的职责）

```java
// ✅ 推荐
@Service
public class ProjectServiceImpl {
    public ProjectEntity createProject(CreateProjectDTO dto) {
        if (existsName(dto.getName())) {
            throw new BusinessException("项目名称已存在");
        }
        return super.save(project);
    }
}

// ❌ 不推荐
@Service
public class ProjectServiceImpl {
    public Result createProject(CreateProjectDTO dto) {
        if (existsName(dto.getName())) {
            return Result.error().message("项目名称已存在");
        }
        return Result.ok().data("project", project);
    }
}
```

### 2. Controller 层职责
- ✅ 参数验证（使用 `@Valid`）
- ✅ 调用 Service
- ✅ 包装响应（Result）
- ✅ 抛出异常让全局处理器处理
- ❌ 不要在 Controller 中 try-catch 处理业务异常

```java
// ✅ 推荐
@PostMapping
public Result create(@Valid @RequestBody CreateProjectDTO dto) {
    ProjectEntity project = projectService.createProject(dto);
    return Result.ok().data("project", project);
}

// ❌ 不推荐
@PostMapping
public Result create(@Valid @RequestBody CreateProjectDTO dto) {
    try {
        ProjectEntity project = projectService.createProject(dto);
        return Result.ok().data("project", project);
    } catch (BusinessException e) {
        return Result.error().message(e.getMessage());
    }
}
```

### 3. 异常选择指南

| 场景 | 使用的异常 |
|------|-----------|
| 资源不存在 | `ResourceNotFoundException` |
| 业务规则违反 | `BusinessException` |
| 参数不合法 | `IllegalArgumentException` |
| 重复创建 | `BusinessException` 或自定义 `DuplicateException` |
| 权限不足 | 自定义 `ForbiddenException` |
| 未认证 | 自定义 `UnauthorizedException` |

## 🧪 测试示例

### 测试参数验证

```bash
# 请求（缺少必填字段）
POST http://localhost:8080/project
Content-Type: application/json

{
  "name": "",
  "status": "进行中"
}

# 响应（自动验证失败）
{
  "success": false,
  "code": 40000,
  "message": "参数验证失败: 项目名称不能为空",
  "data": {}
}
```

### 测试资源不存在

```bash
# 请求
GET http://localhost:8080/project/999

# 响应
{
  "success": false,
  "code": 40400,
  "message": "项目 不存在: ID = 999",
  "data": {}
}
```

## 🔍 调试技巧

### 1. 查看异常堆栈
在 `GlobalExceptionHandler` 中，所有异常都会打印堆栈：
```java
@ExceptionHandler(Exception.class)
public Result handleException(Exception e) {
    e.printStackTrace();  // 查看完整堆栈
    return Result.error().message(e.getMessage());
}
```

### 2. 自定义日志
```java
@ExceptionHandler(BusinessException.class)
public Result handleBusinessException(BusinessException e) {
    log.error("业务异常: {}", e.getMessage());
    return Result.error().message(e.getMessage());
}
```

## 📚 相关文档

- [架构最佳实践](./ARCHITECTURE_BEST_PRACTICES.md)
- [DTO 模式快速参考](./QUICK_REFERENCE_DTO.md)
- [项目重构总结](./PROJECT_REFACTORING_SUMMARY.md)

## ✅ 检查清单

实现全局异常处理时，确保：

- [ ] 创建了 `GlobalExceptionHandler`
- [ ] 创建了自定义异常类（`BusinessException`、`ResourceNotFoundException`）
- [ ] Service 层抛出具体的异常
- [ ] Controller 层不再使用 try-catch
- [ ] 所有异常都有统一的响应格式
- [ ] 添加了适当的 HTTP 状态码
- [ ] 异常信息对前端友好

---

**最后更新**: 2024-11-24
**示例代码**: `ProjectController.java` & `ProjectServiceImpl.java`

