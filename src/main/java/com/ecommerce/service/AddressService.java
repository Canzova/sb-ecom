package com.ecommerce.service;

import com.ecommerce.payload.AddressDTO;
import com.ecommerce.payload.AddressResponse;
import jakarta.validation.Valid;

public interface AddressService {
    AddressDTO addAddress(@Valid AddressDTO address);

    AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressResponse getAddressById(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressResponse getloggedinUserAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
