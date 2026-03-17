package es.mpoea.fairmanager.catalog_service.api.exceptions.category;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String message) {
        super(
                "Category with name %s already exists".formatted(message)
        );
    }
}
