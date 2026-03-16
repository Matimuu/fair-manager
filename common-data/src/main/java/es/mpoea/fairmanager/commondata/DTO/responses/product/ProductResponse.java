package es.mpoea.fairmanager.commondata.DTO.responses.product;

import es.mpoea.fairmanager.commondata.DTO.responses.category.CategoryResponse;

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
