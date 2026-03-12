package es.mpoea.fairmanager.commondata.DTO.responses.Product;

import es.mpoea.fairmanager.commondata.DTO.responses.Category.CategoryResponse;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        Long id,
        UUID sku,
        String label,
        String description,
        Instant createdAt,
        Instant updatedAt,
        CategoryResponse category
) {
}
