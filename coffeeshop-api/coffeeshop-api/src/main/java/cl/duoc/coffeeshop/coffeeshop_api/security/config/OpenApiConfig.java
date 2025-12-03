package cl.duoc.coffeeshop.coffeeshop_api.security.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "BearerAuth", // 👈 Nombre de referencia para usar en los Controllers (IMPORTANTE)
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer" // El prefijo "Bearer "
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API CoffeShop - Gestión de Productos")
                        .version("v1.0")
                        .description("Documentación de API REST para la Ev3. Rutas bajo /api/v1/products."));
    }
}