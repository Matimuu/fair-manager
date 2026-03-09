package es.mpoea.fairmanager.product_service.api.commands;

public record CreateProductCommand(
        String label,
        String description,
        String category
) {
}
