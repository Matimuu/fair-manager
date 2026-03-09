package es.mpoea.fairmanager.product_service.slices;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.UpdateProductRequest;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    private static final UUID sku = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final Instant createdAt = Instant.parse("2026-03-09T10:00:00Z");
    private static final Instant updatedAt = Instant.parse("2026-03-09T10:05:00Z");

    @Test
    void postProduct_shouldReturnCreatedStatusAndLocation() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Label", "Description", "Category");

        Product createdProduct = mock(Product.class);

//        Mocking fake product
        when(createdProduct.getId()).thenReturn(1L);

        when(productService.createProduct(request)).thenReturn(createdProduct);
        when(productMapper.toProductResponse(createdProduct)).thenReturn(new ProductResponse(1L, sku, "Label", "Description", "Category", createdAt, updatedAt));


        mockMvc.perform(post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/product/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value(sku.toString()))
                .andExpect(jsonPath("$.label").value("Label"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.category").value("Category"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$.updatedAt").value(updatedAt.toString()));

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

        when(productMapper.toProductResponse(product1)).thenReturn(new ProductResponse(1L, sku, product1.getLabel(), product1.getDescription(), product1.getCategory(), createdAt, updatedAt));
        when(productMapper.toProductResponse(product2)).thenReturn(new ProductResponse(2L, sku, product2.getLabel(), product2.getDescription(), product2.getCategory(), createdAt, updatedAt));

        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].sku").value(sku.toString()))
                .andExpect(jsonPath("$[0].label").value("Label1"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].category").value("Category1"))
                .andExpect(jsonPath("$[0].createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$[0].updatedAt").value(updatedAt.toString()))

                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].sku").value(sku.toString()))
                .andExpect(jsonPath("$[1].label").value("Label2"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].category").value("Category2"))
                .andExpect(jsonPath("$[1].createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$[1].updatedAt").value(updatedAt.toString()));

        verify(productService, times(1)).getAllProducts();
        verify(productMapper, times(2)).toProductResponse(any(Product.class));
    }

    @Test
    void getProducts_shouldReturnProductsNotExistsException() throws Exception {

        when(productService.getAllProducts()).thenThrow(new ProductsNotExistsException());

        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No products found in the database"))
                .andExpect(jsonPath("$.path").value("/api/v1/product"));

        verify(productService, times(1)).getAllProducts();
        verifyNoInteractions(productMapper);
    }

    @Test
    void getProductById_shouldReturnOkWithProduct() throws Exception {
        Product product = new Product("Label", "Description", "Category");

        when(productService.getProductById(1L)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(new ProductResponse(1L, sku, product.getLabel(), product.getDescription(), product.getCategory(), createdAt, updatedAt));

        mockMvc.perform(get("/api/v1/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value(sku.toString()))
                .andExpect(jsonPath("$.label").value("Label"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.category").value("Category"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$.updatedAt").value(updatedAt.toString()));

        verify(productService, times(1)).getProductById(1L);
        verify(productMapper, times(1)).toProductResponse(product);

    }

    @Test
    void getProductById_shouldReturnProductNotFoundException() throws Exception {
        when(productService.getProductById(1L)).thenThrow(new ProductNotFoundException(1L));

        mockMvc.perform(get("/api/v1/product/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/product/1"));

        verify(productService, times(1)).getProductById(1L);
        verifyNoInteractions(productMapper);
    }

    @Test
    void deleteProduct_shouldReturnOk() throws Exception {

        mockMvc.perform(delete("/api/v1/product/1"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void deleteProduct_shouldReturnProductNotFoundException() throws Exception {
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

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void updateProduct_shouldReturnOkWithUpdatedProduct() throws Exception {
        UpdateProductRequest updateDTO = new UpdateProductRequest("Updated Label", "Updated Description", "Updated Category");
        Product updatedProduct = new Product("Updated Label", "Updated Description", "Updated Category");

        when(productService.updateProduct(1L, updateDTO)).thenReturn(updatedProduct);
        when(productMapper.toProductResponse(updatedProduct)).thenReturn(new ProductResponse(1L, sku, updatedProduct.getLabel(), updatedProduct.getDescription(), updatedProduct.getCategory(), createdAt, updatedAt));

        mockMvc.perform(put("/api/v1/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value(sku.toString()))
                .andExpect(jsonPath("$.label").value("Updated Label"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.category").value("Updated Category"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$.updatedAt").value(updatedAt.toString()));

        verify(productService, times(1)).updateProduct(1L, updateDTO);
        verify(productMapper, times(1)).toProductResponse(updatedProduct);

    }

    @Test
    void updateProduct_shouldReturnProductNotFoundException() throws Exception {
        UpdateProductRequest updateDTO = new UpdateProductRequest("Updated Label", "Updated Description", "Updated Category");

        when(productService.updateProduct(1L, updateDTO)).thenThrow(new ProductNotFoundException(1L));

        mockMvc.perform(
                        put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/product/1"));

        verify(productService, times(1)).updateProduct(1L, updateDTO);
        verifyNoInteractions(productMapper);
    }

    @Test
    void updateProduct_shouldReturnBadRequestWhenCategorylIsTooLong() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest("Updated Label", "Updated Description",
                """
                **#%%%%%%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@#==-+###*++*#%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @#*##+%+#@@@@@@@#++#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@%+#=%@@@@@@@@@@@@%+=#@@@%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@#+*+*@@+@@@@@@@@@@@%#++#%@@@@@@@@@@@@@@@@@@@@@@@@**=#%%@@@@@@@@@@@@
                #@@%*+#*%@@+@@@@@#*#%%**%##*@@@@@@@@@@@@@@@@@@%*=---::::::-=*%@@@@@@@@@
                *+%%=#%@@@%+@@@@%@@%+++%@%*+%@@@@@@@@@@@@@@@*++==+**+:::-==-::-*@@@@@@@
                @@##@@@@@%+@@@@@@%*+#*+*%%@#*#@@@@@@@@@@@@%+**+#%#%@@@#@@@@@@#-:-#@@@@@
                @@@@@@@%**@@@@@@@@@@#+++****##*#%@@@@@@@%*=*#+%@++%#####+-*#@@#:::%@@@@
                @@@@@@*#@@@@@@@@@@@@@#***%%###*#**%@@@@%+=##-=#%@@%=:-+-=%@@@@*:::=@@@@
                @@@@@@@@@@@@@@@@@@@@%++++%@%@@%%%##*%@@%=*#-::=+=-::*@@#-:#%##+:-*%-@@@
                @@@@@%#@@@@@@@@@@%*#@@%***%%@%#*#@@@**%*++-::+#%+-*%#%@*%*:=%#=::*#=@@@
                @@@@@*#@@@@@@@@@@@@%#**+*%%@@%%%+-+#+*%++#=--+%*-#@@@@@@@@*:*@#-:::-%@@
                @@@@@%*@@@@@@@@@@@@#%@#*@%%@%%@@@+=+*%%+*@@#++#+*@@@@@@@@@@=*##++#%@@@@
                @@@@@@%%@@@@@@@@@@@@@@#@%%@@@@@@@#+#@@%+#@@#*%#+#@%#*##%*=-::=*%@@@@@@@
                @@@@@@@@@@@@@@@@@@@@@@#+==-=*+=++=*@@@#+#@@**@@*#@@@@@@@@#=-=:-=*%@@@@@
                @@@@*#@@@@@@@#%@@@@@@*-::::=##*++*%@@@*+@@@#*%@@##%@@@@%%%%-=:::::*@@@@
                @@@@@@#*@@@*+@@@@@@%=:::-#@%#@@@@@@@@#+#@@@%=+%@@@@#*++:::::::::::::%@@
                @@@@@@@@%#+@@@@@@@%+=:=*@@@@@@@@@@@@%+*@@*+#%+#@@@@@@@%=-=*##*-:::::::+
                @@@@@@@@@@@@@@@@@%+*@#-*@@@@@@@@@@@*+%@%*%@@@@%*+*##*+:-#%%@@#-::::::::
                @@@@@@@@@@@@@@@@%=*#@@#*@@@@@@@@%++#@@@%@@@@@@@@@@%%@@=-#%@@%=:::::::::
                @#**#%##%###*%@@%=%*%@%*@@@@@@@#=*@@@@@@@@@@@@@@@@@@@@%+:=+==*+=-::::::
                #%@@@@@%@@@@#*#@%+%%=+*=*%@@@@%+*@@@@@@@@@#*@@@@@@@@@@@@%*=-::::*@#+=::
                @@@@@@@@@@@@@@#*@#*%**#+*@@@@@*+@@@@@@@@@*=#@@@@@@@@@@@@@@@@%*-::-##=::
                @@@@@@@@@#@@@@@%#@*+#+*++@@@@@#*@@@@@@@@#=+%@@@@@@@@@@@@@@@@@@@#:::::::
                @@@@@@@@@@**@@@@#%@@*=-=%@@@@%*#@@@@@@%#@+*@@@@@@@@@@@@@@@@@@@@@:::::::
                @@@@@@@@@@@#+@@@#-+*++#%@@@@@#+@@@@@@##@%=*@@@@@@@@@@@@@@@@@@@@%::=::::
                @@@@@@@@@@@@*#@@%+#%@@@@@@@@@*+@@@@@#%@@%=*@@@@@@@@@@@@@@@@@@@@%:::::::
                @@@@@@@@@@@@%=@@@#*@@@@@@@@@%*#@@@@##@@@@++@@@@@@@@@@@@@@@@@@@@%:::*:::
                """);

        mockMvc.perform(
                put("/api/v1/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("One or more fields failed validation. Please check the errors and try again."))
                .andExpect(jsonPath("$.path").value("/api/v1/product/1"))
                .andExpect(jsonPath("$.errors.category").value("Category must not exceed 120 characters."));

        verifyNoInteractions(productService, productMapper);
    }
}
