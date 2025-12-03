package cl.duoc.coffeeshop.coffeeshop_api.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        try {
            // Carga el archivo de clave de servicio desde src/main/resources
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("🔥 Firebase Admin SDK inicializado correctamente.");

        } catch (Exception e) {
            System.err.println("❌ ERROR al inicializar Firebase Admin SDK: " + e.getMessage());
            // Si Firebase falla al iniciar, lanzamos una excepción para detener la aplicación.
            throw new IOException("No se pudo iniciar Firebase. Verifique firebase-service-account.json", e);
        }
    }
}