package es.mpoea.fairmanager.catalog_service.api.exceptions;

public class CategoryNotExistsException extends RuntimeException {
    public CategoryNotExistsException(Long id) {
        super("Category with id " + id + " not found");
    }
}
