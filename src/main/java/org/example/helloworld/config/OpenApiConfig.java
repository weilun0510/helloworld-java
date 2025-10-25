package org.example.helloworld.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    // http://localhost:8080/doc.htm
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("管理系统接口文档")
                        .description("管理系统接口文档")
                        .version("v1.0")
                        .contact(new Contact().name("CWL").url(""))
                );
    }
}