package org.example.helloworld.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.helloworld.entity.UserEntity;

import java.util.List;

/**
 * 用户服务接口
 * 继承 IService 获得 MyBatis-Plus 提供的 CRUD 方法
 */
public interface UserService extends IService<UserEntity> {

  /**
   * 用户登录
   * 
   * @param username 用户名
   * @param password 密码
   * @return 登录成功返回 token，失败抛出异常
   */
  String login(String username, String password);

  /**
   * 获取用户信息（包含默认头像等业务逻辑）
   * 
   * @param username 用户名
   * @return 用户信息
   */
  UserEntity getUserInfo(String username);

  /**
   * 根据用户名查询用户（不返回敏感信息）
   * 
   * @param username 用户名
   * @return 用户实体
   */
  UserEntity getUserByUsername(String username);

  /**
   * 条件查询用户（按用户名）
   * 
   * @param username 用户名（可为空，为空则查询所有）
   * @return 用户列表
   */
  List<UserEntity> findByUsername(String username);

  /**
   * 验证 Token 是否有效
   * 
   * @param token JWT Token
   * @return true 表示有效，false 表示无效
   */
  boolean validateToken(String token);

  /**
   * 从 Token 中获取用户ID
   * 
   * @param token JWT Token
   * @return 用户ID
   */
  Integer getUserIdFromToken(String token);

  /**
   * 从 Token 中获取用户名
   * 
   * @param token JWT Token
   * @return 用户名
   */
  String getUsernameFromToken(String token);

  /**
   * 用户注册
   * 
   * @param user 用户实体
   * @return 注册成功返回 true，失败返回 false
   */
  boolean register(UserEntity user);
}
