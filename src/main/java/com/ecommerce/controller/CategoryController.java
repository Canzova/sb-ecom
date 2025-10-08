package com.ecommerce.controller;

import com.ecommerce.model.Category;
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
    public ResponseEntity<List<Category>>getAllCategories(){
        List<Category>res = categoryService.getAllCategories();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@Valid @RequestBody Category category){
        categoryService.createCategory(category);
        String res = "category added Successfully !";
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
        try{
            String status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
        }catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category , @PathVariable Long categoryId){
        try{
            String updatedRes = categoryService.updateCategory(category, categoryId);
            return new ResponseEntity<>(updatedRes, HttpStatus.OK);
        }catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

}
