package org.example.helloworld.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.helloworld.entity.UserEntity;
import org.example.helloworld.exception.BusinessException;
import org.example.helloworld.mapper.UserMapper;
import org.example.helloworld.service.UserService;
import org.example.helloworld.utils.BusinessCode;
import org.example.helloworld.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户服务实现类
 * 继承 ServiceImpl 获得 MyBatis-Plus 提供的 CRUD 方法
 * ServiceImpl<Mapper, Entity>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

  /**
   * 用户登录
   * 
   * @param username 用户名（已在 Controller 层验证，这里作为防御性检查）
   * @param password 密码（已在 Controller 层验证，这里作为防御性检查）
   * @return 登录成功返回 token，失败抛出异常
   */
  @Override
  public String login(String username, String password) {
    // 查询用户（使用 baseMapper 或 this.getOne()）
    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    UserEntity user = this.getOne(queryWrapper);

    // 验证用户
    if (user == null) {
      // 不暴露用户是否存在，避免用户枚举攻击
      throw new BusinessException(BusinessCode.LOGIN_FAILED);
    }

    // 验证密码（这里简单比较，实际项目中应该使用加密后的密码比较）
    // 如果数据库存储的是加密密码，需要将输入的密码加密后再比较
    if (!password.equals(user.getPassword())) {
      throw new BusinessException(BusinessCode.LOGIN_FAILED);
    }

    // 生成 token（subject 存储用户ID，username 作为附加信息）
    String token = JwtUtil.generateToken(user.getId(), user.getUsername());
    return token;
  }

  /**
   * 根据用户名查询用户
   * 
   * @param username 用户名
   * @return 用户实体
   */
  @Override
  public UserEntity getUserByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("用户名不能为空");
    }

    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    UserEntity user = this.getOne(queryWrapper);

    if (user != null) {
      // 不返回密码字段
      user.setPassword(null);
    }
    return user;
  }

  /**
   * 获取用户信息（包含业务逻辑，如默认头像）
   * 
   * @param username 用户名
   * @return 用户信息
   */
  @Override
  public UserEntity getUserInfo(String username) {
    UserEntity user = getUserByUsername(username);

    // 设置默认头像（业务逻辑）
    String defaultAvatar = "https://aisearch.cdn.bcebos.com/homepage/dashboard/ai_picture_create/04.jpg";
    user.setAvatar(defaultAvatar);

    return user;
  }

  /**
   * 条件查询用户（按用户名）
   * 
   * @param username 用户名（可为空）
   * @return 用户列表
   */
  @Override
  public java.util.List<UserEntity> findByUsername(String username) {
    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();

    // 如果用户名不为空，添加查询条件
    if (username != null && !username.trim().isEmpty()) {
      queryWrapper.eq("username", username);
    }

    return this.list(queryWrapper);
  }

  /**
   * 验证 Token 是否有效
   * 
   * @param token JWT Token
   * @return true 表示有效，false 表示无效
   */
  @Override
  public boolean validateToken(String token) {
    if (token == null || token.trim().isEmpty()) {
      return false;
    }
    return JwtUtil.validateToken(token);
  }

  /**
   * 从 Token 中获取用户ID
   * 
   * @param token JWT Token
   * @return 用户ID
   */
  @Override
  public Integer getUserIdFromToken(String token) {
    if (token == null || token.trim().isEmpty()) {
      throw new IllegalArgumentException("Token 不能为空");
    }

    try {
      return JwtUtil.getUserIdFromToken(token);
    } catch (Exception e) {
      throw new IllegalArgumentException("Token 无效或已过期: " + e.getMessage());
    }
  }

  /**
   * 从 Token 中获取用户名
   * 
   * @param token JWT Token
   * @return 用户名
   */
  @Override
  public String getUsernameFromToken(String token) {
    if (token == null || token.trim().isEmpty()) {
      throw new IllegalArgumentException("Token 不能为空");
    }

    try {
      return JwtUtil.getUsernameFromToken(token);
    } catch (Exception e) {
      throw new IllegalArgumentException("Token 无效或已过期: " + e.getMessage());
    }
  }

  /**
   * 用户注册
   * 
   * @param user 用户实体（已在 Controller 层验证基本参数，这里作为防御性检查）
   * @return 注册成功返回 true，失败返回 false
   */
  @Override
  public boolean register(UserEntity user) {
    // 防御性参数校验（Controller 层已使用 @Valid 验证，这里作为额外保护）
    if (user == null) {
      throw new IllegalArgumentException("用户信息不能为空");
    }
    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("用户名不能为空");
    }
    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("密码不能为空");
    }

    // 检查用户名是否已存在（业务逻辑错误，使用 BusinessException）
    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", user.getUsername());
    long count = this.count(queryWrapper);
    if (count > 0) {
      throw new BusinessException(BusinessCode.USERNAME_ALREADY_EXISTS);
    }

    // 密码加密（可选，实际项目中建议使用 BCrypt 等加密方式）
    // user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

    // 插入用户（使用 MyBatis-Plus 的 save 方法）
    return this.save(user);
  }
}
