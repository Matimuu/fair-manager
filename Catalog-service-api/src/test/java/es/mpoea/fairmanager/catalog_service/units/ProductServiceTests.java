package es.mpoea.fairmanager.catalog_service.units;

import es.mpoea.fairmanager.catalog_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductsNotExistsException;
import es.mpoea.fairmanager.catalog_service.api.services.ProductService;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTests {

    private ProductRepo productRepo;
    private CategoryRepo categoryRepo;
    private ProductService productService;

    private static final Long EXISTING_PRODUCT_ID = 10L;
    private static final Long NON_EXISTING_PRODUCT_ID = 999L;

    private static final String NON_EXISTING_CATEGORY_NAME = "Not in DB";

    private static final String EXISTING_CATEGORY_NAME = "Existing category";
    private static final Category EXISTING_CATEGORY = new Category("Existing category");

    @BeforeEach
    void setUp() {
        productRepo = mock(ProductRepo.class);
        categoryRepo = mock(CategoryRepo.class);
        productService = new ProductService(productRepo, categoryRepo);
    }

    @Test
    void updateProduct_shouldUpdateOnlyNonNullFields() {
        Product oldProduct = new Product("Oldlabel", "Old description", EXISTING_CATEGORY);
        Category newCategory = new Category("New category");
        UpdateProductCommand command = new UpdateProductCommand("New label", null, "New category");

        when(productRepo.findById(EXISTING_PRODUCT_ID)).thenReturn(Optional.of(oldProduct));
        when(productRepo.save(oldProduct)).thenReturn(oldProduct);
        when(categoryRepo.findByName(newCategory.getName())).thenReturn(Optional.of(newCategory));

        Product result = productService.updateProduct(EXISTING_PRODUCT_ID, command);

        assertEquals("New label", result.getLabel());
        assertEquals("Old description", result.getDescription());
        assertEquals(newCategory, result.getCategory());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productRepo).save(captor.capture());
        verify(categoryRepo).findByName("New category");

        assertSame(oldProduct, captor.getValue(), "The same product instance should be saved");
    }

    @Test
    void updateProduct_shouldThrow_whenProductNotFound() {
        UpdateProductCommand command = new UpdateProductCommand("new label", null, null);

        when(productRepo.findById(NON_EXISTING_PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(NON_EXISTING_PRODUCT_ID, command));
        verify(productRepo, times(1)).findById(NON_EXISTING_PRODUCT_ID);
        verifyNoInteractions(categoryRepo);
    }

    @Test
    void updateProduct_shouldUpdateProductWithoutChangingCategory() {
        UpdateProductCommand command = new UpdateProductCommand("New Label", null, null);
        Product existing = new Product("Old Label", "Old Description", EXISTING_CATEGORY);

        when(productRepo.findById(EXISTING_PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(productRepo.save(existing)).thenReturn(existing);

        Product result = productService.updateProduct(EXISTING_PRODUCT_ID, command);

        assertEquals("New Label", result.getLabel());
        assertEquals("Old Description", result.getDescription());
        assertEquals(EXISTING_CATEGORY, result.getCategory());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productRepo).save(captor.capture());
        verifyNoInteractions(categoryRepo);
    }

    @Test
    void updateProduct_shouldRemoveCategory_whenCategoryNameIsBlank() {
        UpdateProductCommand command = new UpdateProductCommand("New Label", "New description", "");

        Product existingProduct = new Product("Old Label", "Old description", EXISTING_CATEGORY);

        when(productRepo.findById(EXISTING_PRODUCT_ID)).thenReturn(Optional.of(existingProduct));

        productService.updateProduct(EXISTING_PRODUCT_ID, command);

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        verify(productRepo, times(1)).save(trap.capture());
        verifyNoInteractions(categoryRepo);

        Product saved = trap.getValue();

        assertEquals("New Label", saved.getLabel());
        assertEquals("New description", saved.getDescription());
        assertNull(saved.getCategory(), "Category should be removed when category name is blank");
    }

    @Test
    void createProduct_shouldSaveNewProduct() {
        when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(categoryRepo.findByName(EXISTING_CATEGORY_NAME)).thenReturn(Optional.of(EXISTING_CATEGORY));

        CreateProductCommand command = new CreateProductCommand("Label", "Description", EXISTING_CATEGORY_NAME);

        Product result = productService.createProduct(command);

        ArgumentCaptor<Product> productTrap = ArgumentCaptor.forClass(Product.class);

        verify(productRepo, times(1)).save(productTrap.capture());
        verify(categoryRepo, times(1)).findByName(EXISTING_CATEGORY_NAME);

        Product saved = productTrap.getValue();

//        Check saved product
        assertEquals("Label", saved.getLabel());
        assertEquals("Description", saved.getDescription());
        assertEquals(EXISTING_CATEGORY, saved.getCategory());

//        Check product returned by service
        assertEquals("Label", result.getLabel());
        assertEquals("Description", result.getDescription());
        assertEquals(EXISTING_CATEGORY, result.getCategory());

        assertNotNull(result.getSku());
        assertEquals(result.getSku(), saved.getSku(), "SKU should be generated and set on the product");
    }

    @Test
    void createProduct_shouldCreateCategory_whenCategoryNotExists() {
        Category category = new Category(NON_EXISTING_CATEGORY_NAME);
        Product productToSave = new Product("Label", "Description", category);

        CreateProductCommand command = new CreateProductCommand("Label", "Description", NON_EXISTING_CATEGORY_NAME);

        when(categoryRepo.findByName(NON_EXISTING_CATEGORY_NAME)).thenReturn(Optional.empty());
        when(categoryRepo.save(any(Category.class))).thenReturn(category);
        when(productRepo.save(any(Product.class))).thenReturn(productToSave);

        Product createdProduct = productService.createProduct(command);

        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getCategory());

        Category categoryResult = createdProduct.getCategory();

        assertEquals(NON_EXISTING_CATEGORY_NAME, categoryResult.getName());

        verify(categoryRepo, times(1)).findByName(NON_EXISTING_CATEGORY_NAME);
        verify(categoryRepo, times(1)).save(any(Category.class));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepo, times(1)).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        assertEquals("Label", savedProduct.getLabel());
        assertEquals("Description", savedProduct.getDescription());
        assertNotNull(savedProduct.getCategory());
        assertEquals(NON_EXISTING_CATEGORY_NAME, savedProduct.getCategory().getName());
    }

    @Test
    void createProduct_shouldCreateProductWithoutCategory_whenCategoryNameIsNull() {
        CreateProductCommand command = new CreateProductCommand("Label", "Description", null);
        Product returnProduct = new Product("Label", "Description", null);

        when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        productService.createProduct(command);

        verify(productRepo, times(1)).save(trap.capture());

        Product saved = trap.getValue();

        assertEquals("Label", saved.getLabel());
        assertEquals("Description", saved.getDescription());
        assertNull(saved.getCategory());
    }

    @Test
    void deleteProduct_shouldDeleteExistingProduct() {
        when(productRepo.existsById(EXISTING_PRODUCT_ID)).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct(EXISTING_PRODUCT_ID));

        verify(productRepo, times(1)).existsById(EXISTING_PRODUCT_ID);
        verify(productRepo, times(1)).deleteById(EXISTING_PRODUCT_ID);

        verifyNoMoreInteractions(productRepo, categoryRepo);
    }

    @Test
    void deleteProduct_shouldThrow_whenProductNotFound() {
        long id = 404L;

        when(productRepo.existsById(id)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));

        verify(productRepo, times(1)).existsById(id);
        verify(productRepo, never()).deleteById(anyLong());
        verifyNoMoreInteractions(productRepo);
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        Product existing = new Product("Label", "Description", EXISTING_CATEGORY);

        when(productRepo.findById(EXISTING_PRODUCT_ID)).thenReturn(Optional.of(existing));

        Product result = productService.getProductById(EXISTING_PRODUCT_ID);

        assertEquals(existing.getLabel(), result.getLabel());
        assertEquals(existing.getDescription(), result.getDescription());
        assertEquals(existing.getCategory(), result.getCategory());

        verify(productRepo, times(1)).findById(EXISTING_PRODUCT_ID);
        verifyNoMoreInteractions(productRepo, categoryRepo);
    }

    @Test
    void getProductById_shouldThrow_whenNotFound() {
        long id = 404L;

        when(productRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(id));

        verify(productRepo, times(1)).findById(id);
        verifyNoMoreInteractions(productRepo, categoryRepo);
    }

    @Test
    void getAllProducts_shouldReturnProductsList() {
        List<Product> initialProducts = List.of(
                new Product("Label1", "Description1", EXISTING_CATEGORY),
                new Product("Label2", "Description2", EXISTING_CATEGORY)
        );

        when(productRepo.findAll()).thenReturn(initialProducts);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertArrayEquals(initialProducts.toArray(), result.toArray());


        verify(productRepo, times(1)).findAll();
        verifyNoMoreInteractions(productRepo, categoryRepo);
    }

    @Test
    void getAllProducts_shouldThrow_ProductsNotExistsException() {
        when(productRepo.findAll()).thenReturn(List.of());

        assertThrows(ProductsNotExistsException.class, () -> productService.getAllProducts());

        verify(productRepo, times(1)).findAll();
        verifyNoMoreInteractions(productRepo, categoryRepo);
    }
}
