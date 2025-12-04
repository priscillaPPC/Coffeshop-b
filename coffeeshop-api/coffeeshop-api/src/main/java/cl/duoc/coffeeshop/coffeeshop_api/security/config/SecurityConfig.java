package cl.duoc.coffeeshop.coffeeshop_api.security.config;

import cl.duoc.coffeeshop.coffeeshop_api.security.filter.FirebaseTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Importación necesaria para HttpMethod.GET
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. BEAN DEL FILTRO
    @Bean
    public FirebaseTokenFilter firebaseTokenFilter() {
        return new FirebaseTokenFilter();
    }

    // 2. CADENA DE FILTROS Y REGLAS DE SEGURIDAD
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, FirebaseTokenFilter firebaseTokenFilter) throws Exception {

        http
                // Deshabilitar CSRF (necesario para API REST)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar política de sesión sin estado (STATELESS)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Definición de reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de Swagger (Documentación Ev3)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 🔥 CORRECCIÓN CLAVE: Listar Productos (GET) es PÚBLICO (permitAll)
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()

                        // 2. Crear, Actualizar, Eliminar: Requiere rol ADMIN
                        .requestMatchers("/api/v1/products/**").hasRole("ADMIN")

                        // 3. Cualquier otra ruta (si la hay) requiere autenticación
                        .anyRequest().authenticated()
                )

                // 3. AÑADIR FILTRO
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}