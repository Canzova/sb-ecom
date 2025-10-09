package com.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

//  This is the response which we will give to the controller / user

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private List<CategoryDTO> content;
}
