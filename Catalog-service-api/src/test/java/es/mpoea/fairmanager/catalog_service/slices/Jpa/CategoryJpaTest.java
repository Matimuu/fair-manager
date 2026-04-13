package es.mpoea.fairmanager.catalog_service.slices.Jpa;

import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryJpaTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void createCategory_shouldCreateCategory() {
        String name = "Default";
        Category result = persistAndClear(name);

        assertAll(
                () -> assertEquals(1, categoryRepo.count()),
                () -> assertNotNull(result.getId())
        );
    }

    @Test
    void createCategory_shouldThrowException_whenNameIsNull() {
        String name = null;

        assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepo.saveAndFlush(new Category(name))
        );
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        String name = "Default";
        Category result = persistAndClear(name);

        Optional<Category> fromDb = categoryRepo.findById(result.getId());

        assertOptionalCategory_nameEqualsAnd_allFieldsShouldBeNotNull(fromDb, name);
    }

    @Test
    void getCategoryById_shouldReturnEmptyOptional_whenCategoryDoesNotExist() {
        Optional<Category> fromDb = categoryRepo.findById(1L);

        assertAll(
                () -> assertTrue(fromDb.isEmpty())
        );
    }

    @Test
    void findByName_shouldReturnCategory() {
        String name = "Default";
        persistAndClear(name);

        Optional<Category> fromDb = categoryRepo.findByName(name);

        assertOptionalCategory_nameEqualsAnd_allFieldsShouldBeNotNull(fromDb, name);
    }

    @Test
    void findByName_shouldReturnEmptyOptional_whenCategoryDoesNotExist() {
        Optional<Category> fromDb = categoryRepo.findByName("Not exists");

        assertAll(
                () -> assertTrue(fromDb.isEmpty())
        );
    }

    @Test
    void existsByName_shouldReturnTrue_whenCategoryExists() {
        String name = "Default";
        persistAndClear(name);

        assertTrue(categoryRepo.existsByName(name));
    }

    @Test
    void existsByName_shouldReturnFalse_whenCategoryDoesNotExist() {
        assertFalse(categoryRepo.existsByName("Not exists"));
    }

    @Test
    void updateCategory_shouldUpdateCategory() {
        String name = "Default";
        String newName = "Updated";

        persistAndClear(name);

        Category fromDb = categoryRepo.findByName(name).orElseThrow();

        Instant created = fromDb.getCreatedAt();
        Instant updated = fromDb.getUpdatedAt();
        long ver = fromDb.getVersion();


        fromDb.setName(newName);

        categoryRepo.save(fromDb);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Category> updatedFromDb = categoryRepo.findByName(newName);

        assertAll(
                () -> assertTrue(categoryRepo.findByName(name).isEmpty()),
                () -> assertTrue(updatedFromDb.isPresent()),

                () -> {
                    var assertCategory = updatedFromDb.get();

                    assertNotNull(assertCategory.getId());
                    assertEquals(newName, assertCategory.getName());
                    assertEquals(created, assertCategory.getCreatedAt());
                    assertFalse(assertCategory.getUpdatedAt().isBefore(updated));
                    assertNotEquals(ver, assertCategory.getVersion());
                }
        );
    }


    @Test
    void deleteCategory_shouldDeleteCategory() {
        String name = "Default";

        persistAndClear(name);

        Category fromDb = categoryRepo.findByName(name).orElseThrow();

        long idToCheck = fromDb.getId();

        categoryRepo.delete(fromDb);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Category> deletedFromDbById = categoryRepo.findById(idToCheck);

        assertTrue(deletedFromDbById.isEmpty());
    }

    @Test
    void deleteCategoryByName_shouldDeleteCategory() {
        String name = "Default";

        persistAndClear(name);

        categoryRepo.deleteByName(name);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Category> deletedFromDbById = categoryRepo.findByName(name);

        assertTrue(deletedFromDbById.isEmpty());
    }

    private @NotNull Category persistAndClear(String name) {
        Category category = new Category(name);

        Category result = categoryRepo.save(category);

        testEntityManager.flush();
        testEntityManager.clear();
        return result;
    }
    private void assertOptionalCategory_nameEqualsAnd_allFieldsShouldBeNotNull(Optional<Category> category, String name) {
        assertAll(
                () -> assertTrue(category.isPresent()),

                () -> {
                    var assertCategory = category.get();

                    assertNotNull(assertCategory.getId());
                    assertEquals(name, assertCategory.getName());
                    assertNotNull(assertCategory.getCreatedAt());
                    assertNotNull(assertCategory.getUpdatedAt());
                    assertNotNull(assertCategory.getVersion());
                }
        );
    }
}
