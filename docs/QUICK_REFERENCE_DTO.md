# DTO 模式快速参考

## 🚀 快速开始

### 步骤 1：创建 DTO 类

```java
// dto/CreateXxxDTO.java
@Data
public class CreateXxxDTO {
    @NotBlank(message = "名称不能为空")
    private String name;
    
    // 其他必填字段
}

// dto/UpdateXxxDTO.java
@Data
public class UpdateXxxDTO {
    private String name;  // 所有字段都是可选的
    // 其他可更新字段
}
```

### 步骤 2：修改 Service 接口

```java
public interface XxxService extends IService<XxxEntity> {
    
    /**
     * 创建
     */
    XxxEntity createXxx(CreateXxxDTO dto);
    
    /**
     * 更新（只更新传入的字段）
     */
    boolean updateXxx(Integer id, UpdateXxxDTO dto);
}
```

### 步骤 3：实现 Service

```java
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxEntity> 
        implements XxxService {

    @Override
    public XxxEntity createXxx(CreateXxxDTO dto) {
        // DTO 转 Entity
        XxxEntity entity = new XxxEntity();
        entity.setName(dto.getName());
        // ... 设置其他字段
        entity.setCreateTime(LocalDateTime.now());
        
        // 保存
        super.save(entity);
        return entity;
    }

    @Override
    public boolean updateXxx(Integer id, UpdateXxxDTO dto) {
        // 检查是否存在
        if (baseMapper.selectById(id) == null) {
            throw new RuntimeException("资源不存在");
        }

        // 精确更新
        LambdaUpdateWrapper<XxxEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(XxxEntity::getId, id);
        
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            wrapper.set(XxxEntity::getName, dto.getName());
        }
        // ... 处理其他字段
        
        return this.update(wrapper);
    }
}
```

### 步骤 4：修改 Controller

```java
@RestController
@RequestMapping("/xxx")
@Tag(name = "Xxx管理")
public class XxxController {

    @Autowired
    private XxxService xxxService;

    // 查询列表
    @GetMapping
    public Result findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<XxxEntity> result = xxxService.page(new Page<>(page, pageSize));
        return Result.ok()
                .data("total", result.getTotal())
                .data("records", result.getRecords());
    }

    // 查询详情
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        XxxEntity entity = xxxService.getById(id);
        if (entity == null) {
            return Result.error().message("资源不存在");
        }
        return Result.ok().data("data", entity);
    }

    // 创建
    @PostMapping
    public Result create(@Valid @RequestBody CreateXxxDTO dto) {
        try {
            XxxEntity entity = xxxService.createXxx(dto);
            return Result.ok()
                    .message("创建成功")
                    .data("id", entity.getId());
        } catch (Exception e) {
            return Result.error().message("创建失败: " + e.getMessage());
        }
    }

    // 更新
    @PatchMapping("/{id}")
    public Result update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateXxxDTO dto) {
        try {
            xxxService.updateXxx(id, dto);
            return Result.ok().message("更新成功");
        } catch (Exception e) {
            return Result.error().message(e.getMessage());
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (xxxService.getById(id) == null) {
            return Result.error().message("资源不存在");
        }
        xxxService.removeById(id);
        return Result.ok().message("删除成功");
    }
}
```

## 📋 常用验证注解

```java
@NotNull(message = "不能为null")
private Integer id;

@NotBlank(message = "不能为空字符串")
private String name;

@NotEmpty(message = "集合不能为空")
private List<String> tags;

@Size(min = 2, max = 50, message = "长度必须在2-50之间")
private String name;

@Min(value = 0, message = "最小值为0")
private Integer age;

@Max(value = 100, message = "最大值为100")
private Integer score;

@Email(message = "邮箱格式不正确")
private String email;

@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
private String phone;
```

## 🎯 LambdaUpdateWrapper 常用操作

```java
LambdaUpdateWrapper<Entity> wrapper = new LambdaUpdateWrapper<>();

// 设置条件
wrapper.eq(Entity::getId, id);

// 设置要更新的字段
wrapper.set(Entity::getName, "新名称");
wrapper.set(Entity::getStatus, "已完成");

// 执行更新
this.update(wrapper);
```

## ✅ 检查清单

创建新模块时，确保：

- [ ] 创建 `CreateXxxDTO`（必填字段加 `@NotBlank`）
- [ ] 创建 `UpdateXxxDTO`（所有字段可选）
- [ ] Service 方法使用 DTO 作为参数
- [ ] Service 实现使用 `LambdaUpdateWrapper` 精确更新
- [ ] Controller 使用 `@Valid` 验证参数
- [ ] Controller 使用 `PATCH` 而不是 `PUT`
- [ ] 添加 Swagger 注解
- [ ] 检查资源是否存在
- [ ] 异常处理

## 🔗 完整示例

参考 `ProjectController.java` 及相关文件。

## 📚 相关文档

- [架构最佳实践](./ARCHITECTURE_BEST_PRACTICES.md)
- [重构总结](./PROJECT_REFACTORING_SUMMARY.md)

