package com.ecommerce.controller;

import com.ecommerce.config.AppConstants;
import com.ecommerce.payload.AddressDTO;
import com.ecommerce.payload.AddressResponse;
import com.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
/*
    🌟 Why Should Controller Methods Be Public?
    Spring Boot uses reflection to find and call your controller methods.
 */
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO address) {
        AddressDTO savedAddress = addressService.addAddress(address);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<AddressResponse> getAllAddress(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                         @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                         @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESS_BY, required = false) String sortBy,
                                                         @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        AddressResponse allAddresses = addressService.getAllAddresses(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(allAddresses, HttpStatus.OK);
    }

    @GetMapping("/addresses/{userId}")
    public ResponseEntity<AddressResponse> getAddressById(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                          @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                          @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESS_BY, required = false) String sortBy,
                                                          @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
                                                          @PathVariable Long userId) {
        AddressResponse allAddresses = addressService.getAddressById(userId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(allAddresses, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<AddressResponse> getloggedinUserAddress(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                         @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                         @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESS_BY, required = false) String sortBy,
                                                         @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        AddressResponse allAddresses = addressService.getloggedinUserAddress(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(allAddresses, HttpStatus.OK);
    }
}
