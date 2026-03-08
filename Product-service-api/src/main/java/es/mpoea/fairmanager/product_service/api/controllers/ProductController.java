package es.mpoea.fairmanager.product_service.api.controllers;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.product_service.api.mappers.ProductMapper;
import es.mpoea.fairmanager.product_service.api.services.ProductService;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

//TODO: Implement all endpoints and return appropriate response.

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest productDTO, UriComponentsBuilder uriComponentsBuilder) {

        Product createdProduct = productService.createProduct(productDTO);

        URI location = uriComponentsBuilder
                .path("/api/v1/product/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        ProductResponse productResponse = productMapper.toProductResponse(createdProduct);

        return ResponseEntity.created(location).body(productResponse);
    }

    @PutMapping()
    public void updateProduct() {
//        TODO
    }

    @DeleteMapping()
    public void deleteProduct() {
//        TODO
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<Product> products = productService.getAllProducts();

        List<ProductResponse> productResponses = new LinkedList<>(
                products.stream().map(productMapper::toProductResponse).toList()
        );

        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{id}")
    public void getProductById(@PathVariable long id) {
//        TODO
    }
}
//WARN: Вопрос на полях, лучше оставлять мэппер в сервисе или контроллере ?