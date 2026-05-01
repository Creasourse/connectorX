package com.cs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: OpenApiConfig
 * @Author: wwd
 * @TODO:
 * @Date: 2026/1/30 19:26
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ConnectorX 接口文档")
                        .version("1.0")
                        .description("Spring Boot 3.5.X + Swagger 示例")
                        .contact(new Contact()
                                .name("创源无限开发者团队")
                                .email("wending@creasourse.com")
                                .url("https://www.livepulse.com.cn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
