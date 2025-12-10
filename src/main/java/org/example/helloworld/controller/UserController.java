package org.example.helloworld.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.helloworld.dto.LoginDTO;
import org.example.helloworld.dto.RegisterDTO;
import org.example.helloworld.entity.UserEntity;
import org.example.helloworld.service.UserService;
import org.example.helloworld.utils.BusinessCode;
import org.example.helloworld.utils.JwtUtil;
import org.example.helloworld.utils.Result;
import org.example.helloworld.vo.LoginVO;
import org.example.helloworld.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 只依赖 Service 层，不直接使用 Mapper
 */
@Slf4j
@Tag(name = "用户管理")
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
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT Token", responses = {
            @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = LoginResult.class)))
    })
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 调用 service 层进行登录校验（参数验证已在 Controller 层完成）
        String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 从 token 中获取用户ID
        Integer userId = JwtUtil.getUserIdFromToken(token);

        // 构建响应 VO
        LoginVO loginVO = LoginVO.builder()
                .token(token)
                .username(loginDTO.getUsername())
                .userId(userId)
                .build();

        return Result.ok("登录成功", loginVO);
    }

    /**
     * 获取当前登录用户信息
     * 拦截器已验证 Token，直接从请求中获取用户ID
     * 
     * @param request HTTP 请求对象
     * @return 返回用户信息
     */
    @Operation(summary = "用户详情", description = "根据Token获取当前登录用户的信息", responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = UserInfoResult.class)))
    })
    @GetMapping("/info")
    public Result<UserInfoVO> info(jakarta.servlet.http.HttpServletRequest request) {
        // 从请求属性中获取用户ID（由拦截器设置）
        Integer userId = (Integer) request.getAttribute("userId");

        if (userId == null) {
            return Result.fail(BusinessCode.INTERNAL_ERROR, "获取用户信息失败");
        }

        // 根据用户ID查询用户信息
        UserEntity user = userService.getById(userId);
        if (user == null) {
            return Result.fail(BusinessCode.USER_NOT_FOUND);
        }

        // 设置默认头像（业务逻辑）
        String defaultAvatar = "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg";

        // 构建响应 VO
        UserInfoVO userInfoVO = UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(defaultAvatar)
                .build();

        return Result.ok(userInfoVO);
    }

    /**
     * 用户注册
     * 
     * @param registerDTO 注册请求 DTO（自动进行参数验证）
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 转换为 UserEntity
        UserEntity user = new UserEntity();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());

        // 调用 service 层进行注册（参数验证已在 Controller 层完成）
        boolean success = userService.register(user);
        if (success) {
            return Result.ok("注册成功", null);
        } else {
            return Result.fail(BusinessCode.OPERATION_FAILED, "注册失败");
        }
    }

    // ==================== 内部类：用于 Swagger 文档 ====================

    /**
     * 登录响应（用于 Swagger 文档展示）
     */
    @Schema(description = "登录响应")
    private static class LoginResult extends Result<LoginVO> {
    }

    /**
     * 用户信息响应（用于 Swagger 文档展示）
     */
    @Schema(description = "用户信息响应")
    private static class UserInfoResult extends Result<UserInfoVO> {
    }
}
