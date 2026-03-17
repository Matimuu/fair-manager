package es.mpoea.fairmanager.catalog_service.api.services;

import es.mpoea.fairmanager.catalog_service.api.commands.category.CreateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.category.UpdateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.category.CategoryAlreadyExistsException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.category.CategoryNotFoundException;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;

    @Transactional
    public Category createCategory(CreateCategoryCommand command) {
        String categoryName = command.name() == null ? null : command.name().trim();

        if (categoryName == null || categoryName.isBlank())
            throw new IllegalArgumentException("Category name cannot be null or blank");

        Optional<Category> category = categoryRepo.findByName(categoryName);

        if (category.isPresent())
            throw new CategoryAlreadyExistsException(categoryName);

        return categoryRepo.save(new Category(categoryName));
    }

    @Transactional
    public Category getOrCreateCategory(String name) {
        String categoryName = name == null ? null : name.trim();

        if (categoryName == null || categoryName.isBlank())
            throw new IllegalArgumentException("Category name cannot be null or blank");

        return categoryRepo
                .findByName(categoryName)
                .orElseGet(() -> categoryRepo.save(new Category(categoryName)));
    }

    public Category getCategory(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Category name cannot be null or blank");

        return categoryRepo.findByName(name.trim())
                .orElseThrow(() -> new CategoryNotFoundException(name));
    }

    public Category getCategory(long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Transactional
    public Category updateCategory(long id, UpdateCategoryCommand command) {
        String categoryName = command.name() == null ? null : command.name().trim();

        if (categoryName == null || categoryName.isBlank())
            throw new IllegalArgumentException("Category name cannot be null or blank");

        Category category = categoryRepo
                .findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepo
                .findByName(categoryName)
                .filter(cat -> cat.getId() != id)
                .ifPresent(found -> {
                    throw new CategoryAlreadyExistsException(command.name());
                });

        category.setName(categoryName);

        return categoryRepo.save(category);
    }

    @Transactional
    public void deleteCategory(long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        productRepo.clearCategoryByCategoryId(category.getId());
        categoryRepo.deleteById(id);
    }
}
