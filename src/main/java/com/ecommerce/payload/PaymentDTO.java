package com.ecommerce.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Represents the payment details of the order
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod;

    // Info which we are getting from the payment gateway
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}
