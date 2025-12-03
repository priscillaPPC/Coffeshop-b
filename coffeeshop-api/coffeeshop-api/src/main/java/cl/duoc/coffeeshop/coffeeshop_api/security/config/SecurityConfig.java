package cl.duoc.coffeeshop.coffeeshop_api.security.config; // 👈 PAQUETE CORREGIDO

import cl.duoc.coffeeshop.coffeeshop_api.security.filter.FirebaseTokenFilter; // 👈 IMPORTACIÓN CORREGIDA
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean; // Importación necesaria para el Bean

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. BEAN DEL FILTRO: DEBE CREARSE COMO UN OBJETO DE JAVA SIMPLE
    // Al usar GenericFilterBean como retorno, ayudamos a Spring a resolver el conflicto de tipos.
    // Esto resuelve el error en la línea 54.
    @Bean
    public FirebaseTokenFilter firebaseTokenFilter() {
        return new FirebaseTokenFilter();
    }

    // 2. CADENA DE FILTROS: El filtro se inyecta como parámetro en el método
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

                        // RUTAS PROTEGIDAS DE COFFEESHOP
                        // 1. Listar Productos: Requiere solo estar autenticado (ROLE_USER o ADMIN)
                        .requestMatchers("/api/v1/products").authenticated()

                        // 2. Crear, Actualizar, Eliminar: Requiere rol ADMIN
                        .requestMatchers("/api/v1/products/**").hasRole("ADMIN")

                        // 3. Cualquier otra ruta (si la hay) requiere autenticación
                        .anyRequest().authenticated()
                )

                // 3. AÑADIR FILTRO: Spring sabrá que es un filtro de servlet válido
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}