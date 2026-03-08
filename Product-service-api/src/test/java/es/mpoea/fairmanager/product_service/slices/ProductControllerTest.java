package es.mpoea.fairmanager.product_service.slices;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.product_service.api.controllers.ProductController;
import es.mpoea.fairmanager.product_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.product_service.api.exceptions.ProductsNotExistsException;
import es.mpoea.fairmanager.product_service.api.mappers.ProductMapper;
import es.mpoea.fairmanager.product_service.api.services.ProductService;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static java.time.Instant.now;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    void postProduct_shouldReturnCreatedStatusAndLocation() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Label", "Description", "Category");

        Product createdProduct = mock(Product.class);

//        Mocking fake product
        when(createdProduct.getId()).thenReturn(1L);


        when(productService.createProduct(request)).thenReturn(createdProduct);
        when(productMapper.toProductResponse(createdProduct)).thenReturn(new ProductResponse(1L, UUID.randomUUID(), "Label", "Description", "Category", now(), now()));


        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/product/1"))
                .andExpect(jsonPath("$.label").value("Label"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.category").value("Category"));

        verify(productService, times(1)).createProduct(request);
        verify(productMapper, times(1)).toProductResponse(any(Product.class));
    }

    @Test
    void postProduct_shouldReturnBadRequestWhenLabelIsBlank() throws Exception {
        CreateProductRequest request = new CreateProductRequest("", "Description", "Category");

        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("One or more fields failed validation. Please check the errors and try again."))
                .andExpect(jsonPath("$.path").value("/api/v1/product"))
                .andExpect(jsonPath("$.errors.label").value("Label is required."));

        verifyNoInteractions(productService, productMapper);
    }

    @Test
    void postProduct_shouldReturnBadRequestWhenCategoryIsNull() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Label", "Description", null);

        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("One or more fields failed validation. Please check the errors and try again."))
                .andExpect(jsonPath("$.path").value("/api/v1/product"))
                .andExpect(jsonPath("$.errors.category").value("Category is required."));

        verifyNoInteractions(productService, productMapper);
    }

    @Test
    void postProduct_shouldReturnBadRequestWhenCategoryIsNullAndLabelIsBlank() throws Exception {
        CreateProductRequest request = new CreateProductRequest("", "Description", null);

        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("One or more fields failed validation. Please check the errors and try again."))
                .andExpect(jsonPath("$.path").value("/api/v1/product"))
                .andExpect(jsonPath("$.errors.category").value("Category is required."))
                .andExpect(jsonPath("$.errors.label").value("Label is required."));

        verifyNoInteractions(productService, productMapper);
    }

    @Test
    void getProducts_shouldReturnOkWithProductList() throws Exception {

        Product product1 = new Product("Label1", "Description1", "Category1");
        Product product2 = new Product("Label2", "Description2", "Category2");

        when(productService.getAllProducts()).thenReturn(List.of(product1, product2));

        when(productMapper.toProductResponse(product1)).thenReturn(new ProductResponse(1L, UUID.randomUUID(), product1.getLabel(), product1.getDescription(), product1.getCategory(), now(), now()));
        when(productMapper.toProductResponse(product2)).thenReturn(new ProductResponse(2L, UUID.randomUUID(), product2.getLabel(), product2.getDescription(), product2.getCategory(), now(), now()));

        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].label").value("Label1"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].category").value("Category1"))
                .andExpect(jsonPath("$[1].label").value("Label2"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].category").value("Category2"));

        verify(productService, times(1)).getAllProducts();
        verify(productMapper, times(2)).toProductResponse(any(Product.class));
    }

    @Test
    void getProducts_shouldThrowProductsNotExistsException() throws Exception {

        when(productService.getAllProducts()).thenThrow(new ProductsNotExistsException());

        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No products found in the database"))
                .andExpect(jsonPath("$.path").value("/api/v1/product"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_shouldReturnOkWithProduct() throws Exception {
        Product product = new Product("Label", "Description", "Category");

        when(productService.getProductById(1L)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(new ProductResponse(1L, UUID.randomUUID(), product.getLabel(), product.getDescription(), product.getCategory(), now(), now()));

        mockMvc.perform(get("/api/v1/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.label").value("Label"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.category").value("Category"));

        verify(productService, times(1)).getProductById(1L);
        verify(productMapper, times(1)).toProductResponse(product);

    }

    @Test
    void getProductById_shouldThrowProductNotFoundException() throws Exception {
        when(productService.getProductById(1L)).thenThrow(new ProductNotFoundException(1L));

        mockMvc.perform(get("/api/v1/product/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/product/1"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void deleteProduct_shouldReturnOk() throws Exception {

        mockMvc.perform(delete("/api/v1/product/1"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void deleteProduct_shouldThrowProductNotFoundException() throws Exception {
        doThrow(new ProductNotFoundException(1L))
                .when(productService)
                .deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/product/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/product/1"));
    }
}
