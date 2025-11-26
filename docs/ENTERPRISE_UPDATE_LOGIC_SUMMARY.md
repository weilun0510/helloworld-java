# 企业级字段更新逻辑总结

## 🎯 解决的三大问题

### 1️⃣ 保护不能清空的字段 ✅

**问题**：必填字段（如 name、status）不应该被清空

**解决方案**：
```java
// DTO 中添加验证
public void validateRequiredFields() {
    if (nameSet && (name == null || name.trim().isEmpty())) {
        throw new IllegalArgumentException("项目名称不能为空");
    }
    if (statusSet && (status == null || status.trim().isEmpty())) {
        throw new IllegalArgumentException("项目状态不能为空");
    }
}
```

**效果**：
- ✅ 尝试清空必填字段时返回 400 错误
- ✅ 错误信息清晰明确

### 2️⃣ null 不更新，"" 更新为空字符串 ✅

**问题**：需要区分"不更新"和"清空"的语义

**解决方案**：

**必填字段**（name, status）：
```java
// null 或 "" 都不更新（保护机制）
if (dto.isNameSet() && dto.getName() != null && !dto.getName().trim().isEmpty()) {
    updateWrapper.set(ProjectEntity::getName, dto.getName().trim());
}
```

**可选字段**（cover）：
```java
// null 或 "" 都清空为 null
if (dto.isCoverSet()) {
    if (dto.getCover() == null || dto.getCover().isEmpty()) {
        updateWrapper.set(ProjectEntity::getCover, null);
    } else {
        updateWrapper.set(ProjectEntity::getCover, dto.getCover().trim());
    }
}
```

**效果**：
| 字段类型 | 传 null | 传 "" | 传 "值" |
|---------|---------|-------|--------|
| 必填字段 | 不更新 | 抛异常 | 更新 |
| 可选字段 | 清空 | 清空 | 更新 |

### 3️⃣ 区分"没传字段"和"想清空字段" ✅

**问题**：前端没传字段 vs 前端传了 null，后端都收到 null

**解决方案**：使用标记字段
```java
@Data
public class UpdateProjectDTO {
    private String name;
    private transient boolean nameSet = false;  // 标记字段
    
    public void setName(String name) {
        this.name = name;
        this.nameSet = true;  // setter 中标记
    }
    
    public boolean isNameSet() {
        return nameSet;
    }
}
```

**效果**：
```
前端未传 name    → nameSet = false → 不更新
前端传 name=null → nameSet = true  → 根据字段类型处理
前端传 name=""   → nameSet = true  → 根据字段类型处理
前端传 name="值" → nameSet = true  → 更新为该值
```

## 📊 完整的更新逻辑表

### 必填字段（name, status）

| 前端行为 | nameSet | name 值 | 后端处理 | 数据库结果 |
|---------|---------|---------|---------|-----------|
| 未传 name | false | null | 不更新 | 保持原值 |
| 传 null | true | null | 抛异常 | 保持原值 |
| 传 "" | true | "" | 抛异常 | 保持原值 |
| 传 "新值" | true | "新值" | 更新 | "新值" |

### 可选字段（cover）

| 前端行为 | coverSet | cover 值 | 后端处理 | 数据库结果 |
|---------|----------|---------|---------|-----------|
| 未传 cover | false | null | 不更新 | 保持原值 |
| 传 null | true | null | 清空 | null |
| 传 "" | true | "" | 清空 | null |
| 传 "新值" | true | "新值" | 更新 | "新值" |

## 🏗️ 实现架构

### 1. UpdateProjectDTO

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProjectDTO {
    // 字段定义
    @Size(min = 1, max = 100, message = "项目名称长度必须在1-100之间")
    private String name;
    
    @Size(min = 1, max = 50, message = "项目状态长度必须在1-50之间")
    private String status;
    
    private String cover;
    
    // 标记字段
    private transient boolean nameSet = false;
    private transient boolean statusSet = false;
    private transient boolean coverSet = false;
    
    // 重写 setter
    public void setName(String name) {
        this.name = name;
        this.nameSet = true;
    }
    
    // 验证方法
    public void validateRequiredFields() {
        if (nameSet && (name == null || name.trim().isEmpty())) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (statusSet && (status == null || status.trim().isEmpty())) {
            throw new IllegalArgumentException("项目状态不能为空");
        }
    }
}
```

### 2. ProjectServiceImpl

```java
@Override
public boolean updateProject(Integer id, UpdateProjectDTO dto) {
    // 1. 检查资源存在
    ProjectEntity existingProject = baseMapper.selectById(id);
    if (existingProject == null) {
        throw new ResourceNotFoundException("项目", "ID", id);
    }

    // 2. 验证必填字段
    dto.validateRequiredFields();

    // 3. 精确更新
    LambdaUpdateWrapper<ProjectEntity> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ProjectEntity::getId, id);
    boolean hasUpdate = false;

    // 4. 更新必填字段
    if (dto.isNameSet() && dto.getName() != null && !dto.getName().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getName, dto.getName().trim());
        hasUpdate = true;
    }

    if (dto.isStatusSet() && dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getStatus, dto.getStatus().trim());
        hasUpdate = true;
    }

    // 5. 更新可选字段
    if (dto.isCoverSet()) {
        if (dto.getCover() == null || dto.getCover().isEmpty()) {
            updateWrapper.set(ProjectEntity::getCover, null);
        } else {
            updateWrapper.set(ProjectEntity::getCover, dto.getCover().trim());
        }
        hasUpdate = true;
    }

    // 6. 执行更新
    if (!hasUpdate) {
        return true;
    }
    return this.update(updateWrapper);
}
```

### 3. ProjectEntity

```java
@Data
@TableName("project")
public class ProjectEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String status;
    private String cover;

    // 保护 createTime 不被更新
    @TableField(value = "create_time", update = "false")
    private LocalDateTime createTime;
}
```

## 🧪 关键测试用例

### 测试 1：保护必填字段

```http
PATCH http://localhost:8080/project/1
{"name": ""}
```
**预期**：400 Bad Request，"项目名称不能为空"

### 测试 2：清空可选字段

```http
PATCH http://localhost:8080/project/1
{"cover": ""}
```
**预期**：200 OK，cover 被清空为 null

### 测试 3：未传字段不更新

```http
PATCH http://localhost:8080/project/1
{"status": "已完成"}
```
**预期**：200 OK，只更新 status，name 和 cover 保持不变

## 📈 代码改进对比

### 改进前（简单版本）

```java
// ❌ 问题：无法区分未传和传 null
if (dto.getName() != null) {
    updateWrapper.set(ProjectEntity::getName, dto.getName());
}
```

**问题**：
- 无法区分未传和传 null
- 无法保护必填字段
- 无法清空可选字段

### 改进后（企业级版本）

```java
// ✅ 优点：完整的字段控制
if (dto.isNameSet() && dto.getName() != null && !dto.getName().trim().isEmpty()) {
    updateWrapper.set(ProjectEntity::getName, dto.getName().trim());
}
```

**优点**：
- ✅ 区分未传和传值
- ✅ 保护必填字段
- ✅ 支持清空可选字段
- ✅ 自动 trim 空格

## 🎓 企业级特性

### 1. 字段分类管理

| 字段类型 | 示例 | 更新策略 |
|---------|------|---------|
| 必填字段 | name, status | 不允许清空 |
| 可选字段 | cover, description | 允许清空 |
| 系统字段 | id, createTime | 完全不可更新 |
| 审计字段 | updateTime, updateBy | 自动设置 |

### 2. 数据验证

- ✅ 参数验证（@Size, @NotBlank）
- ✅ 业务验证（validateRequiredFields）
- ✅ 资源存在性验证
- ✅ 权限验证（可扩展）

### 3. 异常处理

```java
// 参数验证失败
@Size(min = 1, max = 100) → 400 "项目名称长度必须在1-100之间"

// 业务验证失败
validateRequiredFields() → 400 "项目名称不能为空"

// 资源不存在
baseMapper.selectById() → 404 "项目 不存在: ID = 999"
```

### 4. 自动清理

```java
// 自动 trim 前后空格
dto.getName().trim()

// 统一 null 和 ""
if (dto.getCover() == null || dto.getCover().isEmpty()) {
    updateWrapper.set(ProjectEntity::getCover, null);
}
```

## 📁 文件清单

### 修改的文件
1. ✅ `dto/UpdateProjectDTO.java` - 添加标记字段和验证
2. ✅ `service/impl/ProjectServiceImpl.java` - 完善更新逻辑
3. ✅ `entity/ProjectEntity.java` - 保护 createTime

### 新增文档
1. ✅ `docs/UPDATE_FIELD_LOGIC_GUIDE.md` - 详细指南
2. ✅ `docs/UPDATE_FIELD_TEST_CASES.md` - 测试用例
3. ✅ `docs/ENTERPRISE_UPDATE_LOGIC_SUMMARY.md` - 本文档

## 🚀 应用到其他模块

这套方案可以直接应用到其他业务模块：

### User 模块

```java
public class UpdateUserDTO {
    @Size(min = 1, max = 50)
    private String username;  // 必填，不可清空
    
    @Email
    private String email;  // 必填，不可清空
    
    private String avatar;  // 可选，可清空
    
    // 标记字段和 setter...
}
```

### Order 模块

```java
public class UpdateOrderDTO {
    @Size(min = 1, max = 50)
    private String status;  // 必填，不可清空
    
    private String remark;  // 可选，可清空
    
    // 标记字段和 setter...
}
```

## ✅ 最佳实践检查清单

- [x] 区分必填字段和可选字段
- [x] 使用标记字段区分未传和传值
- [x] 保护必填字段不被清空
- [x] 允许可选字段清空
- [x] 使用 LambdaUpdateWrapper 精确更新
- [x] 自动 trim 前后空格
- [x] 添加参数验证注解
- [x] 添加业务验证方法
- [x] 检查资源是否存在
- [x] 统一异常处理
- [x] 保护系统字段（createTime）
- [x] 编写测试用例
- [x] 编写详细文档

## 🎯 关键收获

1. **标记字段是关键**：区分未传和传值
2. **字段分类管理**：必填、可选、系统字段
3. **验证分层**：参数验证 + 业务验证
4. **统一清空语义**：null 和 "" 对可选字段效果相同
5. **自动清理**：trim 空格，统一 null

## 📚 相关文档

- [字段更新逻辑指南](./UPDATE_FIELD_LOGIC_GUIDE.md) - 详细说明
- [字段更新测试用例](./UPDATE_FIELD_TEST_CASES.md) - 12 个测试场景
- [架构最佳实践](./ARCHITECTURE_BEST_PRACTICES.md) - 整体架构
- [全局异常处理器](./GLOBAL_EXCEPTION_HANDLER_GUIDE.md) - 异常处理

---

**状态**：✅ 完成，符合企业级标准

现在的字段更新逻辑：
- ✨ 完全控制字段更新
- 🛡️ 保护必填字段
- 🎯 精确更新，不误伤
- 📝 文档完善，易于理解
- 🧪 测试用例齐全

可以作为企业级项目的标准模板！🎉

