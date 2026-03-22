package br.com.nexus.change.commons.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Nexus Change API",
                        email = "contact@nexus.com.br",
                        url = "https://nexus.com.br"
                ),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification - SelectGearMotors Transaction API",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://www.nexus.com.br"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Default ENV",
                        url = "${swagger.api.url}"  // Use o valor da propriedade
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    @Value("${swagger.api.url}")
    private String apiUrl;
}
