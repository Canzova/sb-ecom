package com.ecommerce.service;

import com.ecommerce.model.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> getAllCategories();
    public void createCategory(Category category);

    String deleteCategory(Long categoryId);

    String updateCategory(Category category, Long categoryId);
}
