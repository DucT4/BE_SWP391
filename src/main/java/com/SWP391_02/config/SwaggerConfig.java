// src/main/java/com/SWP391_02/config/SwaggerConfig.java
package com.SWP391_02.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("EV Warranty API").version("v1"))
                // thêm requirement để nút Authorize hoạt động
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // khai báo scheme tên "bearerAuth"
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Nhập token dạng: Bearer eyJhbGciOi...")));
    }
}
