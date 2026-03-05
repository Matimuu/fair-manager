package es.mpoea.fairmanager.commondata.DTO.requests.Product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(

        @NotNull
        @Size(max = 240)
        String label,

        String description,

        @NotNull
        @Size(max = 120)
        String category
) {
}
