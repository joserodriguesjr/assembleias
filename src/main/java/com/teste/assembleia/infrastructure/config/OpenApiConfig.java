package com.teste.assembleia.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Votações em Assembleias",
                version = "v1.0",
                description = "API REST para gerenciar o sistema de votação de pautas em assembleias de associados."
        )
)
public class OpenApiConfig {
}