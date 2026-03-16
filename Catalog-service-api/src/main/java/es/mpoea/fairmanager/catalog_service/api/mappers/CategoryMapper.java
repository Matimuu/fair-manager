package es.mpoea.fairmanager.catalog_service.api.mappers;

import es.mpoea.fairmanager.catalog_service.api.commands.category.CreateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.category.UpdateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.commondata.DTO.requests.category.CreateCategoryRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.category.UpdateCategoryRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.category.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CreateCategoryCommand toCreateCommand(CreateCategoryRequest request) {
        return new CreateCategoryCommand(request.name());
    }

    public UpdateCategoryCommand toUpdateCommand(UpdateCategoryRequest request) {
        return new UpdateCategoryCommand(request.name());
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
