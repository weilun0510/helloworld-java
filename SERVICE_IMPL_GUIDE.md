# ServiceImpl 继承说明文档

## 📋 概述

所有 Service 实现类都继承了 MyBatis-Plus 提供的 `ServiceImpl` 基类，这样可以获得丰富的 CRUD 方法，无需手动编写常用的数据库操作。

## 🏗️ 架构设计

### 1. Service 接口继承 IService

```java
public interface UserService extends IService<UserEntity> {
    // 自定义业务方法
    String login(String username, String password);
    // ...其他业务方法
}
```

### 2. ServiceImpl 实现类继承 ServiceImpl

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    // 实现自定义业务方法
    @Override
    public String login(String username, String password) {
        // 使用继承自 ServiceImpl 的方法
        UserEntity user = this.getOne(queryWrapper);
        // 业务逻辑
    }
}
```

## 🔧 ServiceImpl 提供的常用方法

### 保存操作

- `save(T entity)` - 插入一条记录
- `saveBatch(Collection<T> entityList)` - 批量插入
- `saveOrUpdate(T entity)` - 根据 ID 判断是插入还是更新

### 删除操作

- `removeById(Serializable id)` - 根据 ID 删除
- `removeByIds(Collection<? extends Serializable> idList)` - 批量删除
- `remove(Wrapper<T> queryWrapper)` - 根据条件删除

### 更新操作

- `updateById(T entity)` - 根据 ID 更新
- `update(Wrapper<T> updateWrapper)` - 根据条件更新
- `updateBatchById(Collection<T> entityList)` - 批量更新

### 查询操作

- `getById(Serializable id)` - 根据 ID 查询
- `getOne(Wrapper<T> queryWrapper)` - 根据条件查询一条记录
- `list()` - 查询所有
- `list(Wrapper<T> queryWrapper)` - 根据条件查询列表
- `listByIds(Collection<? extends Serializable> idList)` - 根据 ID 列表查询
- `count()` - 查询总记录数
- `count(Wrapper<T> queryWrapper)` - 根据条件查询记录数

### 分页查询

- `page(IPage<T> page)` - 分页查询所有
- `page(IPage<T> page, Wrapper<T> queryWrapper)` - 分页条件查询

## 📝 实际应用示例

### UserServiceImpl 示例

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public String login(String username, String password) {
        // 使用 this.getOne() 方法，继承自 ServiceImpl
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UserEntity user = this.getOne(queryWrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return JwtUtil.generateToken(username);
    }

    @Override
    public boolean register(UserEntity user) {
        // 使用 this.count() 方法检查是否存在
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        long count = this.count(queryWrapper);

        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 使用 this.save() 方法保存
        return this.save(user);
    }
}
```

### OrderServiceImpl 示例

```java
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Override
    public List<OrderEntity> getAllOrdersWithUsers() {
        // 使用 baseMapper 访问自定义的 Mapper 方法
        return baseMapper.selectAllOrdersAndUsers();
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(Integer uid) {
        if (uid == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        // 使用 baseMapper 访问自定义的 Mapper 方法
        return baseMapper.selectByUid(uid);
    }
}
```

## 🎯 优势

### 1. 减少重复代码

不需要为每个实体类编写基本的 CRUD 方法，直接继承即可使用。

### 2. 统一的方法签名

所有 Service 都使用相同的方法名和参数，便于维护和理解。

### 3. 支持链式调用

```java
// 链式调用示例
this.lambdaQuery()
    .eq(UserEntity::getUsername, username)
    .one();
```

### 4. 支持批量操作

```java
// 批量保存
List<UserEntity> users = Arrays.asList(user1, user2, user3);
this.saveBatch(users);
```

### 5. 自动填充

配合 MyBatis-Plus 的自动填充功能，可以自动设置创建时间、更新时间等字段。

## 🔄 在 Controller 中使用

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 直接使用 Service 提供的方法
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        UserEntity user = userService.getById(id);  // ← 继承自 ServiceImpl
        if (user != null) {
            user.setPassword(null);
            return Result.ok().data("user", user);
        } else {
            return Result.error().message("用户不存在");
        }
    }

    @GetMapping("/list")
    public Result list() {
        List<UserEntity> users = userService.list();  // ← 继承自 ServiceImpl
        return Result.ok().data("users", users);
    }
}
```

## 📌 注意事项

### 1. 访问自定义 Mapper 方法

使用 `baseMapper` 访问 Mapper 接口中自定义的方法：

```java
baseMapper.selectAllOrdersAndUsers();  // 自定义方法
```

### 2. 使用 this 调用继承的方法

使用 `this` 调用从 ServiceImpl 继承的方法：

```java
this.getOne(queryWrapper);  // 继承的方法
this.save(user);            // 继承的方法
this.count(queryWrapper);   // 继承的方法
```

### 3. 不需要 @Autowired Mapper

继承 ServiceImpl 后，不需要再手动注入 Mapper，可以直接通过 `baseMapper` 访问。

**错误示例：**

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Autowired
    private UserMapper userMapper;  // ❌ 不需要，应该使用 baseMapper
}
```

**正确示例：**

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    // ✅ 直接使用 baseMapper 或 this.xxx() 方法
}
```

## 📊 完整的类结构

```
UserEntity.java (实体类)
    ↓
UserMapper.java (Mapper 接口) extends BaseMapper<UserEntity>
    ↓
UserService.java (Service 接口) extends IService<UserEntity>
    ↓
UserServiceImpl.java (Service 实现) extends ServiceImpl<UserMapper, UserEntity> implements UserService
    ↓
UserController.java (控制器) 注入 UserService
```

## 🚀 总结

通过继承 `ServiceImpl`，我们获得了：

- ✅ 丰富的 CRUD 方法
- ✅ 减少样板代码
- ✅ 统一的编程风格
- ✅ 更好的可维护性
- ✅ 支持链式调用
- ✅ 批量操作支持

这是 MyBatis-Plus 推荐的最佳实践！
