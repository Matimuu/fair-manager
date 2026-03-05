package es.mpoea.fairmanager.product_service.api.controllers;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.product_service.api.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: Implement all endpoints and return appropriate response.

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<@NotNull ProductResponse> createProduct(CreateProductRequest createThisProduct) {

        



        return null;
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
