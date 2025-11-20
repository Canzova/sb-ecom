package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.payload.CartDTO;
import com.ecommerce.payload.ProductDTO;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Step 1 : check if the current user already have a cart, if not create one
        Cart cart = createCart(productId);

        // Step 2 : Get the product
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        // Step 3 : Do some validations

        // Check if this product is already their in the user cart
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());

        if(cartItem != null){
            throw new APIException("Product " + product.getProductName() + " already exists in cart");
        }

        if(product.getQuantity() == 0) throw new APIException("Product " + product.getProductName() + " is not available");

        if(product.getQuantity() < quantity)
            throw new APIException("Please, make an order of the " + product.getProductName()
             + " less than or equal to the quantity " + product.getQuantity() + ".");

        // Step 3 : Create  a new Cart Item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        // Reduce the stock
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        // Also put this cartItem into your cart
        cart.getCartItem().add(newCartItem);
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // This step is required because your cartDTO has a list of ProductDTO
        // This ProductDTO represents a product added into the cart by the user
        // The quantity in this cart is the quantity of product user has added into the cart
        List<CartItem> cartItems = cart.getCartItem();

        Stream<ProductDTO> productDtoList = cartItems.stream()
                .map(item -> {
                    ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                    /*
                        Product 1 : Available quantity 200
                                    Quantity added itno cart 4

                        CartDTO{
                            carrtId,
                            totalPrce,
                            products{
                                product 1 {
                                    quantity : 4
                                }
                            }
                        }
                     */
                    map.setQuantity(item.getQuantity());

                    return map;
                });

        cartDTO.setProducts(productDtoList.toList());

        return cartDTO;
    }

    private Cart createCart(Long productId) {
        Cart currentCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(currentCart != null) return currentCart;

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);
    }


    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()) throw new APIException("No cart exists!");

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItem().stream()
                            .map(item ->{
                                ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                                productDTO.setQuantity(item.getQuantity());

                                return productDTO;
                            })
                            .toList();

                    cartDTO.setProducts(products);
                    return cartDTO;
                })
                .toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getUserCart(String userEmail, Long cartId) {
        // Step 1 : Get the cart
        Cart cart = cartRepository.findByEmailAndCartId(userEmail, cartId);

        if(cart == null) throw new APIException("Cart is Empty");

        // Step 2 : Convert this into DTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // Step 3 : Inside cartDTO you have products, convert them into ProductDTO
        List<CartItem> cartItems = cart.getCartItem();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map((item)->{
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());

                    return productDTO;
                });

        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity() == 0) throw new APIException("Product " + product.getProductName() + " is not available");

        if(product.getQuantity() < quantity)
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);

        if(cartItem == null) throw new APIException("Product " + product.getProductName() + " is not available in cart.");
        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < 0) throw new APIException("The resulting quantity can not be negative.");

        if(newQuantity == 0) deleteProductFromCart(cartId, productId);
        else{
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() +  quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
        }


//        CartItem updatedItem = cartItemRepository.save(cartItem);
//        if(updatedItem.getQuantity() == 0){
//            cartItemRepository.deleteById(updatedItem.getCartItemId());
//        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem>cartItems = cart.getCartItem();

        Stream<ProductDTO>productStream = cartItems.stream()
                .map(item->{
                    ProductDTO prod = modelMapper.map(item.getProduct(), ProductDTO.class);
                    prod.setQuantity(item.getQuantity());
                    return prod;
                });

        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }


//    @Override
//    @Transactional
//    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
//
//        // Step 1: Get the cart
//        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
//        if (cart == null) throw new APIException("Cart not found");
//
//        // Step 2: Get the product
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
//
//        // Step 3: Find the CartItem inside this cart
//        CartItem cartItem = cart.getCartItem().stream()
//                .filter(item -> item.getProduct().getProductId().equals(productId))
//                .findFirst()
//                .orElseThrow(() -> new APIException("Product is not in cart"));
//
//        int currentQty = cartItem.getQuantity();
//        int newQty = currentQty + quantity;
//
//        // Step 4: Validations
//        if (quantity == 1 && currentQty >= product.getQuantity()) {
//            throw new APIException("Stock not available");
//        }
//
//        if (newQty < 0) {
//            throw new APIException("Invalid quantity");
//        }
//
//        // Step 5: Update or remove cart item
//        if (newQty == 0) {
//            // Remove item from cart → orphanRemoval deletes from DB
////            cartItem.setCart(null);  // VERY IMPORTANT
//            cart.getCartItem().remove(cartItem);
////            cartItemRepository.delete(cartItem);
//
//        } else {
//            // Update quantity
//            cartItem.setQuantity(newQty);
//            cartItemRepository.save(cartItem);
//        }
//
//        // Step 6: Recalculate total price from scratch
//        double newTotal = cart.getCartItem().stream()
//                .mapToDouble(item -> item.getProduct().getSpecialPrice() * item.getQuantity())
//                .sum();
//
//        cart.setTotalPrice(newTotal);
//        cartRepository.save(cart);
//
//        // Step 7: Convert to DTO
//        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
//
//        List<ProductDTO> productDtoList = cart.getCartItem().stream()
//                .map(item -> {
//                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
//                    dto.setQuantity(item.getQuantity());
//                    return dto;
//                }).toList();
//
//        cartDTO.setProducts(productDtoList);
//
//        return cartDTO;
//    }


    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart","cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null) throw new ResourceNotFoundException("Product", "productId", productId);

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cartId);

        return "Product" + cartItem.getProduct().getProductName() + " is deleted.";
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null) throw new APIException("Cart Item not found");

        // Step 1 : Remove this product price from total price in cart
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getQuantity() * cartItem.getProductPrice()));

        // Stp 2 : Now update the product price in cartItem
        cartItem.setProductPrice(product.getSpecialPrice());

        // Step 3 : Now add this updated cartItem price and quantity in cart
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getQuantity() * cartItem.getProductPrice()));

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }


}
