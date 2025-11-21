package com.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressResponse {
    List<AddressDTO>content;
    private Integer pageNumber;
    private Integer totalPages;
    private Integer pageSize;
    private Long totalElements;
    private Boolean lastPage;
}
