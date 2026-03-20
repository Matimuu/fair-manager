package es.mpoea.fairmanager.catalog_service.units;

import es.mpoea.fairmanager.catalog_service.api.commands.product.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.product.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.product.ProductAlreadyExistsException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.product.ProductNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.services.CategoryService;
import es.mpoea.fairmanager.catalog_service.api.services.ProductService;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTests {

    private ProductRepo productRepo;
    private CategoryService categoryService;
    private ProductService productService;

    private static final Long EXISTING_PRODUCT_ID = 10L;
    private static final Long NON_EXISTING_PRODUCT_ID = 999L;

    private static final String NON_EXISTING_CATEGORY_NAME = "Not in DB";

    private static final String EXISTING_CATEGORY_NAME = "Existing category";
    private final Category EXISTING_CATEGORY = new Category("Existing category");

    private final Product EXISTING_PRODUCT = new Product("Label", "Description", EXISTING_CATEGORY);

    @BeforeEach
    void setUp() {
        productRepo = mock(ProductRepo.class);
        categoryService = mock(CategoryService.class);

        productService = new ProductService(productRepo, categoryService);
    }

    /*
    Create
    * */

    @Test
    void createProduct_shouldReturnProductWithCategory() {
        CreateProductCommand command = new CreateProductCommand(
                "Label",
                "Description",
                EXISTING_CATEGORY_NAME);

        when(productRepo.existsByLabel(command.label()))
                .thenReturn(false);
        when(categoryService.getOrCreateCategory(EXISTING_CATEGORY_NAME))
                .thenReturn(EXISTING_CATEGORY);
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        Product result = productService.createProduct(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getSku()),
                () -> assertEquals(command.label(), result.getLabel()),
                () -> assertEquals(command.description(), result.getDescription()),

                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY_NAME, result.getCategory().getName())
        );

        verify(productRepo).existsByLabel(command.label());
        verify(categoryService).getOrCreateCategory(EXISTING_CATEGORY_NAME);
        verify(productRepo).save(trap.capture());

        Product providedToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(command.label(), providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNotNull(providedToRepo.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY_NAME, providedToRepo.getCategory().getName())
        );
    }

    @Test
    void createProduct_shouldReturnProductWithCategory_productNameWithoutBlankSpaces() {
        String labelWithBlankSpaces = " Label ";
        String labelWithoutBlankSpaces = "Label";

        CreateProductCommand command = new CreateProductCommand(
                labelWithBlankSpaces,
                "Description",
                EXISTING_CATEGORY_NAME
        );

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        when(productRepo.existsByLabel(labelWithoutBlankSpaces)).thenReturn(false);
        when(categoryService.getOrCreateCategory(EXISTING_CATEGORY_NAME)).thenReturn(EXISTING_CATEGORY);
        when(productRepo.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(labelWithoutBlankSpaces, result.getLabel()),
                () -> assertEquals(command.description(), result.getDescription()),

                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY_NAME, result.getCategory().getName())
        );

        verify(productRepo).existsByLabel(labelWithoutBlankSpaces);
        verify(categoryService).getOrCreateCategory(EXISTING_CATEGORY_NAME);
        verify(productRepo).save(trap.capture());

        Product providedToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(labelWithoutBlankSpaces, providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNotNull(providedToRepo.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY_NAME, providedToRepo.getCategory().getName())
        );
    }

    @Test
    void createProduct_shouldReturnProductWithCategory_categoryNameWithoutBlankSpaces() {
        String categoryNameWithBlankSpaces = " Category ";
        String categoryNameWithoutBlankSpaces = "Category";

        CreateProductCommand command = new CreateProductCommand(
                "Label",
                "Description",
                categoryNameWithBlankSpaces
        );

        Category category = new Category(categoryNameWithoutBlankSpaces);

        ArgumentCaptor<Product> ProductTrap = ArgumentCaptor.forClass(Product.class);

        when(productRepo.existsByLabel(command.label())).thenReturn(false);
        when(categoryService.getOrCreateCategory(categoryNameWithoutBlankSpaces)).thenReturn(category);
        when(productRepo.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(command.label(), result.getLabel()),
                () -> assertEquals(command.description(), result.getDescription()),

                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(categoryNameWithoutBlankSpaces, result.getCategory().getName())
        );

        verify(productRepo).existsByLabel(command.label());
        verify(categoryService).getOrCreateCategory(categoryNameWithoutBlankSpaces);
        verify(productRepo).save(ProductTrap.capture());

        Product providedToRepo = ProductTrap.getValue();

        assertAll(
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(command.label(), providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNotNull(providedToRepo.getCategory()),
                () -> assertEquals(categoryNameWithoutBlankSpaces, providedToRepo.getCategory().getName())
        );
    }

    @Test
    void createProduct_shouldReturnProductWithoutCategory_whenCategoryNameIsNull() {
        CreateProductCommand command = new CreateProductCommand(
                "Label",
                "Description",
                null
        );

        when(productRepo.existsByLabel(command.label()))
                .thenReturn(false);
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        Product result = productService.createProduct(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(command.label(), result.getLabel()),
                () -> assertEquals(command.description(), result.getDescription()),

                () -> assertNull(result.getCategory())
        );

        verify(productRepo).existsByLabel(command.label());
        verifyNoInteractions(categoryService);
        verify(productRepo).save(trap.capture());

        Product providedToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(command.label(), providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNull(providedToRepo.getCategory())
        );
    }

    @Test
    void createProduct_shouldReturnProductWithoutCategory_whenCategoryNameIsBlank() {
        CreateProductCommand command = new CreateProductCommand(
                "Label",
                "Description",
                " "
        );

        when(productRepo.existsByLabel(command.label()))
                .thenReturn(false);
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        Product result = productService.createProduct(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(command.label(), result.getLabel()),
                () -> assertEquals(command.description(), result.getDescription()),

                () -> assertNull(result.getCategory())
        );

        verify(productRepo).existsByLabel(command.label());
        verifyNoInteractions(categoryService);
        verify(productRepo).save(trap.capture());

        Product providedToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(command.label(), providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNull(providedToRepo.getCategory())
        );
    }

    @Test
    void createProduct_shouldThrowIllegalArgumentException_whenProductLabelIsNull() {
        String ERROR_MESSAGE = "Product label cannot be null or blank";
        CreateProductCommand commandNullLabel = new CreateProductCommand(
                null,
                "Description",
                EXISTING_CATEGORY_NAME
        );

        assertAll(
                () -> {
                    IllegalArgumentException ex = assertThrows(
                            IllegalArgumentException.class,
                            () -> productService.createProduct(commandNullLabel)
                    );
                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(productRepo, categoryService);
    }

    @Test
    void createProduct_shouldThrowIllegalArgumentException_whenProductLabelIsBlank() {
        String ERROR_MESSAGE = "Product label cannot be null or blank";

        CreateProductCommand commandBlankLabel = new CreateProductCommand(
                " ",
                "Description",
                EXISTING_CATEGORY_NAME
        );

        assertAll(
                () -> {
                    IllegalArgumentException ex = assertThrows(
                            IllegalArgumentException.class,
                            () -> productService.createProduct(commandBlankLabel)
                    );
                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(productRepo, categoryService);
    }

    @Test
    void createProduct_shouldThrowProductAlreadyExistsException() {
        CreateProductCommand command = new CreateProductCommand(
                "Label",
                "Description",
                EXISTING_CATEGORY_NAME
        );

        when(productRepo.existsByLabel(command.label())).thenReturn(true);

        assertAll(
                () -> {
                    ProductAlreadyExistsException ex = assertThrows(
                            ProductAlreadyExistsException.class,
                            () -> productService.createProduct(command)
                    );
                    assertEquals("Product with name %s already exists.".formatted(command.label()), ex.getMessage());
                }
        );

        verify(productRepo).existsByLabel(command.label());
        verifyNoInteractions(categoryService);
    }

    /*
    Get
    * */

    @Test
    void getAllProducts_shouldReturnProductsList_whenExists() {
        List<Product> products = List.of(
                new Product("Label1", "Description1", EXISTING_CATEGORY),
                new Product("Label2", "Description2", EXISTING_CATEGORY)
        );

        when(productRepo.findAllWithCategory()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(products.size(), result.size()),

                () -> assertEquals(products.get(0).getLabel(), result.get(0).getLabel()),
                () -> assertEquals(products.get(0).getDescription(), result.get(0).getDescription()),
                () -> assertEquals(products.get(0).getCategory().getName(), result.get(0).getCategory().getName()),

                () -> assertEquals(products.get(1).getLabel(), result.get(1).getLabel()),
                () -> assertEquals(products.get(1).getDescription(), result.get(1).getDescription()),
                () -> assertEquals(products.get(1).getCategory().getName(), result.get(1).getCategory().getName())
        );

        verify(productRepo).findAllWithCategory();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNotExists() {
        when(productRepo.findAllWithCategory()).thenReturn(List.of());

        List<Product> result = productService.getAllProducts();

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(0, result.size())
        );

        verify(productRepo).findAllWithCategory();
    }

    @Test
    void getProductById_shouldReturnProduct_whenExists() {
        Product existingProduct = new Product("Label", "Description", EXISTING_CATEGORY);
        long existingProductId = 1;

        when(productRepo.findByIdWithCategory(existingProductId)).thenReturn(Optional.of(existingProduct));

        Product result = productService.getProductById(existingProductId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(existingProduct.getSku(), result.getSku()),
                () -> assertEquals(existingProduct.getLabel(), result.getLabel()),
                () -> assertEquals(existingProduct.getDescription(), result.getDescription()),

                () -> assertEquals(existingProduct.getCategory().getName(), result.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(existingProductId);
    }

    @Test
    void getProductById_shouldThrowProductNotFoundException_whenProductNotExists() {
        long notExistingProductId = 404;
        String ERROR_MESSAGE = "Product with id %d not found";

        when(productRepo.findByIdWithCategory(notExistingProductId)).thenReturn(Optional.empty());

        assertAll(
                () -> {
                    ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> productService.getProductById(notExistingProductId));
                    assertEquals(ERROR_MESSAGE.formatted(notExistingProductId), ex.getMessage());
                }
        );

        verify(productRepo).findByIdWithCategory(notExistingProductId);
    }

    /*
    Update
    * */

    @Test
    void updateProduct_shouldUpdateAllFields() {
        long productId = 1L;

        String orgLabel = "Label";
        String orgDescription = "Description";

        String newLabel = "New label";
        String newDescription = "New description";
        String newCategoryName = "New category";

        Product existingProduct = spy(new Product(
                orgLabel,
                orgDescription,
                EXISTING_CATEGORY
        ));


        Category newCategory = new Category(
                newCategoryName
        );

        UpdateProductCommand command = new UpdateProductCommand(
                newLabel,
                newDescription,
                newCategoryName
        );

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        doReturn(productId).when(existingProduct).getId();

        when(productRepo
                .findByIdWithCategory(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepo
                .findByLabel(command.label()))
                .thenReturn(Optional.empty());
        when(categoryService
                .getOrCreateCategory(command.categoryName()))
                .thenReturn(newCategory);
        when(productRepo
                .save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(productId, command);

        verify(productRepo).save(trap.capture());
        Product providedToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(command.label(), providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNotNull(providedToRepo.getCategory()),
                () -> assertEquals(newCategoryName, providedToRepo.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).findByLabel(command.label());
        verify(categoryService).getOrCreateCategory(command.categoryName());
        verify(productRepo).save(existingProduct);
    }

    @Test
    void updateProduct_shouldThrowProductNotFoundException() {
        long notExistingProductId = 404L;
        String ERROR_MESSAGE = "Product with id %d not found";

        UpdateProductCommand command = new UpdateProductCommand(
                "New label",
                "New description",
                "New category"
        );

        when(productRepo
                .findByIdWithCategory(notExistingProductId))
                .thenReturn(Optional.empty()
                );

        assertAll(
                () -> {
                    ProductNotFoundException ex = assertThrows(
                            ProductNotFoundException.class,
                            () -> {
                                productService.updateProduct(notExistingProductId, command);
                            }
                    );
                    assertEquals(ERROR_MESSAGE.formatted(notExistingProductId), ex.getMessage());
                }
        );

        verify(productRepo).findByIdWithCategory(notExistingProductId);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldThrowIllegalArgumentException() {
        long productId = 1L;
        String ERROR_MESSAGE = "New label cannot be null or blank";

        Product existingProduct = mock(Product.class);

        UpdateProductCommand command = new UpdateProductCommand(
                " ",
                "New description",
                "New category"
        );

        when(existingProduct.getId()).thenReturn(productId);

        when(productRepo
                .findByIdWithCategory(productId))
                .thenReturn(Optional.of(existingProduct));

        assertAll(
                () -> {
                    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                            () -> productService.updateProduct(productId, command));

                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verify(productRepo).findByIdWithCategory(productId);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldThrowProductAlreadyExistsException() {
        long productId = 1L;

        String existingLabel = "Existing label";
        String ERROR_MESSAGE = "Product with name %s already exists.";

        Product productFirst = spy(
                new Product(
                        "Label",
                        "Description",
                        EXISTING_CATEGORY
                )
        );
        Product productSecond = spy(
                new Product(
                        existingLabel,
                        "Description",
                        EXISTING_CATEGORY
                )
        );

        UpdateProductCommand command = new UpdateProductCommand(
                existingLabel,
                "New description",
                "New category"
        );

        doReturn(productId).when(productFirst).getId();
        doReturn(2L).when(productSecond).getId();

        when(productRepo.findByIdWithCategory(productId)).thenReturn(Optional.of(productFirst));
        when(productRepo.findByLabel(existingLabel)).thenReturn(Optional.of(productSecond));

        assertAll(
                () -> {
                    ProductAlreadyExistsException ex = assertThrows(ProductAlreadyExistsException.class,
                            () -> productService.updateProduct(productId, command)
                    );
                    assertEquals(ERROR_MESSAGE.formatted(existingLabel), ex.getMessage());
                }
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).findByLabel(existingLabel);
        verifyNoMoreInteractions(productRepo);

        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldUpdateProduct_withTrimmingLabel() {
        long productId = 1L;

        String labelWithBlankSpaces = " New label ";
        String labelWithoutBlankSpaces = "New label";

        Product existingProduct = new Product(
                "Label",
                "Descritption",
                EXISTING_CATEGORY
        );

        UpdateProductCommand command = new UpdateProductCommand(
                labelWithBlankSpaces,
                "Description",
                "Category"
        );

        ArgumentCaptor<Product> trap = ArgumentCaptor.forClass(Product.class);

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepo.findByLabel("New label"))
                .thenReturn(Optional.empty());
        when(categoryService.getOrCreateCategory(command.categoryName()))
                .thenReturn(EXISTING_CATEGORY);
        when(productRepo.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(productId, command);

        verify(productRepo).save(trap.capture());

        Product providedToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(providedToRepo),
                () -> assertEquals(labelWithoutBlankSpaces, providedToRepo.getLabel()),
                () -> assertEquals(command.description(), providedToRepo.getDescription()),

                () -> assertNotNull(providedToRepo.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY_NAME, providedToRepo.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).findByLabel(labelWithoutBlankSpaces);
        verify(categoryService).getOrCreateCategory(command.categoryName());
        verify(productRepo).save(existingProduct);
    }

    @Test
    void updateProduct_shouldUpdateProduct_withTrimmingCategoryName() {
        long productId = 1L;

        String categoryNameWithBlankSpaces = " New category ";
        String categoryNameWithoutBlankSpaces = "New category";

        Category existingCategory = new Category(categoryNameWithoutBlankSpaces);

        UpdateProductCommand command = new UpdateProductCommand(
                "New label",
                "Description",
                categoryNameWithBlankSpaces

        );

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));
        when(productRepo.findByLabel(command.label()))
                .thenReturn(Optional.empty());
        when(categoryService.getOrCreateCategory(categoryNameWithoutBlankSpaces))
                .thenReturn(existingCategory);
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(productId, command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(command.label(), result.getLabel()),
                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(categoryNameWithoutBlankSpaces, result.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).findByLabel(command.label());
        verify(categoryService).getOrCreateCategory(categoryNameWithoutBlankSpaces);
        verify(productRepo).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldUpdateProduct_withNullCategory() {
        long productId = 1L;

        UpdateProductCommand command = new UpdateProductCommand(
                null,
                null,
                " "
        );

        String orgLabel = EXISTING_PRODUCT.getLabel();
        String orgDescription = EXISTING_PRODUCT.getDescription();

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(productId, command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(orgLabel, result.getLabel()),
                () -> assertEquals(orgDescription, result.getDescription()),
                () -> assertNull(result.getCategory())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).save(any(Product.class));
        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldReturnUnchangedProduct_whenCommandHasAllNullFields() {
        long productId = 1L;

        String orgLabel = EXISTING_PRODUCT.getLabel();
        String orgDescription = EXISTING_PRODUCT.getDescription();

        UpdateProductCommand command = new UpdateProductCommand(
                null,
                null,
                null
        );

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));

        Product result = productService.updateProduct(productId, command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(orgLabel, result.getLabel()),
                () -> assertEquals(orgDescription, result.getDescription()),

                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY.getName(), result.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldUpdateProduct_onlyNonNullFields() {
        long productId = 1L;

        String orgDescription = EXISTING_PRODUCT.getDescription();

        UpdateProductCommand command = new UpdateProductCommand(
                "New label",
                null,
                null
        );

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));
        when(productRepo.findByLabel(command.label()))
                .thenReturn(Optional.empty());
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(productId, command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(command.label(), result.getLabel()),
                () -> assertEquals(orgDescription, result.getDescription()),

                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY.getName(), result.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).findByLabel(command.label());
        verify(productRepo).save(any(Product.class));
        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldUpdateProduct_OnlyDescriptionField() {
        long productId = 1L;

        String orgLabel = EXISTING_PRODUCT.getLabel();
        String orgDescription = EXISTING_PRODUCT.getDescription();

        UpdateProductCommand command = new UpdateProductCommand(
                null,
                "New description",
                null
        );

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(productId, command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(orgLabel, result.getLabel()),
                () -> assertEquals(command.description(), result.getDescription()),

                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(EXISTING_CATEGORY.getName(), result.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo).save(any(Product.class));
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(categoryService);
    }

    @Test
    void updateProduct_shouldNotThrowException_whenNewLabelBelongsToSameProduct() {
        long productId = 1L;

        String existingLabel = EXISTING_PRODUCT.getLabel();
        String existingDescription = EXISTING_PRODUCT.getDescription();
        Category existingCategory = EXISTING_PRODUCT.getCategory();

        UpdateProductCommand command = new UpdateProductCommand(
                existingLabel,
                null,
                null
        );

        when(productRepo.findByIdWithCategory(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));
        when(productRepo.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        Product result = assertDoesNotThrow(
                () -> productService.updateProduct(productId, command)
        );

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(existingLabel, result.getLabel()),
                () -> assertEquals(existingDescription, result.getDescription()),
                () -> assertNotNull(result.getCategory()),
                () -> assertEquals(existingCategory.getName(), result.getCategory().getName())
        );

        verify(productRepo).findByIdWithCategory(productId);
        verify(productRepo, never()).findByLabel(anyString());
        verify(productRepo).save(EXISTING_PRODUCT);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(categoryService);
    }

    /*
    Delete
    * */

    @Test
    void deleteProduct_shouldDeleteProduct() {
        long productId = 1L;

        when(productRepo.findById(productId))
                .thenReturn(Optional.of(EXISTING_PRODUCT));

        productService.deleteProduct(productId);

        verify(productRepo).findById(productId);
        verify(productRepo).deleteById(productId);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    void deleteProduct_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        long productId = 404L;
        String ERROR_MESSAGE = "Product with id %d not found";

        when(productRepo.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct(productId));

        assertEquals(ERROR_MESSAGE.formatted(productId), ex.getMessage());

        verify(productRepo).findById(productId);
        verify(productRepo, never()).deleteById(productId);
        verifyNoMoreInteractions(productRepo);
    }
}
