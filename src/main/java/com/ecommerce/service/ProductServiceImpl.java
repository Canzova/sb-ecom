package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.payload.ProductDTO;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    ProductServiceImpl( ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        // Step 1 : Get the category
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category", "Category Id", categoryId));

        Product product = modelMapper.map(productDTO, Product.class);

        product.setCategory(category);
        Double price = product.getPrice();
        Double discount = product.getDiscount();

        Double specialPrize = price - ((discount * 0.01) * price);
        product.setSpecialPrice(specialPrize);
        product.setImage("default.png");

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO>productDTOS = products.stream()
                .map(product-> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category", "Category Id", categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO>productDTOS = products.stream()
                .map(product-> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword) {
        List<Product>products = productRepository.findAllByProductNameLikeIgnoreCase('%' + keyword + '%');
        if(products.isEmpty()) throw new APIException("No Product Found");

        List<ProductDTO>productDTOs = products.stream()
                .map((product)-> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        // Firstly it is imp to find out this productId exists or not
        Product product = productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("Product", "ProductId", productId));

        Product updatedProduct = modelMapper.map(productDTO, Product.class);

        updatedProduct.setProductId(productId);

        // Updating image and specialPrice
        Double price = updatedProduct.getPrice();
        Double discount = updatedProduct.getDiscount();

        Double specialPrize = price - ((discount * 0.01) * price);
        updatedProduct.setSpecialPrice(specialPrize);
        updatedProduct.setImage("default.png");

        return modelMapper.map(productRepository.save(updatedProduct), ProductDTO.class);
    }
}
