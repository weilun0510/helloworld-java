# 项目架构最佳实践

## 📋 概述

本文档记录了项目中采用的最佳实践和架构设计模式，以 `ProjectController` 为示例。

## 🏗️ 分层架构

### 1. Controller 层
- **职责**：接收 HTTP 请求，参数验证，调用 Service，返回响应
- **不应该**：包含业务逻辑、直接操作数据库

### 2. Service 层
- **职责**：业务逻辑处理，事务管理，协调多个 Mapper
- **不应该**：直接处理 HTTP 请求和响应

### 3. Mapper 层
- **职责**：数据库操作，SQL 映射
- **不应该**：包含业务逻辑

```
┌─────────────┐
│  Controller │  ← 接收请求，使用 DTO
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ← 业务逻辑，使用 Entity
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Mapper    │  ← 数据库操作
└─────────────┘
```

## 🎯 DTO 模式

### 为什么使用 DTO？

1. **分离关注点**：API 模型和数据库模型解耦
2. **安全性**：避免暴露敏感字段（如密码哈希）
3. **灵活性**：API 可以独立于数据库结构变化
4. **清晰性**：明确哪些字段是必需的，哪些是可选的

### DTO 类型

#### CreateDTO - 创建资源
```java
@Data
public class CreateProjectDTO {
    @NotBlank(message = "项目名称不能为空")
    private String name;
    
    @NotBlank(message = "项目状态不能为空")
    private String status;
    
    private String cover; // 可选
}
```

#### UpdateDTO - 更新资源
```java
@Data
public class UpdateProjectDTO {
    // 所有字段都是可选的
    private String name;
    private String status;
    private String cover;
}
```

#### ResponseDTO - 响应数据（可选）
```java
@Data
public class ProjectResponseDTO {
    private Integer id;
    private String name;
    private String status;
    private String cover;
    private LocalDateTime createTime;
    // 不包含敏感信息
}
```

## ✅ 参数验证

### Bean Validation 注解

```java
// Controller 中使用 @Valid
@PostMapping
public Result create(@Valid @RequestBody CreateProjectDTO dto) {
    // 参数已经自动验证
}
```

### 常用验证注解

| 注解 | 说明 | 示例 |
|-----|------|------|
| `@NotNull` | 不能为 null | `@NotNull private Integer age;` |
| `@NotBlank` | 不能为空字符串 | `@NotBlank private String name;` |
| `@NotEmpty` | 不能为空集合 | `@NotEmpty private List<String> tags;` |
| `@Size` | 长度限制 | `@Size(min=2, max=50) private String name;` |
| `@Min` / `@Max` | 数值范围 | `@Min(0) @Max(100) private Integer score;` |
| `@Email` | 邮箱格式 | `@Email private String email;` |
| `@Pattern` | 正则表达式 | `@Pattern(regexp="^[0-9]{11}$") private String phone;` |

### 自定义错误消息

```java
@NotBlank(message = "项目名称不能为空")
private String name;

@Size(min = 2, max = 50, message = "项目名称长度必须在 2-50 之间")
private String name;
```

## 🔄 精确更新 vs 完整更新

### ❌ 不推荐：使用 updateById + 完整实体

```java
@PutMapping("/{id}")
public Result update(@PathVariable Integer id, @RequestBody ProjectEntity project) {
    project.setId(id);
    projectService.updateById(project);  // ❌ 可能覆盖未传入的字段
    return Result.ok();
}
```

**问题**：
1. 前端如果只想更新 `status`，但 `name` 字段未传入（null），会导致 `name` 被设置为 null
2. 无法区分"不更新"和"设置为空"

### ✅ 推荐：使用 LambdaUpdateWrapper + DTO

```java
@Override
public boolean updateProject(Integer id, UpdateProjectDTO dto) {
    LambdaUpdateWrapper<ProjectEntity> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ProjectEntity::getId, id);
    
    // 只设置传入的字段
    if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getName, dto.getName());
    }
    
    if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getStatus, dto.getStatus());
    }
    
    return this.update(updateWrapper);
}
```

**优点**：
1. ✅ 只更新传入的字段
2. ✅ 未传入的字段保持不变
3. ✅ 避免误覆盖
4. ✅ 更安全

## 🌐 RESTful API 设计

### HTTP 方法使用

| 方法 | 用途 | 幂等性 | 示例 |
|------|------|--------|------|
| `GET` | 查询资源 | ✅ | `GET /project` 查询列表 |
| `POST` | 创建资源 | ❌ | `POST /project` 创建项目 |
| `PUT` | 完整更新 | ✅ | `PUT /project/1` 完整更新 |
| `PATCH` | 部分更新 | ❌ | `PATCH /project/1` 部分更新 |
| `DELETE` | 删除资源 | ✅ | `DELETE /project/1` 删除 |

### URL 设计

```
GET    /project          # 查询列表（分页）
GET    /project/{id}     # 查询详情
POST   /project          # 创建
PATCH  /project/{id}     # 部分更新（推荐）
PUT    /project/{id}     # 完整更新（不推荐）
DELETE /project/{id}     # 删除
DELETE /project/batch    # 批量删除
```

### 响应格式

使用统一的响应格式：

```json
{
  "success": true,
  "code": 20000,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "项目名称"
  }
}
```

## 📝 代码示例：完整的 CRUD

### 1. Controller

```java
@RestController
@RequestMapping("/project")
@Tag(name = "项目管理")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 查询列表
    @GetMapping
    public Result findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ProjectEntity> result = projectService.page(new Page<>(page, pageSize));
        return Result.ok().data("records", result.getRecords());
    }

    // 查询详情
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        ProjectEntity project = projectService.getById(id);
        return Result.ok().data("project", project);
    }

    // 创建
    @PostMapping
    public Result create(@Valid @RequestBody CreateProjectDTO dto) {
        ProjectEntity project = projectService.createProject(dto);
        return Result.ok().data("id", project.getId());
    }

    // 更新
    @PatchMapping("/{id}")
    public Result update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProjectDTO dto) {
        projectService.updateProject(id, dto);
        return Result.ok();
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        projectService.removeById(id);
        return Result.ok();
    }
}
```

### 2. Service 接口

```java
public interface ProjectService extends IService<ProjectEntity> {
    
    /**
     * 创建项目
     */
    ProjectEntity createProject(CreateProjectDTO dto);
    
    /**
     * 更新项目（只更新传入的字段）
     */
    boolean updateProject(Integer id, UpdateProjectDTO dto);
}
```

### 3. Service 实现

```java
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> 
        implements ProjectService {

    @Override
    public ProjectEntity createProject(CreateProjectDTO dto) {
        // DTO 转 Entity
        ProjectEntity project = new ProjectEntity();
        project.setName(dto.getName());
        project.setStatus(dto.getStatus());
        project.setCover(dto.getCover());
        project.setCreateTime(LocalDateTime.now());
        
        // 保存
        super.save(project);
        return project;
    }

    @Override
    public boolean updateProject(Integer id, UpdateProjectDTO dto) {
        // 使用 LambdaUpdateWrapper 精确更新
        LambdaUpdateWrapper<ProjectEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProjectEntity::getId, id);
        
        if (dto.getName() != null) {
            wrapper.set(ProjectEntity::getName, dto.getName());
        }
        if (dto.getStatus() != null) {
            wrapper.set(ProjectEntity::getStatus, dto.getStatus());
        }
        if (dto.getCover() != null) {
            wrapper.set(ProjectEntity::getCover, dto.getCover());
        }
        
        return this.update(wrapper);
    }
}
```

## 🔒 安全最佳实践

### 1. 输入验证
- ✅ 使用 `@Valid` 和 Bean Validation
- ✅ 在 Service 层进行业务规则验证
- ✅ 验证 ID 是否存在

### 2. 输出过滤
- ✅ 使用 ResponseDTO 避免暴露敏感字段
- ✅ 使用 `@JsonIgnore` 隐藏敏感字段
- ✅ 根据用户权限返回不同的数据

### 3. 权限控制
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id) {
    // 只有管理员可以删除
}
```

## 📊 性能优化

### 1. 分页查询
```java
// 使用 MyBatis-Plus 分页
Page<ProjectEntity> page = new Page<>(pageNum, pageSize);
IPage<ProjectEntity> result = projectService.page(page);
```

### 2. 批量操作
```java
// 批量插入
projectService.saveBatch(projectList);

// 批量更新
projectService.updateBatchById(projectList);

// 批量删除
projectService.removeByIds(idList);
```

### 3. 缓存
```java
@Cacheable(value = "project", key = "#id")
public ProjectEntity getById(Integer id) {
    return super.getById(id);
}
```

## 🧪 测试

### 单元测试示例

```java
@SpringBootTest
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Test
    void testCreateProject() {
        CreateProjectDTO dto = new CreateProjectDTO();
        dto.setName("测试项目");
        dto.setStatus("进行中");
        
        ProjectEntity project = projectService.createProject(dto);
        
        assertNotNull(project.getId());
        assertEquals("测试项目", project.getName());
    }

    @Test
    void testUpdateProject() {
        UpdateProjectDTO dto = new UpdateProjectDTO();
        dto.setStatus("已完成");
        
        boolean success = projectService.updateProject(1, dto);
        
        assertTrue(success);
        
        ProjectEntity updated = projectService.getById(1);
        assertEquals("已完成", updated.getStatus());
    }
}
```

## 📚 参考资料

- [Spring Boot Validation](https://spring.io/guides/gs/validating-form-input/)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [RESTful API 设计指南](https://restfulapi.net/)
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)

## 🎯 检查清单

在编写新的 Controller 时，确保：

- [ ] 使用 DTO 而不是直接使用 Entity
- [ ] 使用 `@Valid` 进行参数验证
- [ ] 创建和更新使用不同的 DTO
- [ ] 更新操作使用 `LambdaUpdateWrapper` 精确更新
- [ ] 使用 `PATCH` 而不是 `PUT` 进行部分更新
- [ ] 遵循 RESTful API 设计规范
- [ ] 添加 Swagger 注解（`@Operation`、`@Parameter`）
- [ ] 返回统一的响应格式（`Result`）
- [ ] 进行异常处理
- [ ] 检查资源是否存在
- [ ] 编写单元测试

---

**最后更新**: 2024-11-24
**示例代码**: `ProjectController.java`

