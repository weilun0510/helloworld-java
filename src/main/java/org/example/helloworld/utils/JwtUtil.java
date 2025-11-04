package org.example.helloworld.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和验证 JWT Token
 * subject 存储用户ID（唯一标识），username 作为附加信息存储在 claims 中
 */
public class JwtUtil {
  /** Token 过期时间：7天 */
  private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

  /** 密钥字符串 - 至少32字节 */
  private static final String SECRET = "secretsecretsecretsecretsecretsecretsecret";

  /** 生成密钥对象 */
  private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

  /** 自定义 Claims 键名 */
  private static final String CLAIM_KEY_USERNAME = "username";

  /**
   * 生成 JWT Token
   * 
   * @param userId   用户ID（作为 subject）
   * @param username 用户名（作为附加信息）
   * @return JWT Token 字符串
   */
  public static String generateToken(Integer userId, String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME, username);

    return Jwts.builder()
        .subject(userId.toString()) // subject 存储用户ID
        .claims(claims) // 附加信息：用户名
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(KEY)
        .compact();
  }

  /**
   * 验证 Token 是否有效
   * 
   * @param token JWT Token
   * @return true 表示有效，false 表示无效
   */
  public static boolean validateToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject() != null;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 从 Token 中提取用户ID
   * 
   * @param token JWT Token
   * @return 用户ID
   */
  public static Integer getUserIdFromToken(String token) {
    String subject = Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
    return Integer.parseInt(subject);
  }

  /**
   * 从 Token 中提取用户名
   * 
   * @param token JWT Token
   * @return 用户名
   */
  public static String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return claims.get(CLAIM_KEY_USERNAME, String.class);
  }
}