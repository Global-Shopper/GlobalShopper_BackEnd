package com.sep490.gshop.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Log4j2
public class SwaggerConfig {
    @Value("${global-shopper.base-url}")
    private String baseURL;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();

        devServer.setUrl(baseURL);
        devServer.setDescription("Server URL in Development environment");

        Contact myContact = new Contact();
        myContact.setName("GShop Company");
        myContact.setEmail("globalshopper@gmail.com");
        log.info("Swagger: {}/swagger-ui/index.html", baseURL);

        Info information = new Info()
                .title("GShop API")
                .version("1.0")
                .description("This Web API is for Global Shopper website.")
                .contact(myContact);

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Authentication");

        return new OpenAPI()
                .info(information)
                .servers(List.of(devServer))
                .components(new Components().addSecuritySchemes("Bearer Authentication", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}