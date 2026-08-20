package br.com.ferrickharmony.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ferrick Harmony API")
                        .description("API for managing clinic, patients, professionals, and appointments.")
                        .version("v1.0.0"));
        }

}
