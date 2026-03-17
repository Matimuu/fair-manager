package es.mpoea.fairmanager.catalog_service.api.exceptions.product;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String name) {
        super("Product with name %s already exists.".formatted(name));
    }
    public ProductAlreadyExistsException(long id) {
        super("Product with id %d already exists.".formatted(id));
    }
}
