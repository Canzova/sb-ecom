package com.ecommerce.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotNull
    private String productName;

    private String image;

    @NotNull
    private String description;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double price;

    @NotNull
    private Double discount;

    private Double specialPrice;

}
