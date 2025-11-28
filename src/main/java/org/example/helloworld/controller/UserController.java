package org.example.helloworld.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.example.helloworld.dto.LoginDTO;
import org.example.helloworld.dto.RegisterDTO;
import org.example.helloworld.entity.UserEntity;
import org.example.helloworld.service.UserService;
import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 只依赖 Service 层，不直接使用 Mapper
 */
@Tag(name = "用户管理", description = "用户相关接口：登录、注册、CRUD")
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * 
     * @param loginDTO 登录请求 DTO（自动进行参数验证）
     * @return 返回 token 和用户信息
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT Token")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
        // 调用 service 层进行登录校验（参数验证已在 Controller 层完成）
        String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 登录成功，返回 token
        return Result.ok()
                .message("登录成功")
                .data("token", token)
                .data("username", loginDTO.getUsername());
    }

    /**
     * 获取当前登录用户信息
     * 拦截器已验证 Token，直接从请求中获取用户ID
     * 
     * @param request HTTP 请求对象
     * @return 返回用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "根据Token获取当前登录用户的信息")
    @GetMapping("/info")
    public Result info(jakarta.servlet.http.HttpServletRequest request) {
        // 从请求属性中获取用户ID（由拦截器设置）
        Integer userId = (Integer) request.getAttribute("userId");

        if (userId == null) {
            return Result.error(500).message("获取用户信息失败");
        }

        // 根据用户ID查询用户信息
        UserEntity user = userService.getById(userId);
        if (user == null) {
            return Result.error(404).message("用户不存在");
        }

        // 设置默认头像（业务逻辑）
        String defaultAvatar = "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg";
        user.setAvatar(defaultAvatar);

        // 不返回密码
        user.setPassword(null);

        return Result.ok()
                .data("username", user.getUsername())
                .data("id", user.getId())
                .data("avatar", user.getAvatar());
    }

    /**
     * 用户注册
     * 
     * @param registerDTO 注册请求 DTO（自动进行参数验证）
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 转换为 UserEntity
        UserEntity user = new UserEntity();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());

        // 调用 service 层进行注册（参数验证已在 Controller 层完成）
        boolean success = userService.register(user);
        if (success) {
            return Result.ok().message("注册成功");
        } else {
            return Result.error(500).message("注册失败");
        }
    }

    // ==================== CRUD 接口 ====================

    /**
     * 条件查询用户
     * 
     * @param username 用户名（可选）
     * @return 用户列表
     */
    @GetMapping("/findByUsername")
    public Result findByUsername(@RequestParam(value = "username", required = false) String username) {
        // 业务逻辑（构建查询条件）交给 Service 层处理
        List<UserEntity> users = userService.findByUsername(username);
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
        boolean success = userService.save(user);
        if (success) {
            return Result.ok()
                    .message("插入成功")
                    .data("id", user.getId());
        } else {
            return Result.error(500).message("插入失败");
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
        UserEntity user = userService.getById(id);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            return Result.ok().data("user", user);
        } else {
            return Result.error(404).message("用户不存在");
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
        boolean success = userService.removeById(id);
        if (success) {
            return Result.ok().message("删除成功");
        } else {
            return Result.error(500).message("删除失败");
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
        boolean success = userService.updateById(user);
        if (success) {
            return Result.ok().message("更新成功");
        } else {
            return Result.error(500).message("更新失败");
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
        IPage<UserEntity> userPage = userService.page(pageParam);

        return Result.ok()
                .data("total", userPage.getTotal())
                .data("pages", userPage.getPages())
                .data("records", userPage.getRecords());
    }
}
