package com.cabbooking.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String id;
    private String rideId;
    private String userId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
}
