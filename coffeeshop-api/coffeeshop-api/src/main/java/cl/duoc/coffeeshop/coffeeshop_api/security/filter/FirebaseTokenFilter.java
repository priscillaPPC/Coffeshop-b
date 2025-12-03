package cl.duoc.coffeeshop.coffeeshop_api.security.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import cl.duoc.coffeeshop.coffeeshop_api.security.model.FirebaseAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FirebaseTokenFilter extends OncePerRequestFilter {

    // 🔥 IMPORTANTE: La inicialización de FirebaseAuth debe ser eliminada de aquí
    // y llamada dentro de doFilterInternal para evitar el error de inicialización (orden).

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Rutas públicas de Swagger
        if (request.getRequestURI().startsWith("/swagger-ui") || request.getRequestURI().startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Verificar el encabezado Authorization
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extraer y validar el token
            String token = header.substring(7);

            // CORRECCIÓN CLAVE: Obtener la instancia de FirebaseAuth aquí,
            // asegurando que FirebaseConfig ya se haya ejecutado.
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);

            // 4. Crear objeto de autenticación
            FirebaseAuthentication authentication = new FirebaseAuthentication(firebaseToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // ❌ Si el token es inválido o expiró
            System.err.println("Token Validation Failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }
}