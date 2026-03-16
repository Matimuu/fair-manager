package es.mpoea.fairmanager.catalog_service.api.commands.product;

public sealed interface ProductCommand permits CreateProductCommand, UpdateProductCommand {
}
