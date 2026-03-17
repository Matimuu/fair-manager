package es.mpoea.fairmanager.catalog_service.api.exceptions.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(long id) {
        super("Product with id %d not found".formatted(id));
    }
}
