package cl.duoc.coffeeshop.coffeeshop_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        try {
            // 🔥 CORRECCIÓN CRÍTICA: Usa getResourceAsStream para que Spring busque
            // el archivo dentro del classpath (dentro de la carpeta 'target/classes').
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

            if (serviceAccount == null) {
                // Si la aplicación no encuentra el archivo, lanza una excepción clara.
                throw new FileNotFoundException("firebase-service-account.json no encontrado en el classpath.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Solo inicializa si no ha sido inicializado antes (evita errores en tests).
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("🔥 Firebase Admin SDK inicializado correctamente.");

        } catch (FileNotFoundException e) {
            // Este catch maneja específicamente si el archivo no existe
            System.err.println("❌ ERROR: El archivo de credenciales de Firebase no fue encontrado.");
            System.err.println("❌ Asegúrese de que el archivo se llame 'firebase-service-account.json' y esté en 'src/main/resources'.");
            throw e; // Relanza la excepción para detener el inicio de la aplicación
        } catch (Exception e) {
            System.err.println("❌ ERROR al inicializar Firebase Admin SDK: " + e.getMessage());
            throw new IOException("No se pudo iniciar Firebase. Verifique la configuración.", e);
        }
    }
}