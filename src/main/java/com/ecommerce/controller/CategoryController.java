package com.ecommerce.controller;

import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    // For filed injection you can write @Autowired here as well
    CategoryServiceImpl categoryService;

    /*

        If you have a single constructor spring automatically does the injection
        If you have multiple constructors you need to use @Autowired to tell spring that from this constructor only do DI

    */

    @Autowired
    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    // @RequestMapping(value = "/public/categories", method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse>getAllCategories(){
        CategoryResponse categoryResponse = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryResponse);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDto){
        CategoryDTO savedcategoryDto = categoryService.createCategory(categoryDto);
        String res = "category added Successfully !";
        return ResponseEntity.status(HttpStatus.CREATED).body(savedcategoryDto);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        CategoryDTO deletedCategoryDto = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategoryDto, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDto , @PathVariable Long categoryId){
        CategoryDTO updatedCategoryDto = categoryService.updateCategory(categoryDto, categoryId);
        return new ResponseEntity<>(updatedCategoryDto, HttpStatus.OK);

    }

}
