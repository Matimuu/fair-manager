package es.mpoea.fairmanager.commondata.DTO.requests.Product;

import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @Size(max = 240, message = "Label must not exceed 240 characters.")
        String label,

        String description,

        Long category
) {
}
