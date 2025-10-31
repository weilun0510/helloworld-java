package org.example.helloworld.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.example.helloworld.entity.UserEntity;
import org.example.helloworld.mapper.UserMapper;
import org.example.helloworld.service.UserService;
import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 用户控制器
 */
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // @GetMapping("")
    // public List<User> query() {
    // List<User> list = userMapper.selectList();
    // // 使用 BaseMapper 提供的内置方法
    // // List<User> list = userMapper.selectList(null);
    // System.out.println(list);
    // return list;
    // }

    // @GetMapping("/findAll")
    // public List<User> findAll() {
    // return userMapper.selectAllUserAndOrders();
    // }

    /**
     * 用户登录
     * 
     * @param user 用户登录信息（用户名和密码）
     * @return 返回 token 和用户信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserEntity user) {
        // 调用 service 层进行登录校验
        String token = userService.login(user.getUsername(), user.getPassword());

        // 登录成功，返回 token
        return Result.ok()
                .message("登录成功")
                .data("token", token)
                .data("username", user.getUsername());
    }

    /**
     * 获取用户信息
     * 
     * @param token JWT Token
     * @return 返回用户信息
     */
    @GetMapping("/info")
    public Result info(@RequestParam("token") String token) {
        // 验证 token
        if (!userService.validateToken(token)) {
            return Result.error().message("Token 无效或已过期");
        }

        // 从 token 中获取用户名
        String username = userService.getUsernameFromToken(token);

        // 查询用户信息
        UserEntity user = userService.getUserByUsername(username);

        // 默认头像
        String avatarUrl = "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg";

        return Result.ok()
                .data("username", user.getUsername())
                .data("id", user.getId())
                .data("avatar", avatarUrl);
    }

    /**
     * 用户注册
     * 
     * @param user 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserEntity user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.ok().message("注册成功");
        } else {
            return Result.error().message("注册失败");
        }
    }

    // ------------------ baseMapper 提供的内置方法 ------------------

    // ------------------ 以下是其他 CRUD 接口 ------------------

    /**
     * 条件查询用户
     * 
     * @param username 用户名（可选）
     * @return 用户列表
     */
    @GetMapping("/findByUsername")
    public Result findByUsername(@RequestParam(value = "username", required = false) String username) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.eq("username", username);
        }
        List<UserEntity> users = userMapper.selectList(queryWrapper);
        return Result.ok().data("users", users);
    }

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 创建结果
     */
    @PostMapping("")
    public Result save(@RequestBody UserEntity user) {
        int i = userMapper.insert(user);
        if (i > 0) {
            return Result.ok()
                    .message("插入成功")
                    .data("id", user.getId());
        } else {
            return Result.error().message("插入失败");
        }
    }

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        UserEntity user = userMapper.selectById(id);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            return Result.ok().data("user", user);
        } else {
            return Result.error().message("用户不存在");
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = userMapper.deleteById(id);
        if (i > 0) {
            return Result.ok().message("删除成功");
        } else {
            return Result.error().message("删除失败");
        }
    }

    /**
     * 更新用户信息
     * 
     * @param id   用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody UserEntity user) {
        user.setId(id);
        int result = userMapper.updateById(user);
        if (result > 0) {
            return Result.ok().message("更新成功");
        } else {
            return Result.error().message("更新失败");
        }
    }

    /**
     * 分页查询用户
     * 
     * @param page     页码，默认第1页
     * @param pageSize 每页大小，默认10条
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result page(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<UserEntity> pageParam = new Page<>(page, pageSize);
        IPage<UserEntity> userPage = userMapper.selectPage(pageParam, null);

        return Result.ok()
                .data("total", userPage.getTotal())
                .data("pages", userPage.getPages())
                .data("records", userPage.getRecords());
    }
}
