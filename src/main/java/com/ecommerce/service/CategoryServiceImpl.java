package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category>allCategories = categoryPage.getContent();
        if(allCategories.isEmpty()) throw new APIException("No Category created till now.");

        List<CategoryDTO>categoryDtoList = allCategories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        return new CategoryResponse(categoryDtoList, categoryPage.getNumber(),
                categoryPage.getTotalPages(), categoryPage.getSize(), categoryPage.getTotalElements()
                ,categoryPage.isLast());

    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {

        Category category = modelMapper.map(categoryDto, Category.class);
        String categoryName = category.getCategoryName().toLowerCase();

        // This category is already present
        if(categoryRepository.findByCategoryName(categoryName).isPresent()){
            throw new APIException("Category with this name already exists.");
        }

        // Convert into lower case so that when user enter the same category with different case it can be found in the db
        category.setCategoryName(categoryName);
        Category savedCategory = categoryRepository.save(category); // Save into my table

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "id",categoryId));

        categoryRepository.delete(existingCategory); // Deleting row directly into table
        //categoryRepository.deleteById(categoryId);
        return modelMapper.map(existingCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDto, Long categoryId) {

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id",categoryId));

        Category updatedCategory = modelMapper.map(categoryDto, Category.class);
        // Before saving just change the category name into lowercase. Because in our db we are storing the categoryNames
        // in lowercase only

        updatedCategory.setCategoryName(updatedCategory.getCategoryName().toLowerCase());
        /*
        🧠 Why:
        This ensures that when you save it, JPA understands you are updating the existing entity (not inserting a new one).
         */

        updatedCategory.setCategoryId(categoryId);
        categoryRepository.save(updatedCategory);

       return modelMapper.map(updatedCategory, CategoryDTO.class);
    }
}
