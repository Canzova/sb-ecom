package com.ecommerce.service;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

//    private List<Category> categories = new ArrayList<>();
    //private Long categoryId = 1L;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        //category.setCategoryId(categoryId++);
        categoryRepository.save(category); // Save into my table
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found."));

        categoryRepository.delete(existingCategory); // Deleting row directly into table
        //categoryRepository.deleteById(categoryId);
        return "Category with categoryId : " + categoryId + " is deleted successfully!";
    }

    @Override
    public String updateCategory(Category updatedCategory, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found."));
        /*
        🧠 Why:
        This ensures that when you save it, JPA understands you are updating the existing entity (not inserting a new one).
         */
        updatedCategory.setCategoryId(categoryId);
        categoryRepository.save(updatedCategory);

        return "Category Updated Successfully!";
    }
}
