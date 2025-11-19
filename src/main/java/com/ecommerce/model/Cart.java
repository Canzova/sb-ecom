package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor

/*
 Cart {
    CartItem 1 → cartItemId, cartId,  Product name, product price, quantity
    CartItem 2 → cartItemId, cartId,  Product name, product price, quantity
    CartItem 3 → cartItemId, cartId,  Product name, product price, quantity
 }
 */


public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "User_id")
    private User user;

    @OneToMany(mappedBy = "cart",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
//    If a child (CartItem) is removed from the parent’s list,
//    Hibernate will DELETE that child from the DB automatically.
    private List<CartItem> cartItem = new ArrayList<>();

    private Double totalPrice = 0.0;
}
