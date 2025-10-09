package com.ecommerce.service;


import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;



public interface CategoryService {
    public CategoryResponse getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDto);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO category, Long categoryId);
}
