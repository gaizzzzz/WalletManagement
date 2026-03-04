package com.example.managewallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI manageWalletOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ManageWallet API")
                        .version("v1")
                        .description("Backend APIs for transaction management and dashboard analytics.")
                        .contact(new Contact().name("ManageWallet")));
    }
}
