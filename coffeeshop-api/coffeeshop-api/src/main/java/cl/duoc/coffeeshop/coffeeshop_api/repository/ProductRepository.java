package cl.duoc.coffeeshop.coffeeshop_api.repository;

import cl.duoc.coffeeshop.coffeeshop_api.model.Product; // Importa la Entity correcta
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedFalse();
}