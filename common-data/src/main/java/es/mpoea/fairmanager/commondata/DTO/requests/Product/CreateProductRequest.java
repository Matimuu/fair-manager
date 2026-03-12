package es.mpoea.fairmanager.commondata.DTO.requests.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record CreateProductRequest(

        @NotBlank(message = "Label is required.")
        @Size(max = 240, message = "Label must not exceed 240 characters.")
        String label,

        String description,

        Long category
) {
}
