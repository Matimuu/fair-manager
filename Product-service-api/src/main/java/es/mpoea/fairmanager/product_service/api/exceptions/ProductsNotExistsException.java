package es.mpoea.fairmanager.product_service.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductsNotExistsException extends RuntimeException {
    public ProductsNotExistsException() {
        super("No products found in the database");
    }
}
