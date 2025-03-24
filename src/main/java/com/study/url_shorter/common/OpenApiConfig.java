package com.study.url_shorter.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("UrlShorter API Document")
                .version("1.0")
                .description(
                        "Url 단축 서비스 API 문서\n")
                .contact(new io.swagger.v3.oas.models.info.Contact().email("jiterin908@gmail.com"));

        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8080"))
//                .addServersItem(new Server().url("https://your-weddy.pe.kr"))
                .info(info);
    }

}
