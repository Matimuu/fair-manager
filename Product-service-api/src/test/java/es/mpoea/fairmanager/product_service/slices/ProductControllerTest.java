package es.mpoea.fairmanager.product_service.slices;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.product_service.api.controllers.ProductController;
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

import java.util.UUID;

import static java.time.Instant.now;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
