package es.mpoea.fairmanager.product_service.api.mappers;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.UpdateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.product_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.product_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getLabel(),
                product.getDescription(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public CreateProductCommand toCreateProductCommand(CreateProductRequest request) {
        return new CreateProductCommand(
                request.label(),
                request.description(),
                request.category()
        );
    }

    public UpdateProductCommand toUpdateProductCommand(UpdateProductRequest request) {
        return new UpdateProductCommand(
                request.label(),
                request.description(),
                request.category()
        );
    }
}
