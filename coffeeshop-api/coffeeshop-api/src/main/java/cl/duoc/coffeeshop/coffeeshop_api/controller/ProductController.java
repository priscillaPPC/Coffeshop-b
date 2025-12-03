package cl.duoc.coffeeshop.coffeeshop_api.controller;

import cl.duoc.coffeeshop.coffeeshop_api.model.Product;
import cl.duoc.coffeeshop.coffeeshop_api.service.ProductService; // 👈 Apunta al paquete 'service'
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products") // Versión v1 añadida
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET: Listar todos los productos
    @Operation(
            summary = "Lista todos los productos activos",
            description = "Retorna una lista de todos los productos disponibles (no eliminados lógicamente).",
            security = { @SecurityRequirement(name = "BearerAuth") } // Candado de seguridad
    )
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.findAllActive(), HttpStatus.OK);
    }

    // POST: Crear un nuevo producto
    @Operation(
            summary = "Crear un nuevo producto",
            description = "Requiere el rol ADMIN. Crea un producto en el catálogo.",
            security = { @SecurityRequirement(name = "BearerAuth") } // Candado de seguridad
    )
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto
    @Operation(
            summary = "Actualizar producto por ID",
            description = "Requiere el rol ADMIN. Actualiza los detalles de un producto existente.",
            security = { @SecurityRequirement(name = "BearerAuth") } // Candado de seguridad
    )
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        // ... (Tu lógica de actualización)
        return productService.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setCategory(productDetails.getCategory());
            return new ResponseEntity<>(productService.save(product), HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE: Borrado LÓGICO de producto
    @Operation(
            summary = "Borrado Lógico de producto por ID",
            description = "Requiere el rol ADMIN. Marca el producto como eliminado (deleted=true).",
            security = { @SecurityRequirement(name = "BearerAuth") } // Candado de seguridad
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}