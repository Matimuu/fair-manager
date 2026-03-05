package es.mpoea.fairmanager.commondata.DTO.requests.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(

        @NotBlank
        @Size(max = 240)
        String label,

        String description,

        @NotBlank
        @Size(max = 120)
        String category
) {
}
