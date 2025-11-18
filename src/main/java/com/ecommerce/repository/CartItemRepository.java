package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // JPA cannot do nested query if naming convention is not correct. So we wrote the query by ourself
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?2 AND ci.product.productId = ?1")
    CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);
}
