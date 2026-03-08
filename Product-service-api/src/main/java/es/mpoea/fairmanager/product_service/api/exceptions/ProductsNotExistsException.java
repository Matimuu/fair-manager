package es.mpoea.fairmanager.product_service.api.exceptions;

public class ProductsNotExistsException extends RuntimeException {
    public ProductsNotExistsException() {
        super("No products found in the database");
    }
}
