package es.mpoea.fairmanager.catalog_service.units;

import es.mpoea.fairmanager.catalog_service.api.commands.category.CreateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.category.CategoryAlreadyExistsException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.category.CategoryNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.services.CategoryService;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final CategoryService categoryService;

    private final long EXISTING_CATEGORY_ID = 1L;
    private final long NOT_EXISTING_CATEGORY_ID = 404L;
    private static final Category EXISTING_CATEGORY = new Category(
            "Default"
    );

    public CategoryServiceTest() {
        categoryRepo = mock(CategoryRepo.class);
        productRepo = mock(ProductRepo.class);

        categoryService = new CategoryService(categoryRepo, productRepo);
    }

    /*
     *   Create Tests
     * */

    @Test
    void createCategory_shouldCreateCategory() {
        String categoryName = "Default";

        CreateCategoryCommand command = new CreateCategoryCommand(
                categoryName
        );

        when(categoryRepo.existsByName(categoryName))
                .thenReturn(false);
        when(categoryRepo.save(any(Category.class)))
                .thenReturn(EXISTING_CATEGORY);

        Category result = categoryService.createCategory(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoryName, result.getName())
        );

        verify(categoryRepo).existsByName(categoryName);
        verify(categoryRepo).save(any(Category.class));
    }

    @Test
    void createCategory_shouldThrowIllegalArgumentException_whenNameIsNull() {
        String ERROR_MESSAGE = "Category name cannot be null or blank";

        CreateCategoryCommand command = new CreateCategoryCommand(null);

        assertAll(
                () -> {
                    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                            () -> categoryService.createCategory(command));
                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(categoryRepo);
    }

    @Test
    void createCategory_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        String ERROR_MESSAGE = "Category name cannot be null or blank";

        CreateCategoryCommand command = new CreateCategoryCommand(" ");

        assertAll(
                () -> {
                    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                            () -> categoryService.createCategory(command));
                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(categoryRepo);
    }

    @Test
    void createCategory_shouldThrowCategoryAlreadyExistsException_whenCategoryNameAlreadyExists() {
        String ERROR_MESSAGE = "Category with name %s already exists";
        String categoryName = EXISTING_CATEGORY.getName();

        CreateCategoryCommand command = new CreateCategoryCommand(
                categoryName
        );

        when(categoryRepo
                .existsByName(categoryName))
                .thenReturn(true);

        assertAll(
                () -> {
                    CategoryAlreadyExistsException ex = assertThrows(CategoryAlreadyExistsException.class,
                            () -> categoryService.createCategory(command));
                    assertEquals(ERROR_MESSAGE.formatted(categoryName), ex.getMessage());
                }
        );

        verify(categoryRepo).existsByName(categoryName);
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void createCategory_shouldCreateCategoryWithoutBlankSpaces() {
        String categoryNameWithSpaces = " Default ";
        String categoryNameWithoutSpaces = "Default";

        ArgumentCaptor<Category> trap = ArgumentCaptor.forClass(Category.class);

        CreateCategoryCommand command = new CreateCategoryCommand(categoryNameWithSpaces);

        when(categoryRepo.existsByName(categoryNameWithoutSpaces)).thenReturn(false);
        when(categoryRepo.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        Category result = categoryService.createCategory(command);

        verify(categoryRepo).save(trap.capture());

        Category categoryToRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(categoryToRepo),
                () -> assertEquals(categoryNameWithoutSpaces, categoryToRepo.getName())
        );

        verify(categoryRepo).existsByName(categoryNameWithoutSpaces);
        verify(categoryRepo).save(trap.getValue());

        verifyNoMoreInteractions(categoryRepo);
    }

    /*
     *   Get Tests
     * */

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        List<Category> existingCategories = List.of(
                new Category("Default"),
                new Category("Default 2")
        );

        when(categoryRepo.findAll())
                .thenReturn(existingCategories);

        List<Category> result = categoryService.getAllCategories();

        assertAll(
                () -> assertFalse(result.isEmpty()),
                () -> assertSame(existingCategories, result)
        );

        verify(categoryRepo).findAll();
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getAllCategories_shouldReturnEmptyList_whenNoCategoriesFound() {
        List<Category> emptyList = List.of();

        when(categoryRepo.findAll())
                .thenReturn(emptyList);

        List<Category> result = categoryService.getAllCategories();

        assertAll(
                () -> assertTrue(result.isEmpty()),
                () -> assertSame(emptyList, result)
        );

        verify(categoryRepo).findAll();
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        when(categoryRepo.findById(EXISTING_CATEGORY_ID))
                .thenReturn(Optional.of(EXISTING_CATEGORY));

        Category result = categoryService.getCategory(EXISTING_CATEGORY_ID);

        assertAll(
                () -> assertNotNull(result),
                () -> assertSame(EXISTING_CATEGORY, result)
        );

        verify(categoryRepo).findById(EXISTING_CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getCategoryById_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
        String ERROR_MESSAGE = "Category with id %s not found";

        when(categoryRepo.findById(NOT_EXISTING_CATEGORY_ID))
                .thenReturn(Optional.empty());

        assertAll(
                () -> {
                    var ex = assertThrows(CategoryNotFoundException.class,
                            () -> categoryService.getCategory(NOT_EXISTING_CATEGORY_ID));

                    assertEquals(ERROR_MESSAGE.formatted(NOT_EXISTING_CATEGORY_ID), ex.getMessage());
                }
        );

        verify(categoryRepo).findById(NOT_EXISTING_CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getCategoryByName_shouldReturnCategory() {
        String categoryName = EXISTING_CATEGORY.getName();

        when(categoryRepo.findByName(categoryName))
                .thenReturn(Optional.of(EXISTING_CATEGORY));

        Category result = categoryService.getCategory(categoryName);

        assertAll(
                () -> assertNotNull(result),
                () -> assertSame(EXISTING_CATEGORY, result)
        );

        verify(categoryRepo).findByName(categoryName);
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getCategoryByName_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
        String ERROR_MESSAGE = "Category with name %s not found";
        String categoryName = EXISTING_CATEGORY.getName();

        when(categoryRepo.findByName(categoryName))
                .thenReturn(Optional.empty());

        assertAll(
                () -> {
                    var ex = assertThrows(CategoryNotFoundException.class,
                            () -> categoryService.getCategory(categoryName));

                    assertEquals(ERROR_MESSAGE.formatted(categoryName), ex.getMessage());
                }
        );

        verify(categoryRepo).findByName(categoryName);
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getCategoryByName_shouldThrowIllegalArgumentException_whenNameIsNull() {
        String ERROR_MESSAGE = "Category name cannot be null or blank";
        String categoryName = null;

        assertAll(
                () -> {
                    var ex = assertThrows(IllegalArgumentException.class,
                            () -> categoryService.getCategory(categoryName));
                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(categoryRepo);
    }

    @Test
    void getCategoryByName_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        String ERROR_MESSAGE = "Category name cannot be null or blank";
        String categoryName = " ";

        assertAll(
                () -> {
                    var ex = assertThrows(IllegalArgumentException.class,
                            () -> categoryService.getCategory(categoryName));
                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(categoryRepo);
    }

    @Test
    void getOrCreateCategory_shouldReturnExistingCategory() {
        String categoryName = EXISTING_CATEGORY.getName();

        when(categoryRepo.findByName(categoryName))
                .thenReturn(Optional.of(EXISTING_CATEGORY));

        Category result = categoryService.getOrCreateCategory(categoryName);

        assertAll(
                () -> assertNotNull(result),
                () -> assertSame(EXISTING_CATEGORY, result)
        );

        verify(categoryRepo).findByName(categoryName);
        verify(categoryRepo, never()).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getOrCreateCategory_shouldCreateAndReturnCategory_whenCategoryNotExists() {
        String categoryName = "Not existing category";

        Category newCategory = new Category(categoryName);

        ArgumentCaptor<Category> trap = ArgumentCaptor.forClass(Category.class);

        when(categoryRepo.findByName(categoryName))
                .thenReturn(Optional.empty());
        when(categoryRepo.save(any(Category.class)))
                .thenReturn(newCategory);

        Category result = categoryService.getOrCreateCategory(categoryName);

        verify(categoryRepo).save(trap.capture());

        Category toRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(toRepo),
                () -> assertNotNull(result),

                () -> assertEquals(categoryName, toRepo.getName())
        );

        verify(categoryRepo).findByName(categoryName);
        verify(categoryRepo).save(any(Category.class));

        verifyNoMoreInteractions(categoryRepo);
    }

    @Test
    void getOrCreateCategory_shouldThrowIllegalArgumentException_whenNameIsNull() {
        String ERROR_MESSAGE = "Category name cannot be null or blank";
        String categoryName = null;

        assertAll(
                () -> {
                    var ex = assertThrows(IllegalArgumentException.class,
                            () -> categoryService.getOrCreateCategory(categoryName));

                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(categoryRepo);
    }

    @Test
    void getOrCreateCategory_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        String ERROR_MESSAGE = "Category name cannot be null or blank";
        String categoryName = " ";

        assertAll(
                () -> {
                    var ex = assertThrows(IllegalArgumentException.class,
                            () -> categoryService.getOrCreateCategory(categoryName));

                    assertEquals(ERROR_MESSAGE, ex.getMessage());
                }
        );

        verifyNoInteractions(categoryRepo);
    }

    @Test
    void getOrCreateCategory_shouldCreateAndReturnCategory_withNameWithoutBlankSpaces() {
        String categoryNameWithSpaces = " Default ";
        String categoryNameWithoutSpaces = "Default";

        ArgumentCaptor<Category> trap = ArgumentCaptor.forClass(Category.class);

        when(categoryRepo.findByName(categoryNameWithoutSpaces))
                .thenReturn(Optional.empty());
        when(categoryRepo.save(any(Category.class)))
                .thenReturn(EXISTING_CATEGORY);


        Category result = categoryService.getOrCreateCategory(categoryNameWithSpaces);

        verify(categoryRepo).save(trap.capture());

        Category toRepo = trap.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(toRepo),

                () -> assertEquals(categoryNameWithoutSpaces, toRepo.getName())
        );

        verify(categoryRepo).findByName(categoryNameWithoutSpaces);
        verify(categoryRepo).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepo);
    }

    /*
     *   Update Tests
     * */

    @Test
    void updateCategory_shouldUpdateCategoryName() {
//        TODO
    }

    @Test
    void updateCategory_shouldUpdateCategoryName_withNameWithoutBlankSpaces() {
//        TODO
    }

    @Test
    void updateCategory_shouldThrowIllegalArgumentException_whenNameIsNull() {
//        TODO
    }

    @Test
    void updateCategory_shouldThrowIllegalArgumentException_whenNameIsBlank() {
//        TODO
    }

    @Test
    void updateCategory_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
        // TODO
    }

    @Test
    void updateCategory_shouldThrowCategoryAlreadyExistsException_whenCategoryNameAlreadyExists() {
//        TODO
    }

    /*
     *   Delete Tests
     * */

    @Test
    void deleteCategory_shouldDeleteCategory_andDeattachCategoryFromProducts() {
//        TODO
    }

    @Test
    void deleteCategory_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
//        TODO
    }
}
