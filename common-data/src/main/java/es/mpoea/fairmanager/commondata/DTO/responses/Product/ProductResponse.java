package es.mpoea.fairmanager.commondata.DTO.responses.Product;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        Long id,
        UUID sku,
        String label,
        String description,
        String category,
        Instant createdAt,
        Instant updatedAt
) {
}
