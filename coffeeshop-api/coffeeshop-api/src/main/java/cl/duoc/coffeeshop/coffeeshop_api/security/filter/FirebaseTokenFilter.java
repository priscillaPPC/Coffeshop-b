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

    // Lista de rutas que deben IGNORAR la aplicación del filtro (no requiere token)
    private static final String[] EXCLUDED_PATHS = {
            "/api/v1/products", // GET de productos
            "/swagger-ui",
            "/v3/api-docs"
    };

    // Método estándar de Spring para excluir rutas del filtro (NO aplica validación de token a estas rutas)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Excluir todas las peticiones OPTIONS (CORS Preflight)
        if (method.equals("OPTIONS")) {
            return true;
        }

        // 2. Excluir las rutas GET de productos y Swagger (tal como en SecurityConfig)
        if (method.equals("GET")) {
            for (String excludedPath : EXCLUDED_PATHS) {
                // Comprueba si la ruta es la exacta O si empieza con la ruta (para /swagger-ui/index.html, etc.)
                if (path.equals(excludedPath) || path.startsWith(excludedPath + "/")) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Si shouldNotFilter devolvió true, este método ni siquiera se llama.
        // Solo llegamos aquí si se requiere autenticación (ej: POST, PUT, DELETE).

        String header = request.getHeader("Authorization");

        // 2. Verificar el encabezado Authorization
        if (header == null || !header.startsWith("Bearer ")) {
            // Si es una ruta protegida y no hay token, Spring Security bloqueará con 403
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extraer y validar el token
            String token = header.substring(7);

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