package es.mpoea.fairmanager.product_service.api.controllers;

import es.mpoea.fairmanager.product_service.api.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//TODO: Implement all endpoints and return appropriate response.

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public void createProduct() {

    }

    @PutMapping()
    public void updateProduct() {

    }

    @DeleteMapping()
    public void deleteProduct() {

    }

    @GetMapping()
    public void getAllProducts() {

    }

    @GetMapping("/{id}")
    public void getProductById(@PathVariable long id) {

    }

}
