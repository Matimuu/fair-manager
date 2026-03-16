package es.mpoea.fairmanager.commondata.DTO.requests.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(

        @NotBlank(message = "Label is required.")
        @Size(max = 240, message = "Label must not exceed 240 characters.")
        String label,

        String description,

        @Size(max = 120, message = "Category name must not exceed 120 characters.")
        String categoryName
) {
}
