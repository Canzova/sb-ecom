package com.ecommerce.repository;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository <Product, Long>{
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    List<Product> findAllByProductNameLikeIgnoreCase(String s);

    Product findByProductNameIgnoreCase(@NotNull String productName);

    Page<Product> findByCategory(Category category, Pageable pageDetails);

    Page<Product> findAllByProductNameLikeIgnoreCase(String s, Pageable pageDetails);
}
