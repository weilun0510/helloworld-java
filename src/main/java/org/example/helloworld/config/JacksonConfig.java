package org.example.helloworld.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson 配置类
 * 
 * 配置 Jackson 的行为，包括：
 * 1. 禁用类型强制转换（严格类型检查）
 * 2. 其他 JSON 序列化/反序列化配置
 */
@Configuration
public class JacksonConfig {

  /**
   * 自定义 ObjectMapper 配置
   * 
   * 主要配置：
   * - 禁用 Number -> String 的自动转换
   * - 禁用 String -> Number 的自动转换
   * - 禁用其他类型的自动强制转换
   * 
   * @param builder Jackson2ObjectMapperBuilder
   * @return ObjectMapper
   */
  @Bean
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper objectMapper = builder.build();

    // 获取 CoercionConfig 并禁用类型强制转换
    // 对于 String 类型，不接受任何强制转换
    objectMapper.coercionConfigFor(LogicalType.Textual)
        .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
        .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
        .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);

    // 对于 Number 类型，不接受 String 强制转换
    objectMapper.coercionConfigFor(LogicalType.Integer)
        .setCoercion(CoercionInputShape.String, CoercionAction.Fail);

    objectMapper.coercionConfigFor(LogicalType.Float)
        .setCoercion(CoercionInputShape.String, CoercionAction.Fail);

    // 对于 Boolean 类型，不接受 String 强制转换
    objectMapper.coercionConfigFor(LogicalType.Boolean)
        .setCoercion(CoercionInputShape.String, CoercionAction.Fail);

    return objectMapper;
  }
}
