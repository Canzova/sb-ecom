package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category>allCategories = categoryRepository.findAll();
        if(allCategories.isEmpty()) throw new APIException("No Category created till now.");
        return allCategories;
    }

    @Override
    public void createCategory(Category category) {
        String categoryName = category.getCategoryName().toLowerCase();

        // This category is already present
        if(categoryRepository.findByCategoryName(categoryName).isPresent()){
            throw new APIException("Category with this name already exists.");
        }

        // Convert into lower case so that when user enter the same category with different case it can be found in the db
        category.setCategoryName(categoryName);
        categoryRepository.save(category); // Save into my table
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "id",categoryId));

        categoryRepository.delete(existingCategory); // Deleting row directly into table
        //categoryRepository.deleteById(categoryId);
        return "Category with categoryId : " + categoryId + " is deleted successfully!";
    }

    @Override
    public String updateCategory(Category updatedCategory, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id",categoryId));
        /*
        🧠 Why:
        This ensures that when you save it, JPA understands you are updating the existing entity (not inserting a new one).
         */
        updatedCategory.setCategoryId(categoryId);
        categoryRepository.save(updatedCategory);

        return "Category Updated Successfully!";
    }
}
