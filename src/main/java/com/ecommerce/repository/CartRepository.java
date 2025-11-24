package com.ecommerce.repository;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c FROM Cart c WHERE c.cartId = ?2 AND c.user.email = ?1")
    Cart findByEmailAndCartId(String userEmail, Long cartId);

    /*
    🧩 Breaking down your query

    JOIN FETCH c.cartItem ci
    → Load Cart and CartItems eagerly.

    JOIN FETCH ci.product p
    → Load the Product for each CartItem eagerly.

    WHERE p.productId = ?1
    → Only return carts that contain a cartItem whose product matches the given productId.

       🧠 Why is this useful?
    ✔ Avoids LazyInitializationException

     */

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItem ci JOIN FETCH ci.product p WHERE p.productId = ?1")
    List<Cart> findCartByProductId(Long productId);

    List<CartItem> findByCartId(Long cartId);
}
