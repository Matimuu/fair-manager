package es.mpoea.fairmanager.catalog_service.api.commands;

public record UpdateProductCommand(
        String label,
        String description,
        String category
) {
}
