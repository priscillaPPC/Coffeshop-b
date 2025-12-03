package cl.duoc.coffeeshop.coffeeshop_api.security.model;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class FirebaseAuthentication extends AbstractAuthenticationToken {

    private final FirebaseToken firebaseToken;

    public FirebaseAuthentication(FirebaseToken firebaseToken) {
        super(createAuthorities(firebaseToken));
        this.firebaseToken = firebaseToken;
        setAuthenticated(true); // Token fue validado
    }

    // Método CRÍTICO: Asigna el rol por defecto si no hay claims personalizados
    private static Collection<GrantedAuthority> createAuthorities(FirebaseToken token) {
        // Asigna "USER" si el token no tiene un claim "role" (Evita el 403 Forbidden)
        // Esto es necesario para que Spring Security asigne el rol ROLE_USER.
        String role = (String) token.getClaims().getOrDefault("role", "USER");

        // Spring Security requiere el prefijo "ROLE_"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return firebaseToken.getUid();
    }

    public String getEmail() {
        return firebaseToken.getEmail();
    }
}