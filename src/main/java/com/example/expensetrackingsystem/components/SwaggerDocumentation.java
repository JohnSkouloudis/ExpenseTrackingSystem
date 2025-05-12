package com.example.expensetrackingsystem.components;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerDocumentation {

    @Bean
    public OpenAPI openAPI() {

       return new OpenAPI().info(new Info().title("Expense Tracking System").version("1.0")
                .description("API documentation for the Expense Tracking System")
                .version("1.0")
                .contact(new Contact().name("John Skouloudis").url("https://github.com/JohnSkouloudis/ExpenseTrackingSystem"))
                .license(new License().name("License of API").url("https://swagger.io/license/"))
        );


    }



}
