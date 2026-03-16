package es.mpoea.fairmanager.catalog_service.api.commands.category;

public record CreateCategoryCommand(
        String name
) implements CategoryCommand {
}
