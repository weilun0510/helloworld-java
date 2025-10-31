package org.example.helloworld.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成和验证 JWT Token
 */
public class JwtUtil {
  /** Token 过期时间：7天 */
  private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

  /** 密钥字符串 - 至少32字节 */
  private static final String SECRET = "secretsecretsecretsecretsecretsecretsecret";

  /** 生成密钥对象 */
  private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

  /**
   * 生成 JWT Token
   * 
   * @param username 用户名
   * @return JWT Token 字符串
   */
  public static String generateToken(String username) {
    return Jwts.builder()
        .subject(username)
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
   * 从 Token 中提取用户名
   * 
   * @param token JWT Token
   * @return 用户名
   */
  public static String getUsernameFromToken(String token) {
    return Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }
}