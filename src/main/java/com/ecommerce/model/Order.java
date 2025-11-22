package com.ecommerce.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Data
@AllArgsConstructor
@NoArgsConstructor

// AN order placed by customer
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Email
    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrderItem> orderItemList = new ArrayList<>();

    private LocalDate orderDate;
    private String orderStatus;

    @OneToOne
    @JoinColumn(name="payment_id")
    private Payment payment;

    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
}
