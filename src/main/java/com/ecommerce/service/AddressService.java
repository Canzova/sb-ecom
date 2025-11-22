package com.ecommerce.service;

import com.ecommerce.payload.AddressDTO;
import com.ecommerce.payload.AddressResponse;
import jakarta.validation.Valid;

public interface AddressService {
    AddressDTO addAddress(@Valid AddressDTO address);

    AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressResponse getAddressByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressResponse getloggedinUserAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressDTO updateAddressById(Long addressId, AddressDTO address);

    AddressDTO deleteAddressById(Long addressId);

    AddressDTO getAddressByAddressId(Long addressId);
}
