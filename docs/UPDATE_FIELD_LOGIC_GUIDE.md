# 字段更新逻辑最佳实践

## 📋 概述

在企业级应用中，PATCH 更新操作需要精确控制哪些字段被更新，哪些字段保持不变。本文档详细说明如何实现一个健壮的字段更新逻辑。

## 🎯 核心需求

### 1. 区分三种状态

| 状态 | 含义 | 处理方式 |
|------|------|---------|
| **未传字段** | 前端没有传该字段 | 不更新，保持原值 |
| **传 null** | 前端传了字段，值为 null | 取决于字段类型（见下文）|
| **传空字符串 ""** | 前端传了字段，值为空字符串 | 取决于字段类型（见下文）|
| **传有值** | 前端传了字段，有具体值 | 更新为该值 |

### 2. 字段分类保护

| 字段类型 | 示例 | 是否可清空 | 处理规则 |
|---------|------|-----------|---------|
| **必填字段** | name, status | ❌ 不可清空 | null 或 "" 都不更新 |
| **可选字段** | cover, description | ✅ 可清空 | null 或 "" 都清空为 null |

### 3. 更新逻辑表

#### 必填字段（如 name, status）

| 前端传值 | 后端接收 | 是否更新 | 更新为 | 说明 |
|---------|---------|---------|--------|------|
| 未传 | null | ❌ 否 | - | 保持原值 |
| null | null | ❌ 否 | - | 保持原值 |
| "" | "" | ❌ 否 | - | 抛出异常（不允许清空）|
| "新值" | "新值" | ✅ 是 | "新值" | 更新为新值 |

#### 可选字段（如 cover）

| 前端传值 | 后端接收 | 是否更新 | 更新为 | 说明 |
|---------|---------|---------|--------|------|
| 未传 | null | ❌ 否 | - | 保持原值 |
| null | null | ✅ 是 | null | 清空 |
| "" | "" | ✅ 是 | null | 清空 |
| "新值" | "新值" | ✅ 是 | "新值" | 更新为新值 |

## 🏗️ 实现方案

### 方案：使用标记字段（推荐）

#### UpdateProjectDTO.java

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProjectDTO {

    // 必填字段
    @Size(min = 1, max = 100, message = "项目名称长度必须在1-100之间")
    private String name;

    @Size(min = 1, max = 50, message = "项目状态长度必须在1-50之间")
    private String status;

    // 可选字段
    private String cover;

    // 标记字段（transient 不序列化）
    private transient boolean nameSet = false;
    private transient boolean statusSet = false;
    private transient boolean coverSet = false;

    // 重写 setter，标记字段已设置
    public void setName(String name) {
        this.name = name;
        this.nameSet = true;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusSet = true;
    }

    public void setCover(String cover) {
        this.cover = cover;
        this.coverSet = true;
    }

    // 验证必填字段
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

#### ProjectServiceImpl.java

```java
@Override
public boolean updateProject(Integer id, UpdateProjectDTO dto) {
    // 1. 检查资源是否存在
    ProjectEntity existingProject = baseMapper.selectById(id);
    if (existingProject == null) {
        throw new ResourceNotFoundException("项目", "ID", id);
    }

    // 2. 验证必填字段不能清空
    dto.validateRequiredFields();

    // 3. 使用 LambdaUpdateWrapper 精确更新
    LambdaUpdateWrapper<ProjectEntity> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ProjectEntity::getId, id);

    boolean hasUpdate = false;

    // 4. 更新必填字段（只有传入且非空时才更新）
    if (dto.isNameSet() && dto.getName() != null && !dto.getName().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getName, dto.getName().trim());
        hasUpdate = true;
    }

    if (dto.isStatusSet() && dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getStatus, dto.getStatus().trim());
        hasUpdate = true;
    }

    // 5. 更新可选字段（允许清空）
    if (dto.isCoverSet()) {
        if (dto.getCover() == null || dto.getCover().isEmpty()) {
            updateWrapper.set(ProjectEntity::getCover, null);  // 清空
        } else {
            updateWrapper.set(ProjectEntity::getCover, dto.getCover().trim());
        }
        hasUpdate = true;
    }

    // 6. 如果没有更新，直接返回
    if (!hasUpdate) {
        return true;
    }

    // 7. 执行更新
    return this.update(updateWrapper);
}
```

## 🧪 测试用例

### 测试 1：只更新 status（必填字段）

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "status": "已完成"
}
```

**预期结果**：
- ✅ status 更新为 "已完成"
- ✅ name 保持不变
- ✅ cover 保持不变
- ✅ createTime 保持不变

### 测试 2：尝试清空 name（必填字段）

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "name": ""
}
```

**预期结果**：
- ❌ 返回 400 Bad Request
- 错误信息：`"参数错误: 项目名称不能为空"`

### 测试 3：清空 cover（可选字段）

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "cover": ""
}
```

**预期结果**：
- ✅ cover 更新为 null
- ✅ name 保持不变
- ✅ status 保持不变

### 测试 4：传 null 清空 cover

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "cover": null
}
```

**预期结果**：
- ✅ cover 更新为 null
- ✅ name 保持不变
- ✅ status 保持不变

### 测试 5：同时更新多个字段

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{
  "name": "项目名称 V2",
  "status": "已完成",
  "cover": "https://example.com/new-cover.jpg"
}
```

**预期结果**：
- ✅ name 更新为 "项目名称 V2"
- ✅ status 更新为 "已完成"
- ✅ cover 更新为新 URL
- ✅ createTime 保持不变

### 测试 6：传空对象（不更新任何字段）

```http
PATCH http://localhost:8080/project/1
Content-Type: application/json

{}
```

**预期结果**：
- ✅ 所有字段保持不变
- ✅ 返回 200 OK

## 📊 实现对比

### ❌ 简单实现（不推荐）

```java
// 问题：无法区分"未传"和"传 null"
if (dto.getName() != null) {
    updateWrapper.set(ProjectEntity::getName, dto.getName());
}
```

**问题**：
- 如果前端传 `{"name": null}`，无法清空
- 如果前端传 `{"name": ""}`，会清空必填字段

### ✅ 标记字段实现（推荐）

```java
// 优点：可以区分"未传"和"传值"
if (dto.isNameSet()) {
    // 前端传了 name 字段
    if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
        updateWrapper.set(ProjectEntity::getName, dto.getName().trim());
    }
}
```

**优点**：
- ✅ 可以区分未传和传值
- ✅ 可以保护必填字段
- ✅ 可以允许清空可选字段

## 🔒 安全考虑

### 1. 字段白名单

只允许更新预定义的字段：

```java
// ✅ 推荐：使用 DTO 限制可更新字段
public class UpdateProjectDTO {
    private String name;
    private String status;
    private String cover;
    // 不包含 id, createTime 等不可更新字段
}

// ❌ 不推荐：直接使用 Entity
public Result update(@RequestBody ProjectEntity entity) {
    // 危险！可能更新 id, createTime 等
}
```

### 2. 权限控制

某些字段只有特定角色可以更新：

```java
@Override
public boolean updateProject(Integer id, UpdateProjectDTO dto, User currentUser) {
    // 只有管理员可以修改状态
    if (dto.isStatusSet() && !currentUser.isAdmin()) {
        throw new BusinessException("无权修改项目状态");
    }
    // ...
}
```

### 3. 业务规则验证

```java
@Override
public boolean updateProject(Integer id, UpdateProjectDTO dto) {
    // 已完成的项目不能再修改
    ProjectEntity existing = baseMapper.selectById(id);
    if ("已完成".equals(existing.getStatus())) {
        throw new BusinessException("已完成的项目不能修改");
    }
    // ...
}
```

## 📝 字段类型指南

### 必填字段（不可清空）

适用于：
- 业务主键（name, code）
- 状态字段（status）
- 关联 ID（userId, categoryId）
- 必需的业务字段

处理规则：
```java
if (dto.isFieldSet() && dto.getField() != null && !dto.getField().trim().isEmpty()) {
    updateWrapper.set(Entity::getField, dto.getField().trim());
}
```

### 可选字段（可清空）

适用于：
- 描述信息（description, remark）
- 可选图片（cover, avatar）
- 可选链接（url, link）
- 扩展字段（extra, metadata）

处理规则：
```java
if (dto.isFieldSet()) {
    if (dto.getField() == null || dto.getField().isEmpty()) {
        updateWrapper.set(Entity::getField, null);  // 清空
    } else {
        updateWrapper.set(Entity::getField, dto.getField().trim());
    }
}
```

### 系统字段（完全不可更新）

适用于：
- 主键 ID
- 创建时间（createTime）
- 创建人（createBy）
- 系统生成的字段

处理规则：
```java
// 1. DTO 中不包含这些字段
// 2. Entity 中使用 @TableField(update = "false")
@TableField(value = "create_time", update = "false")
private LocalDateTime createTime;
```

## 🎨 最佳实践总结

### ✅ DO（推荐做法）

1. **使用标记字段区分未传和传值**
2. **保护必填字段不被清空**
3. **允许可选字段清空**
4. **使用 LambdaUpdateWrapper 精确更新**
5. **添加业务规则验证**
6. **使用 trim() 清理前后空格**
7. **记录更新日志**

### ❌ DON'T（不推荐做法）

1. ~~直接使用 `updateById(entity)`~~（覆盖所有字段）
2. ~~不区分必填和可选字段~~
3. ~~允许清空必填字段~~
4. ~~不验证业务规则~~
5. ~~不检查资源是否存在~~
6. ~~直接使用 Entity 作为更新参数~~

## 🔗 相关文档

- [架构最佳实践](./ARCHITECTURE_BEST_PRACTICES.md)
- [全局异常处理器指南](./GLOBAL_EXCEPTION_HANDLER_GUIDE.md)
- [DTO 模式快速参考](./QUICK_REFERENCE_DTO.md)

## 📚 扩展阅读

- [RESTful API 设计指南](https://restfulapi.net/)
- [HTTP PATCH 方法](https://tools.ietf.org/html/rfc5789)
- [JSON Merge Patch](https://tools.ietf.org/html/rfc7386)

---

**最后更新**: 2024-11-24
**示例代码**: `ProjectController.java` & `ProjectServiceImpl.java`

