package cl.duoc.coffeeshop.coffeeshop_api.service;

import cl.duoc.coffeeshop.coffeeshop_api.model.Product; // 👈 IMPORTACIÓN CORREGIDA
import cl.duoc.coffeeshop.coffeeshop_api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAllActive() {
        // Usa el método del repositorio para obtener solo productos NO eliminados
        return productRepository.findByDeletedFalse();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        // Implementación de borrado LÓGICO (setea deleted = true)
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setDeleted(true);
            productRepository.save(product);
        }
    }
}