package es.mpoea.fairmanager.product_service.units;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.UpdateProductRequest;
import es.mpoea.fairmanager.product_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.product_service.api.exceptions.ProductsNotExistsException;
import es.mpoea.fairmanager.product_service.api.services.ProductService;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import es.mpoea.fairmanager.product_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTests {

    private ProductRepo productRepo;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepo = mock(ProductRepo.class);
        productService = new ProductService(productRepo);
    }

    @Test
    void updateProduct_shouldUpdateOnlyNonNullFields() {
        long id = 10L;
        Product existing = new Product("Oldlabel", "Old description", "Old category");

        when(productRepo.findById(id)).thenReturn(Optional.of(existing));
        when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProductRequest updateDTO = new UpdateProductRequest("New label", null, "New category");

        Product result = productService.updateProduct(id, updateDTO);

        assertEquals("New label", result.getLabel());
        assertEquals("Old description", result.getDescription());
        assertEquals("New category", result.getCategory());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        assertSame(existing, captor.getValue(), "The same product instance should be saved");
    }

    @Test
    void createProduct_shouldSaveNewProduct() {
        when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateProductRequest createDTO = new CreateProductRequest("Label", "Description", "Category");

        Product result = productService.createProduct(createDTO);

        ArgumentCaptor<Product> productTrap = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(productTrap.capture());

        Product saved = productTrap.getValue();

//        Check saved product
        assertEquals("Label", saved.getLabel());
        assertEquals("Description", saved.getDescription());
        assertEquals("Category", saved.getCategory());

//        Check product returned by service
        assertEquals("Label", result.getLabel());
        assertEquals("Description", result.getDescription());
        assertEquals("Category", result.getCategory());
    }

    @Test
    void deleteProduct_shouldDeleteExistingProduct() {
        long id = 10L;

        when(productRepo.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct(id));

        verify(productRepo, times(1)).existsById(id);
        verify(productRepo, times(1)).deleteById(id);

        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void deleteProduct_shouldThrow_whenProductNotFound() {
        long id = 404L;

        when(productRepo.existsById(id)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));

        verify(productRepo, times(1)).existsById(id);
        verify(productRepo, never()).deleteById(anyLong());
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        long id = 10L;

        Product existing = new Product("Label", "Description", "Category");

        when(productRepo.findById(id)).thenReturn(Optional.of(existing));

        Product result = productService.getProductById(id);

        assertEquals(existing.getLabel(), result.getLabel());
        assertEquals(existing.getDescription(), result.getDescription());
        assertEquals(existing.getCategory(), result.getCategory());

        verify(productRepo, times(1)).findById(id);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void getProductById_shouldThrow_whenNotFound() {
        long id = 404L;

        when(productRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(id));

        verify(productRepo, times(1)).findById(id);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void getAllProducts_shouldReturnProductsList() {
        List<Product> initialProducts = new ArrayList<>();

        initialProducts.add(new Product("Label1", "Description1", "Category1"));
        initialProducts.add(new Product("Label2", "Description2", "Category2"));

        when(productRepo.findAll()).thenReturn(initialProducts);

        List<Product> result = productService.getAllProducts();

        assertSame(initialProducts, result);

        verify(productRepo, times(1)).findAll();
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void getAllProducts_shouldThrow_ProductsNotExistsException() {
        when(productRepo.findAll()).thenReturn(List.of());

        assertThrows(ProductsNotExistsException.class, () -> productService.getAllProducts());

        verify(productRepo, times(1)).findAll();
        verifyNoMoreInteractions(productRepo);
    }
}
