package es.mpoea.fairmanager.catalog_service.api.commands.product;

public record CreateProductCommand(
        String label,
        String description,
        String categoryName
) implements ProductCommand {
}
