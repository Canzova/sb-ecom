package com.ecommerce.repository;

import com.ecommerce.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT add FROM Address add WHERE add.user.userId = ?1")
    Page<Address> findAddressByUserId(Long userId, Pageable pageDetails);
}
