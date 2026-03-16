package es.mpoea.fairmanager.commondata.DTO.responses.category;

import java.time.Instant;

public record CategoryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}

