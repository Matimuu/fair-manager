package es.mpoea.fairmanager.catalog_service.api.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String message) {
        super(
                "Category with name "+ message +" already exists"
        );
    }
}
