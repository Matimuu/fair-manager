package es.mpoea.fairmanager.product_service.api.commands;

public record UpdateProductCommand(
        String label,
        String description,
        String category
) {
}
