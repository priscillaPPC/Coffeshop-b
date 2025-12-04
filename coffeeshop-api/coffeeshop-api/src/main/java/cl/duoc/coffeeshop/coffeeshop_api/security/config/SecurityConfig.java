package cl.duoc.coffeeshop.coffeeshop_api.security.config;

import cl.duoc.coffeeshop.coffeeshop_api.security.filter.FirebaseTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public FirebaseTokenFilter firebaseTokenFilter() {
        return new FirebaseTokenFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, FirebaseTokenFilter firebaseTokenFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // 1. Permitir todas las peticiones OPTIONS (CORS Preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <-- CRÍTICO

                        // 2. Rutas públicas: Swagger y Listar Productos (GET)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll() // <-- CRÍTICO

                        // 3. Rutas protegidas: Requiere rol ADMIN (PUT, POST, DELETE)
                        .requestMatchers("/api/v1/products/**").hasRole("ADMIN")

                        // 4. Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // 5. Añadir filtro personalizado
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}