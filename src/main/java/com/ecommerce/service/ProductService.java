package com.ecommerce.service;

import com.ecommerce.payload.ProductDTO;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);
}
