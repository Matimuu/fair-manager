package es.mpoea.fairmanager.catalog_service.api.services;

import es.mpoea.fairmanager.catalog_service.api.commands.category.CreateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.category.UpdateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.category.CategoryAlreadyExistsException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.category.CategoryNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.utils.Util;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private static final String ERROR_MESSAGE = "Category name cannot be null or blank";

    @Transactional
    public Category createCategory(CreateCategoryCommand command) {
        String categoryName = Util.normalizeName(command.name());
        Util.requireValidName(categoryName, ERROR_MESSAGE);

        if (categoryRepo.existsByName(categoryName))
            throw new CategoryAlreadyExistsException(categoryName);

        return categoryRepo.save(new Category(categoryName));
    }

    @Transactional
    public Category getOrCreateCategory(String name) {
        String categoryName = Util.normalizeName(name);
        Util.requireValidName(categoryName, ERROR_MESSAGE);

        return categoryRepo
                .findByName(categoryName)
                .orElseGet(() -> categoryRepo.save(new Category(categoryName)));
    }

    public Category getCategory(String name) {
        String categoryName =  Util.normalizeName(name);
        Util.requireValidName(categoryName, ERROR_MESSAGE);

        return categoryRepo.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName));
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
        String categoryName = Util.normalizeName(command.name());
        Util.requireValidName(categoryName, ERROR_MESSAGE);

        Category category = categoryRepo
                .findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (categoryName.equals(category.getName()))
            return category;

        categoryRepo
                .findByName(categoryName)
                .ifPresent(found -> {
                            throw new CategoryAlreadyExistsException(categoryName);
                        }
                );

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
