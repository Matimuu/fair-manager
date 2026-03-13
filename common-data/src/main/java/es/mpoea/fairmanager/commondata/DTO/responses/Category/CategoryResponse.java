package es.mpoea.fairmanager.commondata.DTO.responses.Category;

import java.time.Instant;

public record CategoryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}

