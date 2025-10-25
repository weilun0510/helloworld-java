package org.example.helloworld.config;

import org.example.helloworld.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addInterceptors(InterceptorRegistry registry) {
        // 不传路径，则拦截所有
         registry.addInterceptor(new LoginInterceptor()).addPathPatterns();

        // 拦截user下的所有
//        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/user/**");
    }
}
