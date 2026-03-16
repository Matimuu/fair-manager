package es.mpoea.fairmanager.commondata.DTO.requests.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank(message = "Name must not be blank.")
        @Size(max = 120, message = "Name must not exceed 120 characters.")
        String name
) {
}
