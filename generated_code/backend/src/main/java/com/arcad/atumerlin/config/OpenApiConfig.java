package com.arcad.atumerlin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI atuMerlinOpenApi() {
        return new OpenAPI().info(new Info()
                .title("ATU Merlin API")
                .description("REST API migrated from the ATU Merlin IBM i (AS/400) application. "
                        + "Provides articles, families, countries, VAT rates, customers, providers, "
                        + "parameters and customer orders.")
                .version("1.0.0")
                .contact(new Contact().name("ATU Merlin Modernization"))
                .license(new License().name("Proprietary")));
    }
}
