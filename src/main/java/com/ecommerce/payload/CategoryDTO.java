package com.ecommerce.payload;

// Represents the request which we are getting from the controller / user
// From client to server
// ---> Representing category in presentation layer, When you send the response it should also be in Same format i.e in DTO
// Also used to send only required data to user, data hiding

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long categoryId;

    @NotBlank
    @Size(min = 5, message = "Category must have at least 5 characters.")
    private String categoryName;
}
