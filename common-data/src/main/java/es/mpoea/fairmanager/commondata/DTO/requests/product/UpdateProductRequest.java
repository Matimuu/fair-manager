package es.mpoea.fairmanager.commondata.DTO.requests.product;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @Size(max = 240, message = "Label must not exceed 240 characters.")
        @Pattern(regexp = ".*\\S.*", message = "Label must not be blank.")
        String label,

        String description,

        @Size(max = 120, message = "Category name must not exceed 120 characters.")
        String categoryName
) {
}
