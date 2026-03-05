package es.mpoea.fairmanager.commondata.DTO.requests.Product;

import jakarta.validation.constraints.Size;

public record UpdateProductRequest(

        @Size(max = 240)
        String label,

        String description,

        @Size(max = 120)
        String category
) {
}
