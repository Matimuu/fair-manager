package es.mpoea.fairmanager.catalog_service.api.exceptions.category;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category with id %s not found".formatted(id));
    }

    public CategoryNotFoundException(String name) {
        super("Category with name %s not found".formatted(name));
    }
}
