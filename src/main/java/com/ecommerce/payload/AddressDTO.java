package com.ecommerce.payload;

import com.ecommerce.model.User;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long addressId;

    @NotBlank
    @Size(min = 3, message = "Street name must be atleast 3 characters")
    private String street;

    @NotBlank
    @Size(min = 3, message = "Building name must be atleast 3 characters")
    private String building;

    @NotBlank
    @Size(min = 2, message = "State name must be atleast 3 characters")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name must be atleast 3 characters")
    private String country;

    @NotBlank
    @Size(min = 2, message = "Country name must be atleast 3 characters")
    private String city;

    @NotNull(message = "Pin code is required")
    @Min(value = 2, message = "Country name must be atleast 3 characters")
    private Long pinCode;

//    @ToString.Exclude
//    @ManyToMany(mappedBy = "addressList")
//    private List<User> userList = new ArrayList<>();
}
