# 参数验证指南

## 概述

本文档介绍如何在 Controller 层正确使用 Bean Validation 进行参数验证。

---

## 1. Controller 类级别添加 @Validated

在使用 `@PathVariable` 和 `@RequestParam` 参数验证时，必须在 Controller 类上添加 `@Validated` 注解。

```java
@RestController
@RequestMapping("/project")
@Validated  // ← 必须添加此注解
public class ProjectController {
    // ...
}
```

**为什么需要 @Validated？**

- `@Valid` 只对 `@RequestBody` 的对象参数生效
- `@PathVariable` 和 `@RequestParam` 的验证需要 `@Validated` 在类级别启用

---

## 2. 常用验证注解

### 2.1 数值验证

```java
import jakarta.validation.constraints.*;

// 必须为正整数（> 0）
@Positive(message = "ID必须为正整数")
Integer id

// 必须大于等于指定值
@Min(value = 1, message = "页码必须大于等于1")
int page

// 必须小于等于指定值
@Max(value = 100, message = "每页数量不能超过100")
int pageSize

// 范围验证（包含边界）
@Min(1) @Max(100)
int size
```

### 2.2 字符串验证

```java
// 不能为 null
@NotNull(message = "用户名不能为空")
String username

// 不能为空字符串（去除空格后）
@NotBlank(message = "项目名称不能为空")
String name

// 不能为空（null 或空字符串）
@NotEmpty(message = "状态不能为空")
String status

// 长度限制
@Size(min = 2, max = 50, message = "用户名长度必须在2-50之间")
String username

// 正则表达式
@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
String username
```

### 2.3 集合验证

```java
// 集合不能为空
@NotEmpty(message = "ID列表不能为空")
List<Integer> ids

// 集合大小限制
@Size(min = 1, max = 100, message = "ID列表大小必须在1-100之间")
List<Integer> ids
```

---

## 3. 使用示例

### 3.1 @PathVariable 参数验证

```java
@GetMapping("/{id}")
public Result getById(
    @PathVariable 
    @NotNull(message = "项目ID不能为空") 
    @Positive(message = "项目ID必须为正整数") 
    Integer id) {
    // ...
}
```

### 3.2 @RequestParam 参数验证

```java
@GetMapping
public Result findAll(
    @RequestParam(required = false)
    @Positive(message = "项目ID必须为正整数") 
    Integer id,
    
    @RequestParam(value = "page", defaultValue = "1")
    @Min(value = 1, message = "页码必须大于等于1") 
    int page,
    
    @RequestParam(value = "pageSize", defaultValue = "10")
    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 100, message = "每页数量不能超过100")
    int pageSize) {
    // ...
}
```

### 3.3 @RequestBody 参数验证

```java
@PostMapping
public Result create(@Valid @RequestBody CreateProjectDTO dto) {
    // DTO 内部使用验证注解
}
```

**DTO 示例：**

```java
@Data
public class CreateProjectDTO {
    @NotBlank(message = "项目名称不能为空")
    @Size(min = 2, max = 100, message = "项目名称长度必须在2-100之间")
    private String name;

    @NotBlank(message = "项目状态不能为空")
    private String status;
}
```

---

## 4. ProjectController 完整示例

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
@Validated  // ← 类级别验证注解
public class ProjectController {

    /**
     * 查询项目列表
     */
    @GetMapping
    public Result findAll(
        @Positive(message = "项目ID必须为正整数") 
        @RequestParam(required = false) Integer id,
        
        @RequestParam(required = false) String name,
        
        @Min(value = 1, message = "页码必须大于等于1") 
        @RequestParam(value = "page", defaultValue = "1") int page,
        
        @Min(value = 1, message = "每页数量必须大于等于1") 
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // ...
    }

    /**
     * 查询项目详情
     */
    @GetMapping("/{id}")
    public Result getById(
        @PathVariable 
        @NotNull(message = "项目ID不能为空") 
        @Positive(message = "项目ID必须为正整数") 
        Integer id) {
        // ...
    }

    /**
     * 创建项目
     */
    @PostMapping
    public Result create(@Valid @RequestBody CreateProjectDTO dto) {
        // ...
    }

    /**
     * 更新项目
     */
    @PatchMapping("/{id}")
    public Result update(
        @PathVariable 
        @NotNull(message = "项目ID不能为空") 
        @Positive(message = "项目ID必须为正整数") 
        Integer id,
        
        @Valid @RequestBody UpdateProjectDTO dto) {
        // ...
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public Result delete(
        @PathVariable 
        @NotNull(message = "项目ID不能为空") 
        @Positive(message = "项目ID必须为正整数") 
        Integer id) {
        // ...
    }
}
```

---

## 5. 全局异常处理

验证失败时会抛出以下异常，需要在 `GlobalExceptionHandler` 中统一处理：

### 5.1 异常类型

- `ConstraintViolationException` - `@PathVariable` 和 `@RequestParam` 验证失败
- `MethodArgumentNotValidException` - `@RequestBody` 验证失败
- `BindException` - 表单验证失败

### 5.2 异常处理示例

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @PathVariable 和 @RequestParam 参数验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException e) {
        // 收集所有错误信息
        String message = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        
        return Result.error(400).message("参数验证失败: " + message);
    }

    /**
     * 处理 @RequestBody 参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 收集所有字段错误
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        return Result.error(400).message("参数验证失败: " + message);
    }
}
```

---

## 6. 验证注解对比

| 注解 | 作用 | 适用类型 | null 处理 |
|------|------|----------|-----------|
| `@NotNull` | 不能为 null | 所有类型 | 验证失败 |
| `@NotBlank` | 不能为空字符串（去空格） | String | null 验证失败 |
| `@NotEmpty` | 不能为空 | String/Collection/Map/Array | null 验证失败 |
| `@Positive` | 必须为正数（> 0） | 数值类型 | null 通过 |
| `@PositiveOrZero` | 必须为正数或0（>= 0） | 数值类型 | null 通过 |
| `@Min` | 最小值 | 数值类型 | null 通过 |
| `@Max` | 最大值 | 数值类型 | null 通过 |
| `@Size` | 长度/大小限制 | String/Collection/Map/Array | null 通过 |
| `@Pattern` | 正则表达式 | String | null 通过 |

**重要提示：**
- 除了 `@NotNull`、`@NotBlank`、`@NotEmpty` 外，其他注解对 null 值都会通过验证
- 如果参数不能为 null，需要同时使用 `@NotNull`

---

## 7. 最佳实践

### 7.1 验证顺序

```java
// 推荐顺序：null 检查 → 类型检查 → 范围检查 → 格式检查
@PathVariable 
@NotNull(message = "ID不能为空")      // 1. null 检查
@Positive(message = "ID必须为正整数")   // 2. 类型/范围检查
Integer id
```

### 7.2 错误消息

- ✅ 好的错误消息：`"项目ID必须为正整数"`
- ❌ 不好的错误消息：`"invalid"`, `"错误"`

### 7.3 分组验证

对于复杂场景，可以使用分组验证：

```java
// 定义验证组
public interface CreateGroup {}
public interface UpdateGroup {}

// DTO 中使用分组
@Data
public class ProjectDTO {
    @Null(groups = CreateGroup.class)
    @NotNull(groups = UpdateGroup.class)
    private Integer id;
    
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String name;
}

// Controller 中指定验证组
@PostMapping
public Result create(@Validated(CreateGroup.class) @RequestBody ProjectDTO dto) {
    // ...
}

@PutMapping
public Result update(@Validated(UpdateGroup.class) @RequestBody ProjectDTO dto) {
    // ...
}
```

### 7.4 自定义验证注解

```java
// 定义注解
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProjectStatusValidator.class)
public @interface ValidProjectStatus {
    String message() default "项目状态不合法";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 实现验证器
public class ProjectStatusValidator implements ConstraintValidator<ValidProjectStatus, String> {
    private static final Set<String> VALID_STATUS = Set.of("进行中", "已完成", "已暂停", "计划中");
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || VALID_STATUS.contains(value);
    }
}

// 使用
@ValidProjectStatus(message = "项目状态必须为：进行中、已完成、已暂停、计划中")
@RequestParam(required = false) String status
```

---

## 8. 测试示例

### 8.1 测试验证失败场景

```bash
# 测试负数ID（应该失败）
curl "http://localhost:8080/project/-1"

# 响应：
{
  "code": 400,
  "message": "参数验证失败: 项目ID必须为正整数"
}

# 测试页码为0（应该失败）
curl "http://localhost:8080/project?page=0&pageSize=10"

# 响应：
{
  "code": 400,
  "message": "参数验证失败: 页码必须大于等于1"
}
```

### 8.2 测试验证成功场景

```bash
# 正常请求（应该成功）
curl "http://localhost:8080/project/1"

# 响应：
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "project": { ... }
  }
}
```

---

## 9. 常见问题

### Q1: @Validated 和 @Valid 的区别？

**@Valid（JSR-303 标准）：**
- 只能用于 `@RequestBody` 参数
- 支持嵌套验证
- 不支持分组验证

**@Validated（Spring 扩展）：**
- 可以用于类级别，启用 `@PathVariable` 和 `@RequestParam` 验证
- 支持分组验证
- Spring 特有功能

### Q2: 为什么验证不生效？

**检查清单：**
1. Controller 类是否添加了 `@Validated` 注解？
2. 是否导入了正确的包（`jakarta.validation.constraints.*`）？
3. 验证注解是否放在参数前面？
4. 全局异常处理器是否配置了 `ConstraintViolationException`？

### Q3: 如何验证可选参数？

```java
// 方式1：使用 required = false，验证注解仍然生效
@Positive(message = "ID必须为正整数")  // null 值会通过，非 null 值会验证
@RequestParam(required = false) Integer id

// 方式2：如果需要同时验证 null
@NotNull(message = "ID不能为空")
@Positive(message = "ID必须为正整数")
@RequestParam Integer id  // required 默认为 true
```

---

## 10. 更新日志

- **2024-11-28**: 创建参数验证指南，添加 ProjectController 完整验证示例


