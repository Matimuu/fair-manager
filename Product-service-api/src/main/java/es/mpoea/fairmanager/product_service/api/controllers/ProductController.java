package es.mpoea.fairmanager.product_service.api.controllers;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.UpdateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.product_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.product_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.product_service.api.mappers.ProductMapper;
import es.mpoea.fairmanager.product_service.api.services.ProductService;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

//TODO: Change implementation from providing DTO to the service layer.

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest productDTO, UriComponentsBuilder uriComponentsBuilder) {

        CreateProductCommand command = productMapper.toCreateProductCommand(productDTO);
        Product createdProduct = productService.createProduct(command);

        URI location = uriComponentsBuilder
                .path("/api/v1/product/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        ProductResponse productResponse = productMapper.toProductResponse(createdProduct);

        return ResponseEntity.created(location).body(productResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") long id, @Valid @RequestBody UpdateProductRequest productDTO) {

        UpdateProductCommand command = productMapper.toUpdateProductCommand(productDTO);
        Product updatedProduct = productService.updateProduct(id, command);

        ProductResponse productResponse = productMapper.toProductResponse(updatedProduct);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);

        return ResponseEntity.ok().build();
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
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long id) {
        return ResponseEntity.ok(productMapper.toProductResponse(productService.getProductById(id)));
    }
}