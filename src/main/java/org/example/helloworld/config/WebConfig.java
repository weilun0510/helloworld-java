package org.example.helloworld.config;

import org.example.helloworld.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 配置拦截器、跨域等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     * 
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                // // 白名单：不需要登录就可以访问的接口
                .excludePathPatterns(
                        // 用户认证相关接口
                        "/user/login", // 登录接口
                        "/user/register", // 注册接口

                        // 测试接口
                        "/hello",
                        "/hello/**",
                        "/getName",
                        "/getName1",
                        "/postTest1",
                        "/postTest2",
                        "/test/**",

                        // 文件上传接口（根据需求可以移除，添加认证）
                        // "/file/upload",
                        // "/file/batch-upload",

                        // Swagger 和 Knife4j 文档接口
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/doc.html",
                        "/favicon.ico",

                        // 静态资源
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",

                        // 错误页面
                        "/error");

        System.out.println("LoginInterceptor 已注册，白名单接口不需要 Token 认证");
    }

    /**
     * 配置跨域访问
     * 
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径跨域
                .allowedOriginPatterns("*") // 允许所有来源（支持携带凭证）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 允许的请求方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带凭证（如 Cookie、Authorization 头）
                .maxAge(3600); // 预检请求的有效期（秒）

        System.out.println("CORS 跨域配置已启用");
    }
}
