package com.ecommerce.controller;

import com.ecommerce.payload.ProductDTO;
import com.ecommerce.service.ProductServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {
    ProductServiceImpl productService;

    ProductController(ProductServiceImpl productService){
        this.productService = productService;
    }

    // Add a product in a specific category
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO>addProduct(@RequestBody ProductDTO productDTO,
                                                @PathVariable Long categoryId){
        ProductDTO productAdded = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(productAdded, HttpStatus.CREATED);
    }
}
