package com.example.expensetrackingsystem.components;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SwaggerDocumentation {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {

       return new OpenAPI().addSecurityItem(new SecurityRequirement().
                       addList("Bearer Authentication"))
               .components(new Components().addSecuritySchemes
                       ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Expense Tracking System").version("1.0")
                .description("API documentation for the Expense Tracking System")
                .version("1.0")
                .contact(new Contact().name("John Skouloudis").url("https://github.com/JohnSkouloudis/ExpenseTrackingSystem"))
                .license(new License().name("License of API").url("https://swagger.io/license/"))
        );


    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
