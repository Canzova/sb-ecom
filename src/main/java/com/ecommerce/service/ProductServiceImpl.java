package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.payload.CartDTO;
import com.ecommerce.payload.ProductDTO;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductServiceImpl implements ProductService{

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    @Autowired
    private CartService cartService;

    ProductServiceImpl( ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Autowired
    FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        // Check if product already exists with the product name
        Product productWithSameName = productRepository.findByProductNameIgnoreCase(productDTO.getProductName());

        if(productWithSameName != null) throw new APIException("Product exists already.");

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
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // This sort object will tell the variable based on which sorting to be done and its order
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product>productList = productPage.getContent();

        if(productList.isEmpty()) throw new APIException("No Products added till now.");

        // List<Product> products = productRepository.findAll();
        List<ProductDTO>productDTOS = productList.stream()
                .map(product-> modelMapper.map(product, ProductDTO.class))
                .toList();

        return new ProductResponse(productDTOS, productPage.getNumber(),
                productPage.getTotalPages(), productPage.getSize(), productPage.getTotalElements()
                ,productPage.isLast());
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category", "Category Id", categoryId));

        //List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategory(category, pageDetails);

        List<Product>productList = productPage.getContent();

        if(productList.isEmpty()) throw new APIException("Products Not Found.");

        List<ProductDTO>productDTOS = productList.stream()
                .map(product-> modelMapper.map(product, ProductDTO.class))
                .toList();

        return new ProductResponse(productDTOS, productPage.getNumber(),
                productPage.getTotalPages(), productPage.getSize(), productPage.getTotalElements()
                ,productPage.isLast());
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAllByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Product>productList = productPage.getContent();

        if(productList.isEmpty()) throw new APIException("Products Not Found.");

        List<ProductDTO>productDTOs = productList.stream()
                .map((product)-> modelMapper.map(product, ProductDTO.class))
                .toList();

        return new ProductResponse(productDTOs, productPage.getNumber(),
                productPage.getTotalPages(), productPage.getSize(), productPage.getTotalElements()
                ,productPage.isLast());
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        // Firstly it is imp to find out this productId exists or not
        Product product = productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("Product", "ProductId", productId));

        // Update fields manually
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setImage("default.png");

        // Recalculate special price
        double price = product.getPrice();
        double discount = product.getDiscount();
        product.setSpecialPrice(price - ((discount * 0.01) * price));

        Product savedProduct = productRepository.save(product);

        // Get all the carts which have this product
        List<Cart>carts = cartRepository.findCartByProductId(productId);

        // Convert carts into cartDTOs which we will use to update the cart info
        List<CartDTO>cartDTOS = carts.stream()
                .map(cart-> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                    List<ProductDTO>products = cart.getCartItem().stream()
                            .map(p-> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .toList();

                    cartDTO.setProducts(products);
                    return cartDTO;
                })
                .toList();

        // Delete the product from each cart one by one
        cartDTOS.forEach(cartDto -> cartService.updateProductInCart(cartDto.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        // Check if this product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        // Get all the carts which have this product
        List<Cart>carts = cartRepository.findCartByProductId(productId);

        List<CartDTO>cartDTOS = carts.stream()
                .map(cart-> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                    List<ProductDTO>products = cart.getCartItem().stream()
                            .map(p-> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .toList();

                    cartDTO.setProducts(products);
                    return cartDTO;
                })
                .toList();

        // Delete the product from each cart one by one
        cartDTOS.forEach(cartDto -> cartService.deleteProductFromCart(cartDto.getCartId(), productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    // when a user uploads an image in a form (like photo.jpg), Spring Boot wraps it inside this MultipartFile object.
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get the product from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        // Upload the image to server (in /image directory & Get the file name of uploaded image

        //String path = "images/";  // Image folder in root directory
        String fileName = fileService.uploadImage(path, image);

        // Updating the new filename to the product
        productFromDb.setImage(fileName);

        // Save the updated product
        Product updatedProduct = productRepository.save(productFromDb);

        // Return ProductDTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
