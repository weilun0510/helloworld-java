package org.example.helloworld.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置
 * Knife4j 访问地址: http://localhost:8080/doc.html
 * Swagger UI 访问地址: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置 OpenAPI 基本信息和安全认证
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("管理系统接口文档")
                        .description("管理系统接口文档")
                        .version("v1.0")
                        .contact(new Contact().name("CWL").url("")))
                // JWT 安全认证配置
                .components(new Components()
                        .addSecuritySchemes("Bearer认证", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("请输入 JWT Token，格式: Bearer {token}")))
                // 全局应用安全认证
                .addSecurityItem(new SecurityRequirement().addList("Bearer认证"));
    }

    /**
     * 用户管理模块
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("1. 用户管理")
                .pathsToMatch("/user/**")
                .build();
    }

    /**
     * 订单管理模块
     */
    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("2. 订单管理")
                .pathsToMatch("/order/**")
                .build();
    }

    /**
     * 文件管理模块
     */
    @Bean
    public GroupedOpenApi fileApi() {
        return GroupedOpenApi.builder()
                .group("3. 文件管理")
                .pathsToMatch("/file/**")
                .build();
    }

    /**
     * 所有接口
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("0. 所有接口")
                .pathsToMatch("/**")
                .build();
    }
}