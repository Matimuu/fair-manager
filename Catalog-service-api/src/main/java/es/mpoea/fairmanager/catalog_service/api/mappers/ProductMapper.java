package es.mpoea.fairmanager.catalog_service.api.mappers;

import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.UpdateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.Category.CategoryResponse;
import es.mpoea.fairmanager.commondata.DTO.responses.Product.ProductResponse;
import es.mpoea.fairmanager.catalog_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toProductResponse(Product product) {
        Category category = product.getCategory();
        CategoryResponse categoryResponse = null;

        if (category != null) {
            categoryResponse = new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );
        }

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getLabel(),
                product.getDescription(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                categoryResponse
        );
    }

    public CreateProductCommand toCreateProductCommand(CreateProductRequest request) {
        return new CreateProductCommand(
                request.label(),
                request.description(),
                request.categoryName()
        );
    }

    public UpdateProductCommand toUpdateProductCommand(UpdateProductRequest request) {
        return new UpdateProductCommand(
                request.label(),
                request.description(),
                request.categoryName()
        );
    }
}
