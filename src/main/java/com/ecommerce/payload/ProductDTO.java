package com.ecommerce.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank(message = "Product name is required.")
    @Size(min = 5, message = "Product name must be at least 5 characters long.")
    private String productName;


    private String image;

    @NotBlank(message = "Product description is required.")
    @Size(min = 10, message = "Product name must be at least 10 characters long.")
    private String description;

    @NotNull
    @Min(value = 1, message = "Quantity can not be 0")
    private Integer quantity;

    @NotNull
    @Min(value = 1, message = "Price can not be 0")
    private Double price;

    @NotNull
    private Double discount;

    private Double specialPrice;

}
