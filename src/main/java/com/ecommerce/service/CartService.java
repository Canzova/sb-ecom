package com.ecommerce.service;

import com.ecommerce.payload.CartDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart(String userEmail, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
}
