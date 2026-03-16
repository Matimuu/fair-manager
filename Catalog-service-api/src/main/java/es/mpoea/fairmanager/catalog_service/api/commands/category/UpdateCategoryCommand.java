package es.mpoea.fairmanager.catalog_service.api.commands.category;

public record UpdateCategoryCommand(
        String name
) implements CategoryCommand{
}
