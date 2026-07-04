package com.munevver.rabam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rabamOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rabam Car Service Shop API")
                        .description("Backend API for managing cars, services, service status transitions, audit logs and RabbitMQ domain events.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Munevver Verim")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local/Docker development server")
                ));
    }
}