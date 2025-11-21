package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Address;
import com.ecommerce.model.Category;
import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;
import com.ecommerce.payload.AddressResponse;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.util.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO addAddress(AddressDTO address) {

        // Step 0 : Get the logged-in user
        User user = authUtil.loggedInUser();

        // Step 1 : Convert this addressDTO to address entity
        Address addressEntity = modelMapper.map(address, Address.class);

        // Add the user info
        addressEntity.setUser(user);

        // Step 2 : Save it into the DB
        //  ---> No Need because User is owner it has cascade properties it wil automatically save the child address
        Address savedAddress = addressRepository.save(addressEntity);

        // Step 3 : Also add this address into user
        user.getAddressList().add(savedAddress);

        // Step 5 : You have updated the address inside user so also need to save user
        User savedUser = userRepository.save(user);


        // Step 4 : Convert the savedAddress to AddressDTO
        return modelMapper.map(savedAddress, AddressDTO.class);
    }


    @Override
    public AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Address> categoryPage = addressRepository.findAll(pageDetails);

        List<Address>addressList = categoryPage.getContent();
        if(addressList.isEmpty()) throw new APIException("Address not found.");

        List<AddressDTO>addressDTOList = addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        AddressResponse response = new AddressResponse(addressDTOList, categoryPage.getNumber(),
                categoryPage.getTotalPages(), categoryPage.getSize(), categoryPage.getTotalElements(), categoryPage.isLast());

        return response;
    }

    @Override
    public AddressResponse getAddressById(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Step 1 : Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User", "userId", userId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Address> categoryPage = addressRepository.findAddressByUserId(userId, pageDetails);

        // Get the addresses which belongs to this user
        List<Address>addressList = categoryPage.getContent();
        if(addressList.isEmpty()) throw new APIException("Address not found.");


        // Convert it to DTO
        List<AddressDTO>addressDTOList = addressList.stream().
                map(add->modelMapper.map(add, AddressDTO.class))
                .toList();

        AddressResponse response = new AddressResponse(addressDTOList, categoryPage.getNumber(),
                categoryPage.getTotalPages(), categoryPage.getSize(), categoryPage.getTotalElements(), categoryPage.isLast());

        return response;
    }

    @Override
    public AddressResponse getloggedinUserAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        User user = authUtil.loggedInUser();
        if(user == null) throw new APIException("Please Signing first");
        Long userId = user.getUserId();

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Address> categoryPage = addressRepository.findAddressByUserId(userId, pageDetails);

        List<Address>addressList = categoryPage.getContent();
        if(addressList.isEmpty()) throw new APIException("Address not found.");

        List<AddressDTO>addressDTOList = addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        AddressResponse response = new AddressResponse(addressDTOList, categoryPage.getNumber(),
                categoryPage.getTotalPages(), categoryPage.getSize(), categoryPage.getTotalElements(), categoryPage.isLast());

        return response;
    }

}
