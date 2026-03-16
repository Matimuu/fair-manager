package es.mpoea.fairmanager.catalog_service.api.controllers;

import es.mpoea.fairmanager.commondata.DTO.requests.product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.product.UpdateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.product.ProductResponse;
import es.mpoea.fairmanager.catalog_service.api.commands.product.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.product.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.mappers.ProductMapper;
import es.mpoea.fairmanager.catalog_service.api.services.ProductService;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest productDTO, UriComponentsBuilder uriComponentsBuilder) {
        CreateProductCommand command = productMapper.toCreateCommand(productDTO);
        Product createdProduct = productService.createProduct(command);

        URI location = uriComponentsBuilder
                .path("/api/v1/products/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        ProductResponse productResponse = productMapper.toResponse(createdProduct);

        return ResponseEntity.created(location).body(productResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") long id, @Valid @RequestBody UpdateProductRequest productDTO) {
        UpdateProductCommand command = productMapper.toUpdateCommand(productDTO);
        Product updatedProduct = productService.updateProduct(id, command);

        ProductResponse productResponse = productMapper.toResponse(updatedProduct);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();

        List<ProductResponse> productResponses = products.stream().map(productMapper::toResponse).toList();

        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long id) {
        return ResponseEntity.ok(productMapper.toResponse(productService.getProductById(id)));
    }
}