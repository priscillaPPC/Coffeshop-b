package cl.duoc.coffeeshop.coffeeshop_api.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica la configuración a todas las rutas de la API
                .allowedOrigins("http://localhost:3000") // Permite al frontend de React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite todos los métodos
                .allowedHeaders("*") // Permite todos los headers, incluyendo el Authorization para Firebase
                .allowCredentials(true);
    }
}
