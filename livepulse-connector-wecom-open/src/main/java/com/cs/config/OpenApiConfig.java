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
 * @TODO: 企业微信连接器 OpenAPI 配置
 * @Date: 2026/4/14
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("企业微信连接器接口文档")
                        .version("1.0")
                        .description("企业微信连接器 API 文档")
                        .contact(new Contact()
                                .name("创源无限开发者团队")
                                .email("wending@creasourse.com")
                                .url("https://www.livepulse.com.cn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}