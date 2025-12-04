package org.example.helloworld.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 配置类
 * 
 * 配置 LocalDateTime 的序列化和反序列化格式
 */
@Configuration
public class LocalDateTimeConfig {

  private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * 自定义 LocalDateTime 的序列化和反序列化格式
   * 
   * 注意：这个 Customizer 会在 JacksonConfig 之前应用
   * 
   * @return Jackson2ObjectMapperBuilderCustomizer
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    return builder -> {
      builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
      builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
    };
  }
}